/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DATE_FORMAT);
    }

    public static String today() {
        return LocalDate.now().toString();
    }

    public static String now() {
        return LocalDateTime.now().format(DATE_TIME_FORMAT);
    }

    public static boolean isDateInRange(String date, String startDate, String endDate) {
        LocalDate value = parseDate(date);
        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);

        return !value.isBefore(start) && !value.isAfter(end);
    }
}

