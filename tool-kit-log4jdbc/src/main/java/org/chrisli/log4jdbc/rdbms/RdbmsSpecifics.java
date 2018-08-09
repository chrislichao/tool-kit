package org.chrisli.log4jdbc.rdbms;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * [关系型数据库特性类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class RdbmsSpecifics {

    public RdbmsSpecifics() {
    }

    protected static final String DATE_TIME_FORMAT_A = "yyyy-MM-dd HH:mm:ss.SSS";

    protected static final String DATE_TIME_FORMAT_B = "yyyy-MM-dd HH:mm:ss";

    protected static final String DATE_FORMAT = "yyyy-MM-dd";

    protected static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * [格式化参数对象]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public String formatParameterObject(Object object) {
        if (object == null) {
            return "NULL";
        }
        if (object instanceof String) {
            return "'" + escapeString((String) object) + "'";
        }
        if (object instanceof Date) {
            return "'" + new SimpleDateFormat(DATE_TIME_FORMAT_A).format(object) + "'";
        }
        if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? "1" : "0";
        }
        return object.toString();
    }

    /**
     * [字符串中包含单引号,则换成两个单引号]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    String escapeString(String in) {
        StringBuilder out = new StringBuilder();
        for (int i = 0, j = in.length(); i < j; i++) {
            char c = in.charAt(i);
            if (c == '\'') {
                out.append(c);
            }
            out.append(c);
        }
        return out.toString();
    }

}
