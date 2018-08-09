package org.chrisli.utils.test.concurrency;

/**
 * [并发业务回调]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public interface Callback {
    /**
     * [需经过并发测试的业务逻辑]
     */
    public void execute();
}
