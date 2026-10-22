package saucedemo.com.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import saucedemo.com.properties.ApplicationConfig;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class PathUtil {
    private static final Logger log = LoggerFactory.getLogger(PathUtil.class);

    private PathUtil() {
    }

    public static String getProjectRootDir() {
        return System.getProperty("user.dir");
    }

    public static String getAbsolutePath(String... relativePathSegments) {
        Path path = Paths.get(getProjectRootDir(), relativePathSegments);
        Path normalized = path.normalize();
        log.debug("Resolved framework path from {} segment(s)", relativePathSegments.length);
        return normalized.toString();
    }

    public static String getConfigPropertiesPath() {
        return getConfigFilePath("config.properties");
    }

    public static String getConfigFilePath(String fileName) {
        return getAbsolutePath("src", "test", "resources", "config", fileName);
    }

    public static String getTestDataExcelFilePath(String excelFileName) {
        return getAbsolutePath("src", "test", "resources", "data", "excelfiles", excelFileName);
    }

    public static String getTestDataJsonFilePath(String jsonFileName) {
        return getAbsolutePath("src", "test", "resources", "data", "jsonfiles", jsonFileName);
    }

    public static String getReportsDir() {
        return getAbsolutePath("accessibility-reports");
    }

    public static String getExcelFilePath(String s) {
        return getAbsolutePath("src", "test", "resources", "data", s);
    }

    public static String getDownloadsDir() {
        return getAbsolutePath(ApplicationConfig.required("framework.paths.download"));
    }

    public static String getUploadsDir() {
        return getAbsolutePath(ApplicationConfig.required("framework.paths.upload"));
    }

    public static String getReportingCsvDir() {
        return getAbsolutePath(ApplicationConfig.required("framework.paths.reporting.csv"));
    }

    public static String getLighthouseReportPath() {
        String configuredPath = ApplicationConfig.required("framework.paths.lighthouse.report");

        assert configuredPath != null;

        return getAbsolutePath(configuredPath.split("[/\\\\]"));
    }

    public static String getResourcesPath() {
        return getAbsolutePath("src", "test", "resources");
    }
}
