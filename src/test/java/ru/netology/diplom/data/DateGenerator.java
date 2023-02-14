package ru.netology.diplom.data;

import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateGenerator {

    private DateGenerator() {
    }

    private static LocalDate date = LocalDate.now();

    private static String generateLowerBorderYear(String calendarType) {
        var currentDate = date.format(DateTimeFormatter.ofPattern("MM.yy"));
        return cutNecessaryDate(currentDate, calendarType);
    }

    private static String generateUpperBorderPeriod(String calendarType) {
        var upperBorderDate = date.plusYears(5).format(DateTimeFormatter.ofPattern("MM.yy"));
        return cutNecessaryDate(upperBorderDate, calendarType);
    }

    private static String generatePreviousMonth(String calendarType) {
        var previousMonth = date.minusMonths(1).format(DateTimeFormatter.ofPattern("MM.yy"));
        return cutNecessaryDate(previousMonth, calendarType);
    }

    private static String generatePreviousYear(String calendarType) {
        var previousYear = date.minusYears(1).format(DateTimeFormatter.ofPattern("MM.yy"));
        return cutNecessaryDate(previousYear, calendarType);
    }

    private static String generateOverPeriod(String calendarType) {
        var overCardPeriodYear = date.plusYears(5).plusMonths(1)
                .format(DateTimeFormatter.ofPattern("MM.yy"));
        return cutNecessaryDate(overCardPeriodYear, calendarType);
    }

    private static String cutNecessaryDate(String cutDate, String calendarType) {
        var month = cutDate.substring(0, 2);
        var year = cutDate.substring(3);
        if (calendarType.equals("month")) {
            return month;
        }
        if (calendarType.equals("year")) {
            return year;
        }
        return null;
    }

    @Value
    public static class CardDate {
        private String cardMonth;
        private String cardYear;
    }

    public static DateGenerator.CardDate getLowerBorder() {
        return new DateGenerator.CardDate(generateLowerBorderYear("month"),
                generateLowerBorderYear("year"));
    }

    public static DateGenerator.CardDate getUpperBorder() {
        return new DateGenerator.CardDate(generateUpperBorderPeriod("month"),
                generateUpperBorderPeriod("year"));
    }

    public static DateGenerator.CardDate getPreviousMonth() {
        return new DateGenerator.CardDate(generatePreviousMonth("month"),
                generatePreviousMonth("year"));
    }

    public static DateGenerator.CardDate getPreviousYear() {
        return new DateGenerator.CardDate(generatePreviousYear("month"),
                generatePreviousYear("year"));
    }

    public static DateGenerator.CardDate getOverDate() {
        return new DateGenerator.CardDate(generateOverPeriod("month"),
                generateOverPeriod("year"));
    }

    public static String wrongInputDateFormat() {
        int[] correctCardPeriodYear = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        var random = (int) Math.floor(Math.random() * correctCardPeriodYear.length);
        var wrongFormatValue = correctCardPeriodYear[random];
        return String.valueOf(wrongFormatValue);
    }
}