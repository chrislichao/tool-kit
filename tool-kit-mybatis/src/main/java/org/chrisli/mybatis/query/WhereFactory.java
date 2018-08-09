package org.chrisli.mybatis.query;

import org.chrisli.mybatis.base.BaseEntity;

import java.lang.reflect.Constructor;

/**
 * [自定义查询类的工厂类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class WhereFactory {
    /**
     * [获取一个自定义查询类的实例]
     */
    public static Where createInstance(Class<? extends BaseEntity> clazz) {
        try {
            Constructor<Where> whereCls = Where.class.getDeclaredConstructor(Class.class);
            whereCls.setAccessible(true);
            return whereCls.newInstance(clazz);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Get Where instance failed by class[%s]!", clazz.getName()), e);
        }
    }
}
