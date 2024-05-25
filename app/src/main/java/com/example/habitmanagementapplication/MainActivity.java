package com.example.habitmanagementapplication;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
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

        backToMainInMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "메인 화면으로 돌아가기(메인 화면에서)", Toast.LENGTH_SHORT).show();
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
                showAddHabitDialog();
            }
        });

        habitLayout = findViewById(R.id.layout_habit);
        displayHabits();
    }

    // 사용자로부터 목표 습관 정보를 입력받기 위한 대화상자
    private void showAddHabitDialog() {
        View dialogView = (View) View.inflate(MainActivity.this, R.layout.dialog_habit, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);

        dlg.setTitle("목표 습관 정보 입력");
        dlg.setView(dialogView);
        dlg.setPositiveButton("확인", (dialog, which) -> {
            EditText selectedTitle = dialogView.findViewById(R.id.selectedTitle);

            TimePicker selectedTime = dialogView.findViewById(R.id.selectedTime);

            CheckBox checkBoxMonday = dialogView.findViewById(R.id.checkBoxMonday);
            CheckBox checkBoxTuesday = dialogView.findViewById(R.id.checkBoxTuesday);
            CheckBox checkBoxWednesday = dialogView.findViewById(R.id.checkBoxWednesday);
            CheckBox checkBoxThursday = dialogView.findViewById(R.id.checkBoxThursday);
            CheckBox checkBoxFriday = dialogView.findViewById(R.id.checkBoxFriday);
            CheckBox checkBoxSaturday = dialogView.findViewById(R.id.checkBoxSaturday);
            CheckBox checkBoxSunday = dialogView.findViewById(R.id.checkBoxSunday);

            String title = selectedTitle.getText().toString();

            String hour = (Integer.toString(selectedTime.getHour()));
            String minute = (Integer.toString(selectedTime.getMinute()));

            boolean[] dailyGoals = {
                    checkBoxMonday.isChecked(),
                    checkBoxTuesday.isChecked(),
                    checkBoxWednesday.isChecked(),
                    checkBoxThursday.isChecked(),
                    checkBoxFriday.isChecked(),
                    checkBoxSaturday.isChecked(),
                    checkBoxSunday.isChecked(),
            };

            boolean[] dailyAchievements = new boolean[7];

            Habit habit = new Habit(title, hour, minute, dailyGoals, dailyAchievements); // 목표 습관 객체 생성

            // 생성한 목표 습관 객체를 데이터베이스에 저장
            dbHelper.insertHabit(habit);
            displayHabits();
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();

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
        cardContentLayout.addView(titleTextView);
        cardContentLayout.addView(timeTextView);
        cardContentLayout.addView(goalsTextView); // dailyGoals 정보 추가
        cardContentLayout.addView(achievementsTextView); // dailyAchievements 정보 추가
        cardView.addView(cardContentLayout);

        return cardView;
    }
}