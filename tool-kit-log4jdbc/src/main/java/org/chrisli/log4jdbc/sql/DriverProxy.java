package org.chrisli.log4jdbc.sql;

import org.chrisli.log4jdbc.proxy.ProxyLogDelegator;
import org.chrisli.log4jdbc.proxy.ProxyLogFactory;
import org.chrisli.log4jdbc.rdbms.MySqlRdbmsSpecifics;
import org.chrisli.log4jdbc.rdbms.OracleRdbmsSpecifics;
import org.chrisli.log4jdbc.rdbms.SqlServerRdbmsSpecifics;
import org.chrisli.log4jdbc.rdbms.base.RdbmsSpecifics;

import java.sql.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * [数据库驱动代理类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class DriverProxy implements Driver {

    private Driver lastUnderlyingDriverRequested;

    private static Map<String, RdbmsSpecifics> rdbmsSpecifics;

    static RdbmsSpecifics defaultRdbmsSpecifics = new RdbmsSpecifics();

    static ProxyLogDelegator log = ProxyLogFactory.getProxyLogDelegator();

    static {
        log.debug("... log4jdbc initializing ...");

        OracleRdbmsSpecifics oracle = new OracleRdbmsSpecifics();
        MySqlRdbmsSpecifics mySql = new MySqlRdbmsSpecifics();
        SqlServerRdbmsSpecifics sqlServer = new SqlServerRdbmsSpecifics();

        rdbmsSpecifics = new HashMap<String, RdbmsSpecifics>();
        rdbmsSpecifics.put("Oracle JDBC driver", oracle);
        rdbmsSpecifics.put("oracle.jdbc.driver.OracleDriver", oracle);
        rdbmsSpecifics.put("oracle.jdbc.OracleDriver", oracle);
        rdbmsSpecifics.put("com.mysql.jdbc.Driver", mySql);
        rdbmsSpecifics.put("com.microsoft.sqlserver.jdbc.SQLServerDriver", sqlServer);

        log.debug("... log4jdbc initialized! ...");
    }

    /**
     * [获取匹配的关系数据库特性]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static RdbmsSpecifics getRdbmsSpecifics(Connection conn) {
        String driverName = "";
        try {
            DatabaseMetaData dbm = conn.getMetaData();
            driverName = dbm.getDriverName();
        } catch (SQLException s) {
            // 忽略
        }
        log.debug("driver name is " + driverName);
        RdbmsSpecifics rdbmsSpecificx = (RdbmsSpecifics) rdbmsSpecifics.get(driverName);
        if (rdbmsSpecificx == null) {
            return defaultRdbmsSpecifics;
        }
        return rdbmsSpecificx;
    }

    public boolean acceptsURL(String url) throws SQLException {
        Driver driver = getUnderlyingDriver(url);
        if (driver != null) {
            lastUnderlyingDriverRequested = driver;
            return true;
        }
        return false;
    }

    /**
     * [获取当前的驱动]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private Driver getUnderlyingDriver(String url) throws SQLException {
        if (url.startsWith("jdbc:log4")) {
            url = url.substring(9);
            Enumeration<Driver> e = DriverManager.getDrivers();
            Driver driver;
            while (e.hasMoreElements()) {
                driver = (Driver) e.nextElement();
                if (driver.acceptsURL(url)) {
                    return driver;
                }
            }
        }
        return null;
    }

    public Connection connect(String url, Properties info) throws SQLException {
        Driver driver = getUnderlyingDriver(url);
        if (driver == null) {
            return null;
        }
        // url中需删除"jdbc:log4"
        url = url.substring(9);
        lastUnderlyingDriverRequested = driver;
        Connection connection = driver.connect(url, info);
        if (connection == null) {
            throw new SQLException("invalid or unknown driver url: " + url);
        }
        if (log.isJdbcLoggingEnabled()) {
            ConnectionProxy connectionProxy = new ConnectionProxy(connection);
            RdbmsSpecifics rdbmsSpecificx = null;
            String dclass = driver.getClass().getName();
            if (dclass != null && dclass.length() > 0) {
                rdbmsSpecificx = (RdbmsSpecifics) rdbmsSpecifics.get(dclass);
            }
            if (rdbmsSpecificx == null) {
                rdbmsSpecificx = defaultRdbmsSpecifics;
            }
            connectionProxy.setRdbmsSpecifics(rdbmsSpecificx);
            return connectionProxy;
        }
        return connection;
    }

    public int getMajorVersion() {
        if (lastUnderlyingDriverRequested == null) {
            return 1;
        }
        return lastUnderlyingDriverRequested.getMajorVersion();

    }

    public int getMinorVersion() {
        if (lastUnderlyingDriverRequested == null) {
            return 0;
        }
        return lastUnderlyingDriverRequested.getMinorVersion();
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        Driver driver = getUnderlyingDriver(url);
        if (driver == null) {
            return new DriverPropertyInfo[0];
        }

        lastUnderlyingDriverRequested = driver;
        return driver.getPropertyInfo(url, info);

    }

    public boolean jdbcCompliant() {
        return lastUnderlyingDriverRequested != null && lastUnderlyingDriverRequested.jdbcCompliant();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
}