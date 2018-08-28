package org.chrisli.utils.kit;

import java.util.HashMap;
import java.util.Map;

/**
 * [map工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class MapUtil {
    /**
     * [数组转换成map]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static <K, V> Map<K, V> toMap(Object... mapping) {
        Map<K, V> map = new HashMap<K, V>();
        for (int i = 0; i < mapping.length; i += 2) {
            map.put((K) mapping[i], (V) mapping[i + 1]);
        }
        return map;
    }
}