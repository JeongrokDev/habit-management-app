package com.example.habitmanagementapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.habitmanagementapplication.R;
import com.example.habitmanagementapplication.diary.Diary;
import com.example.habitmanagementapplication.diary.DiaryAdapter;
import com.example.habitmanagementapplication.diary.DiaryDatabaseHelper;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {
    // 상단에 위치한 4개의 버튼
    Button backToMainInDiaryBtn;
    Button backToDiaryInDiaryBtn;
    Button backToProgressInDiaryBtn;
    Button addInDiaryBtn;

    // 습관 일기 데이터베이스 관련
    HabitDatabaseHelper dbHabitHelper;

    // 습관 일기 데이터베이스 관련
    DiaryDatabaseHelper dbDiaryHelper;

    // 습관 일기를 표시할 레이아웃 관련
    private LinearLayout diaryLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // DatabaseHelper 초기화
        dbDiaryHelper = new DiaryDatabaseHelper(this);
        dbHabitHelper = new HabitDatabaseHelper(this);

        backToMainInDiaryBtn = findViewById(R.id.backToMainInDiaryBtn);
        backToDiaryInDiaryBtn = findViewById(R.id.backToDiaryInDiaryBtn);
        backToProgressInDiaryBtn = findViewById(R.id.backToProgressInDiaryBtn);
        addInDiaryBtn = findViewById(R.id.addInDiaryBtn);

        diaryLayout = findViewById(R.id.layout_diary);
        DiaryAdapter.displayDiarys(DiaryActivity.this, dbHabitHelper, dbDiaryHelper, diaryLayout);

        backToMainInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        backToDiaryInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        backToProgressInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                startActivity(intent);
            }
        });

        addInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiaryAdapter.showAddDiaryDialog(DiaryActivity.this, dbHabitHelper, dbDiaryHelper, diaryLayout);
            }
        });
    }
}
