package org.chrisli.utils.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.chrisli.utils.test.concurrency.Callback;

import java.util.concurrent.CountDownLatch;

/**
 * [并发测试工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class ConcurrencyTestUtil {

    private static Logger logger = LoggerFactory.getLogger(ConcurrencyTestUtil.class);

    private Callback callBack;

    /**
     * [并发测试的工具类构造方法]
     */
    public ConcurrencyTestUtil(Callback callBack) {
        this.callBack = callBack;
    }

    /**
     * [执行并发测试]
     */
    public void doTest(int concurrencyCount) {
        final CountDownLatch begin = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(concurrencyCount);
        for (int i = 0; i < concurrencyCount; i++) {
            new Thread() {

                public void run() {
                    try {
                        logger.info(Thread.currentThread() + "准备就绪!");
                        begin.await();
                        callBack.execute();
                    } catch (Exception e) {
                        logger.error("并发出现异常!", e);
                    } finally {
                        end.countDown();
                    }
                }
            }.start();
        }
        try {
            Thread.sleep(2000);
            logger.info("######################## 开始并发啦!");
            begin.countDown();
            end.await();
            logger.info("$$$$$$$$$$$$$$$$$$$$$$$$ 并发结束啦!");
        } catch (Exception e) {
            logger.error("并发出现异常!", e);
        }
    }
}
