package org.chrisli.log4jdbc.spring;

import org.chrisli.log4jdbc.proxy.DataSourceInvocationHandler;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

/**
 * [数据源工厂Bean]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class DataSourceFactoryBean<T> implements FactoryBean<T> {

    private Object realDataSourceBean;

    /**
     * [获取真实数据源Bean的代理]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(DataSource.class.getClassLoader(), new Class[]{DataSource.class}, new DataSourceInvocationHandler(realDataSourceBean));
    }

    /**
     * [获取Bean类型]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    /**
     * [是否单例]
     *
     * @author Chris li[黎超]
     * @create [2019/5/20]
     */
    public boolean isSingleton() {
        return true;
    }

    public Object getRealDataSourceBean() {
        return realDataSourceBean;
    }

    public void setRealDataSourceBean(Object realDataSourceBean) {
        this.realDataSourceBean = realDataSourceBean;
    }
}