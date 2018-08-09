package org.chrisli.mybatis.query;

import org.apache.commons.lang.StringUtils;
import org.chrisli.mybatis.base.BaseEntity;
import org.chrisli.mybatis.annotation.Column;
import org.chrisli.mybatis.annotation.Id;
import org.chrisli.mybatis.annotation.LeftJoin;
import org.chrisli.mybatis.core.model.CoreColumn;
import org.chrisli.mybatis.core.model.CoreTable;
import org.chrisli.mybatis.enums.OrderByPolicy;
import org.chrisli.mybatis.helper.JdbcHelper;
import org.chrisli.utils.Assert;
import org.chrisli.utils.exception.FrameworkException;
import org.chrisli.utils.jdbc.JdbcUtil;
import org.chrisli.utils.reflect.ReflectUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [单表自定义查询条件]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class Where implements Serializable {
    /**
     * [field方法错误异常]
     */
    private static final String FIELD_ERROR_MSG = "Method[field] must be the first or after method[and,or].";
    /**
     * [oprate方法错误异常]
     */
    private static final String OPRATE_ERROR_MSG = "Method[equal,notEqual,contains,notContains,startsWith,notStartsWith,endsWith,notEndsWith,in,notIn,greaterThan,gtoet,lessThan,ltoet,isNull,isNotNull] must after method[field].";
    /**
     * [connect方法错误异常]
     */
    private static final String CONNECT_ERROR_MSG = "Method[and,or] must after method[equal,notEqual,contains,notContains,startsWith,notStartsWith,endsWith,notEndsWith,in,notIn,greaterThan,gtoet,lessThan,ltoet,isNull,isNotNull].";
    /**
     * [orderBy方法错误异常]
     */
    private static final String ORDER_BY_ERROR_MSG = "Method[orderByAsc,orderByDesc] must after method[orderByAsc,orderByDesc] or after method[equal,notEqual,contains,notContains,startsWith,notStartsWith,endsWith,notEndsWith,in,notIn,greaterThan,gtoet,lessThan,ltoet,isNull,isNotNull].";

    private static final long serialVersionUID = 1L;
    private boolean nextIsField = true;
    private boolean nextIsOprate = false;
    private boolean nextIsConnect = false;
    private boolean nextIsOrderBy = true;

    private Class<?> clazz;

    /**
     * [当前字段,在执行field方法时赋值]
     */
    private String curField = "";
    /**
     * [存放所有条件]
     */
    private StringBuffer whereSb = new StringBuffer();
    /**
     * [存放排序字段]
     */
    private StringBuffer orderBySb = new StringBuffer();

    /**
     * [存放查询字段信息]
     */
    private Map<String, String> fieldMap = new HashMap<String, String>();

    /**
     * [存放查询字段和值信息]
     */
    private Map<String, String> fieldValueMap = new HashMap<String, String>();

    /**
     * [存放左连接字段信息]
     */
    private Map<String, LeftJoin> fieldLeftJoinMap = new HashMap<String, LeftJoin>();

    /**
     * [存放操作字段集合]
     */
    private List<String> fieldList = new ArrayList<String>();

    /**
     * [ 私有构造方法,不能通过new创建对象]
     */
    private Where(Class<?> clazz) {
        this.clazz = clazz;
    }

    /**
     * [获取实体类的class]
     */
    public Class<?> getEntityClass() {
        Assert.notNull(this.clazz);
        return this.clazz;
    }

    // ------------------------------------------------字段-----------------------------------------------//

    /**
     * [设置字段]
     */
    public Where field(String field) {
        curField = field;
        fieldList.add(field);
        // 必须先设置字段
        if (nextIsField) {
            whereSb.append(" ").append(fieldToColumn(field));
            nextIsField = false;
            nextIsOprate = true;
            nextIsConnect = false;
            nextIsOrderBy = false;
            return this;
        }
        if (nextIsConnect) {
            // 如果没有加上连接符,默认添加and
            whereSb.append(" AND ").append(fieldToColumn(field));
            nextIsField = false;
            nextIsOprate = true;
            nextIsConnect = false;
            nextIsOrderBy = false;
            return this;
        }
        throw new FrameworkException(FIELD_ERROR_MSG);
    }

    /**
     * [更新左连接字段信息集合]
     */
    private void updateFieldLeftJoinMap(String field) {
        // 判断是否能取到对应的字段
        Field f = ReflectUtil.getField(clazz, BaseEntity.class, field);
        // 被Id注解标注
        if (f.isAnnotationPresent(Id.class)) {
            Id id = f.getAnnotation(Id.class);
            if (StringUtils.isNotBlank(id.name())) {
                // 有值则直接取大写值
                fieldMap.put(field, JdbcHelper.switchByCase(id.name()));
            } else {
                fieldMap.put(field, JdbcUtil.toHump(field));
            }
            return;
        }
        // 被Column注解标注
        if (f.isAnnotationPresent(Column.class)) {
            Column column = f.getAnnotation(Column.class);
            if (StringUtils.isNotBlank(column.name())) {
                // 有值则直接取大写值
                fieldMap.put(field, JdbcHelper.switchByCase(column.name()));
            } else {
                fieldMap.put(field, JdbcUtil.toHump(field));
            }
            return;
        }
        if (f.isAnnotationPresent(LeftJoin.class)) {
            fieldMap.put(field, "refModelField");// 随便设值,不会用到
            fieldLeftJoinMap.put(field, f.getAnnotation(LeftJoin.class));
        }
    }

    // 操作符
    // equal,notEqual,contains,notContains,startsWith,notStartsWith,endsWith,notEndsWith,in,notIn,greaterThan,gtoet,lessThan,ltoet,isNull,isNotNull//

    /**
     * [等于XXX]
     */
    public Where equal(Object obj) {
        JdbcUtil.sensitiveValidate(obj);
        oprate("=", JdbcUtil.toJdbcValue(obj));
        return this;
    }

    /**
     * [不等于XXX]
     */
    public Where notEqual(Object obj) {
        JdbcUtil.sensitiveValidate(obj);
        oprate("!=", JdbcUtil.toJdbcValue(obj));
        return this;
    }

    /**
     * [包含XXX]
     */
    public Where contains(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("LIKE", "'%" + str + "%'");
        return this;
    }

    /**
     * [不包含XXX]
     */
    public Where notContains(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("NOT LIKE", "'%" + str + "%'");
        return this;
    }

    /**
     * [以XXX开始]
     */
    public Where startsWith(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("LIKE", "'" + str + "%'");
        return this;
    }

    /**
     * [不是以XXX开始]
     */
    public Where notStartsWith(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("NOT LIKE", "'" + str + "%'");
        return this;
    }

    /**
     * [以XXX结束]
     */
    public Where endsWith(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("LIKE", "'%" + str + "'");
        return this;
    }

    /**
     * [不是以XXX结束]
     */
    public Where notEndsWith(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("NOT LIKE", "'%" + str + "'");
        return this;
    }

    /**
     * [在XXX范围内]
     */
    public Where in(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("IN", "(" + str + ")");
        return this;
    }

    /**
     * [不在XXX范围内]
     */
    public Where notIn(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("NOT IN", "(" + str + ")");
        return this;
    }

    /**
     * [大于XXX]
     */
    public Where greaterThan(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate(">", str);
        return this;
    }

    /**
     * [(greaterThanOrEqualTo)大于等于XXX]
     */
    public Where gtoet(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate(">=", str);
        return this;
    }

    /**
     * [小于XXX]
     */
    public Where lessThan(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("<", str);
        return this;
    }

    /**
     * [(lessThanOrEqualTo)小于等于XXX]
     */
    public Where ltoet(String str) {
        JdbcUtil.sensitiveValidate(str);
        oprate("<=", str);
        return this;
    }

    /**
     * [为空]
     */
    public Where isNull() {
        oprate("IS NULL", "");
        return this;
    }

    /**
     * [不为空]
     */
    public Where isNotNull() {
        oprate("IS NOT NULL", "");
        return this;
    }

    /**
     * [操作符方法]
     */
    private void oprate(String oprate, String value) {
        if (nextIsOprate) {
            whereSb.append(" ").append(oprate).append(" ").append(value);
            fieldValueMap.put(curField, " " + oprate + " " + value);
            nextIsField = false;
            nextIsOprate = false;
            nextIsConnect = true;
            nextIsOrderBy = true;
            return;
        }
        throw new FrameworkException(OPRATE_ERROR_MSG);
    }

    // ------------------------------------------------连接符-----------------------------------------------//

    /**
     * [连接符and]
     */
    public Where and() {
        connect("AND");
        return this;
    }

    /**
     * [连接符or]
     */
    public Where or() {
        connect("OR");
        return this;
    }

    /**
     * [连接符方法]
     */
    private void connect(String connect) {
        if (nextIsConnect) {
            whereSb.append(" ").append(connect);
            nextIsField = true;
            nextIsOprate = false;
            nextIsConnect = false;
            nextIsOrderBy = false;
            return;
        }
        throw new FrameworkException(CONNECT_ERROR_MSG);
    }

    // ------------------------------------------------排序-----------------------------------------------//

    /**
     * [顺序排列]
     */
    public Where orderByAsc(String field) {
        orderBy(field, OrderByPolicy.ASC);
        return this;
    }

    /**
     * [逆序排列]
     */
    public Where orderByDesc(String field) {
        orderBy(field, OrderByPolicy.DESC);
        return this;
    }

    /**
     * [排序方法]
     */
    private void orderBy(String field, OrderByPolicy policy) {
        if (nextIsOrderBy) {
            if (!getOrderBy().equals("")) {
                orderBySb.append(",");
            }
            orderBySb.append(" ").append(field).append(" ").append(policy.toString());
            nextIsField = false;
            nextIsOprate = false;
            nextIsConnect = false;
            nextIsOrderBy = true;
            return;
        }
        throw new FrameworkException(ORDER_BY_ERROR_MSG);
    }

    /**
     * [实体类字段转换成数据库表字段]
     */
    private String fieldToColumn(String field) {
        // 判断是否能取到对应的字段
        Field f = ReflectUtil.getField(clazz, BaseEntity.class, field);
        Assert.notNull(f);
        // 被Id注解标注
        if (f.isAnnotationPresent(Id.class)) {
            Id id = f.getAnnotation(Id.class);
            if (StringUtils.isNotBlank(id.name())) {
                return JdbcHelper.switchByCase(id.name());
            }
        }
        // 被Column注解标注
        if (f.isAnnotationPresent(Column.class)) {
            Column column = f.getAnnotation(Column.class);
            if (StringUtils.isNotBlank(column.name())) {
                // 有值则直接取大写值
                return JdbcHelper.switchByCase(column.name());
            }
        }
        // 否则返回约定的格式
        return JdbcUtil.toHump(field);
    }


    public String toString() {
        String where = whereSb.toString();
        // 如果以" AND "开头,则删除" AND "
        if (where.startsWith(" AND ")) {
            where = where.substring(5);
        }
        return where;
    }

    public String getOrderBy() {
        return orderBySb.toString();
    }

    /**
     * [获取左连接查询条件]
     */
    public String getLeftJoinWhereCondition(boolean needLeftJoin, CoreTable table) {
        StringBuffer leftJoinWhereSb = new StringBuffer();
        for (String field : fieldList) {
            // 检查该字段是否被LeftJoin标记,如果是,加入到map中
            updateFieldLeftJoinMap(field);
        }

        // 遍历所有的查询字段
        for (String field : fieldMap.keySet()) {
            if (needLeftJoin) {
                leftJoinWhereSb.append(" AND ");
                if (fieldLeftJoinMap.keySet().contains(field)) {
                    // 该字段为左连接字段
                    leftJoinWhereSb.append(JdbcHelper.getReferenceTableAlias(fieldLeftJoinMap.get(field))).append(".");
                    leftJoinWhereSb.append(JdbcHelper.getReferenceValueFieldColumn(fieldLeftJoinMap.get(field)));
                    leftJoinWhereSb.append(fieldValueMap.get(field));
                } else {
                    // 该字段为自身字段
                    leftJoinWhereSb.append(table.getTableAlias()).append(".");
                    leftJoinWhereSb.append(fieldToColumn(field)).append(fieldValueMap.get(field));
                }
            } else {
                // 不考虑左连接字段
                CoreColumn column = table.getCoreColumnByFieldName(field);
                if (!column.isLeftJoinField()) {
                    leftJoinWhereSb.append(" AND ");
                    leftJoinWhereSb.append(fieldToColumn(field)).append(fieldValueMap.get(field));
                }
            }
        }
        String leftJoinWhere = leftJoinWhereSb.toString();
        // 如果以" AND "开头,则删除" AND "
        if (leftJoinWhere.startsWith(" AND ")) {
            leftJoinWhere = leftJoinWhere.substring(5);
        }
        return leftJoinWhere;
    }
}
