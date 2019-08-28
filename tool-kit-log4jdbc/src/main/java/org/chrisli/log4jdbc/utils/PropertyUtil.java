package org.chrisli.log4jdbc.utils;

import org.chrisli.log4jdbc.config.Log4jdbcConfig;
import org.chrisli.log4jdbc.constant.Log4jdbcConstant;
import org.chrisli.log4jdbc.sql.DriverProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * [配置文件工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class PropertyUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

    /**
     * [获取配置档的属性值,返回long]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static long getLongOption(Properties properties, String key, long defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        value = value.trim().toLowerCase();
        if (value.length() == 0) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            logger.warn("配置项【{}】的值非【Long】类型!", key);
            return defaultValue;
        }
    }

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

    /**
     * [读取配置文件,更新常量值]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static void readProperty() {
        // 加载配置文件
        InputStream propertyStream = DriverProxy.class.getResourceAsStream(Log4jdbcConstant.DEFAULT_PROPERTY_PATH);
        Properties properties = new Properties(System.getProperties());
        if (propertyStream != null) {
            try {
                properties.load(propertyStream);
                logger.debug("classpath下发现配置文件【{}】,将读取相关配置项", Log4jdbcConstant.DEFAULT_PROPERTY_NAME);
                // 读取配置并刷新配置类中的属性
                Log4jdbcConfig.enable_sql_logged = getBooleanOption(properties, Log4jdbcConstant.PROPERTY_KEY_ENABLE_SQL_LOGGED, Log4jdbcConstant.DEFAULT_ENABLE_SQL_LOGGED);
            } catch (IOException e) {
                logger.warn("加载classpath下配置文件【{}】出现异常:【{}】", Log4jdbcConstant.DEFAULT_PROPERTY_NAME, e.getLocalizedMessage());
            } finally {
                try {
                    propertyStream.close();
                } catch (IOException e) {
                    logger.warn("关闭classpath下配置文件【{}】文件流出现异常:【{}】", Log4jdbcConstant.DEFAULT_PROPERTY_NAME, e.getLocalizedMessage());
                }
            }
        } else {
            logger.debug("classpath下未发现配置文件【log4jdbc.properties】,将使用默认配置");
        }
    }
}