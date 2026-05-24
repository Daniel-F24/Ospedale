package packagee.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class DateTimeUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private DateTimeUtil() {
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DATE_FORMATTER);
    }

    public static LocalTime parseTime(String time) {
        return LocalTime.parse(time, TIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String date, String time) {
        return LocalDateTime.of(parseDate(date), parseTime(time));
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMATTER);
    }

    public static String formatTime(LocalTime time) {
        return time == null ? "" : time.format(TIME_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return formatDate(dateTime.toLocalDate()) + " " + formatTime(dateTime.toLocalTime());
    }
}

