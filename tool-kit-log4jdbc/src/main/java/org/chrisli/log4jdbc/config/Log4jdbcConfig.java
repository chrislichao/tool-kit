package org.chrisli.log4jdbc.config;

import org.chrisli.log4jdbc.constant.Log4jdbcConstant;

/**
 * [Log4jdbc配置类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class Log4jdbcConfig {
    /**
     * 配置文件读取更新时间间隔,单位:秒
     */
    public static long property_refresh_period = Log4jdbcConstant.DEFAULT_PROPERTY_REFRESH_PERIOD;
    /**
     * 默认启用日志记录,跟随配置文件更新而刷新
     */
    public static boolean enable_sql_logged = Log4jdbcConstant.DEFAULT_ENABLE_SQL_LOGGED;
}
