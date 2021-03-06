package org.chrisli.log4jdbc;

import org.chrisli.log4jdbc.handler.DataSourceInvocationHandler;
import org.chrisli.log4jdbc.task.PropertyRefreshTask;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

/**
 * [数据源代理器]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class DataSourceAgent {

    static {
        // 启动配置文件刷新任务
        PropertyRefreshTask.start();
    }

    /**
     * [获取真实数据源的代理]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static DataSource newProxyInstance(DataSource realDataSourceBean) {
        return (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(), new Class[]{DataSource.class}, new DataSourceInvocationHandler(realDataSourceBean));
    }
}