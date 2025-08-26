package com.allo.utils;

import android.graphics.Typeface;
import android.text.TextUtils;

import androidx.core.content.res.ResourcesCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 时间处理工具
 * Created by Nereo on 2015/4/8.
 */
public class TimeUtils {
    public static final long MILLIS_OF_SECOND = 1000;
    public static final long MILLIS_OF_MINUTE = 60 * 1000;
    public static final long MILLIS_OF_ONE_HOUR = 60 * MILLIS_OF_MINUTE;
    public static final long MILLIS_OF_DAY = 24 * MILLIS_OF_ONE_HOUR;

    private static final TimeZone TIME_ZONE = TimeZone.getDefault();
    private static final int OFFSET = TIME_ZONE.getOffset(System.currentTimeMillis());

    public static final String FORMAT_1 = "yyyyMMdd";

    public static final String FORMAT_2 = "yyyy/MM/dd";

    public static final String FORMAT_3 = "yyyy-MM-dd";

    public static final String FORMAT_4 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_5 = "yyyy-MM-dd'T'HH:mm:ss";

    private TimeUtils() {
    }

    public static String timeFormat(long timeMillis, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return format.format(new Date(timeMillis));
    }

    public static String formatDateByChinese(long time) {
        return timeFormat(time, "yyyy年MM月dd日");
    }

    public static String formatDateByHyphen(long time) {
        if (isThisYear(time)) {
            return timeFormat(time, "MM/dd");
        }
        return timeFormat(time, "yyyy/MM/dd");
    }

    public static String formatShippingDate(long time) {
        return timeFormat(time, "yyyy年MM月dd日 HH:mm:ss");
    }

    public static String formatDateByUS(long time) {
        return timeFormat(time, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatDateByUS2(long time) {
        return timeFormat(time, "yyyy-MM-dd");
    }

    /**
     * 不显示今年的年份
     */
    public static String formatDateWithoutThisYear(long timestamp) {
        if (isThisYear(timestamp)) {
            return timeFormat(timestamp, "MM-dd HH:mm");
        }
        return timeFormat(timestamp, "yyyy-MM-dd HH:mm");
    }

    /**
     * 时间格式化 yyyy-MM-dd HH:mm
     */
    public static String formatDate(long timestamp) {
        return timeFormat(timestamp, "yyyy-MM-dd HH:mm");
    }

    /**
     * 三种日期格式化
     * 大于当前年 格式 yyyy/MM/dd
     * 大于当前天 格式 MM/dd HH:mm
     * 当前天    格式 HH:mm
     */
    public static String formatDate3Formats(long timestamp) {
        if (isToday(timestamp)) {
            return timeFormat(timestamp, "HH:mm");
        } else if (isThisYear(timestamp)) {
            return timeFormat(timestamp, "MM/dd HH:mm");
        }
        return timeFormat(timestamp, "yyyy/MM/dd");
    }

    /**
     * 三种日期格式化
     * 大于当前年 格式 yyyy/MM/dd HH:mm
     * 大于当前天 格式 MM/dd HH:mm
     * 当前天    格式 HH:mm
     */
    public static String formatDate4Formats(long timestamp) {
        if (isToday(timestamp)) {
            return timeFormat(timestamp, "HH:mm");
        } else if (isThisYear(timestamp)) {
            return timeFormat(timestamp, "MM-dd HH:mm");
        }
        return timeFormat(timestamp, "yyyy-MM-dd HH:mm");
    }

    /**
     * 是否是今年
     */
    public static boolean isThisYear(long timestamp) {
        final Calendar current = Calendar.getInstance();
        final Calendar time = Calendar.getInstance();
        time.setTime(new Date(timestamp));
        return current.get(Calendar.YEAR) == time.get(Calendar.YEAR);
    }

    public static String getLeaveTime(long leaveTime) {
        StringBuilder builder = new StringBuilder();
        if (leaveTime > 0) {
            long day = leaveTime / MILLIS_OF_DAY;
            long hour = (leaveTime - (day * MILLIS_OF_DAY)) / MILLIS_OF_ONE_HOUR;
            long minute = (leaveTime - (day * MILLIS_OF_DAY) - (hour * MILLIS_OF_ONE_HOUR)) / MILLIS_OF_MINUTE;

            if (day > 0) {
                builder.append(day).append("天");
            }

            if (hour > 0 || builder.length() > 0) {
                builder.append(hour).append("小时");
            }

            if (minute > 0) {
                builder.append(minute).append("分钟");
            }
        }
        return builder.toString();
    }

    public static String getLeaveTimeWithSecond(long leaveTime) {
        StringBuilder builder = new StringBuilder();
        if (leaveTime > 0) {
            long day = leaveTime / MILLIS_OF_DAY;
            long hour = (leaveTime - (day * MILLIS_OF_DAY)) / (1000 * 60 * 60);
            long minute = (leaveTime - (day * MILLIS_OF_DAY) - (hour * 1000 * 60 * 60)) / (1000 * 60);
            long second = (leaveTime - (day * MILLIS_OF_DAY) - (hour * 1000 * 60 * 60) - (minute * 1000 * 60)) / 1000;

            if (day > 0) {
                builder.append(day).append("天");
            }

            if (hour > 0 || builder.length() > 0) {
                builder.append(hour).append("小时");
            }

            if (minute > 0) {
                builder.append(minute).append("分钟");
            }
            builder.append(second).append("秒");
        }
        return builder.toString();
    }

    public static String leaveSeconds(int seconds){
        StringBuffer times = new StringBuffer();

        if (seconds%3600 > 0 && seconds > 60){
            times.append((seconds%3600)/60).append("分");
        }

        if (seconds%60 > 0){
            times.append(seconds%60).append("秒");
        }
        return times.toString();

    }

    /**
     * 大于1天，返回xx天，否则返回hh:mm:ss
     * dayStr: "天"字符串
     */
    public static String getLeaveTimeWithSecond2(long leaveTime, String dayStr) {

        if (leaveTime > 0) {
            long day = leaveTime / MILLIS_OF_DAY;
            long hour = (leaveTime - (day * MILLIS_OF_DAY)) / (1000 * 60 * 60);
            long minute = (leaveTime - (day * MILLIS_OF_DAY) - (hour * 1000 * 60 * 60)) / (1000 * 60);
            long second = (leaveTime - (day * MILLIS_OF_DAY) - (hour * 1000 * 60 * 60) - (minute * 1000 * 60)) / 1000;

            StringBuilder builder = new StringBuilder();
            /// 大于1天直接返回天
            if (day > 0) {
                builder.append(day).append(dayStr);
                return builder.toString();
            }

            builder.append(String.format("%02d", hour)).append(":");
            builder.append(String.format("%02d", minute)).append(":");
            builder.append(String.format("%02d", second));
            return builder.toString();
        }
        return "";
    }

    public static long getTimeStamp(String s, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.CHINA);
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        try {
            return format.parse(s).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean day4Num(long timestamp, int num) {
        return day4Num(System.currentTimeMillis(), timestamp, num);
    }

    public static boolean day4Num(long timeone, long timetwo, int num) {
        final Calendar day = Calendar.getInstance();
        day.setTime(new Date(timeone));
        day.add(Calendar.DAY_OF_YEAR, num);

        final Calendar comparedDate = Calendar.getInstance();
        comparedDate.setTime(new Date(timetwo));

        return day.get(Calendar.YEAR) == comparedDate.get(Calendar.YEAR)
                && day.get(Calendar.DAY_OF_YEAR) == comparedDate.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isToday(long timestamp) {
        return isSameDay(timestamp,System.currentTimeMillis());
    }

    public static boolean isYesterday(long timestamp) {
        return day4Num(timestamp, -1);
    }

    public static boolean isTheDayBeforeYesterday(long timestamp) {
        return day4Num(timestamp, -2);
    }

    public static boolean withinTheSameDay(long one, long two) {
        return day4Num(one, two, 0);
    }

    public static String getRelatedDayFormat(long timestamp) {
        if (isToday(timestamp)) {
            return "今天";
        } else if (isYesterday(timestamp)) {
            return "昨天";
        } else if (isTheDayBeforeYesterday(timestamp)) {
            return "前天";
        } else {
            return formatDateByHyphen(timestamp);
        }
    }

    public static String getShockingTimeFormat(long timestamp) {
        long time = System.currentTimeMillis() - timestamp;
        if (time < MILLIS_OF_MINUTE) {
            return Utils.getApp().getString(R.string.just_recently);
        } else {
            if (isToday(timestamp)){
                return timeFormat(timestamp,"HH:mm");
            }else {
                return formatDateByHyphen(timestamp);
            }
        }
    }

    /**
     * 当前月有多少天
     */
    public static int getDaysInMonth(int month) {
        final Calendar c = Calendar.getInstance();
        // start from 0
        c.set(c.get(Calendar.YEAR), month, 1);
        final int days = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return days;
    }

    /**
     * 从时间戳中返回时间
     *
     * @param timetamp
     * @return
     */
    public static int getHourOfDayFromTimestamp(final long timetamp) {
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timetamp);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 判断是否过期
     *
     * @param expiresAt 结束时间
     * @return true:过期
     */
    public static boolean isExpired(long expiresAt) {
        return expiresAt - System.currentTimeMillis() <= 0;
    }

    public static int getLeaveDays(long time) {
        return (int) (time / MILLIS_OF_DAY);
    }

    public static int getLeaveHours(long time) {
        return (int) (time / MILLIS_OF_ONE_HOUR);
    }

    /**
     * 超过24小时返回天，小时，分钟，否则返回小时，分钟，秒。
     *
     * @param leaveTime
     * @return
     */
    public static List<String> getCountDownTime(long leaveTime) {
        ArrayList<String> list = new ArrayList<>();
        long day = leaveTime / MILLIS_OF_DAY;
        if (day > 0) {
            long hour = leaveTime % MILLIS_OF_DAY / MILLIS_OF_ONE_HOUR;
            long minute = leaveTime % MILLIS_OF_ONE_HOUR / (1000 * 60);
            list.add(String.format(Locale.getDefault(), "%02dd", day));
            list.add(String.format(Locale.getDefault(), "%02dh", hour));
            list.add(String.format(Locale.getDefault(), "%02dm", minute));
        } else {
            long hour = (int) (leaveTime / MILLIS_OF_ONE_HOUR);
            long minute = (leaveTime - (hour * MILLIS_OF_ONE_HOUR)) / (1000 * 60);
            long second = (leaveTime - (hour * 1000 * 60 * 60) - (minute * 1000 * 60)) / 1000;

            list.add(String.format(Locale.getDefault(), "%02dh", hour));
            list.add(String.format(Locale.getDefault(), "%02dm", minute));
            list.add(String.format(Locale.getDefault(), "%02ds", second));
        }

        return list;
    }

    public static boolean isSameDay(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return Math.abs(interval)  <  MILLIS_OF_DAY && toDay(ms1) == toDay(ms2);
    }

    private static long toDay(long millis) {
        return (millis + OFFSET) / MILLIS_OF_DAY;
    }

    /**
     * 秒格式化时间 HH:mm:ss
     * @param time
     * @return
     */
    public static String formatTime(Long time)  {

        long hour = time / 60 / 60;
        long minute = time / 60;
        long second = time % 60;


        StringBuilder timeStr = new StringBuilder();
        if (hour > 0) {
            timeStr.append(hour).append(":");
        }

        if (minute < 10) {
            timeStr.append("0").append(minute);
        } else {
            timeStr.append(minute);
        }
        timeStr.append(":");

        if (second < 10) {
            timeStr.append("0").append(second);
        } else {
            timeStr.append(second);
        }

        return timeStr.toString();
    }

    /**
     * 日期格式化
     * @param d 输入字符串
     * @param orgFormat 原始日期格式
     * @param convertFormat 转换日期格式
     * @return
     */
    public static String formatDate(String d, String orgFormat, String convertFormat) {
        try {
            Date date = new SimpleDateFormat(orgFormat).parse(d);
            String now = new SimpleDateFormat(convertFormat).format(date);
            return now;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取今天日期
     * @return
     */
    public static String getToday() {
        return timeFormat(System.currentTimeMillis(), "yyyy-MM-dd");
    }


    /**
     *  最近消息为昨日需显示：昨日时间，精确到分，例如：昨日 19:23
     *  超过2天且小于7天的显示星期即可，例如：周日
     *  历史消息大于7天且在本年的显示月日，例如：3-6
     *  非本年的历史消息需显示年月日，例如：2020-1-2
     * @return
     */
    public static CharSequence formatSpecialDate(long timestamp) {
        if (timestamp < 1000000000000L) {
            timestamp *= 1000;
        }
        /// 今天
        if (isToday(timestamp)) {
            return timeFormat(timestamp, "HH:mm");
        }
        /// 昨日
        else if (isYesterday(timestamp)) {
            SpanUtils spanText = SpanUtils.Companion.with(null).append(Utils.getApp().getString(R.string.yesterday));
            try {
                if (!LanguageUtils.isZh()) {
                    int fontResId = Utils.getApp().getResources().getIdentifier("ukijtor", "font", Utils.getApp().getPackageName());
                    Typeface tf = ResourcesCompat.getFont(Utils.getApp(), fontResId);
                    spanText.setTypeface(tf);
                }
            } catch (Exception e) {}

            return spanText.append(" ").append(timeFormat(timestamp, "HH:mm")).create();
        }
        /// 7天内
        else if (System.currentTimeMillis() - timestamp <= 6 * 24 * 60 * 60 * 1000) {
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timestamp);
            SpanUtils spanText = SpanUtils.Companion.with(null);
            int week = c.get(Calendar.DAY_OF_WEEK);
            switch (week) {
                case Calendar.SUNDAY:
                    spanText.append(Utils.getApp().getString(R.string.sunday));
                    break;
                case Calendar.MONDAY:
                    spanText.append(Utils.getApp().getString(R.string.monday));
                    break;
                case Calendar.TUESDAY:
                    spanText.append(Utils.getApp().getString(R.string.tuesday));
                    break;
                case Calendar.WEDNESDAY:
                    spanText.append(Utils.getApp().getString(R.string.wednesday));
                    break;
                case Calendar.THURSDAY:
                    spanText.append(Utils.getApp().getString(R.string.thursday));
                    break;
                case Calendar.FRIDAY:
                    spanText.append(Utils.getApp().getString(R.string.friday));
                    break;
                case Calendar.SATURDAY:
                    spanText.append(Utils.getApp().getString(R.string.saturday));
                    break;
            }

            try {
                if (!LanguageUtils.isZh()) {
                    int fontResId = Utils.getApp().getResources().getIdentifier("ukijtor", "font", Utils.getApp().getPackageName());
                    Typeface tf = ResourcesCompat.getFont(Utils.getApp(), fontResId);
                    spanText.setTypeface(tf);
                }
            } catch (Exception e) {}
            return spanText.create();
        }
        /// 本年内
        else if (isThisYear(timestamp)) {
            return timeFormat(timestamp, "MM-dd");
        }
        return timeFormat(timestamp, "yyyy-MM-dd");
    }



    /**
     *  昨日的显示为：MM-DD HH:MM 例如：09-21 19:23
     *  超过2天且小于7天的显示为：MM-DD，例如：09-21
     *  历史消息大于7天且在本年的显示月日，例如：3-6
     *  非本年的历史消息需显示年月日，例如：2020-1-2
     * @return
     */
    public static CharSequence formatSpecialDate2(long timestamp) {
        if (timestamp < 1000000000000L) {
            timestamp *= 1000;
        }
        /// 今天
        if (isToday(timestamp)) {
            return timeFormat(timestamp, "HH:mm");
        }
        /// 昨日
        else if (isYesterday(timestamp)) {
            return timeFormat(timestamp, "MM-dd HH:mm");
        }
        /// 7天内
        else if (System.currentTimeMillis() - timestamp <= 6 * 24 * 60 * 60 * 1000) {
            return timeFormat(timestamp, "MM-dd");
        }
        /// 本年内
        else if (isThisYear(timestamp)) {
            return timeFormat(timestamp, "MM-dd");
        }
        return timeFormat(timestamp, "yyyy-MM-dd");
    }

    /**
     *  最近消息为昨日需显示：昨日时间，精确到分，例如：昨日 19:23
     *  超过2天且小于7天的显示星期即可，例如：周日 19:23
     *  历史消息大于7天且在本年的显示月日，例如：3-6 19:23
     *  非本年的历史消息需显示年月日，例如：2020-1-2 19:23
     * @return
     */
    public static CharSequence formatSpecialTime(long timestamp) {
        if (timestamp < 1000000000000L) {
            timestamp *= 1000;
        }
        String time = timeFormat(timestamp, "HH:mm");
        /// 今天
        if (isToday(timestamp)) {
            return time;
        }
        /// 昨日
        else if (isYesterday(timestamp)) {
            SpanUtils spanText = SpanUtils.Companion.with(null).append(Utils.getApp().getString(R.string.yesterday));
            try {
                if (!LanguageUtils.isZh()) {
                    int fontResId = Utils.getApp().getResources().getIdentifier("ukijtor", "font", Utils.getApp().getPackageName());
                    Typeface tf = ResourcesCompat.getFont(Utils.getApp(), fontResId);
                    spanText.setTypeface(tf);
                }
            } catch (Exception e) {}
            return spanText.append(" ").append(time).create();
        }
        /// 7天内
        else if (System.currentTimeMillis() - timestamp <= 6 * 24 * 60 * 60 * 1000) {
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timestamp);
            int week = c.get(Calendar.DAY_OF_WEEK);
            SpanUtils spanText = SpanUtils.Companion.with(null);
            switch (week) {
                case Calendar.SUNDAY:
                    spanText.append(Utils.getApp().getString(R.string.sunday));
                    break;
                case Calendar.MONDAY:
                    spanText.append(Utils.getApp().getString(R.string.monday));
                    break;
                case Calendar.TUESDAY:
                    spanText.append(Utils.getApp().getString(R.string.tuesday));
                    break;
                case Calendar.WEDNESDAY:
                    spanText.append(Utils.getApp().getString(R.string.wednesday));
                    break;
                case Calendar.THURSDAY:
                    spanText.append(Utils.getApp().getString(R.string.thursday));
                    break;
                case Calendar.FRIDAY:
                    spanText.append(Utils.getApp().getString(R.string.friday));
                    break;
                case Calendar.SATURDAY:
                    spanText.append(Utils.getApp().getString(R.string.saturday));
                    break;
            }
            try {
                if (!LanguageUtils.isZh()) {
                    int fontResId = Utils.getApp().getResources().getIdentifier("ukijtor", "font", Utils.getApp().getPackageName());
                    Typeface tf = ResourcesCompat.getFont(Utils.getApp(), fontResId);
                    spanText.setTypeface(tf);
                }
            } catch (Exception e) {}
            return spanText.append(" ").append(time).create();
        }
        /// 本年内
        else if (isThisYear(timestamp)) {
            return timeFormat(timestamp, "MM-dd HH:mm");
        }
        return timeFormat(timestamp, "yyyy-MM-dd HH:mm");
    }

    /**
     * 当天  ->  时分
     * 今年 -> 月-日
     * 跨年 ->  年-月-日
     */
    public static CharSequence formatSpecialDate3(long timestamp) {
        if (timestamp < 1000000000000L) {
            timestamp *= 1000;
        }
        /// 今天
        if (isToday(timestamp)) {
            return timeFormat(timestamp, "HH:mm");
        }
        /// 本年内
        else if (isThisYear(timestamp)) {
            return timeFormat(timestamp, "MM-dd");
        }
        return timeFormat(timestamp, "yyyy-MM-dd");
    }

    /**
     * 当天  ->  HH：MM
     * 今年 -> MM-DD HH：MM
     * 跨年 ->  YYYY-MM-DD HH：MM
     */
    public static CharSequence formatSpecialTime2(long timestamp) {
        if (timestamp < 1000000000000L) {
            timestamp *= 1000;
        }
        String time = timeFormat(timestamp, "HH:mm");
        /// 今天
        if (isToday(timestamp)) {
            return time;
        }
        /// 本年内
        else if (isThisYear(timestamp)) {
            return timeFormat(timestamp, "MM-dd HH:mm");
        }
        return timeFormat(timestamp, "yyyy-MM-dd HH:mm");
    }

    /**
     * 根据年月日计算年龄
     * @param birthStr "1994-11-14"
     * @return
     */
    public static int getAgeFromBirth(String birthStr) {
        if (birthStr == null || birthStr.length() == 0)
            return 0;

        String[] strs = birthStr.trim().split("-");
        int birthYear = Integer.parseInt(strs[0]);
        int birthMonth = Integer.parseInt(strs[1]);
        int birthDay = Integer.parseInt(strs[2]);

        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        int nowMonth = cal.get(Calendar.MONTH) + 1;
        int nowDay = cal.get(Calendar.DAY_OF_MONTH);


        int age = nowYear - birthYear;
        if (nowMonth < birthMonth || (nowMonth == birthMonth && nowDay < birthDay)) {
            age--;
        }
        return age;
    }
}
