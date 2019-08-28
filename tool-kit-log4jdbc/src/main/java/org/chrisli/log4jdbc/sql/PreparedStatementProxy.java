package org.chrisli.log4jdbc.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.chrisli.log4jdbc.proxy.Proxy;
import org.chrisli.log4jdbc.rdbms.base.RdbmsSpecifics;

/**
 * [预执行Statement代理类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class PreparedStatementProxy extends StatementProxy implements PreparedStatement {

    protected PreparedStatement realPreparedStatement;

    protected RdbmsSpecifics rdbmsSpecifics;

    protected final List<String> argTrace = new ArrayList<String>();

    private String sql;

    protected void argTraceSet(int i, String typeHelper, Object arg) {
        String tracedArg;
        try {
            tracedArg = rdbmsSpecifics.formatParameterObject(arg);
        } catch (Throwable t) {
            log.debug("rdbmsSpecifics threw an exception while trying to format a " + "parameter object [" + arg + "] this is very bad!!! ("
                    + t.getMessage() + ")");
            tracedArg = arg == null ? "null" : arg.toString();
        }
        i--;
        synchronized (argTrace) {
            while (i >= argTrace.size()) {
                argTrace.add(argTrace.size(), null);
            }
            argTrace.set(i, tracedArg);
        }
    }

    protected String dumpedSql() {
        StringBuffer dumpSql = new StringBuffer();
        int lastPos = 0;
        int Qpos = sql.indexOf('?', lastPos);
        int argIdx = 0;
        String arg;
        while (Qpos != -1) {
            synchronized (argTrace) {
                try {
                    arg = (String) argTrace.get(argIdx);
                } catch (IndexOutOfBoundsException e) {
                    arg = "?";
                }
            }
            if (arg == null) {
                arg = "?";
            }
            argIdx++;
            dumpSql.append(sql.substring(lastPos, Qpos));
            lastPos = Qpos + 1;
            Qpos = sql.indexOf('?', lastPos);
            dumpSql.append(arg);
        }
        if (lastPos < sql.length()) {
            dumpSql.append(sql.substring(lastPos, sql.length()));
        }
        return dumpSql.toString();
    }

    protected void reportAllReturns(String methodCall, String msg) {
        log.methodReturned(this, methodCall, msg);
    }

    public PreparedStatement getRealPreparedStatement() {
        return realPreparedStatement;
    }

    public PreparedStatementProxy(String sql, ConnectionProxy connectionProxy, PreparedStatement realPreparedStatement) {
        super(connectionProxy, realPreparedStatement);
        this.sql = sql;
        this.realPreparedStatement = realPreparedStatement;
        rdbmsSpecifics = connectionProxy.getRdbmsSpecifics();
    }

    public String getClassType() {
        return "PreparedStatement";
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        String methodCall = "setTime(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(Time)", x);
        try {
            realPreparedStatement.setTime(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        String methodCall = "setTime(" + parameterIndex + ", " + x + ", " + cal + ")";
        argTraceSet(parameterIndex, "(Time)", x);
        try {
            realPreparedStatement.setTime(parameterIndex, x, cal);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        String methodCall = "setCharacterStream(" + parameterIndex + ", " + reader + ", " + length + ")";
        argTraceSet(parameterIndex, "(Reader)", "<Reader of length " + length + ">");
        try {
            realPreparedStatement.setCharacterStream(parameterIndex, reader, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        String methodCall = "setNull(" + parameterIndex + ", " + sqlType + ")";
        argTraceSet(parameterIndex, null, null);
        try {
            realPreparedStatement.setNull(parameterIndex, sqlType);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
        String methodCall = "setNull(" + paramIndex + ", " + sqlType + ", " + typeName + ")";
        argTraceSet(paramIndex, null, null);
        try {
            realPreparedStatement.setNull(paramIndex, sqlType, typeName);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setRef(int i, Ref x) throws SQLException {
        String methodCall = "setRef(" + i + ", " + x + ")";
        argTraceSet(i, "(Ref)", x);
        try {
            realPreparedStatement.setRef(i, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        String methodCall = "setBoolean(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(boolean)", x ? Boolean.TRUE : Boolean.FALSE);
        try {
            realPreparedStatement.setBoolean(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBlob(int i, Blob x) throws SQLException {
        String methodCall = "setBlob(" + i + ", " + x + ")";
        argTraceSet(i, "(Blob)", x == null ? null : ("<Blob of size " + x.length() + ">"));
        try {
            realPreparedStatement.setBlob(i, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setClob(int i, Clob x) throws SQLException {
        String methodCall = "setClob(" + i + ", " + x + ")";
        argTraceSet(i, "(Clob)", x == null ? null : ("<Clob of size " + x.length() + ">"));
        try {
            realPreparedStatement.setClob(i, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setArray(int i, Array x) throws SQLException {
        String methodCall = "setArray(" + i + ", " + x + ")";
        argTraceSet(i, "(Array)", "<Array>");
        try {
            realPreparedStatement.setArray(i, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        String methodCall = "setByte(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(byte)", new Byte(x));
        try {
            realPreparedStatement.setByte(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        String methodCall = "setUnicodeStream(" + parameterIndex + ", " + x + ", " + length + ")";
        argTraceSet(parameterIndex, "(Unicode InputStream)", "<Unicode InputStream of length " + length + ">");
        try {
            realPreparedStatement.setUnicodeStream(parameterIndex, x, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        String methodCall = "setShort(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(short)", new Short(x));
        try {
            realPreparedStatement.setShort(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public boolean execute() throws SQLException {
        String methodCall = "execute()";
        String dumpedSql = dumpedSql();
        reportSql(dumpedSql, methodCall);
        long tstart = System.currentTimeMillis();
        try {
            boolean result = realPreparedStatement.execute();
            reportSqlTiming(System.currentTimeMillis() - tstart, dumpedSql, methodCall);
            return reportReturn(methodCall, result);
        } catch (SQLException s) {
            reportException(methodCall, s, dumpedSql, System.currentTimeMillis() - tstart);
            throw s;
        }
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        String methodCall = "setInt(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(int)", new Integer(x));
        try {
            realPreparedStatement.setInt(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        String methodCall = "setLong(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(long)", new Long(x));
        try {
            realPreparedStatement.setLong(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        String methodCall = "setFloat(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(float)", new Float(x));
        try {
            realPreparedStatement.setFloat(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        String methodCall = "setDouble(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(double)", new Double(x));
        try {
            realPreparedStatement.setDouble(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        String methodCall = "setBigDecimal(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(BigDecimal)", x);
        try {
            realPreparedStatement.setBigDecimal(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        String methodCall = "setURL(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(URL)", x);

        try {
            realPreparedStatement.setURL(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        String methodCall = "setString(" + parameterIndex + ", \"" + x + "\")";
        argTraceSet(parameterIndex, "(String)", x);

        try {
            realPreparedStatement.setString(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        String methodCall = "setBytes(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(byte[])", "<byte[]>");
        try {
            realPreparedStatement.setBytes(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        String methodCall = "setDate(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(Date)", x);
        try {
            realPreparedStatement.setDate(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        String methodCall = "getParameterMetaData()";
        try {
            return (ParameterMetaData) reportReturn(methodCall, realPreparedStatement.getParameterMetaData());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        String methodCall = "setRowId(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(RowId)", x);
        try {
            realPreparedStatement.setRowId(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
        String methodCall = "setNString(" + parameterIndex + ", " + value + ")";
        argTraceSet(parameterIndex, "(String)", value);
        try {
            realPreparedStatement.setNString(parameterIndex, value);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        String methodCall = "setNCharacterStream(" + parameterIndex + ", " + value + ", " + length + ")";
        argTraceSet(parameterIndex, "(Reader)", "<Reader of length " + length + ">");
        try {
            realPreparedStatement.setNCharacterStream(parameterIndex, value, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        String methodCall = "setNClob(" + parameterIndex + ", " + value + ")";
        argTraceSet(parameterIndex, "(NClob)", "<NClob>");
        try {
            realPreparedStatement.setNClob(parameterIndex, value);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        String methodCall = "setClob(" + parameterIndex + ", " + reader + ", " + length + ")";
        argTraceSet(parameterIndex, "(Reader)", "<Reader of length " + length + ">");
        try {
            realPreparedStatement.setClob(parameterIndex, reader, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        String methodCall = "setBlob(" + parameterIndex + ", " + inputStream + ", " + length + ")";
        argTraceSet(parameterIndex, "(InputStream)", "<InputStream of length " + length + ">");
        try {
            realPreparedStatement.setBlob(parameterIndex, inputStream, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        String methodCall = "setNClob(" + parameterIndex + ", " + reader + ", " + length + ")";
        argTraceSet(parameterIndex, "(Reader)", "<Reader of length " + length + ">");
        try {
            realPreparedStatement.setNClob(parameterIndex, reader, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        String methodCall = "setSQLXML(" + parameterIndex + ", " + xmlObject + ")";
        argTraceSet(parameterIndex, "(SQLXML)", xmlObject);
        try {
            realPreparedStatement.setSQLXML(parameterIndex, xmlObject);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        String methodCall = "setDate(" + parameterIndex + ", " + x + ", " + cal + ")";
        argTraceSet(parameterIndex, "(Date)", x);

        try {
            realPreparedStatement.setDate(parameterIndex, x, cal);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public ResultSet executeQuery() throws SQLException {
        String methodCall = "executeQuery()";
        String dumpedSql = dumpedSql();
        reportSql(dumpedSql, methodCall);
        long tstart = System.currentTimeMillis();
        try {
            ResultSet r = realPreparedStatement.executeQuery();
            reportSqlTiming(System.currentTimeMillis() - tstart, dumpedSql, methodCall);
            ResultSetProxy rsp = new ResultSetProxy(this, r);
            return (ResultSet) reportReturn(methodCall, rsp);
        } catch (SQLException s) {
            reportException(methodCall, s, dumpedSql, System.currentTimeMillis() - tstart);
            throw s;
        }
    }

    private String getTypeHelp(Object x) {
        if (x == null) {
            return "(null)";
        } else {
            return "(" + x.getClass().getName() + ")";
        }
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        String methodCall = "setObject(" + parameterIndex + ", " + x + ", " + targetSqlType + ", " + scale + ")";
        argTraceSet(parameterIndex, getTypeHelp(x), x);

        try {
            realPreparedStatement.setObject(parameterIndex, x, targetSqlType, scale);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        String methodCall = "setAsciiStream(" + parameterIndex + ", " + x + ", " + length + ")";
        argTraceSet(parameterIndex, "(Ascii InputStream)", "<Ascii InputStream of length " + length + ">");
        try {
            realPreparedStatement.setAsciiStream(parameterIndex, x, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        String methodCall = "setBinaryStream(" + parameterIndex + ", " + x + ", " + length + ")";
        argTraceSet(parameterIndex, "(Binary InputStream)", "<Binary InputStream of length " + length + ">");
        try {
            realPreparedStatement.setBinaryStream(parameterIndex, x, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        String methodCall = "setCharacterStream(" + parameterIndex + ", " + reader + ", " + length + ")";
        argTraceSet(parameterIndex, "(Reader)", "<Reader of length " + length + ">");
        try {
            realPreparedStatement.setCharacterStream(parameterIndex, reader, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);

    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        String methodCall = "setAsciiStream(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(Ascii InputStream)", "<Ascii InputStream>");
        try {
            realPreparedStatement.setAsciiStream(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        String methodCall = "setBinaryStream(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(Binary InputStream)", "<Binary InputStream>");
        try {
            realPreparedStatement.setBinaryStream(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);

    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        String methodCall = "setCharacterStream(" + parameterIndex + ", " + reader + ")";
        argTraceSet(parameterIndex, "(Reader)", "<Reader>");
        try {
            realPreparedStatement.setCharacterStream(parameterIndex, reader);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        String methodCall = "setNCharacterStream(" + parameterIndex + ", " + reader + ")";
        argTraceSet(parameterIndex, "(Reader)", "<Reader>");
        try {
            realPreparedStatement.setNCharacterStream(parameterIndex, reader);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        String methodCall = "setClob(" + parameterIndex + ", " + reader + ")";
        argTraceSet(parameterIndex, "(Reader)", "<Reader>");
        try {
            realPreparedStatement.setClob(parameterIndex, reader);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        String methodCall = "setBlob(" + parameterIndex + ", " + inputStream + ")";
        argTraceSet(parameterIndex, "(InputStream)", "<InputStream>");
        try {
            realPreparedStatement.setBlob(parameterIndex, inputStream);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        String methodCall = "setNClob(" + parameterIndex + ", " + reader + ")";
        argTraceSet(parameterIndex, "(Reader)", "<Reader>");
        try {
            realPreparedStatement.setNClob(parameterIndex, reader);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);

    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        String methodCall = "setObject(" + parameterIndex + ", " + x + ", " + targetSqlType + ")";
        argTraceSet(parameterIndex, getTypeHelp(x), x);
        try {
            realPreparedStatement.setObject(parameterIndex, x, targetSqlType);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        String methodCall = "setObject(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, getTypeHelp(x), x);
        try {
            realPreparedStatement.setObject(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        String methodCall = "setTimestamp(" + parameterIndex + ", " + x + ")";
        argTraceSet(parameterIndex, "(Date)", x);
        try {
            realPreparedStatement.setTimestamp(parameterIndex, x);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        String methodCall = "setTimestamp(" + parameterIndex + ", " + x + ", " + cal + ")";
        argTraceSet(parameterIndex, "(Timestamp)", x);
        try {
            realPreparedStatement.setTimestamp(parameterIndex, x, cal);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public int executeUpdate() throws SQLException {
        String methodCall = "executeUpdate()";
        String dumpedSql = dumpedSql();
        reportSql(dumpedSql, methodCall);
        long tstart = System.currentTimeMillis();
        try {
            int result = realPreparedStatement.executeUpdate();
            reportSqlTiming(System.currentTimeMillis() - tstart, dumpedSql, methodCall);
            return reportReturn(methodCall, result);
        } catch (SQLException s) {
            reportException(methodCall, s, dumpedSql, System.currentTimeMillis() - tstart);
            throw s;
        }
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        String methodCall = "setAsciiStream(" + parameterIndex + ", " + x + ", " + length + ")";
        argTraceSet(parameterIndex, "(Ascii InputStream)", "<Ascii InputStream of length " + length + ">");
        try {
            realPreparedStatement.setAsciiStream(parameterIndex, x, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        String methodCall = "setBinaryStream(" + parameterIndex + ", " + x + ", " + length + ")";
        argTraceSet(parameterIndex, "(Binary InputStream)", "<Binary InputStream of length " + length + ">");
        try {
            realPreparedStatement.setBinaryStream(parameterIndex, x, length);
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public void clearParameters() throws SQLException {
        String methodCall = "clearParameters()";

        synchronized (argTrace) {
            argTrace.clear();
        }

        try {
            realPreparedStatement.clearParameters();
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        String methodCall = "getMetaData()";
        try {
            return (ResultSetMetaData) reportReturn(methodCall, realPreparedStatement.getMetaData());
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public void addBatch() throws SQLException {
        String methodCall = "addBatch()";
        currentBatch.add(dumpedSql());
        try {
            realPreparedStatement.addBatch();
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
        reportReturn(methodCall);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        String methodCall = "unwrap(" + (iface == null ? "null" : iface.getName()) + ")";
        try {
            return (T) reportReturn(methodCall,
                    (iface != null && (iface == PreparedStatement.class || iface == Statement.class || iface == Proxy.class)) ? (T) this
                            : realPreparedStatement.unwrap(iface));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        String methodCall = "isWrapperFor(" + (iface == null ? "null" : iface.getName()) + ")";
        try {
            return reportReturn(methodCall, (iface != null && (iface == PreparedStatement.class || iface == Statement.class || iface == Proxy.class))
                    || realPreparedStatement.isWrapperFor(iface));
        } catch (SQLException s) {
            reportException(methodCall, s);
            throw s;
        }
    }
}