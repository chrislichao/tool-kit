package org.chrisli.log4jdbc.spring;

import org.chrisli.log4jdbc.DataSourceAgent;
import org.springframework.beans.factory.FactoryBean;

import javax.sql.DataSource;

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
        if (!(realDataSourceBean instanceof DataSource)) {
            throw new Exception("非【javax.sql.DataSource】接口的实例，无法代理!");
        }
        return (T) DataSourceAgent.newProxyInstance((DataSource) realDataSourceBean);
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
     * @create [2017-04-12]
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