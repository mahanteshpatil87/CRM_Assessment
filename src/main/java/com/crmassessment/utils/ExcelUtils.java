package com.crmassessment.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads a classpath .xlsx resource into a list of header-keyed row maps.
 * Used for data-driven tests (e.g. TestDataProvider) so test data lives in
 * an actual spreadsheet rather than hardcoded in Java, per the assignment's
 * requirement to use Excel for test data.
 */
public class ExcelUtils {

    private ExcelUtils() {
    }

    public static List<Map<String, String>> readSheet(String classpathResourcePath, String sheetName) {
        try (InputStream input = ExcelUtils.class.getClassLoader().getResourceAsStream(classpathResourcePath)) {
            if (input == null) {
                throw new RuntimeException("Excel resource not found on classpath: " + classpathResourcePath);
            }
            try (Workbook workbook = new XSSFWorkbook(input)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new RuntimeException("Sheet '" + sheetName + "' not found in " + classpathResourcePath);
                }
                return readRows(sheet);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Excel resource: " + classpathResourcePath, e);
        }
    }

    private static List<Map<String, String>> readRows(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        List<String> headers = new ArrayList<>();
        for (Cell cell : headerRow) {
            headers.add(cell.getStringCellValue());
        }

        List<Map<String, String>> rows = new ArrayList<>();
        for (int r = 1; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            Map<String, String> rowData = new LinkedHashMap<>();
            for (int c = 0; c < headers.size(); c++) {
                rowData.put(headers.get(c), cellValueAsString(row.getCell(c)));
            }
            rows.add(rowData);
        }
        return rows;
    }

    private static String cellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
