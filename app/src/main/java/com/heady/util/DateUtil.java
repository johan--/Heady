package com.heady.util;

import android.text.format.DateUtils;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Yogi.
 */

public class DateUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final String ABR_SECONDS_AGO = "s";
    private static final String ABR_MINUTES_AGO = "m";
    private static final String ABR_HOURS_AGO = "h";
    private static final String ABR_DAYS_AGO = "d";

    /**
     * Returns a string describing 'time' as a time relative to 'now'.
     * <p/>
     * Time spans in the past are formatted like "42 minutes ago". Time spans in
     * the future are formatted like "in 42 minutes".
     * <p/>
     * Can use FORMAT_ABBREV_RELATIVE flag to use abbreviated relative
     * times, like "42 mins ago".
     *
     * @param time          the time to describe, in milliseconds
     * @param now           the current time in milliseconds
     * @param minResolution the minimum timespan to report. For example, a time
     *                      3 seconds in the past will be reported as "0 minutes ago" if
     *                      this is set to MINUTE_IN_MILLIS. Pass one of 0,
     *                      MINUTE_IN_MILLIS, HOUR_IN_MILLIS, DAY_IN_MILLIS,
     *                      WEEK_IN_MILLIS
     * @param flags         a bit mask of formatting options, such as
     *                      FORMAT_NUMERIC_DATE or
     *                      FORMAT_ABBREV_RELATIVE
     */
    public static CharSequence getPastRelativeTimeSpanString(long time, long now, long minResolution, int flags) {
        long duration = Math.abs(now - time);
        String timeStr;
        long count;
        if (duration < DateUtils.MINUTE_IN_MILLIS && minResolution < DateUtils.MINUTE_IN_MILLIS) {
            count = duration / DateUtils.SECOND_IN_MILLIS;
            timeStr = ABR_SECONDS_AGO;
        } else if (duration < DateUtils.HOUR_IN_MILLIS && minResolution < DateUtils.HOUR_IN_MILLIS) {
            count = duration / DateUtils.MINUTE_IN_MILLIS;
            timeStr = ABR_MINUTES_AGO;
        } else if (duration < DateUtils.DAY_IN_MILLIS && minResolution < DateUtils.DAY_IN_MILLIS) {
            count = duration / DateUtils.HOUR_IN_MILLIS;
            timeStr = ABR_HOURS_AGO;
        } else if (duration < DateUtils.WEEK_IN_MILLIS && minResolution < DateUtils.WEEK_IN_MILLIS) {
            return getRelativeDayString(time, now);
        } else {
            // We know that we won't be showing the time, so it is safe to pass in a null context.
            return DateUtils.formatDateRange(null, time, time, flags);
        }
        return count + timeStr;
    }


    /**
     * Returns a string describing a day relative to the current day. For example if the day is
     * today this function returns "Today", if the day was a week ago it returns "7 days ago", and
     * if the day is in 2 weeks it returns "in 14 days".
     * TODO update this method
     *
     * @param day   the relative day to describe in UTC milliseconds
     * @param today the current time in UTC milliseconds
     */
    private static String getRelativeDayString(long day, long today) {
        Time startTime = new Time();
        startTime.set(day);
        int startDay = Time.getJulianDay(day, startTime.gmtoff);
        Time currentTime = new Time();
        currentTime.set(today);
        return Math.abs(Time.getJulianDay(today, currentTime.gmtoff) - startDay) + ABR_DAYS_AGO;

    }

    /**
     * convert the provided date into long value
     *
     * @param dateStr date in String to be converted
     * @return date in long
     */
    public static long convertDateInMillis(String dateStr) {
        String dates = dateStr.substring(0, 19);
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                DATE_FORMAT, Locale.getDefault());
        long dateInMillis;
        try {
            Date date = desiredFormat.parse(dates);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
