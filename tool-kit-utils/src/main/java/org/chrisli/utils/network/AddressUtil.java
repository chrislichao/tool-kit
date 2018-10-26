package org.chrisli.utils.network;

import org.chrisli.utils.Assert;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * [地址工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class AddressUtil {

    private static final String IP_REGION_DB_FILE_PATH = "ip2region/ip2region.db";

    private AddressUtil() {
    }

    public static String getCityInfo(String ip) {
        Assert.isTrue(Util.isIpAddress(ip), "IP地址[" + ip + "]不合法!");
        URL url = AddressUtil.class.getClassLoader().getResource(IP_REGION_DB_FILE_PATH);
        Assert.notNull(url, "IP地区的配置文件路径不正确!");
        File file = new File(url.getFile());
        Assert.isTrue(file.exists(), "IP地区的配置文件不存在!");
        int algorithm = DbSearcher.BTREE_ALGORITHM;
        try {
            DbConfig config = new DbConfig();
            DbSearcher searcher = new DbSearcher(config, file.getPath());
            Method method = null;
            switch (algorithm) {
                case DbSearcher.BTREE_ALGORITHM:
                    method = searcher.getClass().getMethod("btreeSearch", String.class);
                    break;
                case DbSearcher.BINARY_ALGORITHM:
                    method = searcher.getClass().getMethod("binarySearch", String.class);
                    break;
                case DbSearcher.MEMORY_ALGORITYM:
                    method = searcher.getClass().getMethod("memorySearch", String.class);
                    break;
                default:
                    method = searcher.getClass().getMethod("memorySearch", String.class);
                    break;
            }
            DataBlock dataBlock = (DataBlock) method.invoke(searcher, ip);
            return dataBlock.getRegion();
        } catch (Exception e) {
            return "未知地址";
        }
    }
}

