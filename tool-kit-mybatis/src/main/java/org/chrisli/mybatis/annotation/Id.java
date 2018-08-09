package org.chrisli.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [标注在实体类字段上,表的主键]
 * TODO 目前仅支持单主键
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
    /**
     * [主键名,指定属性对应的主键字段名]
     */
    public String name() default "";
}
