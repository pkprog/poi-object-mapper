package io.github.pk.poi.writer;

import java.util.List;

public interface SpreadsheetAppendWriter {

    <T> void changeRow(String sheetName, int rownum, Class<T> beanClz, T rowObjects, List<String> inHeaders);

    /**
     * @return последний номер строки
     */
    <T> int appendRow(String sheetName, Class<T> beanClz, List<T> rowObjects, List<String> inHeaders);

}
