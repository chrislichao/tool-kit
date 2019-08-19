package org.chrisli.log4jdbc.spring.listener;

import org.chrisli.log4jdbc.Constant;
import org.chrisli.log4jdbc.spring.DataSourceBeanRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;

/**
 * [数据源Bean初始化完成监听器]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class DataSourceBeanInitedListener implements InitializingBean, ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(DataSourceBeanInitedListener.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info(">>>>> Spring bean init completed! <<<<<");
        for (String dataSourceBeanName : Constant.enableLog4jdbc.dataSourceBeanNames()) {
            Object dataSourceBean = null;
            try {
                dataSourceBean = applicationContext.getBean(dataSourceBeanName);
            } catch (Exception e) {
                logger.error("Exception when get bean[{}],message : {}!", dataSourceBeanName, e.getMessage());
            }
            if (dataSourceBean == null) {
                logger.error("DataSource bean not exist whose name is [{}], will be ignored!", dataSourceBeanName);
            }
            if (dataSourceBean instanceof DataSource) {
                // 代理真实的DataSource
                DataSourceBeanRegistrar.registryProxyDataSourceBean(applicationContext, dataSourceBeanName, dataSourceBean);
            } else {
                logger.info("Bean[{}] is not instance of class[javax.sql.DataSource], will be ignored!", dataSourceBeanName);
            }
        }
    }
}