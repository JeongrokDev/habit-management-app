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
import com.example.habitmanagementapplication.diary.DiaryAdapter;
import com.example.habitmanagementapplication.diary.DiaryDatabaseHelper;
import com.example.habitmanagementapplication.feedback.ApiClient;
import com.example.habitmanagementapplication.feedback.ChatRequest;
import com.example.habitmanagementapplication.feedback.ChatResponse;
import com.example.habitmanagementapplication.feedback.OpenAIApi;
import com.example.habitmanagementapplication.habit.Habit;
import com.example.habitmanagementapplication.habit.HabitAdapter;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;
import com.example.habitmanagementapplication.progress.ProgressAdapter;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProgressActivity extends AppCompatActivity {
    // 상단에 위치한 4개의 버튼 정의
    Button backToMainInProgressBtn;
    Button backToDiaryInProgressBtn;
    Button backToProgressInProgressBtn;
    Button showAllDataInProgressBtn;
    Button dateInProgressBtn;
    TextView todayDateTextView;

    // 목표 습관 데이터베이스 관련
    HabitDatabaseHelper dbHelper;
    DiaryDatabaseHelper dbHelper2;

    // 진척도를 표시할 레이아웃 관련
    private LinearLayout progressRateLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        // HabitDatabaseHelper 초기화
        dbHelper = new HabitDatabaseHelper(this);
        dbHelper2 = new DiaryDatabaseHelper(this);

        // 버튼 초기화 및 레이아웃 연결
        backToMainInProgressBtn = findViewById(R.id.backToMainInProgressBtn);
        backToDiaryInProgressBtn = findViewById(R.id.backToDiaryInProgressBtn);
        backToProgressInProgressBtn = findViewById(R.id.backToProgressInProgressBtn);
        showAllDataInProgressBtn = findViewById(R.id.showAllDataInProgressBtn);
        dateInProgressBtn = findViewById(R.id.dateInProgress); // 날짜 버튼 초기화

        // 현재 날짜를 "MM월 dd일" 형식으로 설정
        String currentDate = new SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(new Date());
        dateInProgressBtn.setText(currentDate);

        // 진행률 레이아웃 설정 및 진척도 데이터 표
        progressRateLayout = findViewById(R.id.layout_progress);
        ProgressAdapter.displayProgressRate(ProgressActivity.this, dbHelper, dbHelper2, progressRateLayout);

        // 메인 버튼 클릭 시
        backToMainInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 일기 화면으로 이동 버튼 클릭 시
        backToDiaryInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });

        // 진행 상황 화면으로 이동 버튼 클릭 시 진행 상황 새로고침
        backToProgressInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressAdapter.displayProgressRate(ProgressActivity.this, dbHelper, dbHelper2, progressRateLayout);
            }
        });

        // 모든 습관 데이터 표시 버튼 설정 (데이터베이스 점검용)
        showAllDataInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HabitAdapter.displayHabitsForDatabaseReview(ProgressActivity.this, dbHelper, dbHelper2, progressRateLayout);
            }
        });

        // 날짜를 텍스트뷰에 표시
        TextView todayDateTextView = findViewById(R.id.todayDateTextViewInProgress);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd (E)", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        String displayText = "현황 목록 ";
        todayDateTextView.setText(displayText);
    }
}
