package io.github.pk.poi;

import io.github.millij.bean.Employee;
import io.github.pk.poi.writer.AppendXlsxWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;


public class ChangeOneRowTest {
    private String _filepath_xls_test1;

    @Before
    public void setup() throws ParseException {
        // filepaths

        // xls
        _filepath_xls_test1 = "src/test/resources/sample-files/xls_sample_test1.xls";
    }



    @Test
    public void test1() {
        // Employees
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("1", "foo", 12, "WOW-MAN", 1.68));
//        employees.add(new Employee("2", "bar", null, "MALE", 1.68));
//        employees.add(new Employee("3", "foo bar", null, null, null));

        // Write
//        gew.addSheet(Employee.class, employees);
//        gew.write(filepath_output_file);


        List<String> headers = Arrays.asList("ID", "Age", "Name", "Address");


        try (FileInputStream fis = new FileInputStream(_filepath_xls_test1)) {
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            AppendXlsxWriter writer = new AppendXlsxWriter(wb);
            writer.changeRow("Sheet1", 2, Employee.class, employees.get(0), headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
