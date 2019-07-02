package com.ljh.gamedemo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static String getCurrentDate(){
        StringBuilder sb = new StringBuilder("当前时间为：");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime dateTime = LocalDateTime.now();
        String formatStr = dateTime.format(formatter);
        String dayOfWeek = dateTime.getDayOfWeek().toString();

        sb.append(formatStr + " ");
        sb.append(dayOfWeek);

        return sb.toString();
    }


    public static void main(String[] args) {
        LocalDateTime dateTime = LocalDateTime.now();
        System.out.println(dateTime.getDayOfWeek().toString());
    }
}
