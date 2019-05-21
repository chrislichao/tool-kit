package org.chrisli.log4jdbc.spring;

import org.chrisli.log4jdbc.annotation.EnableLog4jdbc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * [数据源Bean注册器]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class DataSourceBeanRegistrar implements ImportBeanDefinitionRegistrar, ApplicationContextAware, ApplicationListener {

    private Logger logger = LoggerFactory.getLogger(DataSourceBeanRegistrar.class);

    private ApplicationContext applicationContext;

    private List<String> dataSourceBeanNameList = new ArrayList<String>();

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> log4jdbcAttributesMap = importingClassMetadata.getAnnotationAttributes(EnableLog4jdbc.class.getName(), true);
        AnnotationAttributes log4jdbcAttributes = AnnotationAttributes.fromMap(log4jdbcAttributesMap);
    }

    /**
     * [获取注册者]
     *
     * @author Chris li[黎超]
     * @create [2017/4/12]
     */
    public BeanDefinitionRegistry getRegistry() {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        return (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
    }

    /**
     * [注册代理的数据源Bean]
     *
     * @author Chris li[黎超]
     * @create [2017/4/12]
     */
    public boolean registryProxyDataSourceBean(String beanId, Object realDataSourceBean) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSource.class);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        definition.getPropertyValues().add("realDataSourceBean", realDataSourceBean);
        definition.setBeanClass(DataSourceFactoryBean.class);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        getRegistry().registerBeanDefinition(beanId, definition);
        return true;
    }

    /**
     * [Spring容器加载完成]
     *
     * @author Chris li[黎超]
     * @create [2017/4/12]
     */
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("- -- --- ---- ----- Application加载完成!!!");
        for (String dataSourceBeanName : dataSourceBeanNameList) {
            Object dataSourceBean = applicationContext.getBean(dataSourceBeanName);
            if (dataSourceBean == null) {
                logger.error("DataSource bean not exist whose name is {0}!", dataSourceBeanName);
                throw new RuntimeException("DataSource bean not exist whose name is " + dataSourceBeanName + "!");
            }
            if (dataSourceBean instanceof DataSource) {
                // 代理真实的DataSource
                registryProxyDataSourceBean(dataSourceBeanName, dataSourceBean);
            } else {
                logger.info("Bean[{0}] is not instance of class[javax.sql.DataSource], will be ignored!", dataSourceBeanName);
            }
        }
    }
}
