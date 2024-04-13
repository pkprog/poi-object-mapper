package io.github.pk.poi.writer;

import java.util.List;

public interface SpreadsheetAppendWriter {

    /**
     * Заменить строку. Полностью.
     * Имя листа и заголовки берутся из класса T
     * @param rownum Номер строки. Заголовок - это номер ноль
     * @param beanClz Класс сохраняемого объекта
     * @param rowObject Сохраняемый объект
     */
    <T> void changeRow(int rownum, Class<T> beanClz, T rowObject);

    /**
     * @return последний номер строки
     */
    <T> int appendRow(Class<T> beanClz, List<T> rowObjects);

}
