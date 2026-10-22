pipeline {
    agent { label 'java17-docker' }

    options {
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        timestamps()
    }

    parameters {
        choice(name: 'TEST_ENVIRONMENT', choices: ['staging', 'sit', 'uat', 'production'], description: 'Target application environment')
        choice(name: 'EXECUTION_PROVIDER', choices: ['both', 'grid', 'browserstack'], description: 'Run on Grid, BrowserStack, or both providers')
        string(name: 'CUCUMBER_FILTER_TAGS', defaultValue: '@Smoke', description: 'Cucumber tag expression; production runs must begin with @ProductionSafe')
        string(name: 'EXPECTED_SCENARIO_NAMES', defaultValue: 'Login page exposes its form and advertised demo accounts,Successful login opens the product catalog', description: 'Comma-separated Serenity scenario names expected in each provider report')
        booleanParam(name: 'CONFIRM_PRODUCTION_TESTS', defaultValue: false, description: 'Required safeguard when TEST_ENVIRONMENT is production')
    }

    environment {
        SELENIUM_PORT = '4444'
        COMPOSE_PROJECT_NAME = "saucedemo-${BUILD_NUMBER}-${EXECUTOR_NUMBER}"
        TEST_BUILD_NAME = "SauceDemo-${BUILD_NUMBER}"
        HEADLESS_MODE = 'true'
    }

    stages {
        stage('Checkout') {
            steps { checkout scm }
        }

        stage('Surefire unit tests') {
            steps {
                script {
                    sh 'rm -rf reports ci-evidence && mkdir -p ci-evidence/unit-tests reports'
                    withEnv(['UNIT_EVIDENCE_DIR=./ci-evidence/unit-tests']) {
                        sh 'docker compose --profile ci run --build --rm unit-test-runner'
                        sh "find ci-evidence/unit-tests/surefire-reports -type f -name 'TEST-*.xml' -print -quit | grep -q . || { echo 'Surefire XML results missing' >&2; exit 1; }"
                    }
                }
            }
        }

        stage('Run SauceDemo UI scenarios') {
            steps {
                script {
                    if (params.TEST_ENVIRONMENT == 'production' && !params.CONFIRM_PRODUCTION_TESTS) {
                        error('Production execution requires CONFIRM_PRODUCTION_TESTS=true.')
                    }
                    if (!params.EXPECTED_SCENARIO_NAMES?.trim()) {
                        error('Set EXPECTED_SCENARIO_NAMES so each provider report can be checked against expected coverage.')
                    }
                    String requestedTags = params.CUCUMBER_FILTER_TAGS?.trim() ?: '@CartPrice'
                    String effectiveTags = "(${requestedTags}) and not @manual"
                    def runProvider = { String provider ->
                        def providerEnvironment = [
                            "TEST_ENVIRONMENT=${params.TEST_ENVIRONMENT}",
                            "CUCUMBER_FILTER_TAGS=${params.CUCUMBER_FILTER_TAGS}",
                            "EXPECTED_EFFECTIVE_TAG_FILTER=${effectiveTags}",
                            "EXPECTED_SCENARIO_NAMES=${params.EXPECTED_SCENARIO_NAMES}",
                            "EXECUTION_PROVIDER=${provider}",
                            "TEST_REPORT_DIR=./reports/${provider}",
                            "TEST_EVIDENCE_DIR=./ci-evidence/${provider}"
                        ]
                        withEnv(providerEnvironment) {
                            sh "rm -rf reports/${provider} ci-evidence/${provider} && mkdir -p reports/${provider} ci-evidence/${provider}"
                            if (provider == 'browserstack') {
                                withCredentials([usernamePassword(credentialsId: 'browserstack-credentials',
                                        usernameVariable: 'BROWSERSTACK_USERNAME',
                                        passwordVariable: 'BROWSERSTACK_ACCESS_KEY')]) {
                                    sh 'docker compose --profile browserstack up --build --abort-on-container-exit --exit-code-from browserstack-runner browserstack-runner'
                                }
                            } else {
                                sh 'docker compose --profile grid up --build --abort-on-container-exit --exit-code-from grid-runner grid-runner'
                            }
                            sh """
                                set -eu
                                find 'reports/${provider}' -type f -name index.html -print -quit | grep -q . || { echo 'Serenity aggregate report missing for ${provider}' >&2; exit 1; }
                                find 'ci-evidence/${provider}/site/serenity' -type f -name '*.json' -print -quit | grep -q . || { echo 'Serenity scenario data missing for ${provider}' >&2; exit 1; }
                                find 'ci-evidence/${provider}' -type f -path '*/failsafe-reports/*.xml' -print -quit | grep -q . || { echo 'Failsafe results missing for ${provider}' >&2; exit 1; }
                                grep -Fxq "cucumber.filter.tags=\$EXPECTED_EFFECTIVE_TAG_FILTER" 'reports/${provider}/run-metadata.properties' || { echo 'Effective Cucumber filter metadata missing for ${provider}' >&2; exit 1; }
                                grep -Fxq "target.environment=\$TEST_ENVIRONMENT" 'ci-evidence/${provider}/site/serenity/run-metadata.properties' || { echo 'Environment metadata missing from the Serenity artifact for ${provider}' >&2; exit 1; }
                                grep -Fxq "execution.provider=${provider}" 'ci-evidence/${provider}/site/serenity/run-metadata.properties' || { echo 'Provider metadata missing from the Serenity artifact for ${provider}' >&2; exit 1; }
                                grep -Fxq "cucumber.filter.tags=\$EXPECTED_EFFECTIVE_TAG_FILTER" 'ci-evidence/${provider}/site/serenity/run-metadata.properties' || { echo 'Effective Cucumber filter missing from the Serenity artifact for ${provider}' >&2; exit 1; }
                                printf '%s' "\$EXPECTED_SCENARIO_NAMES" | tr ',' '\n' > expected-scenarios.txt
                                while IFS= read -r expected_scenario; do
                                    expected_scenario=\$(printf '%s' "\$expected_scenario" | sed 's/^[[:space:]]*//; s/[[:space:]]*$//')
                                    [ -z "\$expected_scenario" ] && continue
                                    grep -R -F -q --include='*.json' -- "\$expected_scenario" 'ci-evidence/${provider}/site/serenity' || { echo "Expected scenario missing from ${provider} Serenity report: \$expected_scenario" >&2; exit 1; }
                                done < expected-scenarios.txt
                                rm -f expected-scenarios.txt
                            """
                        }
                    }
                    if (params.EXECUTION_PROVIDER in ['grid', 'both']) runProvider('grid')
                    if (params.EXECUTION_PROVIDER in ['browserstack', 'both']) runProvider('browserstack')
                }
            }
        }
    }

    post {
        always {
            script {
                int reportScanStatus = sh(returnStatus: true, script: 'docker compose --profile ci run --rm --entrypoint java unit-test-runner -cp /app/target/test-classes saucedemo.com.support.ReportSecretScanner /app/ci-evidence /app/reports')
                if (reportScanStatus == 0) {
                    junit allowEmptyResults: true, testResults: 'ci-evidence/**/failsafe-reports/*.xml,ci-evidence/**/surefire-reports/*.xml'
                    archiveArtifacts allowEmptyArchive: true, artifacts: 'reports/**,ci-evidence/**'
                } else {
                    currentBuild.result = 'FAILURE'
                    echo 'Report secret scan failed; test artifacts were not archived.'
                }
            }
            sh 'docker compose down --remove-orphans || true'
        }
    }
}
