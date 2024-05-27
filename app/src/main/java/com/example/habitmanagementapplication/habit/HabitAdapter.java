package com.example.habitmanagementapplication.habit;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.habitmanagementapplication.R;
import com.example.habitmanagementapplication.activity.MainActivity;
import com.example.habitmanagementapplication.time.TimeInfo;

import java.util.Arrays;
import java.util.List;

public class HabitAdapter {
    // 목표 습관 정보를 입력받기 위한 대화상자
    public static void showAddHabitDialog(Context context, HabitDatabaseHelper dbHelper, LinearLayout habitLayout) {
        View dialogView = (View) View.inflate(context, R.layout.dialog_habit, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);

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
            HabitAdapter.displayHabits(context, dbHelper, habitLayout);
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();

    }

    // 데이터베이스로부터 모든 습관 객체 정보를 가져와서 레이아웃에 추가하는 메서드
    public static void displayHabits(Context context, HabitDatabaseHelper dbHelper, LinearLayout habitLayout) {
        // 데이터베이스로부터 모든 습관 객체 정보를 가져옴
        List<Habit> habitList = dbHelper.getAllHabits();

        // 기존에 있는 카드 뷰들을 모두 제거
        habitLayout.removeAllViews();

        // 각 습관 객체를 카드뷰로 만들어서 레이아웃에 추가
        for (Habit habit : habitList) {

            // 현재 요일에
            // 설정한 수행 요일의 배열은 길이 7인 boolean 타입 배열
            // 0(월)부터 시작하여 6(일)까지 있으며 값이 true인 요일이 설정한 수행 요일
            if (habit.getDailyGoals()[TimeInfo.getCurrentDayOfWeek()]) {
                View habitCardView = createHabitCardView(context, dbHelper, habitLayout, habit);
                habitLayout.addView(habitCardView);
            }
        }
    }

    // 데이터베이스로부터 모든 습관 객체 정보를 가져와서 레이아웃에 추가하는 메서드(데이터베이스의 정보 점검을 위한...)
    public static void displayHabitsForDatabaseReview(Context context, HabitDatabaseHelper dbHelper, LinearLayout habitLayout) {
        // 데이터베이스로부터 모든 습관 객체 정보를 가져옴
        List<Habit> habitList = dbHelper.getAllHabits();

        // 기존에 있는 카드 뷰들을 모두 제거
        habitLayout.removeAllViews();

        // 각 습관 객체를 카드뷰로 만들어서 레이아웃에 추가
        for (Habit habit : habitList) {
            View habitCardView = createHabitCardViewForDatabaseReview(context, dbHelper, habitLayout, habit);
            habitLayout.addView(habitCardView);
        }
    }

    // 습관 객체 정보를 받아서 카드 뷰로 만드는 메서드
    public static View createHabitCardView(Context context, HabitDatabaseHelper dbHelper, LinearLayout habitLayout, Habit habit) {
        // 카드뷰를 생성
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = 5;
        layoutParams.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(layoutParams);

        // 카드 뷰 안에 들어갈 뷰를 생성

        // id 정보를 표시하는 TextView 생성
        TextView idTextView = new TextView(context);
        idTextView.setText("id: " + habit.getId());

        // 제목 정보를 표시하는 TextView 생성
        TextView titleTextView = new TextView(context);
        titleTextView.setText("Title: " + habit.getTitle());

        // 수행 시각 정보를 표시하는 TextView 생성
        TextView timeTextView = new TextView(context);
        timeTextView.setText("Time: " + habit.getHour() + ":" + habit.getMinute());

        // dailyGoals 정보를 표시하는 TextView 생성
        TextView goalsTextView = new TextView(context);
        goalsTextView.setText("Daily Goals: " + Arrays.toString(habit.getDailyGoals()));

        // dailyAchievements 정보를 표시하는 TextView 생성
        TextView achievementsTextView = new TextView(context);
        achievementsTextView.setText("Daily Achievements: " + Arrays.toString(habit.getDailyAchievements()));

        // 달성 버튼 추가
        Button achieveButton = new Button(context);
        achieveButton.setText("달성");

        // 삭제 버튼 추가
        Button deleteButton = new Button(context);
        deleteButton.setText("삭제");

        // 현재 요일의 달성 여부를 확인
        boolean[] achievements = habit.getDailyAchievements();
        int currentDayOfWeek = TimeInfo.getCurrentDayOfWeek();

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
                    displayHabits(context, dbHelper, habitLayout);
                }
            });
        }

        // 삭제 버튼 클릭 이벤트 처리
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 데이터베이스에서 습관 객체 삭제
                dbHelper.deleteHabit(habit);
                // 레이아웃에서 해당 카드 뷰 삭제
                habitLayout.removeView(cardView);
            }
        });

        // 생성한 뷰들을 카드 뷰에 추가
        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(idTextView);
        cardContentLayout.addView(titleTextView);
        cardContentLayout.addView(timeTextView);
        cardContentLayout.addView(goalsTextView); // dailyGoals 정보 추가
        cardContentLayout.addView(achievementsTextView); // dailyAchievements 정보 추가
        cardContentLayout.addView(achieveButton); // 달성 버튼 추가
        cardContentLayout.addView(deleteButton); // 삭제 버튼 추가
        cardView.addView(cardContentLayout);

        return cardView;
    }

    // 습관 객체 정보를 받아서 카드 뷰로 만드는 메서드(데이터베이스의 정보 점검을 위한...)
    public static View createHabitCardViewForDatabaseReview(Context context, HabitDatabaseHelper dbHelper, LinearLayout habitLayout, Habit habit) {
        // 카드뷰를 생성
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = 5;
        layoutParams.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(layoutParams);

        // 카드 뷰 안에 들어갈 뷰를 생성

        // id 정보를 표시하는 TextView 생성
        TextView idTextView = new TextView(context);
        idTextView.setText("id: " + habit.getId());

        // 제목 정보를 표시하는 TextView 생성
        TextView titleTextView = new TextView(context);
        titleTextView.setText("Title: " + habit.getTitle());

        // 수행 시각 정보를 표시하는 TextView 생성
        TextView timeTextView = new TextView(context);
        timeTextView.setText("Time: " + habit.getHour() + ":" + habit.getMinute());

        // dailyGoals 정보를 표시하는 TextView 생성
        TextView goalsTextView = new TextView(context);
        goalsTextView.setText("Daily Goals: " + Arrays.toString(habit.getDailyGoals()));

        // dailyAchievements 정보를 표시하는 TextView 생성
        TextView achievementsTextView = new TextView(context);
        achievementsTextView.setText("Daily Achievements: " + Arrays.toString(habit.getDailyAchievements()));

        // 삭제 버튼 추가
        Button deleteButton = new Button(context);
        deleteButton.setText("삭제");

        // 현재 요일의 달성 여부를 확인
        boolean[] achievements = habit.getDailyAchievements();
        int currentDayOfWeek = TimeInfo.getCurrentDayOfWeek();

        // 삭제 버튼 클릭 이벤트 처리
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 데이터베이스에서 습관 객체 삭제
                dbHelper.deleteHabit(habit);
                // 레이아웃에서 해당 카드 뷰 삭제
                habitLayout.removeView(cardView);
            }
        });

        // 생성한 뷰들을 카드 뷰에 추가
        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(idTextView);
        cardContentLayout.addView(titleTextView);
        cardContentLayout.addView(timeTextView);
        cardContentLayout.addView(goalsTextView); // dailyGoals 정보 추가
        cardContentLayout.addView(achievementsTextView); // dailyAchievements 정보 추가
        cardContentLayout.addView(deleteButton); // 삭제 버튼 추가
        cardView.addView(cardContentLayout);

        return cardView;
    }
}
