package vn.edu.fpt.pharma.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeFormatterUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

    public static String formatDateTime(LocalDateTime time) {
        if (time == null) return "";
        return time.format(DATE_TIME_FORMATTER);
    }

}
