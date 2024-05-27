package com.example.habitmanagementapplication.progress;

import com.example.habitmanagementapplication.habit.Habit;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;

import java.util.List;

public class ProgressManager {
    public double[] calculateProgressRate(HabitDatabaseHelper dbHelper) {
        List<Habit> habitList = dbHelper.getAllHabits();
        double[] progressRate = new double[7];

        if (habitList.isEmpty()) {
            // 습관 리스트가 비어 있는 경우, 모든 요일의 진척도를 0으로 설정
            for (int i = 0; i < progressRate.length; i++) {
                progressRate[i] = 0;
            }
        } else {
            for (int i = 0; i < progressRate.length; i++) {
                double dailyGoals = 0;
                double dailyAchievements = 0;

                for (Habit habit : habitList) {
                    if (habit.getDailyGoals()[i])
                        dailyGoals++;

                    if (habit.getDailyAchievements()[i])
                        dailyAchievements++;
                }

                // dailyGoals가 0인 경우를 방지하기 위해 예외 처리
                if (dailyGoals == 0) {
                    progressRate[i] = 0;
                } else {
                    progressRate[i] = dailyAchievements / dailyGoals;
                }
            }
        }

        return progressRate;
    }
}
