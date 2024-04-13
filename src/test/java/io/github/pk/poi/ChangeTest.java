package io.github.pk.poi;

import io.github.millij.bean.Employee;
import io.github.pk.poi.writer.AppendXlsxWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;


public class ChangeTest {
    private String _filepath_xls_test1;

    @Before
    public void setup() throws ParseException {
        // filepaths

        // xls
        _filepath_xls_test1 = "src/test/resources/sample-files/xls_sample_test1.xlsx";
    }



    @Test
    public void test1() throws IOException {
        // Employees
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("1", "foo", 12, "WOW-MAN1", 1.68));
//        employees.add(new Employee("2", "bar", null, "MALE", 1.68));
//        employees.add(new Employee("3", "foo bar", null, null, null));


        //List<String> headers = Arrays.asList("ID", "Age", "Name", "Address", "Gender");


        AppendXlsxWriter writer = new AppendXlsxWriter(_filepath_xls_test1);
        writer.start();

        writer.changeRow(1, Employee.class, employees.get(0));
        writer.save();


        writer.end();
    }


    @Test
    public void test2() throws IOException {
        // Employees
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("1", "foo", 12, "WOW-MAN0", 1.68));
//        employees.add(new Employee("2", "bar", null, "MALE", 1.68));
//        employees.add(new Employee("3", "foo bar", null, null, null));


        //List<String> headers = Arrays.asList("ID", "Age", "Name", "Address", "Gender");


        AppendXlsxWriter writer = new AppendXlsxWriter(_filepath_xls_test1);
        writer.start();

        writer.appendRow(Employee.class, employees);
        writer.save();


        writer.end();
    }


}
