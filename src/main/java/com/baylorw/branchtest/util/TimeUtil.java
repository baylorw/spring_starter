package com.baylorw.branchtest.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

// NOTE: i already owned this class. i didn't write it for this homework, unlike everything else.
public final class TimeUtil {
    /**
     * Convert a ZonedDateTime to a String.
     * <p>
     * Format (case matters):
     * Date: MM dd yyyy  E (day name)
     * Time: hh (am/pm) HH (24-hour) mm ss SSS (ms) n (ns)  a (am/pm)
     * Misc: O (time zone + offset) V (time zone ID) Z (hours offset) z (time zone name)
     * <p>
     * There are lots of others (day of year, quarter, etc.):
     * https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
     *
     * @param dateTime
     * @param format
     * @return
     */
    public static String format(ZonedDateTime dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        String formattedString = dateTime.format(formatter);
        return formattedString;
    }
}
