package org.chrisli.utils.math;

import org.chrisli.utils.Assert;
import org.chrisli.utils.exception.FrameworkException;

import java.math.BigDecimal;

/**
 * [数学相关的工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class MathUtil {
    /**
     * [加法]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static BigDecimal add(Number base, Number... valArray) {
        return handler(1, base, valArray);
    }

    /**
     * [减法]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static BigDecimal subtract(Number base, Number... valArray) {
        return handler(2, base, valArray);
    }

    /**
     * [乘法]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static BigDecimal multiply(Number base, Number... valArray) {
        return handler(3, base, valArray);
    }

    /**
     * [除法]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static BigDecimal divide(Number base, Number... valArray) {
        return handler(4, base, valArray);
    }

    /**
     * [处理逻辑]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static BigDecimal handler(int operate, Number base, Number... valArray) {
        BigDecimal x = new BigDecimal(base == null ? "0" : base.toString());
        BigDecimal y = null;
        for (Number val : valArray) {
            y = new BigDecimal(val == null ? "0" : val.toString());
            switch (operate) {
                case 1:
                    x = x.add(y);
                    break;
                case 2:
                    x = x.subtract(y);
                    break;
                case 3:
                    x = x.multiply(y);
                    break;
                case 4:
                    Assert.isTrue(y != null && y.intValue() != 0, "除数不允许为0!");
                    x = x.divide(y);
                    break;
                default:
                    // TODO 不做任何处理
            }
        }
        return x;
    }

    /**
     * [除法,四舍五入保留{scale}位小数]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static BigDecimal divideRoundHalfUp(Number base, Number multiplicand, int scale) {
        Assert.isTrue(multiplicand != null && multiplicand.intValue() != 0, "除数不允许为0!");
        BigDecimal x = new BigDecimal(base == null ? "0" : base.toString());
        BigDecimal y = new BigDecimal(multiplicand.toString());
        return x.divide(y, scale, BigDecimal.ROUND_HALF_UP);
    }
}
