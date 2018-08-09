package org.chrisli.log4jdbc;

/**
 * [代理日志委托者接口]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public interface ProxyLogDelegator {
    /**
     * [是否启用Jdbc日志打印]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public boolean isJdbcLoggingEnabled();

    /**
     * [发生异常]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public void exceptionOccured(Proxy proxy, String methodCall, Exception e, String sql, long execTime);

    /**
     * [方法返回]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public void methodReturned(Proxy proxy, String methodCall, String returnMsg);

    /**
     * [构造器返回]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public void constructorReturned(Proxy proxy, String constructionInfo);

    /**
     * [SQL发生时]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public void sqlOccured(Proxy proxy, String methodCall, String sql);

    /**
     * [SQL耗时发生时]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public void sqlTimingOccured(Proxy proxy, long execTime, String methodCall, String sql);

    /**
     * [连接开启时]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public void connectionOpened(Proxy proxy);

    /**
     * [连接关闭时]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public void connectionClosed(Proxy proxy);

    /**
     * [调试]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public void debug(String msg);
}
