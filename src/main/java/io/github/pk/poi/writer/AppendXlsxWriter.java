package io.github.pk.poi.writer;

import io.github.millij.poi.ss.model.Column;
import io.github.millij.poi.util.Spreadsheet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppendXlsxWriter implements SpreadsheetAppendWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppendXlsxWriter.class);

    protected final XSSFWorkbook workbook;

    public AppendXlsxWriter(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }



    @Override
    public <T> void changeRow(String sheetName, int rownum, Class<T> beanClz, T rowObject, List<String> inHeaders) {
        int rowNum = rownum;

            final Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                String errMsg = String.format("Лист не найден по имени: %s", sheetName);
                throw new PoiValidationException(errMsg);
            }

            final List<String> headers = inHeaders;


        try {
            // Header
//            final Row headerRow = sheet.createRow(0);
//            for (int i = 0; i < headers.size(); i++) {
//                Cell cell = headerRow.createCell(i);
//                cell.setCellValue(headers.get(i));
//            }

            // Data Rows
            final Map<String, List<String>> rowsData = this.prepareSheetRowData(headers, rowObject);
           // for (int i = 0; i < rowObjects.size(); i++, ++rowNum) {
                final Row row = sheet.createRow(rowNum);

                int cellNo = 0;
                for (String key : rowsData.keySet()) {
                    Cell cell = row.createCell(cellNo);
                    String value = rowsData.get(key).get(0);
                    cell.setCellValue(value);
                    cellNo++;
                }
            //}

        } catch (Exception ex) {
            String errMsg = String.format("Error while preparing sheet with passed row objects : %s", ex.getMessage());
            LOGGER.error(errMsg, ex);
        }
    }


    @Override
    public <T> int appendRow(String sheetName, Class<T> beanClz, List<T> rowObjects, List<String> inHeaders) {
        return 0;
    }



    // Sheet :: Add

    //@Override
    public <T> void addSheet(final Class<T> beanType, final List<T> rowObjects, final String inSheetName,
                             final List<String> inHeaders) {
        // Sanity checks
        if (Objects.isNull(beanType)) {
            throw new IllegalArgumentException("AbstractSpreadsheetWriter :: Bean Type is NULL");
        }

        // Sheet config
        final String defaultSheetName = Spreadsheet.getSheetName(beanType);
        final List<String> defaultHeaders = this.getColumnNames(beanType);

        // output config
        final String sheetName = Objects.isNull(inSheetName) ? defaultSheetName : inSheetName;
        final List<String> headers = Objects.isNull(inHeaders) || inHeaders.isEmpty() ? defaultHeaders : inHeaders;

        try {
            final Sheet exSheet = workbook.getSheet(sheetName);
            if (Objects.nonNull(exSheet)) {
                String errMsg = String.format("A Sheet with the passed name already exists : %s", sheetName);
                throw new IllegalArgumentException(errMsg);
            }

            // Create sheet
            final Sheet sheet = Objects.isNull(sheetName) || sheetName.isBlank() //
                    ? workbook.createSheet() //
                    : workbook.createSheet(sheetName);
            LOGGER.debug("Added new Sheet[name] to the workbook : {}", sheet.getSheetName());

            // Header
            final Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            // Data Rows
            final Map<String, List<String>> rowsData = this.prepareSheetRowsData(headers, rowObjects);
            for (int i = 0, rowNum = 1; i < rowObjects.size(); i++, rowNum++) {
                final Row row = sheet.createRow(rowNum);

                int cellNo = 0;
                for (String key : rowsData.keySet()) {
                    Cell cell = row.createCell(cellNo);
                    String value = rowsData.get(key).get(i);
                    cell.setCellValue(value);
                    cellNo++;
                }
            }

        } catch (Exception ex) {
            String errMsg = String.format("Error while preparing sheet with passed row objects : %s", ex.getMessage());
            LOGGER.error(errMsg, ex);
        }
    }


    // Sheet :: Append to existing


    // Write

    //@Override
    public void write(final String filepath) throws IOException {
        try (final OutputStream outputStrem = new FileOutputStream(new File(filepath))) {
            workbook.write(outputStrem);
            workbook.close();
        } catch (Exception ex) {
            final String errMsg = String.format("Failed to write workbook data to file : %s", filepath);
            LOGGER.error(errMsg);
            throw ex;
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

    private <T> Map<String, List<String>> prepareSheetRowData(List<String> headers, T rowObj) throws Exception {
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
