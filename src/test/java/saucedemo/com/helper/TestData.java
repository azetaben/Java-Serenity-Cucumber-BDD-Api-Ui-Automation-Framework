package saucedemo.com.helper;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.poi.ss.usermodel.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Classpath fixture reader. Every file contains records uniquely identified by caseId.
 */
public final class TestData {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestData.class);
    private static final ObjectMapper JSON = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);

    private TestData() {
    }

    public static <T> T load(String file, String caseId, Class<T> type) {
        if (caseId == null || caseId.isBlank()) {
            throw new IllegalArgumentException("Test data caseId must not be blank");
        }
        log.info("Loading fixture '{}' for case '{}'", file, caseId);
        List<Map<String, Object>> rows = rows(file);
        Set<String> ids = new HashSet<>();
        Map<String, Object> selected = null;
        for (Map<String, Object> row : rows) {
            if (row == null) throw new IllegalArgumentException("Null case in helper/" + file);
            Object rawId = row.get("caseId");
            if (!(rawId instanceof String id) || id.isBlank() || !ids.add(id)) {
                throw new IllegalArgumentException("Missing or duplicate caseId in helper/" + file);
            }
            if (id.equals(caseId)) selected = row;
        }
        if (selected == null) {
            log.warn("Fixture '{}' does not contain requested case '{}'", file, caseId);
            throw new IllegalArgumentException("No case '" + caseId + "' in helper/" + file);
        }
        try {
            log.debug("Fixture '{}' case '{}' passed record selection; mapping to {}", file, caseId, type.getSimpleName());
            return JSON.convertValue(selected, type);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid schema for case '" + caseId + "' in helper/" + file, exception);
        }
    }

    private static List<Map<String, Object>> rows(String file) {
        if (file == null || !file.matches("[A-Za-z0-9_-]+\\.(csv|xlsx|json|yml|yaml)")) {
            throw new IllegalArgumentException("Use a CSV, XLSX, JSON or YAML filename in the helper directory");
        }
        String resource = "testdata/" + file;
        try (InputStream input = TestData.class.getClassLoader().getResourceAsStream(resource)) {
            if (input == null) throw new IllegalArgumentException("Missing classpath resource: " + resource);
            String extension = file.substring(file.lastIndexOf('.') + 1);
            return switch (extension) {
                case "csv" -> csv(input);
                case "xlsx" -> excel(input);
                case "json" -> JSON.readValue(input, new TypeReference<List<Map<String, Object>>>() {
                });
                case "yml", "yaml" -> yaml(input);
                default -> throw new IllegalArgumentException("Unsupported test data format: " + extension);
            };
        } catch (IOException exception) {
            throw new IllegalArgumentException("Cannot read " + resource, exception);
        }
    }

    static List<Map<String, Object>> csv(InputStream input) throws IOException {
        var format = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
        try (var parser = format.parse(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            validateHeaders(parser.getHeaderNames());
            List<Map<String, Object>> rows = new ArrayList<>();
            for (var record : parser) {
                if (!record.isConsistent()) throw new IllegalArgumentException("CSV row does not match its headers");
                rows.add(new LinkedHashMap<>(record.toMap()));
            }
            return rows;
        }
    }

    private static List<Map<String, Object>> excel(InputStream input) throws IOException {
        try (var workbook = WorkbookFactory.create(input)) {
            if (workbook.getNumberOfSheets() == 0) throw new IllegalArgumentException("Workbook has no sheets");
            var sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null) throw new IllegalArgumentException("Workbook requires headers in the first row");
            var formatter = new DataFormatter(Locale.ROOT);
            List<String> headers = new ArrayList<>();
            for (int column = 0; column < header.getLastCellNum(); column++) {
                headers.add(cellText(header.getCell(column), formatter));
            }
            validateHeaders(headers);
            List<Map<String, Object>> rows = new ArrayList<>();
            for (int index = 1; index <= sheet.getLastRowNum(); index++) {
                Row row = sheet.getRow(index);
                if (row == null) continue;
                Map<String, Object> values = new LinkedHashMap<>();
                for (int column = 0; column < headers.size(); column++) {
                    values.put(headers.get(column), cellText(row.getCell(column), formatter));
                }
                if (values.values().stream().anyMatch(value -> !value.toString().isEmpty())) rows.add(values);
            }
            return rows;
        }
    }

    private static String cellText(Cell cell, DataFormatter formatter) {
        if (cell != null && cell.getCellType() == CellType.FORMULA) {
            throw new IllegalArgumentException("Fixture cells must contain values, not formulas");
        }
        return cell == null ? "" : formatter.formatCellValue(cell);
    }

    static List<Map<String, Object>> yaml(InputStream input) {
        Object document = new Yaml(new SafeConstructor()).load(input);
        if (!(document instanceof List<?> records)) {
            throw new IllegalArgumentException("YAML test data must contain a list of cases");
        }
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object record : records) {
            if (!(record instanceof Map<?, ?> values))
                throw new IllegalArgumentException("YAML case must be a mapping");
            Map<String, Object> row = new LinkedHashMap<>();
            values.forEach((key, value) -> {
                if (!(key instanceof String name))
                    throw new IllegalArgumentException("YAML field names must be strings");
                row.put(name, value);
            });
            rows.add(row);
        }
        return rows;
    }

    private static void validateHeaders(List<String> headers) {
        if (!headers.contains("caseId") || headers.stream().anyMatch(String::isBlank)
                || new HashSet<>(headers).size() != headers.size()) {
            throw new IllegalArgumentException("Fixture headers require caseId and unique, non-empty names");
        }
    }
}
