package com.example.habitmanagementapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.habitmanagementapplication.R;
import com.example.habitmanagementapplication.activity.DiaryActivity;
import com.example.habitmanagementapplication.habit.Habit;
import com.example.habitmanagementapplication.habit.HabitAdapter;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;
import com.example.habitmanagementapplication.progress.ProgressAdapter;

import java.util.Arrays;
import java.util.List;

public class ProgressActivity extends AppCompatActivity {
    // 상단에 위치한 4개의 버튼
    Button backToMainInProgressBtn;
    Button backToDiaryInProgressBtn;
    Button backToProgressInProgressBtn;
    Button showAllDataInProgressBtn;

    // 목표 습관 데이터베이스 관련
    HabitDatabaseHelper dbHelper;

//    // 목표 습관을 표시할 레이아웃 관련
//    private LinearLayout habitLayout;

    // 진척도를 표시할 레이아웃 관련
    private LinearLayout progressRateLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // HabitDatabaseHelper 초기화
        dbHelper = new HabitDatabaseHelper(this);

        backToMainInProgressBtn = findViewById(R.id.backToMainInProgressBtn);
        backToDiaryInProgressBtn = findViewById(R.id.backToDiaryInProgressBtn);
        backToProgressInProgressBtn = findViewById(R.id.backToProgressInProgressBtn);
        showAllDataInProgressBtn = findViewById(R.id.showAllDataInProgressBtn);

//        habitLayout = findViewById(R.id.layout_habit);
        progressRateLayout = findViewById(R.id.layout_progress);
        ProgressAdapter.displayProgressRate(ProgressActivity.this, dbHelper, progressRateLayout);

        backToMainInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backToDiaryInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });

        backToProgressInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressAdapter.displayProgressRate(ProgressActivity.this, dbHelper, progressRateLayout);
            }
        });

        showAllDataInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitAdapter.displayHabitsForDatabaseReview(ProgressActivity.this, dbHelper, progressRateLayout);
            }
        });
    }
}
