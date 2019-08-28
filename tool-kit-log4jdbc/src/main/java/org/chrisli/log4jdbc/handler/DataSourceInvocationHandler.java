package org.chrisli.log4jdbc.handler;

import org.chrisli.log4jdbc.proxy.ProxyLogFactory;
import org.chrisli.log4jdbc.rdbms.base.RdbmsSpecifics;
import org.chrisli.log4jdbc.sql.ConnectionProxy;
import org.chrisli.log4jdbc.sql.DriverProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * [数据库Connection代理处理器]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class DataSourceInvocationHandler implements InvocationHandler {

    private Object realDataSourceBean;

    public DataSourceInvocationHandler(Object realDataSourceBean) {
        this.realDataSourceBean = realDataSourceBean;
    }

    private RdbmsSpecifics rdbmsSpecifics = null;

    private RdbmsSpecifics getRdbmsSpecifics(Connection conn) {
        if (this.rdbmsSpecifics == null) {
            this.rdbmsSpecifics = DriverProxy.getRdbmsSpecifics(conn);
        }
        return this.rdbmsSpecifics;
    }

    /**
     * [代理接口方法调用处理逻辑]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(realDataSourceBean, args);
        if (result == null) {
            return null;
        }
        if (ProxyLogFactory.getProxyLogDelegator().isJdbcLoggingEnabled() && result instanceof Connection) {
            Connection conn = (Connection) result;
            return new ConnectionProxy(conn, this.getRdbmsSpecifics(conn));
        } else {
            return result;
        }
    }
}