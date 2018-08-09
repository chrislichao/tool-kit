package org.chrisli.mybatis.helper;

import org.apache.commons.lang.StringUtils;
import org.chrisli.mybatis.base.BaseEntity;
import org.chrisli.mybatis.annotation.Column;
import org.chrisli.mybatis.annotation.Id;
import org.chrisli.mybatis.annotation.LeftJoin;
import org.chrisli.mybatis.annotation.Table;
import org.chrisli.utils.Assert;
import org.chrisli.utils.exception.FrameworkException;
import org.chrisli.utils.jdbc.JdbcUtil;
import org.chrisli.utils.reflect.ReflectUtil;
import java.lang.reflect.Field;

/**
 * [Jdbc相关工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class JdbcHelper {
    /**
     * 字符串转换为大写
     */
    private static boolean toUpper = true;
    /**
     * 表别名前缀
     */
    private static final String TABLE_ALIAS_PREFIX = "CL_";

    public static String switchByCase(String value) {
        return toUpper ? value.toUpperCase() : value.toLowerCase();
    }

    /**
     * [根据用户设置DataBase大小写策略获取表的别名]
     */
    public static String getTableAlias(String tableName) {
        return switchByCase(TABLE_ALIAS_PREFIX + tableName);
    }

    /**
     * [获取关联表的表名]
     */
    public static String getReferenceTableName(Class<?> clazz) {
        Assert.isAnnotationPresent(Table.class, clazz, String.format("Class[%s] is not presented by annotation[%s].", clazz.getName(), Table.class
                .getName()));
        // 设置Table表名
        Table table = clazz.getAnnotation(Table.class);
        String tableName = table.name();
        if (StringUtils.isBlank(tableName)) {
            tableName = JdbcUtil.toHump(clazz.getSimpleName());
        }
        return switchByCase(tableName);
    }

    /**
     * [获取关联表的别名]
     */
    public static String getReferenceTableAlias(LeftJoin leftJoin) {
        return getTableAlias(getReferenceTableName(leftJoin.refModel())) + "_" + leftJoin.group();
    }

    /**
     * [获取关联表中取值字段的列名]
     */
    public static String getReferenceValueFieldColumn(LeftJoin leftJoin) {
        Field valField = ReflectUtil.getField(leftJoin.refModel(), BaseEntity.class, leftJoin.refValField());
        return getColumnName(valField, leftJoin.refValField(), leftJoin.refModel().getName());
    }

    /**
     * [获取本表中的on列名]
     */
    public static String getSelfOnFieldColumn(Class<?> selfClazz, LeftJoin leftJoin){
        Field onField = ReflectUtil.getField(selfClazz, BaseEntity.class, leftJoin.selfOnField());
        return getColumnName(onField, leftJoin.selfOnField(), leftJoin.refModel().getName());
    }

    /**
     * [获取关联表中的on列名]
     */
    public static String getReferenceOnFieldColumn(LeftJoin leftJoin){
        Field onField = ReflectUtil.getField(leftJoin.refModel(), BaseEntity.class, leftJoin.refOnField());
        return getColumnName(onField, leftJoin.refOnField(), leftJoin.refModel().getName());
    }

    /**
     * [获取对应的列名]
     */
    private static String getColumnName(Field valField, String refModelClass, String refValFieldName) {
        if (valField.isAnnotationPresent(Id.class)) {
            Id id = valField.getAnnotation(Id.class);
            String columnName = JdbcUtil.toHump(valField.getName());
            if (StringUtils.isNotBlank(id.name())) {
                columnName = switchByCase(id.name());
            }
            return columnName;
        }
        if (valField.isAnnotationPresent(Column.class)) {
            Column column = valField.getAnnotation(Column.class);
            // 默认表列名为字段名大写
            String columnName = JdbcUtil.toHump(valField.getName());
            if (StringUtils.isNotBlank(column.name())) {
                // 有值则直接取大写值
                columnName = switchByCase(column.name());
            }
            return columnName;
        }
        // 如果对应的字段未被Id或Column注解标注,则抛异常
        String errorMsg = String.format("Error!Can not find column by ModelClass[%s] and Field[%s].", refModelClass, refValFieldName);
        throw new FrameworkException(errorMsg);
    }
}
