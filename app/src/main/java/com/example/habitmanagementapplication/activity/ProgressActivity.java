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
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;

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

    // 목표 습관을 표시할 레이아웃 관련
    private LinearLayout habitLayout;

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

        habitLayout = findViewById(R.id.layout_habit);

        backToMainInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "메인 화면으로 돌아가기(현황 화면에서)", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        backToDiaryInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "일기 화면으로 돌아가기(현황 화면에서)", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });

        backToProgressInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "현황 화면으로 돌아가기(현황 화면에서)", Toast.LENGTH_SHORT).show();
            }
        });

        showAllDataInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "모든 정보 보기(현황 화면에서)", Toast.LENGTH_SHORT).show();

                displayHabits();
            }
        });
    }

    // 데이터베이스로부터 모든 습관 객체 정보를 가져와서 레이아웃에 추가하는 메서드
    private void displayHabits() {
        // 데이터베이스로부터 모든 습관 객체 정보를 가져옴
        List<Habit> habitList = dbHelper.getAllHabits();

        // 기존에 있는 카드 뷰들을 모두 제거
        habitLayout.removeAllViews();

        // 각 습관 객체를 카드뷰로 만들어서 레이아웃에 추가
        for (Habit habit : habitList) {
            View habitCardView = createHabitCardView(habit);
            habitLayout.addView(habitCardView);
        }
    }

    // 습관 객체 정보를 받아서 카드 뷰로 만드는 메서드
    private View createHabitCardView(Habit habit) {
        // 카드뷰를 생성
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = 5;
        layoutParams.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(layoutParams);

        // 카드 뷰 안에 들어갈 뷰를 생성
        TextView idTextView = new TextView(this);
        idTextView.setText("id: " + habit.getId());

        TextView titleTextView = new TextView(this);
        titleTextView.setText("Title: " + habit.getTitle());

        // 수행 시각 정보를 표시하는 TextView 생성
        TextView timeTextView = new TextView(this);
        timeTextView.setText("Time: " + habit.getHour() + ":" + habit.getMinute());

        // dailyGoals 정보를 표시하는 TextView 생성
        TextView goalsTextView = new TextView(this);
        goalsTextView.setText("Daily Goals: " + Arrays.toString(habit.getDailyGoals()));

        // dailyAchievements 정보를 표시하는 TextView 생성
        TextView achievementsTextView = new TextView(this);
        achievementsTextView.setText("Daily Achievements: " + Arrays.toString(habit.getDailyAchievements()));

        // 생성한 뷰들을 카드 뷰에 추가
        LinearLayout cardContentLayout = new LinearLayout(this);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(idTextView);
        cardContentLayout.addView(titleTextView);
        cardContentLayout.addView(timeTextView);
        cardContentLayout.addView(goalsTextView); // dailyGoals 정보 추가
        cardContentLayout.addView(achievementsTextView); // dailyAchievements 정보 추가
        cardView.addView(cardContentLayout);

        return cardView;
    }
}
