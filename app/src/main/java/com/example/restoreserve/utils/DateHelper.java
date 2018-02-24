package com.example.restoreserve.utils;

import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>
 *     Utility class, for date related methods and patterns.
 * </p>
 */
public class DateHelper {
    public static String PATTERN_API_DATE = "dd-MM-yyyy";
    public static String PATTERN_TIME = "hh:mm a";
    public static String PATTERN_TIME_PERIOD = "a";
    private static SimpleDateFormat sdf;

    /**
     * Returns the timestamp of a given date with the necessary pattern,
     * or -1 if invalid date string.
     */
    public static synchronized long generateTimestamp(String date, String pattern) {
        sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return sdf.parse(date).getTime();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Returns the timestamp of a given timestamp after
     * setting the time to the endt of the day, 23:59:59.
     */
    public static synchronized long generateEndOfDayTimestamp(long timestamp) {
        // convert from seconds to milliseconds
        timestamp *= 1000;
        // get calendar instance
        Calendar cal = Calendar.getInstance();
        // set it with timestamp
        cal.setTimeInMillis(timestamp);
        // set hours, minutes seconds and milliseconds of calendar to end of day
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 99);
        // return new timestamp in seconds
        return cal.getTimeInMillis() / 1000;
    }
/*

    */
/**
     * Returns the date with the API pattern defined, or null
     *//*

    @Nullable
    public static synchronized String getApiDate(long timestamp) {
        if (timestamp < 0) {
            return null;
        } else {
            sdf = new SimpleDateFormat(PATTERN_API_DATE, Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(new Date(timestamp));
        }
    }
*/
    /**
     * Returns the date with the given pattern.
     * @param timestamp the timestamp of the date in milliseconds.
     * @param pattern the date pattern.
     */
    @Nullable
    public static synchronized String getDateFromMilliseconds(long timestamp, String pattern) {
        sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        return sdf.format(new Date(timestamp));
    }

    /**
     * Returns the date from the given timestamp which is in seconds, formatted as time and period.
     * @param timestamp the timestamp of the date in seconds.
     */
     public static synchronized String getTimeFromSeconds(long timestamp) {
         // convert from seconds to milliseconds
         timestamp *= 1000;
         // create date object with timestamp passed
         Date date = new Date(timestamp);
         // set date formatter with time pattern
         sdf = new SimpleDateFormat(PATTERN_TIME, Locale.ENGLISH);
         // get formatted time
         String time = sdf.format(date);
         // set date formatter to time period
         sdf = new SimpleDateFormat(PATTERN_TIME_PERIOD, Locale.ENGLISH);
         // get formatted period
         String period  = sdf.format(date);
         // convert period to lower case
         period = period.toLowerCase();
         // return time and period space separated
         return time + " " + period;
     }

    /**
     * Returns true if the given timestamps
     * have the same calendar day, false otherwise.
     * @param timestamp1 the value of the first timestamp in seconds.
     * @param timestamp2 the value of the second timestamp in seconds.
     */
    public static boolean isSameDay(long timestamp1, long timestamp2) {
        // convert from seconds to milliseconds
        timestamp1 *= 1000;
        timestamp2 *= 1000;
        // get days from timestamps
        String PATTERN_DAY = "ddMMMyy";
        sdf = new SimpleDateFormat(PATTERN_DAY, Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String day1 = sdf.format(new Date(timestamp1));
        final String day2 = sdf.format(new Date(timestamp2));
        // return days comparison
        return day1.equals(day2);
    }

    /**
     * Returns the date of a given string formatted with given pattern.
     * @param strDate the date to be parsed as date object.
     * @param pattern the pattern of the date.
     */
    @Nullable
     public static synchronized Date parseDate(String strDate, String pattern) {
         try {
             // init formatter
             sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
             sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
             // return result date object
             return sdf.parse(strDate);
         } catch (ParseException e) {
             return null;
         }
     }

    @Nullable
     public static synchronized Date parseTime(String strDate) {
         try {
             // init formatter
             sdf = new SimpleDateFormat(PATTERN_TIME, Locale.ENGLISH);
             sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
             // return result date object
             return sdf.parse(strDate);
         } catch (ParseException e) {
             return null;
         }
     }



    /**
     * Returns the date of a given string formatted as an API date.
     * @param strDate the date to be parsed to date object.
     */
    @Nullable
    public static Date parseApiDate(String strDate) {
        return parseDate(strDate, PATTERN_API_DATE);
    }

    /**
     * Converts date from one format to another.
     * @param strDate the string date to be converted.
     * @param patternInput the pattern of the date received.
     * @param patternOutput the desired pattern of the output date.
     * @return the formatted date if no formatting exceptions, else returns
     * null.
     */
    @Nullable
    public static synchronized String formatDate(String strDate, String patternInput, String patternOutput) {
        try {
            // generate date from input string
            Date date = parseDate(strDate, patternInput);
            // format date
            sdf = new SimpleDateFormat(patternOutput, Locale.ENGLISH);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Converts date from one format to another.
     * @return the formatted date if no formatting exceptions, else returns
     * null.
     */
    @Nullable
    public static synchronized String formatDate(Date date) {
        try {
            // format date
            sdf = new SimpleDateFormat(PATTERN_API_DATE, Locale.ENGLISH);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Converts date from one format to another.
     * @return the formatted date if no formatting exceptions, else returns
     * null.
     */
    @Nullable
    public static synchronized String formatTime(Date date) {
        try {
            // format date
            sdf = new SimpleDateFormat(PATTERN_TIME, Locale.ENGLISH);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }



}
