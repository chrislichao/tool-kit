package org.chrisli.log4jdbc.utils;

/**
 * [字符串格式化工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class StringFormatUtil {
    /**
     * [右对齐]
     */
    public static String rightJustify(int fieldSize, String field) {
        if (field == null) {
            field = "";
        }
        StringBuffer output = new StringBuffer();
        for (int i = 0, j = fieldSize - field.length(); i < j; i++) {
            output.append(' ');
        }
        output.append(field);
        return output.toString();
    }
}