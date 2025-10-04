package com.shopizer.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public final class Utils {

    private Utils() {
    }

    private static final Random RANDOM = new Random();


    public static String currencyFormat(Double doubleValue) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(doubleValue);
    }

    public static String toHour(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return formatter.format(date);
    }

    public static Date minusDate(Date date, int calenderOption, int value){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calenderOption, -value);

        return calendar.getTime();
    }

    public static Date plusDate(Date date, int calenderOption, int value){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calenderOption, value);

        return calendar.getTime();
    }

    public static String toDate(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String toDate(LocalDateTime date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(date);
    }

    public static Date toDate(String date) {
        SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return dt.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static int getTotalPage(int totalRecord, int pageSize) {
        int page = (totalRecord - 1) / pageSize;

        if (page == 0 || totalRecord == 0) {
            return 1;
        }

        return page + 1;
    }

    public static int getOffset(int page, int pageSize) {
    return (page - 1) * pageSize;
  }
}
