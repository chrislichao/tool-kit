package org.chrisli.mybatis.enums;

/**
 * [左连接分组策略,目前仅供@LeftJoin注解使用]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public enum Group {

    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10);

    private final Integer value;

    Group(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }


    public String toString() {
        return getValue().toString();
    }
}
