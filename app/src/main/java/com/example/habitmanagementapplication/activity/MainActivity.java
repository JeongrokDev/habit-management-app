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

    // 목표 습관 정보를 입력받기 위한 대화상자
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

            Habit habit = new Habit(0, title, hour, minute, dailyGoals, dailyAchievements); // 목표 습관 객체 생성

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
            
            // 현재 요일에 
            // 설정한 수행 요일의 배열은 길이 7인 boolean 타입 배열
            // 0(월)부터 시작하여 6(일)까지 있으며 값이 true인 요일이 설정한 수행 요일
            if (habit.getDailyGoals()[getCurrentDayOfWeek()]) {
                View habitCardView = createHabitCardView(habit);
                habitLayout.addView(habitCardView);
            }
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
        
        // id 정보를 표시하는 TextView 생성
        TextView idTextView = new TextView(this);
        idTextView.setText("id: " + habit.getId());

        // 제목 정보를 표시하는 TextView 생성
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

        // 달성 버튼 추가
        Button achieveButton = new Button(this);
        achieveButton.setText("달성");

        // 현재 요일의 달성 여부를 확인
        boolean[] achievements = habit.getDailyAchievements();
        int currentDayOfWeek = getCurrentDayOfWeek();

        // 만약 해당 요일에 이미 달성 여부가 true이면 버튼을 비활성화
        if (achievements[currentDayOfWeek]) {
            achieveButton.setEnabled(false);
        } else {
            achieveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 현재 요일에 해당하는 달성 여부를 true로 변경
                    achievements[currentDayOfWeek] = true;
                    habit.setDailyAchievements(achievements);

                    // 데이터베이스에서 습관 정보 업데이트
                    dbHelper.updateHabit(habit);
                    displayHabits();
                }
            });
        }

        // 생성한 뷰들을 카드 뷰에 추가
        LinearLayout cardContentLayout = new LinearLayout(this);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(idTextView);
        cardContentLayout.addView(titleTextView);
        cardContentLayout.addView(timeTextView);
        cardContentLayout.addView(goalsTextView); // dailyGoals 정보 추가
        cardContentLayout.addView(achievementsTextView); // dailyAchievements 정보 추가
        cardContentLayout.addView(achieveButton); // 달성 버튼 추가
        cardView.addView(cardContentLayout);

        return cardView;
    }

    // 현재 요일 정보를 가져오는 메서드(0: 월요일, 1: 화요일, ..., 6: 일요일)
    private int getCurrentDayOfWeek() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        // Date 객체로부터 Calendar 객체 생성
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // 현재 요일 가져오기
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 월요일부터 시작하도록 인덱스 조정
        currentDayOfWeek = (currentDayOfWeek + 5) % 7;

        return currentDayOfWeek;
    }
}