package org.chrisli.mybatis.annotation;

import org.chrisli.mybatis.base.BaseEntity;
import org.chrisli.mybatis.enums.Group;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [左连接,标注在实体类字段上]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LeftJoin {
    /**
     * [关联表(实体类)]
     */
    public Class<? extends BaseEntity> refModel();
    /**
     * [分组,默认为1]
     */
    public Group group() default Group.ONE;
    /**
     * [关联表(实体类)的值字段]
     */
    public String refValField();
    /**
     * [自身表(实体类)的连接字段]
     */
    public String selfOnField();
    /**
     * [关联表(实体类)的连接字段]
     */
    public String refOnField() default "id";
}
