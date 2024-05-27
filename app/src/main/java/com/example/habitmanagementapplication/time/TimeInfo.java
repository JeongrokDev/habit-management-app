package com.example.habitmanagementapplication.time;

import java.util.Calendar;
import java.util.Date;

public class TimeInfo {
    // 현재 요일 정보를 가져오는 메서드(0: 월요일, 1: 화요일, ..., 6: 일요일)
    public static int getCurrentDayOfWeek() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        // Date 객체로부터 Calendar 객체 생성
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 현재 요일 가져오기
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 월요일부터 시작하도록 인덱스 조정
        currentDayOfWeek = (currentDayOfWeek + 5) % 7;

        return currentDayOfWeek;
    }
}
