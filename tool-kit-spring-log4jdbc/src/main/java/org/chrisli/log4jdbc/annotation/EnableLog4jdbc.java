package org.chrisli.log4jdbc.annotation;

import org.chrisli.log4jdbc.spring.DataSourceBeanRegistrar;
import org.chrisli.log4jdbc.spring.listener.DataSourceBeanInitedListener;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * [启用记录Jdbc日志]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({DataSourceBeanRegistrar.class, DataSourceBeanInitedListener.class})
@Documented
@Inherited
public @interface EnableLog4jdbc {
    /**
     * 数据源Bean名称,默认为{"dataSource"}
     */
    String[] dataSourceBeanNames() default {"dataSource"};
}