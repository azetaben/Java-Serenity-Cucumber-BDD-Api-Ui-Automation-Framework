# SauceDemo Serenity BDD API and UI Automation Framework

Java 17 automation for SauceDemo browser workflows and the Postcodes.io API, using Serenity BDD, Selenium, Cucumber, and
JUnit 5.

## Requirements

- JDK 17 and Maven 3.9.x on PATH.
- Chrome for local UI execution; Selenium manages driver downloads.
- Network access to the applications and Maven repositories.
- Docker with Compose v2 for container runs, and BrowserStack credentials for that provider.

Versions are maintained in `pom.xml`: Serenity 4.2.9, Cucumber 7.20.1, JUnit 5.11.4, Selenium 4.26.0, and
Surefire/Failsafe 3.5.4.

## Getting started

```powershell
git clone https://github.com/azetaben/Java-Serenity-Cucumber-BDD-Api-Ui-Automation-Framework.git
cd Java-Serenity-Cucumber-BDD-Api-Ui-Automation-Framework
java -version
mvn -version
mvn test
```

Open `pom.xml` as a Maven project in your IDE and select JDK 17. `mvn test` runs the JUnit unit tests without starting
the Cucumber suites.

## Running scenarios

Run unit tests, both Cucumber suites, and report aggregation:

```powershell
mvn clean verify "-Dheadless.mode=true"
```

Surefire runs unit tests during `test`. Failsafe runs `CucumberUITestSuite` and `CucumberApiTestSuite` during
`integration-test` and checks their results during `verify`. The default pattern is `**/Cucumber*TestSuite.java`. A
pattern matching no suites fails the build.

Select one suite:

```powershell
# UI only
mvn clean verify "-Dtest.suite=**/CucumberUITestSuite.java" "-Dheadless.mode=true"

# API only (no browser required)
mvn clean verify "-Dtest.suite=**/CucumberApiTestSuite.java"
```

Select tags with Cucumber expressions; quote the full argument in PowerShell:

```powershell
mvn clean verify "-Dcucumber.filter.tags=(@Smoke or @Regression) and not @manual" "-Dheadless.mode=true"
```

Direct Maven runs default to `not @manual`. An explicit expression replaces that default; include `and not @manual` when
appropriate. The separate `tags` property filters Serenity report aggregation, not Cucumber execution.

Suite classes declare feature paths and glue. `cucumber.properties` registers the Serenity reporter.
`junit-platform.properties` enables four Cucumber workers. Disable parallel execution when debugging or using a single
Grid slot:

```powershell
mvn clean verify "-Dcucumber.execution.parallel.enabled=false" "-Dheadless.mode=true"
```

Run a selected feature or suite from its IDE run action. IDE runs do not apply the POM's tag default or execute Maven
report aggregation.

## Configuration

`serenity.properties` owns reporting and browser lifecycle settings. `src/test/resources/serenity.conf` owns browser
profiles, application content, endpoints, and timeouts. Capability values are explicit in each profile because Serenity
4.2.9 selects whole capability objects. Selenium is aligned with the version declared by Serenity to avoid
capability-loader linkage errors. Pass `-Dheadless.mode=true` explicitly for headless runs. Use `-Denvironment=chrome`,
`firefox`, `edge`, or `safari` for a local browser, or an existing remote provider profile such as `grid` or
`browserstack`. The separate `test.environment` property labels the application deployment and activates production
checks when set to `production`.

### Local browser environments

Select a browser with Serenity's `environment` property:

```powershell
mvn clean verify "-Dtest.suite=**/CucumberUITestSuite.java" "-Denvironment=chrome" "-Dheadless.mode=true"
mvn clean verify "-Dtest.suite=**/CucumberUITestSuite.java" "-Denvironment=firefox" "-Dheadless.mode=true"
mvn clean verify "-Dtest.suite=**/CucumberUITestSuite.java" "-Denvironment=edge" "-Dheadless.mode=true"
```

The `chrome`, `firefox`, and `edge` profiles enable driver downloads and define vendor options and preferences. Each
browser declares W3C timeouts in milliseconds: implicit `0`, page load `60000`, and script `30000`, with a Serenity
explicit wait of `10000`. Local profiles default to a visible browser; override with `-Dheadless.mode=true` for Chrome,
Firefox, or Edge. An unspecified environment uses Chrome with the same options and preferences as the Chrome profile.
Normal Chrome profiles (including Grid and cloud providers) use Incognito mode with password saving, automatic sign-in,
and password leak detection disabled to suppress Google Password Manager prompts after login. Restart the test browser
to apply these settings; existing browser sessions retain their startup preferences.

Safari runs on macOS with its bundled driver. Enable automation once with `safaridriver --enable`, then run:

```shell
mvn clean verify -Dtest.suite='**/CucumberUITestSuite.java' -Denvironment=safari -Dheadless.mode=false -Dcucumber.execution.parallel.enabled=false
```

Safari supports neither headless execution nor arbitrary browser args/prefs; its profile uses the supported automatic
inspection/profiling capabilities. Firefox saves PDFs to the user's Downloads directory; Chrome and Edge support the
framework's runtime CDP download-directory setup. The existing PDF download workflow requires adaptation before using it
with Firefox or Safari.

Browser profiles and the existing `grid`, `remote`, `saucelabs`, and `browserstack` profiles are alternative values of
`environment`. Choose one per run. Container entrypoint provider mapping continues to select its existing provider
profile.

Configuration
references: [Serenity WebDriver configuration](https://serenity-bdd.github.io/docs/guide/driver_config), [Selenium Safari configuration](https://www.selenium.dev/documentation/webdriver/browsers/safari/).

For direct Maven execution, supply the deployment URL explicitly:

```powershell
mvn clean verify "-Dtest.suite=**/CucumberUITestSuite.java" "-Denvironment=grid" "-Dtest.environment=staging" "-Dwebdriver.remote.url=http://localhost:4444" "-Dwebdriver.base.url=https://www.saucedemo.com" "-Dheadless.mode=true"
```

The container entrypoint, `scripts/run-tests.sh`, maps the following variables to Maven properties:

| Variable                   | Behavior                                               |
|----------------------------|--------------------------------------------------------|
| `TEST_ENVIRONMENT`         | `staging` (default), `sit`, `uat`, or `production`     |
| `EXECUTION_PROVIDER`       | `local`, `grid` (default), or `browserstack`           |
| `SAUCEDEMO_STAGING_URL`    | Defaults to `https://www.saucedemo.com`                |
| `SAUCEDEMO_SIT_URL`        | Required for SIT                                       |
| `SAUCEDEMO_UAT_URL`        | Required for UAT                                       |
| `SAUCEDEMO_PRODUCTION_URL` | Required for production                                |
| `TEST_SUITE`               | Failsafe suite pattern; set explicitly as shown below  |
| `CUCUMBER_FILTER_TAGS`     | Defaults to `@CartPrice` in the entrypoint and Compose |
| `HEADLESS_MODE`            | Defaults to `true` in the entrypoint                   |
| `SELENIUM_GRID_URL`        | Grid endpoint; Compose sets `http://selenium-hub:4444` |

These mappings apply to the entrypoint, not plain Maven execution. Override application credentials with
`SAUCEDEMO_TEST_USERNAME` and `SAUCEDEMO_TEST_PASSWORD`. Keep credentials in shell environment variables or CI secret
stores.

### Waiting for Google Password Manager after login

The opt-in `@PasswordManagerPrompt` scenario allows the native Chrome prompt and waits up to 20 seconds after a
successful UI login. Run on an interactive Windows desktop with English Chrome and Windows PowerShell:

```powershell
mvn clean verify "-Dtest.suite=**/CucumberUITestSuite.java" "-Denvironment=chrome-password-manager" "-Dheadless.mode=false" "-Dcucumber.filter.tags=@PasswordManagerPrompt" "-Dcucumber.execution.parallel.enabled=false"
```

The `chrome-password-manager` profile enables password saving/leak detection and omits Incognito and suppression flags.
Normal Chrome profiles continue suppressing prompts. The desktop scenario skips unless its observation profile is
enabled; it fails if the prompt does not appear before the deadline. Chrome decides whether to display a prompt, so
browser policy, version, and account state can affect the result.

The wait uses Windows UI Automation against the local Chrome process reported by ChromeDriver. It excludes website
document content and recognizes native save/update/compromised-password prompt headings without logging their contents.
It does not support headless mode, Grid/cloud sessions, or other operating systems, and leaves the detected prompt open
in the observation scenario. The shared `I should be redirected to "inventory" page` step separately pauses for five
seconds, then dismisses a recognized native prompt if present. That dismissal is supported on local Windows Chrome and
is a no-op for other browser/provider combinations. This is a native UI wait, not a Selenium alert or page-element wait.
Restart Chrome when switching profiles.

## Docker Compose

Copy `.env.example` to `.env` for local settings. Replace its legacy `TEST_SUITE=**/CucumberTestSuite.java` value with
`**/CucumberUITestSuite.java` or `**/Cucumber*TestSuite.java`. Compose and the entrypoint still carry that legacy
default, so set `TEST_SUITE` explicitly.

```powershell
$env:TEST_ENVIRONMENT = 'staging'
$env:TEST_SUITE = '**/CucumberUITestSuite.java'
docker compose --profile grid up --build --abort-on-container-exit --exit-code-from grid-runner grid-runner
docker compose down --remove-orphans
```

For BrowserStack, first supply `BROWSERSTACK_USERNAME` and `BROWSERSTACK_ACCESS_KEY` through your shell or secret store:

```powershell
$env:TEST_SUITE = '**/CucumberUITestSuite.java'
docker compose --profile browserstack up --build --abort-on-container-exit --exit-code-from browserstack-runner browserstack-runner
docker compose down --remove-orphans
```

Set `CUCUMBER_FILTER_TAGS` to override the container subset. The entrypoint uses `CucumberTagFilter` to exclude
`@manual`, records run metadata, and scans generated reports for credentials.

## Jenkins and production

`Jenkinsfile` runs unit tests before provider execution and verifies report evidence and expected scenario names.
Defaults select Grid and BrowserStack with `@Smoke`. Configure `CUCUMBER_FILTER_TAGS` and `EXPECTED_SCENARIO_NAMES`
together. The agent needs the `java17-docker` label, Docker Compose v2, and a username/password credential named
`browserstack-credentials`. Supply the corrected `TEST_SUITE` value in the Jenkins environment too.

Production container runs require an explicit `@ProductionSafe` expression, optionally followed by `and` conditions; OR
expressions are rejected. Jenkins additionally requires `CONFIRM_PRODUCTION_TESTS=true`. The hook independently enforces
the allowlist in `ProductionSafeScenarios`; its unit test checks parity with tagged Gherkin scenarios. Review the
allowlist, deployment URL, and test account before enabling production execution.

## Reports and logging

Serenity aggregates results during `post-integration-test`, before Failsafe checks failures during `verify`.

| Artifact                 | Location                                                    |
|--------------------------|-------------------------------------------------------------|
| Serenity HTML            | `reports/AUTOMATION_TEST_REPORT_<UTC timestamp>/index.html` |
| Serenity scenario data   | `target/site/serenity/`                                     |
| Unit-test results        | `target/surefire-reports/`                                  |
| Integration-test results | `target/failsafe-reports/`                                  |
| Framework log            | `target/logs/framework.log`                                 |

Jenkins separates reports under `reports/<provider>/` and evidence under `ci-evidence/<provider>/`. Reports are outside
`target`, so `mvn clean` preserves earlier HTML reports.

After an IDE run, generate HTML with `mvn serenity:aggregate`; standalone aggregation uses Serenity's configured output
directory. Logging uses SLF4J and `logback-test.xml`. `FrameworkLogger` records execution context without method
parameters. `ReportSecretScanner` checks text artifacts for configured and public demo passwords.

## Project layout

```text
src/test/java/saucedemo/com/
  actions/          Reusable workflows and domain actions
  api/              HTTP clients and API helpers
  assertions/       Domain assertions
  constants/        Endpoint and environment constants
  contentdesign/    Expected page content, names, titles, and URLs
  enums/            Typed users, products, and checkout values
  helper/           Fixture loading and test-data helpers
  models/           Domain models
  pages/            Serenity page objects and components
  properties/       Configuration access
  stepdefinitions/  Cucumber glue and hooks
  support/          Logging, reports, PDF helpers, and safety checks
  testrunners/      Separate UI and API JUnit Platform suites
  utilities/        Text, path, generated-data, and session helpers
src/test/resources/
  features/         UI and API Gherkin specifications
  testdata/         CSV, XLSX, JSON, and YAML fixtures
  cucumber.properties
  junit-platform.properties
  serenity.conf
scripts/run-tests.sh Container entrypoint
docker-compose.yaml Container services
Jenkinsfile         CI execution and evidence checks
serenity.properties Serenity project and browser settings
pom.xml             Dependencies, tests, and report aggregation
```

## Test data

Fixtures are loaded from the classpath with `TestData.load(...)`, selected by `caseId`, and mapped to the requested
type. Keep postal codes as strings to preserve leading zeroes.
See [the fixture guide](src/test/resources/testdata/README.md).

## Troubleshooting

- **No integration tests found:** check `test.suite` or container `TEST_SUITE` against the two suite names above.
- **No scenarios selected:** check tag spelling and expressions against feature files; containers default to
  `@CartPrice`.
- **Browser startup fails:** check Chrome availability, Grid connectivity, or provider credentials; try disabling
  parallel execution.
- **Missing HTML after an IDE run:** run `mvn serenity:aggregate`.
- **Dependencies fail to resolve:** check repository access and Maven settings, then reload the Maven project in your
  IDE.
