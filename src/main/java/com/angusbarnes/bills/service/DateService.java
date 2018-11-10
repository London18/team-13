package com.angusbarnes.bills.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

public class DateService {
    public static final String DATE_FORMAT;
    public static final int DEFAULT_TRANSACTION_GROUP_DURATION;
    private static final SimpleDateFormat formatter;
    
    static {
        DATE_FORMAT = "yyyy-MM-dd";
        DEFAULT_TRANSACTION_GROUP_DURATION = 7;
        formatter = new SimpleDateFormat(DATE_FORMAT);
    }
    
    /**
     * Gets a date representing the server's current internal time
     *
     * @return Date object for the current time
     */
    public static Date getDate () {
        return Calendar.getInstance().getTime();
    }
    
    /**
     * Gets a date, offset to the future by a number of seconds.
     *
     * @param futureSeconds The number of seconds to advance the current date
     *                      by
     *
     * @return The current date, advanced by futureSeconds seconds
     */
    public static Date getFutureDate (int futureSeconds) {
        Calendar future = Calendar.getInstance();
        future.add(Calendar.SECOND, futureSeconds);
        return future.getTime();
    }
    
    /**
     * Parses a string date into a {@link Date}, using {@link
     * DateService#DATE_FORMAT}.
     * <p>
     * If parsing is not successful, instead gives an empty optional.
     *
     * @param unparsedDate The date to parse
     *
     * @return A parsed date, or an empty optional if parsing fails.
     */
    public static Optional<Date> parseDate (String unparsedDate) {
        try {
            return Optional.of(java.sql.Date.valueOf(
                    LocalDate.parse(
                            unparsedDate,
                            DateTimeFormatter.ofPattern(DATE_FORMAT))));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Finds the date rounded to the most recent Monday at 00:00:00
     *
     * @param date Date to round
     *
     * @return Date rounded to most recent Monday at 00:00:00
     */
    public static Date getEarliestMondayMidnight (Date date) {
        // Convert to a calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // Create a new calendar
        Calendar newCalendar = (Calendar) calendar.clone();
        // Clear time data
        newCalendar.set(Calendar.HOUR_OF_DAY, 0);
        newCalendar.clear(Calendar.MINUTE);
        newCalendar.clear(Calendar.SECOND);
        newCalendar.clear(Calendar.MILLISECOND);
        newCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        // Return result
        return newCalendar.getTime();
    }

    public static Date later (Date date, int hours, int mins) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        cal.add(Calendar.MINUTE, mins);

        return cal.getTime();
    }
    
    public static synchronized String formatDate (Date date) {
        return formatter.format(date);
    }
}
