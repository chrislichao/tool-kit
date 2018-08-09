package org.chrisli.mybatis.annotation;

import org.chrisli.mybatis.enums.OrderByPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [标注在实体类字段上,指定查询时排序策略]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OrderBy {
    /**
     * [排序顺序(从1开始)]
     */
    public int order();
    /**
     * [排序策略,默认为顺序]
     */
    public OrderByPolicy policy() default OrderByPolicy.ASC;
}
