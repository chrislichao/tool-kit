package org.chrisli.utils.jdbc;

import org.chrisli.utils.Assert;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * [Jdbc工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class JdbcUtil {
    /**
     * [SQL注入的正则表达式]
     */
    private static final String SQL_INJECT_REGEX = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|union|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

    /**
     * [验证参数是否存在SQL注入嫌疑]
     */
    public static void sensitiveValidate(Object value) {
        Assert.notNull(value, "参数不允许为空!");
        Assert.notBlank(value.toString(), "参数不允许为空白字符串!");

        Pattern pattern = Pattern.compile(SQL_INJECT_REGEX);
        Matcher matcher = pattern.matcher(value.toString());
        StringBuffer sensitiveBuffer = new StringBuffer();
        while (matcher.find()) {
            sensitiveBuffer.append("{").append(matcher.group()).append("}");
        }

        Assert.isBlank(sensitiveBuffer.toString(), "参数中包含敏感字符串:" + sensitiveBuffer.toString() + ",请修改后重试!");
    }

    /**
     * [字符串转换为驼峰格式]
     */
    public static String toHump(String oldName) {
        Assert.notBlank(oldName, "参数不允许为空白字符串!");
        StringBuffer sbuffer = new StringBuffer();
        sbuffer.append(oldName.charAt(0));
        // 从第二个字符开始,检查字符如果是大写的,则在前面补下划线
        for (int i = 1; i < oldName.length(); i++) {
            if (Character.isUpperCase(oldName.charAt(i))) {
                sbuffer.append("_");
            }
            sbuffer.append(oldName.charAt(i));
        }
        // 返回时变大写
        return sbuffer.toString();
    }

    /**
     * [对象按JDBC字段值的格式要求转换 TODO 待完善]
     */
    public static String toJdbcValue(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return "'" + obj.toString() + "'";
        }
        if (obj instanceof Date) {
            return "sysdate";
        }
        return obj.toString();
    }

    /**
     * [用逗号分隔的字符串组用单引号环绕]
     */
    public static String wrapSingleQuotes(String strs) {
        if (strs == null) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        for (String str : strs.split(",")) {
            buffer.append(",'").append(str).append("'");
        }
        return buffer.toString().equals("") ? buffer.toString() : buffer.toString().substring(1);
    }
}
