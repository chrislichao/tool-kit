package org.chrisli.utils.date;

import org.chrisli.utils.Assert;
import org.chrisli.utils.exception.FrameworkException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * [日期工具类]
 *
 * @author Chris li[黎超]
 * @create [2017-04-12]
 */
public class DateUtil {
    /**
     * [默认的日期格式]
     */
    private static final String DEFAULT_DATE_STR_FORMAT = "yyyy-MM-dd";
    /**
     * [默认的日期时间格式]
     */
    private static final String DEFAULT_DATETIME_STR_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * [获取指定格式的日期格式类]
     */
    private static SimpleDateFormat getDateFormat(String pattern) {
        Assert.notBlank(pattern);
        return new SimpleDateFormat(pattern);
    }

    /**
     * [获取指定时间{millis}的秒数,{millis}是毫秒级别的数]
     */
    private static Integer getSeconds(long millis) {
        return Long.valueOf(millis / 1000L).intValue();
    }

    /**
     * [获取当前时间的秒数]
     */
    public static Integer getCurrentSeconds() {
        return getSeconds(System.currentTimeMillis());
    }

    /**
     * [将秒数{seconds}转为日期]
     */
    public static Date secondsToDate(Integer seconds) {
        Assert.notNull(seconds);
        return millisToDate(Long.valueOf(seconds * 1000L));
    }

    /**
     * [将秒数{seconds}转为日期,格式为{pattern}]
     */
    public static Date secondsToDate(Integer seconds, String pattern) {
        Assert.notNull(seconds);
        return millisToDate(Long.valueOf(seconds * 1000L), pattern);
    }

    /**
     * [将毫秒数{millis}转为日期]
     */
    public static Date millisToDate(Long millis) {
        Assert.notNull(millis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    /**
     * [将毫秒数{millis}转为日期,格式为{pattern}]
     */
    public static Date millisToDate(Long millis, String pattern) {
        Assert.notNull(millis);
        return dateStrToDate(dateToDateStr(millisToDate(millis), pattern), pattern);
    }

    /**
     * [将秒数{seconds}转为日期字符串,格式为{pattern}]
     */
    public static String secondsToDateStr(Integer seconds, String pattern) {
        return dateToDateStr(secondsToDate(seconds), pattern);
    }

    /**
     * [将秒数{seconds}转为日期字符串,格式为"yyyy-MM-dd"]
     */
    public static String secondsToDateStr(Integer seconds) {
        return secondsToDateStr(seconds, DEFAULT_DATE_STR_FORMAT);
    }

    /**
     * [将秒数{seconds}转为日期时间字符串,格式为"yyyy-MM-dd HH:mm:ss"]
     */
    public static String secondsToDatetimeStr(Integer seconds) {
        return secondsToDateStr(seconds, DEFAULT_DATETIME_STR_FORMAT);
    }

    /**
     * [将毫秒数{millis}转为日期字符串,格式为{pattern}]
     */
    public static String millisToDateStr(Long millis, String pattern) {
        return dateToDateStr(millisToDate(millis, pattern), pattern);
    }

    /**
     * [将毫秒数{millis}转为日期字符串,格式为"yyyy-MM-dd"]
     */
    public static String millisToDateStr(Long millis) {
        return millisToDateStr(millis, DEFAULT_DATE_STR_FORMAT);
    }

    /**
     * [将毫秒数{millis}转为日期时间字符串,格式为"yyyy-MM-dd HH:mm:ss"]
     */
    public static String millisToDatetimeStr(Long millis) {
        return millisToDateStr(millis, DEFAULT_DATETIME_STR_FORMAT);
    }

    /**
     * [将当前日期转为日期字符串,格式为"yyyy-MM-dd"]
     */
    public static String getCurrentDateStr() {
        return dateToDateStr(new Date());
    }

    /**
     * [将日期{date}转为日期时间字符串,格式为"yyyy-MM-dd HH:mm:ss"]
     */
    public static String getCurrentDatetimeStr() {
        return dateToDatetimeStr(new Date());
    }

    /**
     * [将日期{date}转为日期字符串,格式为"yyyy-MM-dd"]
     */
    public static String dateToDateStr(Date date) {
        return dateToDateStr(date, DEFAULT_DATE_STR_FORMAT);
    }

    /**
     * [将日期{date}转为日期时间字符串,格式为"yyyy-MM-dd HH:mm:ss"]
     */
    public static String dateToDatetimeStr(Date date) {
        return dateToDateStr(date, DEFAULT_DATETIME_STR_FORMAT);
    }

    /**
     * [将日期{date}转为日期字符串,格式为{pattern}]
     */
    public static String dateToDateStr(Date date, String pattern) {
        Assert.notNull(date);
        return getDateFormat(pattern).format(date);
    }

    /**
     * [将日期{date}转为秒]
     */
    public static Integer dateToSeconds(Date date) {
        Assert.notNull(date);
        return getSeconds(date.getTime());
    }

    /**
     * [将格式为"yyyy-MM-dd"的日期字符串{dateStr}转为日期]
     */
    public static Date dateStrToDate(String dateStr) {
        return dateStrToDate(dateStr, DEFAULT_DATE_STR_FORMAT);
    }

    /**
     * [将格式为"yyyy-MM-dd HH:mm:ss"的日期字符串{dateStr}转为日期]
     */
    public static Date datetimeStrToDate(String dateStr) {
        return dateStrToDate(dateStr, DEFAULT_DATETIME_STR_FORMAT);
    }

    /**
     * [将格式为{pattern}的日期字符串{dateStr}转为日期]
     */
    public static Date dateStrToDate(String dateStr, String pattern) {
        Assert.notBlank(dateStr);
        Assert.notBlank(pattern);
        try {
            return getDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            throw new FrameworkException(String.format("日期字符串[%s]和日期格式[%s]不匹配!", dateStr, pattern));
        }
    }

    /**
     * [将格式为{pattern}的日期字符串{dateStr}转为日期]
     */
    public static Date dateStrToDate(String dateStr, String pattern, String exceptionMessage) {
        Assert.notBlank(dateStr);
        Assert.notBlank(pattern);
        try {
            return getDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            throw new FrameworkException(exceptionMessage);
        }
    }

    /**
     * [将格式为"yyyy-MM-dd"的日期字符串{dateStr}转为秒]
     */
    public static Integer dateStrToSeconds(String dateStr) {
        return dateStrToSeconds(dateStr, DEFAULT_DATE_STR_FORMAT);
    }

    /**
     * [将格式为"yyyy-MM-dd HH:mm:ss"的日期字符串{dateStr}转为秒]
     */
    public static Integer datetimeStrToSeconds(String dateStr) {
        return dateStrToSeconds(dateStr, DEFAULT_DATETIME_STR_FORMAT);
    }

    /**
     * [将格式为{pattern}的日期字符串{dateStr}转为秒]
     */
    public static Integer dateStrToSeconds(String dateStr, String pattern) {
        return dateToSeconds(dateStrToDate(dateStr, pattern));
    }

    /**
     * [日期字符串{dateStr}增加{amount}天返回,格式为"yyyy-MM-dd"]
     */
    public static String addDay(String dateStr, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateStrToDate(dateStr));
        calendar.add(Calendar.DATE, amount);
        return getDateFormat(DEFAULT_DATE_STR_FORMAT).format(calendar.getTime());
    }
}