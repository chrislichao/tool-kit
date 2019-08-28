package org.chrisli.log4jdbc.utils;

import java.util.Properties;

/**
 * [配置文件工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class PropertyUtil {
    /**
     * [获取配置档的属性值,返回boolean]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static boolean getBooleanOption(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        value = value.trim().toLowerCase();
        if (value.length() == 0) {
            return defaultValue;
        }
        return "true".equals(value) || "yes".equals(value) || "on".equals(value) || "1".equals(value);
    }
}
