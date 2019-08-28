package org.chrisli.log4jdbc.task;

import org.chrisli.log4jdbc.config.Log4jdbcConfig;
import org.chrisli.log4jdbc.utils.PropertyUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * [配置刷新任务]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class PropertyRefreshTask {
    /**
     * [开始执行刷新任务]
     *
     * @author Chris li[黎超]
     * @create [2017-04-12]
     */
    public static void start() {
        //创建定时读取这个配置文件的任务
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PropertyUtil.readProperty();
            }
        }, 0, Log4jdbcConfig.property_refresh_period * 1000);
    }
}