package org.chrisli.utils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.chrisli.utils.exception.FrameworkException;

import java.util.List;

/**
 * [Json工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class JsonUtil {
    /**
     * [将JavaBean对象转换成Json字符串,属性为空的字段不参与转化]
     */
    public static String beanToJson(Object object) {
        return JSONObject.toJSONString(object);
    }

    /**
     * [将JavaBean对象转换成Json字符串,所有字段参与转化]
     */
    public static String beanToJsonAll(Object object) {
        return JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue);
    }

    /**
     * [将Json字符串转换成JSONObject对象]
     */
    public static JSONObject jsonToJsonObject(String json) {
        return JSON.parseObject(json);
    }

    /**
     * [将Json字符串转换成JSONObject对象]
     */
    public static JSONObject jsonToJsonObject(String json, String exceptionMessage) {
        try {
            return JSON.parseObject(json);
        } catch (Exception e) {
            throw new FrameworkException(exceptionMessage);
        }
    }

    /**
     * [将Json字符串转换成JavaBean对象]
     */
    public static <T> T jsonToBean(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * [将Json字符串转换成JavaBean对象集合]
     */
    public static <T> List<T> jsonToBeanList(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }
}