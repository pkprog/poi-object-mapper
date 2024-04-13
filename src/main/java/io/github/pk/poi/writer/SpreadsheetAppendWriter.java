package io.github.pk.poi.writer;

import java.util.List;

public interface SpreadsheetAppendWriter {

    /**
     * Заменить строку. Полностью
     * @param sheetName Имя листа
     * @param rownum Номер строки. Заголовок - это номер ноль
     * @param beanClz Класс сохраняемого объекта
     * @param rowObject Сохраняемый объект
     * @param inHeaders Сохраняемые заголовки (зачем?)
     */
    <T> void changeRow(String sheetName, int rownum, Class<T> beanClz, T rowObject);

    /**
     * @return последний номер строки
     */
    <T> int appendRow(String sheetName, Class<T> beanClz, List<T> rowObjects);

}
