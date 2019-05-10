package org.chrisli.mybatis.base;

import org.chrisli.mybatis.enums.OrderByPolicy;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * [抽象的基础实体类，用于所有实体类继承]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 存放字段排序策略
     */
    private Map<String, OrderByPolicy> columnSortPolicyMap = new LinkedHashMap<String, OrderByPolicy>();

    /**
     * [添加字段排序策略,顺序]
     */
    public final BaseEntity orderByAsc(String fieldName) {
        columnSortPolicyMap.put(fieldName, OrderByPolicy.ASC);
        return this;
    }

    /**
     * [添加字段排序策略,逆序]
     */
    public final BaseEntity orderByDesc(String fieldName) {
        columnSortPolicyMap.put(fieldName, OrderByPolicy.DESC);
        return this;
    }
}
