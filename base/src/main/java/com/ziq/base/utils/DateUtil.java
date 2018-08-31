package com.ziq.base.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class DateUtil {

    public static final String WEDDING_DATE_API_RESPONSE_PATTERN = "yyyy-MM-dd";
    public static final String CHECKLIST_GROUP_DATE_PATTERN = "MMM ''yy";
    public static final String CHECKLIST_EDIT_DUE_DATE_PATTERN = "MMMM d, yyyy";
    public static final String CONVERSATION_TIME_API_RESPONSE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String CONVERSATION_TIME_SSS_API_RESPONSE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String VENDOR_REVIEW_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String CONVERSATION_TIME_LESS_THAN_ONE_DAY_PATTERN = "h:mm a";
    public static final String TOUR_TIME_RESULT_PATTERN = "E, MMM d";
    public static final String DATE_FORMAT_TIMELINE_GROUP_RESULT = "EEEE, MMMM d, yyyy";
    public static final String DATE_FORMAT_TIMELINE_DAY_GROUP_RESULT = "EEE, MMM, d, yyyy";
    private static final String CONVERSATION_TIME_MORE_THAN_ONE_DAY_PATTERN = "M.d.yyyy";
    private static final String CONVERSATION_DETAIL_TIME_MORE_THAN_ONE_DAY_PATTERN = "MMM.d.yyyy";
    private static final String WWS_GUEST_BOOK_DATA_PATTERN = "MMMM dd, yyyy 'at' h:mm aa";

    public static Date getDate(String formatString, String dateStr) throws ParseException {
        final DateFormat dateFormat = new SimpleDateFormat(formatString, Locale.US);
        return dateFormat.parse(dateStr);
    }

    public static String getDateString(String formatString, Date date) throws ParseException {
        final DateFormat dateFormat = new SimpleDateFormat(formatString, Locale.US);
        return dateFormat.format(date);
    }

    public static Date getDate(int year, int monthOfYear, int dayOfMonth,
                               int hour, int min, int second, int millisecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    public static Date clearDateTime(Date date, boolean isHourClearNeed) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (isHourClearNeed) {
            calendar.set(Calendar.HOUR, 0);
        }
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private static Calendar clearCalendarTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Date getFirstDayOfCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        clearCalendarTime(calendar);
        int todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_YEAR, 1 - todayOfWeek);
        return calendar.getTime();
    }

    /**
     * 获得 一个月第几天 的后缀， 1st， 2nd
     *
     * @param n 第几天
     * @return 字符
     */
    public static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    /**
     * UTC 时间根据 时区 来 转换本地 时间
     *
     * @param date 日期
     * @return 日期
     */
    public static Date getLocalTimeWithTimeZone(String date) {
        Date messageDate = null;
        try {
            messageDate = getDate(CONVERSATION_TIME_SSS_API_RESPONSE_PATTERN, date);
        } catch (Exception e) {
            try {
                messageDate = getDate(VENDOR_REVIEW_DATE_PATTERN, date);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        if (messageDate != null) {
            Date nowDate = new Date();
            long localTime = messageDate.getTime() + TimeZone.getDefault().getOffset(nowDate.getTime());
            messageDate = new Date(localTime);
        }
        return messageDate;
    }

}
