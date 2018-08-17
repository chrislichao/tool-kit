package org.chrisli.utils.reflect;

import org.apache.commons.lang3.ArrayUtils;
import org.chrisli.utils.Assert;
import org.chrisli.utils.exception.FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * [反射工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class ReflectUtil {

    private static Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    /**
     * [利用反射获取指定对象的指定属性]
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        Object result = null;
        Field field = ReflectUtil.getField(obj.getClass(), Object.class, fieldName);
        try {
            field.setAccessible(true);
            result = field.get(obj);
        } catch (Exception e) {
            throw new FrameworkException(String.format("获取对象字段[%s]值出错!", fieldName), e);
        }
        return result;
    }

    /**
     * [利用反射获取指定对象里面的指定属性]
     */
    public static Field getField(Class<?> startClazz, Class<?> endClazz, String fieldName) {
        Field field = null;
        for (Class<?> clazz = startClazz; clazz != endClazz; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                logger.debug(String.format("Can not find field[%s] in class[%s],will find in it's super class.", fieldName, clazz.getName()));
            }
        }
        Assert.notNull(field, String.format("Can not find field[%s] in class[%s] and it's super classs, please check your config!", fieldName,
                startClazz.getName()));
        return field;
    }

    /**
     * [利用反射设置指定对象的指定属性为指定的值]
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        Field field = ReflectUtil.getField(obj.getClass(), Object.class, fieldName);
        try {
            field.setAccessible(true);
            field.set(obj, fieldValue);
        } catch (Exception e) {
            throw new FrameworkException(String.format("设置对象字段[%s]值出错!", fieldName), e);
        }
    }

    /**
     * [获取类中所有字段(自身找完了找父类),直到父类等于<code>java.lang.Object</code>为止(不包含父类)]
     */
    public static Field[] getAllFields(Class<?> startClass) {
        return getAllFields(startClass, Object.class);
    }

    /**
     * [获取类中所有字段(自身找完了找父类),直到父类等于endClazz为止(不包含父类)]
     */
    public static Field[] getAllFields(Class<?> startClass, Class<?> endClass) {
        // 如果该类的父类为endClass,返回该类所有字段
        if (startClass.getSuperclass() == endClass) {
            return startClass.getDeclaredFields();
        }
        // 否则返回该类所有字段再找其父类的所有字段
        return (Field[]) ArrayUtils.addAll(startClass.getDeclaredFields(), getAllFields(startClass.getSuperclass(), endClass));
    }

    /**
     * [获取类中所有方法(自身找完了找父类),直到父类等于<code>java.lang.Object</code>为止(包含父类)]
     */
    public static Method[] getAllMethods(Class<?> startClass) {
        return getAllMethods(startClass, Object.class);
    }

    /**
     * [获取类中所有方法(自身找完了找父类),直到父类等于endClazz为止(包含父类)]
     */
    public static Method[] getAllMethods(Class<?> startClass, Class<?> endClass) {
        // 如果该类的父类为Object,返回该类所有方法
        if (startClass.getSuperclass() == Object.class) {
            return startClass.getDeclaredMethods();
        }
        // 如果该类的父类为endClass,返回该类和其父类的所有方法
        if (startClass.getSuperclass() == endClass) {
            return (Method[]) ArrayUtils.addAll(startClass.getDeclaredMethods(), endClass.getDeclaredMethods());
        }
        // 否则返回该类所有方法再找其父类的所有字段
        return (Method[]) ArrayUtils.addAll(startClass.getDeclaredMethods(), getAllMethods(startClass.getSuperclass(), endClass));
    }

    /**
     * [利用反射获取指定对象里面的指定方法]
     */
    public static Method getMethod(Class<?> startClazz, Class<?> endClazz, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = startClazz; clazz != endClazz; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                break;
            } catch (NoSuchMethodException e) {
                logger.debug(String.format("Can not find method[%s] in class[%s],will find in it's super class.", methodName, clazz.getName()));
            }
        }
        Assert.notNull(method, String.format("Can not find method[%s] in class[%s] and it's super classs, please check your config!", methodName,
                startClazz.getName()));
        return method;
    }

    /**
     * [将对象转换为Map,仅保留类型为基础数据类型的字段,且忽略值为空<code>null</code>的字段]
     */
    public static <T> Map<String, Object> beanToMap(T t) {
        Map<String, Object> valueMap = new HashMap<String, Object>();
        Field[] allFields = getAllFields(t.getClass());
        Object fieldValue = null;
        for (Field field : allFields) {
            if (isMatchedFiledType(field.getType())) {
                fieldValue = getFieldValue(t, field.getName());
                if (fieldValue != null) {
                    valueMap.put(field.getName(), fieldValue);
                }
            }
        }
        return valueMap;
    }

    /**
     * [验证字段的返回类型是否是约定的处理类型]
     */
    private static boolean isMatchedFiledType(Class<?> type) {
        return String.class == type || Integer.class == type || Long.class == type || Double.class == type || Date.class == type;
    }

    /**
     * [将结果集<Map>转换成指定对象,字段名对应map中的key]
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz) {
        return mapToBean(map, clazz, false);
    }

    /**
     * [将结果集<Map>转换成指定对象,字段名转大写后对应map中的key]
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz, boolean fieldUpperMatch) {
        try {
            T t = clazz.newInstance();
            Field[] fields = getAllFields(clazz, Object.class);
            for (Field field : fields) {
                String key = fieldUpperMatch ? field.getName().toUpperCase() : field.getName();
                Object value = map.get(key);
                value = switchByFieldType(field, value);
                setFieldValue(t, field.getName(), value);
            }
            return t;
        } catch (Exception e) {
            throw new FrameworkException(e.getMessage());
        }
    }

    /**
     * [根据字段类型转换值类型]
     */
    private static Object switchByFieldType(Field field, Object value) {
        if (value == null || field.getType() == value.getClass()) {
            return value;
        }
        if (field.getType() == String.class) {
            return String.valueOf(value);
        }
        if (field.getType() == Long.class) {
            return Long.valueOf(value.toString()).longValue();
        }
        if (field.getType() == Integer.class) {
            return Integer.valueOf(value.toString()).intValue();
        }
        if (field.getType() == Double.class) {
            return Double.valueOf(value.toString()).doubleValue();
        }
        return (Date) value;
    }
}