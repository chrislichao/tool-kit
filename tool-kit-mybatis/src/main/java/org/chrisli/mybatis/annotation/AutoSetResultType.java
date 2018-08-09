package org.chrisli.mybatis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [标注在基础查询方法上,为标注的方法自动设置返回值类型,仅供BaseDao使用]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AutoSetResultType {
}
