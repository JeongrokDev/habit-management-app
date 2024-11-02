package com.example.habitmanagementapplication.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.habitmanagementapplication.R;
import com.example.habitmanagementapplication.habit.Habit;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm received");

        // 데이터베이스 헬퍼 초기화
        HabitDatabaseHelper databaseHelper = new HabitDatabaseHelper(context);

        // 오늘의 습관 목록 가져오기
        List<Habit> habitList = databaseHelper.getTodayHabitList();

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            StringBuilder todayNoti = new StringBuilder();
            for (Habit habit : habitList) {
                todayNoti.append(habit.getTitle()).append(", ");
            }

            // 마지막에 ", "가 있으면 제거
            if (todayNoti.length() > 2 && todayNoti.toString().endsWith(", ")) {
                todayNoti.setLength(todayNoti.length() - 2); // 마지막 두 문자 제거
            }

            // 알림 내용이 너무 길 경우 줄이기
            String notificationText = todayNoti.toString();
            if (notificationText.length() > 50) {
                notificationText = notificationText.substring(0, 47) + "...";
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyDaily")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Today's Tasks")
                    .setContentText(notificationText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(200, builder.build());

            Log.d("AlarmReceiver", "Notification sent: " + notificationText);
        } else {
            Log.d("AlarmReceiver", "Notification permission not granted");
        }
    }
}

