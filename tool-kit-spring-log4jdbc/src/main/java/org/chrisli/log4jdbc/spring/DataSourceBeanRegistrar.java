package org.chrisli.log4jdbc.spring;

import org.chrisli.log4jdbc.annotation.EnableLog4jdbc;
import org.chrisli.log4jdbc.config.Log4jdbcConfig;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import javax.sql.DataSource;

/**
 * [数据源Bean注册器]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class DataSourceBeanRegistrar implements ImportBeanDefinitionRegistrar {

    private static ApplicationContext applicationContext;

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Log4jdbcConfig.dataSourceBeanNames = ((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getAnnotation(EnableLog4jdbc.class).dataSourceBeanNames();
    }

    /**
     * [获取注册者]
     *
     * @author Chris li[黎超]
     * @create [2017/4/12]
     */
    public static BeanDefinitionRegistry getRegistry(ApplicationContext applicationContext) {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        return (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
    }

    /**
     * [注册代理的数据源Bean]
     *
     * @author Chris li[黎超]
     * @create [2017/4/12]
     */
    public static boolean registryProxyDataSourceBean(ApplicationContext applicationContext, String beanId, Object realDataSourceBean) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSource.class);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
        definition.getPropertyValues().add("realDataSourceBean", realDataSourceBean);
        definition.setBeanClass(DataSourceFactoryBean.class);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        getRegistry(applicationContext).registerBeanDefinition(beanId, definition);
        return true;
    }
}