package org.chrisli.log4jdbc;

/**
 * [jdbc中代理的接口]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public interface Proxy {
    /**
     * [获取被代理类的类型]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public String getClassType();
    /**
     * [获取jdbc连接数]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public Integer getConnectionNumber();
}
