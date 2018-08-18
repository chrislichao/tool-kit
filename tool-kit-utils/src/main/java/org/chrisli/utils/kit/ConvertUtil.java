package org.chrisli.utils.kit;

import org.chrisli.utils.date.DateUtil;
import org.chrisli.utils.exception.FrameworkException;
import org.chrisli.utils.json.JsonUtil;

import java.util.Date;

/**
 * [转换工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class ConvertUtil {

    /**
     * [将{value}值转换为{typeClass}类型的值]
     */
    public static Object convert(Class typeClass, Object value) {
        if (value == null || typeClass == value.getClass()) {
            return value;
        }
        if (typeClass == String.class) {
            return String.valueOf(value);
        }
        if (typeClass == Long.class || typeClass.getName().equals("long")) {
            return Long.valueOf(value.toString()).longValue();
        }
        if (typeClass == Integer.class || typeClass.getName().equals("int")) {
            return Integer.valueOf(value.toString()).intValue();
        }
        if (typeClass == Double.class || typeClass.getName().equals("double")) {
            return Double.valueOf(value.toString()).doubleValue();
        }
        return (Date) value;
    }

    /**
     * [将{value}值转换为字符串,日期则转换为当前秒数,若是特殊对象,则转换成Json字符串]
     */
    public static String convert(Object value) {
        if (value == null) {
            return null;
        }
        if (value.getClass() == String.class || value.getClass() == Long.class || value.getClass() == Integer.class || value.getClass() == Double.class || value.getClass().getName().equals("long") || value.getClass().getName().equals("int") || value.getClass().getName().equals("double")) {
            return String.valueOf(value);
        }
        if (value.getClass() == Date.class) {
            return DateUtil.dateToSeconds((Date) value).toString();
        }
        try {
            return JsonUtil.beanToJson(value);
        } catch (Exception e) {
            throw new FrameworkException(value + "转换为json字符串失败!");
        }
    }
}
