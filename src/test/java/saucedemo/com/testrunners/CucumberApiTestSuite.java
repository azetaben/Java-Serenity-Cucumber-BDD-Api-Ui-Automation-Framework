package saucedemo.com.testrunners;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/api_tests")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "saucedemo.com.stepdefinitions")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME,
        value = "io.cucumber.core.plugin.SerenityReporterParallel")
public class CucumberApiTestSuite {
}
