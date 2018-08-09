package org.chrisli.mybatis.enums;

/**
 * [查询排序策略,目前仅供@OrderBy注解使用]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public enum OrderByPolicy {
    /**
     * [顺序排列]
     */
    ASC("ASC"),
    /**
     * [逆序排列]
     */
    DESC("DESC");

    private final String policy;

    OrderByPolicy(String policy) {
        this.policy = policy;
    }

    public String toString() {
        return this.policy;
    }
}
