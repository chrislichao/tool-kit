package org.chrisli.utils.kit;

import org.apache.commons.lang3.ArrayUtils;

/**
 * [随机码工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class RandomCodeUtil {
    /**
     * 数字
     */
    private static char[] numArray = {'2', '3', '4', '5', '6', '7', '8', '9'};
    /**
     * 小写字母
     */
    private static char[] lowerCaseArray = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    /**
     * 大写字母
     */
    private static char[] upperCaseArray = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    /**
     * 小写字母+大写字母
     */
    private static char[] alphabet = ArrayUtils.addAll(lowerCaseArray, upperCaseArray);
    /**
     * 小写字母+大写字母+数字
     */
    private static char[] allType = ArrayUtils.addAll(alphabet, numArray);

    public enum RandomPolicy {
        /**
         * 数字
         */
        NUM(numArray),
        /**
         * 小写字母
         */
        LOWER_CASE(lowerCaseArray),
        /**
         * 大写字母
         */
        UPPER_CASE(upperCaseArray),
        /**
         * 小写字母+大写字母
         */
        ALPHABET(alphabet),
        /**
         * 小写字母+大写字母+数字
         */
        ALL_TYPE(allType);

        private final char[] charArray;

        RandomPolicy(char[] charArray) {
            this.charArray = charArray;
        }

        public char[] getCharArray() {
            return charArray;
        }
    }

    /**
     * [生成随机码]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static String getRandomCode(int num, RandomPolicy policy) {
        char[] mapTable = policy.getCharArray();
        // 取随机产生的认证码
        StringBuffer buffer = new StringBuffer();
        // 循环生成每一个字符
        for (int i = 0; i < num; ++i) {
            buffer.append(mapTable[(int) (mapTable.length * Math.random())]);
        }
        return buffer.toString();
    }
}
