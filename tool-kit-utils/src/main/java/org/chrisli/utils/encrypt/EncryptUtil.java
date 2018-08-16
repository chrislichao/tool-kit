package org.chrisli.utils.encrypt;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;

/**
 * [加解密工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class EncryptUtil {
    /**
     * [SHA1加密]
     */
    public static final String getSha1(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return decode(str, "SHA1");
    }

    /**
     * [MD5加密]
     */
    public static final String getMd5(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        return decode(str, "MD5");
    }

    /**
     * [加密的核心方法]
     */
    private static final String decode(String str, String decodeType) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance(decodeType);
            mdTemp.update(str.getBytes("UTF-8"));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }
}
