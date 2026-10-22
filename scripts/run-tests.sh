#!/bin/sh
set -eu

test_environment=$(printf '%s' "${TEST_ENVIRONMENT:-staging}" | tr '[:upper:]' '[:lower:]')
execution_provider=$(printf '%s' "${EXECUTION_PROVIDER:-grid}" | tr '[:upper:]' '[:lower:]')

case "$test_environment" in
  staging) base_url=${SAUCEDEMO_STAGING_URL:-https://www.saucedemo.com} ;;
  sit) base_url=${SAUCEDEMO_SIT_URL:-} ;;
  uat) base_url=${SAUCEDEMO_UAT_URL:-} ;;
  production) base_url=${SAUCEDEMO_PRODUCTION_URL:-} ;;
  *) echo "Unsupported TEST_ENVIRONMENT '$test_environment'. Use staging, sit, uat, or production." >&2; exit 2 ;;
esac

if [ -z "$base_url" ]; then
  variable_name="SAUCEDEMO_$(printf '%s' "$test_environment" | tr '[:lower:]' '[:upper:]')_URL"
  echo "$variable_name must be set for TEST_ENVIRONMENT=$test_environment." >&2
  exit 2
fi

requested_tags=${CUCUMBER_FILTER_TAGS:-}
if [ -z "$requested_tags" ]; then
  if [ "$test_environment" = production ]; then
    echo "Set CUCUMBER_FILTER_TAGS to @ProductionSafe (optionally AND another tag) for production runs." >&2
    exit 2
  fi
  requested_tags='@CartPrice'
fi

case "$execution_provider" in
  local)
    serenity_environment=default
    ;;
  grid)
    serenity_environment=grid
    ;;
  browserstack)
    serenity_environment=browserstack
    if [ -z "${BROWSERSTACK_USERNAME:-}" ] || [ -z "${BROWSERSTACK_ACCESS_KEY:-}" ]; then
      echo "BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY are required for BrowserStack execution." >&2
      exit 2
    fi
    ;;
  *) echo "Unsupported EXECUTION_PROVIDER '$execution_provider'. Use local, grid, or browserstack." >&2; exit 2 ;;
esac

mvn -B -DskipTests test-compile
effective_tags=$(java -cp target/test-classes saucedemo.com.support.CucumberTagFilter "$test_environment" "$requested_tags")
mkdir -p reports
{
  printf 'target.environment=%s\n' "$test_environment"
  printf 'execution.provider=%s\n' "$execution_provider"
  printf 'cucumber.filter.tags=%s\n' "$effective_tags"
} > reports/run-metadata.properties

set -- mvn -B clean verify \
  -Dtest.suite="${TEST_SUITE:-**/CucumberTestSuite.java}" \
  -Denvironment="$serenity_environment" \
  -Dtest.environment="$test_environment" \
  -Dcucumber.filter.tags="$effective_tags" \
  -Dwebdriver.base.url="$base_url" \
  -Dheadless.mode="${HEADLESS_MODE:-true}"

if [ "$execution_provider" = grid ]; then
  set -- "$@" -Dwebdriver.remote.url="${SELENIUM_GRID_URL:-http://selenium-hub:4444}"
fi

set +e
"$@"
maven_status=$?
set -e

mkdir -p target/site/serenity
{
  printf 'target.environment=%s\n' "$test_environment"
  printf 'execution.provider=%s\n' "$execution_provider"
  printf 'cucumber.filter.tags=%s\n' "$effective_tags"
} > target/site/serenity/run-metadata.properties

set --
for report_directory in target/site/serenity reports target/failsafe-reports target/surefire-reports; do
  if [ -d "$report_directory" ]; then set -- "$@" "$report_directory"; fi
done

if [ "$#" -gt 0 ]; then
  set +e
  java -cp target/test-classes saucedemo.com.support.ReportSecretScanner "$@"
  report_scan_status=$?
  set -e
  if [ "$report_scan_status" -eq 3 ]; then
    mkdir -p target
    : > target/report-secret-scan.failed
    exit 3
  elif [ "$report_scan_status" -ne 0 ]; then
    mkdir -p target
    : > target/report-secret-scan.failed
    echo "Could not complete the report credential scan." >&2
    exit 4
  fi
fi

exit "$maven_status"
