package org.chrisli.log4jdbc.constant;

/**
 * [Log4jdbc实例类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class Log4jdbcConstant {
    /**
     * 默认的配置文件名称
     */
    public static final String DEFAULT_PROPERTY_NAME = "log4jdbc.properties";
    /**
     * 默认的配置文件路径
     */
    public static final String DEFAULT_PROPERTY_PATH = "/log4jdbc.properties";
    /**
     * 配置启用日志记录的键
     */
    public static final String PROPERTY_KEY_PROPERTY_REFRESH_PERIOD = "log4jdbc.config.propertyRefreshPeriod";
    /**
     * 配置启用日志记录的键
     */
    public static final String PROPERTY_KEY_ENABLE_SQL_LOGGED = "log4jdbc.config.enableSqlLogged";
    /**
     * 默认的配置文件读取更新时间间隔,单位:秒
     */
    public static final long DEFAULT_PROPERTY_REFRESH_PERIOD = 5;
    /**
     * 默认启用日志记录
     */
    public static final boolean DEFAULT_ENABLE_SQL_LOGGED = true;
}