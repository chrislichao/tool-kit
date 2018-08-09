package org.chrisli.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [标注在实体类上,指定实体类对应的数据库表名]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    /**
     * [表名,默认为实体类大写,驼峰格式]
     */
    public String name() default "";
    /**
     * [联合主键字段(model中的字段名,逗号分隔),默认为空]
     */
    public String unionPk() default "";
    /**
     * [违反唯一性时的提示信息]
     */
    public String unionPkMsg() default "对象已存在!";
}
