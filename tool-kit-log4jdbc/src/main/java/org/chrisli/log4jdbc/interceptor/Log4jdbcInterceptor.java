package org.chrisli.log4jdbc.interceptor;

import java.sql.Connection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.chrisli.log4jdbc.ProxyLogFactory;
import org.chrisli.log4jdbc.rdbms.RdbmsSpecifics;
import org.chrisli.log4jdbc.sql.ConnectionProxy;
import org.chrisli.log4jdbc.sql.DriverProxy;

/**
 * [JDBC日志拦截器]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class Log4jdbcInterceptor implements MethodInterceptor {

    private RdbmsSpecifics rdbmsSpecifics = null;

    private RdbmsSpecifics getRdbmsSpecifics(Connection conn) {
        if (rdbmsSpecifics == null) {
            rdbmsSpecifics = DriverProxy.getRdbmsSpecifics(conn);
        }
        return rdbmsSpecifics;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object result = invocation.proceed();
        if (ProxyLogFactory.getProxyLogDelegator().isJdbcLoggingEnabled()) {
            if (result instanceof Connection) {
                Connection conn = (Connection) result;
                return new ConnectionProxy(conn, getRdbmsSpecifics(conn));
            }
        }
        return result;
    }

}
