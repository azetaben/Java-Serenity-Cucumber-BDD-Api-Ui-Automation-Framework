# SauceDemo Serenity BDD Automation Framework

Java UI automation framework for the public [SauceDemo](https://www.saucedemo.com/) application. It uses Selenium WebDriver, Serenity BDD, Cucumber, and the JUnit Platform Suite API (JUnit 6) to run behavior-driven UI scenarios and generate execution reports.

## Requirements

- Java 17 or later
- Apache Maven
- Google Chrome for local runs, or Docker with Docker Compose for Selenium Grid or BrowserStack runs
- Network access to SauceDemo and, on first build, Maven artifact repositories

The Maven dependency versions are maintained in `pom.xml` (Serenity 5.3.7, Cucumber 7.34.2, JUnit 6.0.3, Selenium 4.51.0).

## Run the Cucumber suite

From the project root, run:

```powershell
mvn clean verify
```

The `maven-failsafe-plugin` runs the configured suite in the `integration-test` and `verify` phases. By default, `test.suite` points to `CucumberTestSuite.java`.

The Maven run script defaults to the `@CartPrice` subset and passes its tag expression to Cucumber. IntelliJ feature and suite runs have no global tag filter, so an individually selected feature is not silently excluded. Override the Maven subset per run without editing the runner:

```powershell
mvn clean verify "-Dcucumber.filter.tags=(@Smoke or @Regression) and not @manual"
```

The JUnit Platform runner owns the glue and feature package selection; `cucumber.properties` registers the Serenity plugin. The Maven run script owns the intentional `@CartPrice` default subset.

For headless Chrome:

```powershell
mvn clean verify -Dheadless.mode=true
```

For a Selenium Grid running on the local machine:

```powershell
mvn clean verify -Denvironment=grid -Dtest.environment=staging -Dwebdriver.remote.url=http://localhost:4444 -Dheadless.mode=true
```

### Target environments and browser providers

`TEST_ENVIRONMENT` selects the application deployment. `EXECUTION_PROVIDER` selects the browser location. They are separate settings so a SIT or UAT URL can be exercised on either Grid or BrowserStack.

| Target | URL variable | Default |
| --- | --- | --- |
| `staging` | `SAUCEDEMO_STAGING_URL` | `https://www.saucedemo.com` |
| `sit` | `SAUCEDEMO_SIT_URL` | Required |
| `uat` | `SAUCEDEMO_UAT_URL` | Required |
| `production` | `SAUCEDEMO_PRODUCTION_URL` | Required |

The repository only knows the public SauceDemo URL. Supply the actual SIT, UAT, and production URLs through the environment in your local shell, CI job, or secret/configuration store. The runner fails early when the chosen target URL is missing. Do not put credentials in these URL variables. `SAUCEDEMO_TEST_PASSWORD` can override the demo password for a deployment-specific test account; provide it through a CI secret store, not a committed `.env` file.

Copy `.env.example` to `.env` as a local Compose starting point. Keep `.env` out of source control and never add real BrowserStack credentials to the example file.

The Docker entrypoint is `scripts/run-tests.sh`. It validates settings and maps the provider to Serenity's `default`, `grid`, or `browserstack` WebDriver profile. Maven receives the selected `webdriver.base.url`; Grid also receives its remote URL. BrowserStack requires `BROWSERSTACK_USERNAME` and `BROWSERSTACK_ACCESS_KEY` and uses the existing remote capabilities in `src/test/resources/serenity.conf`.

Local browser (requires Chrome):

```powershell
$env:TEST_ENVIRONMENT = 'staging'
$env:EXECUTION_PROVIDER = 'local'
mvn clean verify -Denvironment=default -Dtest.environment=staging -Dheadless.mode=true -Dwebdriver.base.url=https://www.saucedemo.com
```

Docker Compose with Selenium Grid:

```powershell
$env:TEST_ENVIRONMENT = 'staging'
docker compose --profile grid up --build --abort-on-container-exit --exit-code-from grid-runner grid-runner
docker compose down --remove-orphans
```

Override the default cart-price selection in Compose by setting `CUCUMBER_FILTER_TAGS`, for example `@Smoke` or `@Regression`. `CucumberTagFilter` passes the same effective expression to Maven/Cucumber and excludes `@manual`; the report records that expression. For Jenkins, also set `EXPECTED_SCENARIO_NAMES` to one or more comma-separated scenario names that should appear for the selected filter. Production expressions must be `@ProductionSafe` or a conjunction beginning with `@ProductionSafe and`; OR expressions are rejected, and the Cucumber hook independently checks each scenario against the reviewed allowlist. Jenkins also requires `CONFIRM_PRODUCTION_TESTS=true`.

Docker Compose with BrowserStack (set credentials in the shell or CI secret store first):

```powershell
$env:TEST_ENVIRONMENT = 'staging'
$env:BROWSERSTACK_USERNAME = 'your-user'
$env:BROWSERSTACK_ACCESS_KEY = 'your-key'
docker compose --profile browserstack up --build --abort-on-container-exit --exit-code-from browserstack-runner browserstack-runner
docker compose down --remove-orphans
```

To target SIT, UAT, or production, set the matching URL variable before running Compose. For example, `TEST_ENVIRONMENT=sit` requires `SAUCEDEMO_SIT_URL`.

`Jenkinsfile` runs a Dockerized Surefire stage before UI execution and defaults to both providers with an `@Smoke` override and expected login scenarios. Change `CUCUMBER_FILTER_TAGS` and `EXPECTED_SCENARIO_NAMES` together to select another CI subset; `@CartPrice` remains the local/Compose default and can also be selected in Jenkins. Each provider uses isolated evidence/report directories; Jenkins checks Serenity aggregate HTML, scenario JSON, Failsafe XML, effective filter metadata, and every expected scenario name before archiving. `CucumberTagFilter` builds the expression passed by the entrypoint to Cucumber, and the Serenity hook records it in each scenario report. Configure SIT/UAT/production URLs as Jenkins job or folder environment values. Create a Jenkins username/password credential with ID `browserstack-credentials`; the pipeline injects it only for BrowserStack runs. The agent needs the `java17-docker` label and Docker Compose v2. Actual provider execution requires that Jenkins agent and BrowserStack credentials.

The `@ProductionSafe` tag currently marks product-title, price/image, and product-open inventory checks, plus standard-user catalog sorting; the price/image outline expands to one case per product. `ProductionSafeScenarios` is the explicit code allowlist and the production hook requires both the tag and a feature/name allowlist entry. The Surefire parity test compares all tagged Gherkin scenarios with the allowlist. The PR template and [review policy](docs/production-safe-coverage.md) require a recorded risk review and independent maintainer approval; configure branch protection to require that approval. Keep checkout, cart mutation, logout, and account-behavior scenarios out unless separately reviewed as safe. Override the default account with `SAUCEDEMO_TEST_USERNAME` and `SAUCEDEMO_TEST_PASSWORD` through the CI secret store before enabling production execution.

## Reports

Serenity is the single report system. The JUnit Platform suite registers `SerenityReporterParallel`, and Maven runs Serenity's `aggregate` goal after integration tests. The Cucumber hook records the target environment, execution provider, and effective tag expression in each scenario report. Serenity action labels omit credential values; feature data uses password modes (`valid`, `invalid`, `empty`) resolved inside the step definition. `ReportSecretScanner` checks generated text artifacts for configured and public demo passwords; Jenkins uses the same scanner before archiving and fails closed if the scan cannot complete.

- **Serenity HTML report:** `reports/AUTOMATION_TEST_REPORT_<timestamp>/index.html` (Jenkins separates it under `reports/<provider>/`)
- **Serenity execution data:** `target/site/serenity/` (Jenkins preserves copies under `ci-evidence/<provider>/site/serenity/`)
- **Failsafe XML results:** `target/failsafe-reports/` (Jenkins preserves them under `ci-evidence/<provider>/failsafe-reports/`)

The Serenity aggregate report is generated by `mvn clean verify`. Running the suite directly from an IDE may create Serenity execution data without running Maven's aggregate goal. Focused JUnit tests cover configuration overrides, password-mode resolution and CLI tag propagation, report-secret scanning, JSON/CSV/XLSX/YAML fixture parsing, and production allowlist parity; run them without browser tests using `mvn test`.

Framework logging uses SLF4J with the test Logback configuration. Framework packages under `saucedemo.com` log at DEBUG so action, Cucumber step-definition, page-operation, assertion, configuration, fixture, PDF, report-scanning, and scenario-lifecycle events are visible. Action and framework-boundary messages include the Java source line containing the framework logging call (for example, `Page: 30` in `CommonStepDefinitions`), or `Page: n/a` when source line metadata is unavailable. Logs go to the console and to the rolling `target/logs/framework.log`; third-party libraries remain at WARN. `FrameworkLogger` records the layer, class, and method without logging method parameters; credential values and fixture contents are not logged.

## Framework layout

```text
src/test/java/saucedemo/com/
  actions/          Reusable UI workflows and domain actions
  assertions/       Domain-specific assertions
  contentdesign/    Page names, titles, URLs, and expected content
  enums/            Typed user, product, and checkout values
  helper/           Fixture loading and test-data helpers
  models/           Data transfer and domain models
  pages/            Serenity page objects and page components
  properties/       Application and SauceDemo configuration
  stepdefinitions/  Cucumber glue and scenario hooks
  support/          Report, PDF, exception, and UI-check helpers
  testrunners/      JUnit Platform Cucumber suite
  utilities/        Text, path, and generated-data utilities

src/test/resources/
  features/         Cucumber feature specifications
  testdata/         CSV, XLSX, JSON, and YAML fixtures
  cucumber.properties
  serenity.conf

serenity.properties  Serenity project and report settings
pom.xml              Dependency, build, test, and report configuration
```

## Test data

Fixtures are stored under `src/test/resources/testdata/` and loaded from the classpath by `TestData.load(...)`. Supported formats include CSV, Excel (`.xlsx`), JSON, and YAML. Records are selected by `caseId` and mapped to the requested fixture type. Keep postal codes as strings so leading zeroes are preserved. See [testdata/README.md](src/test/resources/testdata/README.md) for the available fixtures and case IDs.

## Configuration

- `serenity.properties` configures the base Serenity output directory and browser defaults.
- `src/test/resources/cucumber.properties` configures Serenity's Cucumber reporter. The Maven run script defaults to `@CartPrice`; override it with `-Dcucumber.filter.tags=...`.
- Override the WebDriver mode, remote URL, and headless setting with Maven system properties as shown above.
- `TEST_ENVIRONMENT` accepts `staging`, `sit`, `uat`, or `production`; non-staging target URLs must be supplied through the corresponding `SAUCEDEMO_*_URL` variable.
- `EXECUTION_PROVIDER` accepts `local`, `grid`, or `browserstack` in the container entrypoint. Grid and BrowserStack are the CI Compose profiles.
- BrowserStack credentials must be stored in a CI secret store. Jenkins expects the credential ID `browserstack-credentials`.

