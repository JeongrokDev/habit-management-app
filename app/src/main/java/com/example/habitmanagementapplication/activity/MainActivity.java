package com.example.habitmanagementapplication.activity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.habitmanagementapplication.R;
import com.example.habitmanagementapplication.habit.HabitAdapter;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;
import com.example.habitmanagementapplication.notification.AlarmReceiver;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    // 상단에 위치한 4개의 버튼 관련
    Button backToMainInMainBtn;
    Button backToDiaryInMainBtn;
    Button backToProgressInMainBtn;
    Button addInMainBtn;

    // 목표 습관 데이터베이스 관련
    HabitDatabaseHelper dbHelper;

    // 목표 습관을 표시할 레이아웃 관련
    private LinearLayout habitLayout;

    HabitAdapter habitAdapter;

    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            } else {
                setDailyAlarm();
            }
        } else {
            setDailyAlarm();
        }

        // HabitDatabaseHelper 초기화
        dbHelper = new HabitDatabaseHelper(this);

        backToMainInMainBtn = findViewById(R.id.backToMainInMainBtn);
        backToDiaryInMainBtn = findViewById(R.id.backToDiaryInMainBtn);
        backToProgressInMainBtn = findViewById(R.id.backToProgressInMainBtn);
        addInMainBtn = findViewById(R.id.addInMainBtn);

        habitLayout = findViewById(R.id.layout_habit);
        HabitAdapter.displayHabits(MainActivity.this, dbHelper, habitLayout);

        backToMainInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitAdapter.displayHabits(MainActivity.this, dbHelper, habitLayout);
            }
        });

        backToDiaryInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });

        backToProgressInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                startActivity(intent);
            }
        });

        addInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitAdapter.showAddHabitDialog(MainActivity.this, dbHelper, habitLayout);
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("AlarmReceiver", "createNotificationChannel()");
            CharSequence name = "DailyReminderChannel";
            String description = "Channel for Daily Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyDaily", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setDailyAlarm() {
        Log.d("AlarmReceiver", "setDailyAlarm()");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // 매일 알람 설정
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        // 디버깅을 위한 로그 출력
        Log.d("AlarmReceiver", "Alarm set for 09:00 every day");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setDailyAlarm();
            } else {
                Log.d("AlarmReceiver", "Notification permission denied");
            }
        }
    }
}