package com.crmassessment.testdata;

import com.crmassessment.config.ConfigReader;
import com.crmassessment.utils.ExcelUtils;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.Map;

public class TestDataProvider {

    private TestDataProvider() {
        // Utility class - no instances
    }

    @DataProvider(name = "employeeData")
    public static Object[][] employeeData() {
        List<Map<String, String>> rows = ExcelUtils.readSheet(
                ConfigReader.getEmployeeTestDataFile(), "EmployeeTestData");

        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            data[i][0] = new EmployeeTestData(row.get("firstName"), row.get("lastName"));
        }
        return data;
    }
}
