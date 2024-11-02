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
    // 상단에 위치한 4개의 버튼 정의
    Button backToMainInDiaryBtn;
    Button backToDiaryInDiaryBtn;
    Button backToProgressInDiaryBtn;
    Button addInDiaryBtn;
    Button dateInDiaryBtn;
    TextView todayDateTextView;

    // 습관 데이터베이스 관리 객체
    HabitDatabaseHelper dbHabitHelper;

    // 일기 데이터베이스 관리 객체
    DiaryDatabaseHelper dbDiaryHelper;

    // 일기 항목을 표시할 레이아웃
    private LinearLayout diaryLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // DatabaseHelper 초기화
        dbDiaryHelper = new DiaryDatabaseHelper(this);
        dbHabitHelper = new HabitDatabaseHelper(this);

        // 상단 버튼 초기화 및 레이아웃 연결
        backToMainInDiaryBtn = findViewById(R.id.backToMainInDiaryBtn);
        backToDiaryInDiaryBtn = findViewById(R.id.backToDiaryInDiaryBtn);
        backToProgressInDiaryBtn = findViewById(R.id.backToProgressInDiaryBtn);
        addInDiaryBtn = findViewById(R.id.addInDiaryBtn);
        dateInDiaryBtn = findViewById(R.id.dateInDiary);

        // 현재 날짜를 버튼에 표시
        String currentDate = new SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(new Date());
        dateInDiaryBtn.setText(currentDate);

        // 일기 레이아웃 설정 및 일기 목록 표시
        diaryLayout = findViewById(R.id.layout_diary);
        DiaryAdapter.displayDiarys(DiaryActivity.this, dbHabitHelper, dbDiaryHelper, diaryLayout);

        // 메인 버튼 클릭 시
        backToMainInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 일기 화면으로 이동 버튼 클릭 시 일기 목록 새로고침
        backToDiaryInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // 진행 상황 화면으로 이동 버튼 클릭 시
        backToProgressInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                startActivity(intent);
            }
        });

        // 새로운 일기 추가 버튼 클릭 시 다이얼로그 표시
        addInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiaryAdapter.showAddDiaryDialog(DiaryActivity.this, dbHabitHelper, dbDiaryHelper, diaryLayout);
            }
        });

        // 날짜를 텍스트뷰에 표시
        TextView todayDateTextView = findViewById(R.id.todayDateTextViewInDiary);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd (E)", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        String displayText = "일기 목록 ";
        todayDateTextView.setText(displayText);
    }
}
