package org.chrisli.log4jdbc.logger;

import org.chrisli.log4jdbc.config.Log4jdbcConfig;
import org.chrisli.log4jdbc.proxy.Proxy;
import org.chrisli.log4jdbc.proxy.ProxyLogDelegator;
import org.chrisli.log4jdbc.sql.ConnectionProxy;
import org.chrisli.log4jdbc.sql.ResultSetProxy;
import org.chrisli.log4jdbc.task.PropertyRefreshTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.StringTokenizer;

/**
 * [SLF4J代理日志的处理者]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class Slf4jProxyLogDelegator implements ProxyLogDelegator {

    private final Logger jdbcLogger = LoggerFactory.getLogger("jdbc.audit");

    private final Logger resultSetLogger = LoggerFactory.getLogger("jdbc.resultset");

    private final Logger sqlOnlyLogger = LoggerFactory.getLogger("jdbc.sqlonly");

    private final Logger sqlTimingLogger = LoggerFactory.getLogger("jdbc.sqltiming");

    private final Logger connectionLogger = LoggerFactory.getLogger("jdbc.connection");

    private final Logger debugLogger = LoggerFactory.getLogger("log4jdbc.debug");

    private static String nl = System.getProperty("line.separator");

    static {
        // 启动配置文件刷新任务
        PropertyRefreshTask.start();
    }

    public boolean isJdbcLoggingEnabled() {
        return jdbcLogger.isErrorEnabled() || resultSetLogger.isErrorEnabled() || sqlOnlyLogger.isErrorEnabled() || sqlTimingLogger.isErrorEnabled()
                || connectionLogger.isErrorEnabled();
    }

    public void exceptionOccured(Proxy proxy, String methodCall, Exception e, String sql, long execTime) {
        String classType = proxy.getClassType();
        Integer proxyNo = proxy.getConnectionNumber();
        String header = proxyNo + ". " + classType + "." + methodCall;
        if (sql == null) {
            jdbcLogger.error(header, e);
            sqlOnlyLogger.error(header, e);
            sqlTimingLogger.error(header, e);
        } else {
            sql = processSql(sql);
            jdbcLogger.error(header + " " + sql, e);

            if (sqlOnlyLogger.isDebugEnabled()) {
                sqlOnlyLogger.error(getDebugInfo() + nl + proxyNo + ". " + sql, e);
            } else {
                sqlOnlyLogger.error(header + " " + sql, e);
            }

            if (sqlTimingLogger.isDebugEnabled()) {
                sqlTimingLogger.error(getDebugInfo() + nl + proxyNo + ". " + sql + " {FAILED after " + execTime + " msec}", e);
            } else {
                sqlTimingLogger.error(header + " FAILED! " + sql + " {FAILED after " + execTime + " msec}", e);
            }
        }
    }

    public void methodReturned(Proxy proxy, String methodCall, String returnMsg) {
        String classType = proxy.getClassType();
        Logger logger = ResultSetProxy.classTypeDescription.equals(classType) ? resultSetLogger : jdbcLogger;
        if (logger.isInfoEnabled()) {
            String header = proxy.getConnectionNumber() + ". " + classType + "." + methodCall + " returned " + returnMsg;
            if (logger.isDebugEnabled()) {
                logger.debug(header + " " + getDebugInfo());
            } else {
                logger.info(header);
            }
        }
    }

    public void constructorReturned(Proxy proxy, String constructionInfo) {
    }

    /**
     * [判断SQL是否需要被记录日志,支持配置档控制]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private boolean shouldSqlBeLogged(String sql) {
        if (sql == null) {
            return false;
        }
        return Log4jdbcConfig.enable_sql_logged;
    }

    public void sqlOccured(Proxy proxy, String methodCall, String sql) {
        if (shouldSqlBeLogged(sql)) {
            if (sqlOnlyLogger.isDebugEnabled()) {
                sqlOnlyLogger.debug(getDebugInfo() + nl + proxy.getConnectionNumber() + ". " + processSql(sql));
            } else if (sqlOnlyLogger.isInfoEnabled()) {
                sqlOnlyLogger.info(processSql(sql));
            }
        }
    }

    /**
     * [处理SQL]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    private String processSql(String sql) {
        if (sql == null) {
            return null;
        }

        sql = sql.trim();

        StringBuilder output = new StringBuilder();

        StringTokenizer st = new StringTokenizer(sql);
        String token;
        int linelength = 0;

        while (st.hasMoreElements()) {
            token = (String) st.nextElement();

            output.append(token);
            linelength += token.length();
            output.append(" ");
            linelength++;
        }

        String stringOutput = output.toString();

        LineNumberReader lineReader = new LineNumberReader(new StringReader(stringOutput));

        output = new StringBuilder();

        int contiguousBlankLines = 0;
        try {
            while (true) {
                String line = lineReader.readLine();
                if (line == null) {
                    break;
                }
                if (line.trim().length() == 0) {
                    contiguousBlankLines++;
                    if (contiguousBlankLines > 1) {
                        continue;
                    }
                } else {
                    contiguousBlankLines = 0;
                    output.append(line);
                }
                output.append("\n");
            }
        } catch (IOException e) {
        }
        stringOutput = output.toString();

        return stringOutput;
    }

    public void sqlTimingOccured(Proxy proxy, long execTime, String methodCall, String sql) {
        if (sqlTimingLogger.isErrorEnabled() && (shouldSqlBeLogged(sql))) {
            if (sqlTimingLogger.isWarnEnabled()) {
                if (sqlTimingLogger.isDebugEnabled()) {
                    sqlTimingLogger.debug(buildSqlTimingDump(proxy, execTime, methodCall, sql, true));
                } else if (sqlTimingLogger.isInfoEnabled()) {
                    sqlTimingLogger.info(buildSqlTimingDump(proxy, execTime, methodCall, sql, false));
                }
            }
        }
    }

    private String buildSqlTimingDump(Proxy proxy, long execTime, String methodCall, String sql, boolean debugInfo) {
        StringBuffer out = new StringBuffer();

        if (debugInfo) {
            out.append(getDebugInfo());
            out.append(nl);
        }

        sql = processSql(sql);

        out.append(" ");
        out.append(sql);

        out.append("\t{executed in ");
        out.append(execTime);
        out.append(" msec, ");
        if (debugInfo) {
            out.append("connection number: [");
            out.append(proxy.getConnectionNumber());
            out.append("]");
        }
        out.append("}");

        return out.toString();
    }

    private static String getDebugInfo() {
        Throwable t = new Throwable();
        t.fillInStackTrace();

        StackTraceElement[] stackTrace = t.getStackTrace();

        if (stackTrace != null) {
            String className;

            StringBuffer dump = new StringBuffer();
            dump.append(" ");
            int firstLog4jdbcCall = 0;
            int lastApplicationCall = 0;

            for (int i = 0; i < stackTrace.length; i++) {
                className = stackTrace[i].getClassName();
                if (className.startsWith("org.chrisli.log4jdbc")) {
                    firstLog4jdbcCall = i;
                }
            }
            int j = lastApplicationCall;

            if (j == 0) {
                j = 1 + firstLog4jdbcCall;
            }

            dump.append(stackTrace[j].getClassName()).append(".").append(stackTrace[j].getMethodName()).append("(").append(
                    stackTrace[j].getFileName()).append(":").append(stackTrace[j].getLineNumber()).append(")");

            return dump.toString();
        } else {
            return null;
        }
    }

    public void debug(String msg) {
        debugLogger.debug(msg);
    }

    public void connectionOpened(Proxy proxy) {
        if (connectionLogger.isDebugEnabled()) {
            connectionLogger.info(proxy.getConnectionNumber() + ". Connection opened " + getDebugInfo());
            connectionLogger.debug(ConnectionProxy.getOpenConnectionsDump());
        } else {
            connectionLogger.info(proxy.getConnectionNumber() + ". Connection opened");
        }
    }

    public void connectionClosed(Proxy proxy) {
        if (connectionLogger.isDebugEnabled()) {
            connectionLogger.info(proxy.getConnectionNumber() + ". Connection closed " + getDebugInfo());
            connectionLogger.debug(ConnectionProxy.getOpenConnectionsDump());
        } else {
            connectionLogger.info(proxy.getConnectionNumber() + ". Connection closed");
        }
    }
}