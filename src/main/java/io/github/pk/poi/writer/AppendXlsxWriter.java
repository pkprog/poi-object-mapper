package io.github.pk.poi.writer;

import io.github.millij.poi.ss.model.Column;
import io.github.millij.poi.util.Spreadsheet;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AppendXlsxWriter implements SpreadsheetAppendWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppendXlsxWriter.class);

    protected final String inputFilePath;
    protected XSSFWorkbook workbook;


    public AppendXlsxWriter(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public AppendXlsxWriter start() throws IOException {
        try (FileInputStream fis = new FileInputStream(this.inputFilePath)) {
            this.workbook = new XSSFWorkbook(fis);
        }

        return this;
    }

    public AppendXlsxWriter end() throws IOException {
        this.workbook.close();
        return this;
    }


    @Override
    public <T> void changeRow(final int rownum, Class<T> beanClz, T rowObject) {
        final String sheetName = Spreadsheet.getSheetName(beanClz);
        final List<String> headers = getColumnNames(beanClz);


        final Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            String errMsg = String.format("Лист не найден по имени: %s", sheetName);
            throw new PoiValidationException(errMsg);
        }


        try {
            Map<String, List<String>> rowsData = this.prepareSheetSingleRowData(headers, rowObject);

            Row row = sheet.createRow(rownum);

            int cellNo = 0;
            for (String key : rowsData.keySet()) {
                Cell cell = row.createCell(cellNo);
                String value = rowsData.get(key).get(0);
                cell.setCellValue(value);
                cellNo++;
            }

        } catch (Exception ex) {
            String errMsg = String.format("Ошибка замены данных в строке на листе: %s", ex.getMessage());
            LOGGER.error(errMsg, ex);
        }
    }


    @Override
    public <T> int appendRow(Class<T> beanClz, List<T> rowObjects) {
        final String sheetName = Spreadsheet.getSheetName(beanClz);
        final List<String> headers = getColumnNames(beanClz);


        final Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            String errMsg = String.format("Лист не найден по имени: %s", sheetName);
            throw new PoiValidationException(errMsg);
        }


        int lasRowNum = sheet.getLastRowNum();


        try {
            //Map<String, List<String>> rowsData = this.prepareSheetSingleRowData(headers, rowObject);

            // Data Rows
            final Map<String, List<String>> rowsData = this.prepareSheetRowsData(headers, rowObjects);
            for (int i = 0; i < rowObjects.size(); i++) {
                lasRowNum++;

                final Row row = sheet.createRow(lasRowNum);

                int cellNo = 0;
                for (String key : rowsData.keySet()) {
                    Cell cell = row.createCell(cellNo);
                    String value = rowsData.get(key).get(i);
                    cell.setCellValue(value);
                    cellNo++;
                }
            }

        } catch (Exception ex) {
            String errMsg = String.format("Ошибка дополнения данных на лист: %s", ex.getMessage());
            LOGGER.error(errMsg, ex);
        }

        return lasRowNum;
    }


    //@Override
    public void save() throws IOException {
        try (final OutputStream outputStrem = new FileOutputStream(this.inputFilePath)) {
            this.workbook.write(outputStrem);
        }
    }



    // Private Methods
    // ------------------------------------------------------------------------

    private <T> Map<String, List<String>> prepareSheetRowsData(List<String> headers, List<T> rowObjects) throws Exception {
        // Sheet data
        final Map<String, List<String>> sheetData = new LinkedHashMap<>();

        // Iterate over Objects
        for (final T rowObj : rowObjects) {
            final Map<String, String> row = Spreadsheet.asRowDataMap(rowObj, headers);
            for (final String header : headers) {
                final List<String> data = sheetData.getOrDefault(header, new ArrayList<>());
                final String value = row.getOrDefault(header, "");

                data.add(value);
                sheetData.put(header, data);
            }
        }

        return sheetData;
    }

    private <T> Map<String, List<String>> prepareSheetSingleRowData(List<String> headers, T rowObj) throws Exception {
        // Sheet data
        final Map<String, List<String>> sheetData = new LinkedHashMap<>();

        // Iterate over Objects
//        for (final T rowObj : rowObjects) {
            final Map<String, String> row = Spreadsheet.asRowDataMap(rowObj, headers);
            for (final String header : headers) {
                final List<String> data = sheetData.getOrDefault(header, new ArrayList<>());
                final String value = row.getOrDefault(header, "");

                data.add(value);
                sheetData.put(header, data);
            }
//        }

        return sheetData;
    }


    private List<String> getColumnNames(Class<?> beanType) {
        // Bean Property to Column Mapping
        final Map<String, Column> propToColumnMap = Spreadsheet.getPropertyToColumnDefMap(beanType);
        final List<Column> colums = new ArrayList<>(propToColumnMap.values());
        Collections.sort(colums);

        final List<String> columnNames = colums.stream().map(Column::getName).collect(Collectors.toList());
        return columnNames;
    }

}
