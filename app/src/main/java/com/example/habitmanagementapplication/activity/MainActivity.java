package com.example.habitmanagementapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.habitmanagementapplication.R;
import com.example.habitmanagementapplication.habit.Habit;
import com.example.habitmanagementapplication.habit.HabitAdapter;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(getApplicationContext(), "메인 화면으로 돌아가기(메인 화면에서)", Toast.LENGTH_SHORT).show();
                HabitAdapter.displayHabits(MainActivity.this, dbHelper, habitLayout);
            }
        });

        backToDiaryInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "일기 화면으로 돌아가기(메인 화면에서)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });

        backToProgressInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "현황 화면으로 돌아가기(메인 화면에서)", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                startActivity(intent);
            }
        });

        addInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "추가하기(메인 화면에서)", Toast.LENGTH_SHORT).show();
                HabitAdapter.showAddHabitDialog(MainActivity.this, dbHelper, habitLayout);
            }
        });
    }
}