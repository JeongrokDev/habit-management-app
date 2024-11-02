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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.habitmanagementapplication.R;
import com.example.habitmanagementapplication.habit.HabitAdapter;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;
import com.example.habitmanagementapplication.notification.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // 상단에 위치한 4개의 버튼 정의
    Button backToMainInMainBtn;
    Button backToDiaryInMainBtn;
    Button backToProgressInMainBtn;
    Button addInMainBtn;
    Button dateInMainBtn;
    TextView todayDateTextView;

    // 습관 관리를 위한 데이터베이스 헬퍼
    HabitDatabaseHelper dbHelper;

    // 습관 목록을 표시할 레이아웃
    private LinearLayout habitLayout;

    // 습관 목록을 위한 어댑터
    HabitAdapter habitAdapter;

    // 알림 권한 요청 코드
    private static final int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android 13 이상에서는 알림 권한을 확인하고, 허가된 경우에만 알람 설정
        createNotificationChannel();

        // 알림 권한을 요청하거나 설정된 경우 매일 알림 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // 알림 권한 요청
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            } else {
                // 권한이 허가된 경우 알람 설정
                setDailyAlarm();
            }
        } else {
            // Android 13 미만에서는 알림 권한 불필요, 바로 알람 설정
            setDailyAlarm();
        }

        // 데이터베이스 헬퍼 초기화
        dbHelper = new HabitDatabaseHelper(this);

        // 상단 버튼 초기화 및 레이아웃 연결
        backToMainInMainBtn = findViewById(R.id.backToMainInMainBtn);
        backToDiaryInMainBtn = findViewById(R.id.backToDiaryInMainBtn);
        backToProgressInMainBtn = findViewById(R.id.backToProgressInMainBtn);
        addInMainBtn = findViewById(R.id.addInMainBtn);
        dateInMainBtn = findViewById(R.id.dateInMain);

        // 현재 날짜를 버튼에 표시
        String currentDate = new SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(new Date());
        dateInMainBtn.setText(currentDate);

        // 습관 목록 레이아웃을 표시하는 LinearLayout 설정
        habitLayout = findViewById(R.id.layout_habit);

        // 어댑터를 통해 습관 목록 표시
        HabitAdapter.displayHabits(MainActivity.this, dbHelper, habitLayout);

        // 메인 버튼 클릭 시 습관 목록 새로고침
        backToMainInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitAdapter.displayHabits(MainActivity.this, dbHelper, habitLayout);
            }
        });

        // 일기 화면으로 이동 버튼 클릭 시
        backToDiaryInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });

        // 진행 상황 화면으로 이동 버튼 클릭 시
        backToProgressInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                startActivity(intent);
            }
        });

        // 습관 추가 버튼 클릭 시 추가 다이얼로그 표시
        addInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitAdapter.showAddHabitDialog(MainActivity.this, dbHelper, habitLayout);
            }
        });

        // 날짜를 텍스트뷰에 표시
        TextView todayDateTextView = findViewById(R.id.todayDateTextViewInMain);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd (E)", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        String displayText = "습관 목록 ";
        todayDateTextView.setText(displayText);
    }

    // 일일 알림을 위한 알림 채널 생성 (안드로이드 O 이상 필요)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("AlarmReceiver", "createNotificationChannel()");
            CharSequence name = "DailyReminderChannel";
            String description = "Channel for Daily Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyDaily", name, importance);
            channel.setDescription(description);

            // NotificationManager를 통해 채널 생성
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // 매일 오전 9시에 알림을 트리거하는 알람 설정
    private void setDailyAlarm() {
        Log.d("AlarmReceiver", "setDailyAlarm()");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // 알람 시간을 오전 9시로 설정
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // 알람을 매일 반복되도록 설정
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        Log.d("AlarmReceiver", "Alarm set for 09:00 every day");
    }

    // 알림 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허가된 경우 알람 설정
                setDailyAlarm();
            } else {
                Log.d("AlarmReceiver", "Notification permission denied");
            }
        }
    }
}