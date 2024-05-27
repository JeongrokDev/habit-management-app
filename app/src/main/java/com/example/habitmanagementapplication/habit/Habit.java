package com.example.habitmanagementapplication.habit;

public class Habit {
    private int id; // 습관 객체의 고유한 식별자
    private String title; // 습관의 제목
    private String hour; // 수행 시각(시)
    private String minute; // 수행 시각(분)
    private boolean[] dailyGoals; // 설정한 수행 요일
    private boolean[] dailyAchievements; // 달성한 수행 요일

    public Habit(int id, String title, String hour, String minute, boolean[] dailyGoals, boolean[] dailyAchievements) {
        this.id = id;
        this.title = title;
        this.hour = hour;
        this.minute = minute;
        this.dailyGoals = dailyGoals;
        this.dailyAchievements = dailyAchievements;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getHour() { return hour; }
    public String getMinute() { return minute; }
    public boolean[] getDailyGoals() { return dailyGoals; }
    public boolean[] getDailyAchievements() { return dailyAchievements; }

    public void setTitle(String title) { this.title = title; }
    public void setHour(String hour) { this.hour = hour; }
    public void setMinute(String minute) { this.minute = minute; }
    public void setDailyGoals(boolean[] dailyGoals) { this.dailyGoals = dailyGoals; }
    public void setDailyAchievements(boolean[] dailyAchievements) { this.dailyAchievements = dailyAchievements; }
}
