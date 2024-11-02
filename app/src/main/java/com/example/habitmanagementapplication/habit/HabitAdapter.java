package com.example.habitmanagementapplication.habit;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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
import com.example.habitmanagementapplication.diary.DiaryDatabaseHelper;
import com.example.habitmanagementapplication.time.TimeInfo;

import java.util.Arrays;
import java.util.List;

public class HabitAdapter {
    // 목표 습관 정보를 입력받기 위한 대화상자를 표시하는 메서드
    public static void showAddHabitDialog(Context context, HabitDatabaseHelper dbHelper, LinearLayout habitLayout) {
        // 대화상자 레이아웃 설정 및 뷰 초기화
        View dialogView = (View) View.inflate(context, R.layout.dialog_habit, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);

        dlg.setTitle("목표 습관 정보 입력");
        dlg.setView(dialogView);

        // "확인" 버튼 클릭 시 입력된 정보를 가져와서 새로운 습관 객체 생성 및 데이터베이스에 저장
        dlg.setPositiveButton("확인", (dialog, which) -> {
            EditText selectedTitle = dialogView.findViewById(R.id.selectedTitle);

            TimePicker selectedTime = dialogView.findViewById(R.id.selectedTime);

            // 요일 체크박스 초기화
            CheckBox checkBoxMonday = dialogView.findViewById(R.id.checkBoxMonday);
            CheckBox checkBoxTuesday = dialogView.findViewById(R.id.checkBoxTuesday);
            CheckBox checkBoxWednesday = dialogView.findViewById(R.id.checkBoxWednesday);
            CheckBox checkBoxThursday = dialogView.findViewById(R.id.checkBoxThursday);
            CheckBox checkBoxFriday = dialogView.findViewById(R.id.checkBoxFriday);
            CheckBox checkBoxSaturday = dialogView.findViewById(R.id.checkBoxSaturday);
            CheckBox checkBoxSunday = dialogView.findViewById(R.id.checkBoxSunday);

            // 제목 및 수행 시간 가져오기
            String title = selectedTitle.getText().toString();
            String hour = (Integer.toString(selectedTime.getHour()));
            String minute = (Integer.toString(selectedTime.getMinute()));

            // 요일별 수행 여부 설정
            boolean[] dailyGoals = {
                    checkBoxMonday.isChecked(),
                    checkBoxTuesday.isChecked(),
                    checkBoxWednesday.isChecked(),
                    checkBoxThursday.isChecked(),
                    checkBoxFriday.isChecked(),
                    checkBoxSaturday.isChecked(),
                    checkBoxSunday.isChecked(),
            };

            boolean[] dailyAchievements = new boolean[7]; // 초기 달성 상태는 모두 미달성으로 설정

            // 새 습관 객체 생성 및 데이터베이스에 저장 후 화면에 표시
            Habit habit = new Habit(0, title, hour, minute, dailyGoals, dailyAchievements);
            dbHelper.insertHabit(habit);
            HabitAdapter.displayHabits(context, dbHelper, habitLayout);
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();

    }

    // 데이터베이스에서 습관 객체를 가져와 현재 요일의 습관을 habitLayout에 추가하는 메서드
    public static void displayHabits(Context context, HabitDatabaseHelper dbHelper, LinearLayout habitLayout) {
        List<Habit> habitList = dbHelper.getAllHabits();
        habitLayout.removeAllViews(); // 기존 뷰 초기화

        for (Habit habit : habitList) {
            // 현재 요일에 수행해야 하는 습관만 추가
            if (habit.getDailyGoals()[TimeInfo.getCurrentDayOfWeek()]) {
                View habitCardView = createHabitCardView(context, dbHelper, habitLayout, habit);
                habitLayout.addView(habitCardView);
            }
        }
    }

    // 데이터베이스에 있는 모든 습관 정보를 habitLayout에 표시하는 메서드 (데이터베이스 점검용)
    public static void displayHabitsForDatabaseReview(Context context, HabitDatabaseHelper dbHelper1, DiaryDatabaseHelper dbHelper2, LinearLayout habitLayout) {
        List<Habit> habitList = dbHelper1.getAllHabits();
        habitLayout.removeAllViews(); // 기존 뷰 초기화

        // 상단에 두 개의 버튼을 담을 레이아웃 생성
        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        // 더미 데이터 삽입 버튼 생성(습관)
        Button insertDummyHabitButton = new Button(context);
        insertDummyHabitButton.setText("더미 습관 삽입");
        insertDummyHabitButton.setOnClickListener(v -> {
            dbHelper1.insertDummyHabits();
            displayHabitsForDatabaseReview(context, dbHelper1, dbHelper2, habitLayout);
        });

        // 더미 데이터 삽입 버튼 생성(일기)
        Button insertDummyDairyButton = new Button(context);
        insertDummyDairyButton.setText("더미 일기 삽입");
        insertDummyDairyButton.setOnClickListener(v -> {
            dbHelper2.insertDummyDiaryData();
            displayHabitsForDatabaseReview(context, dbHelper1, dbHelper2, habitLayout);
        });

        // 버튼 레이아웃에 버튼 추가
        buttonLayout.addView(insertDummyHabitButton);
        buttonLayout.addView(insertDummyDairyButton);

        // 상단 버튼 레이아웃을 habitLayout에 추가
        habitLayout.addView(buttonLayout);

        for (Habit habit : habitList) {
            View habitCardView = createHabitCardViewForDatabaseReview(context, dbHelper1, habitLayout, habit);
            habitLayout.addView(habitCardView);
        }
    }

    // 습관 객체 정보를 기반으로 카드 뷰를 생성하는 메서드
    public static View createHabitCardView(Context context, HabitDatabaseHelper dbHelper, LinearLayout habitLayout, Habit habit) {
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = 16;
        layoutParams.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(20);
        cardView.setCardElevation(8);
//        cardView.setContentPadding(24, 24, 24, 24);
//        cardView.setBackgroundColor(0xFFF1F8E9); // 카드 배경색: 연한 초록색

        // 카드 내부 콘텐츠 레이아웃
        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(0xFFF1F8E9); // 연한 초록색 배경
        backgroundDrawable.setCornerRadius(24); // 모서리 둥글게
        cardContentLayout.setBackground(backgroundDrawable);
        cardContentLayout.setPadding(24, 24, 24, 24);

        // 제목 및 삭제 버튼 레이아웃
        LinearLayout titleLayout = new LinearLayout(context);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // 습관 제목 텍스트
        TextView titleTextView = new TextView(context);
        titleTextView.setText(habit.getTitle());
        titleTextView.setTextColor(0xFF388E3C);
        titleTextView.setTextSize(22);
        titleTextView.setPadding(0, 0, 0, 8);
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // 삭제 버튼 설정 및 클릭 이벤트
        Button deleteButton = new Button(context);
        deleteButton.setText("삭제");
        deleteButton.setTextColor(0xFF888888);
        deleteButton.setBackground(new android.graphics.drawable.GradientDrawable() {{
            setCornerRadius(50);
            setColor(0xFFF0F0F0);
        }});
        deleteButton.setPadding(20, 10, 20, 10);
        deleteButton.setTextSize(12);
        deleteButton.setLayoutParams(new LinearLayout.LayoutParams(
                100, 100
        ));

        deleteButton.setOnClickListener(v -> {
            dbHelper.deleteHabit(habit); // 데이터베이스에서 습관 삭제
            habitLayout.removeView(cardView); // 화면에서도 해당 카드 뷰 제거
        });

        titleLayout.addView(titleTextView);
        titleLayout.addView(deleteButton);

        // 수행 시간 텍스트
        TextView timeTextView = new TextView(context);
        // 문자열로 반환되는 시간을 정수로 변환
        int hour = Integer.parseInt(habit.getHour());
        int minute = Integer.parseInt(habit.getMinute());

        // 오전/오후 결정
        String period = (hour < 12) ? "오전" : "오후";

        // 12시간제로 변환
        hour = (hour == 0) ? 12 : (hour > 12) ? hour - 12 : hour;

        // 분을 두 자리로 표시
        String minuteStr = (minute == 0) ? "00" : String.format("%02d", minute);

        // TextView에 설정
        timeTextView.setText("수행 시간: " + period + " " + hour + ":" + minuteStr);
        timeTextView.setTextColor(0xFF666666);
        timeTextView.setTextSize(16);
        timeTextView.setPadding(0, 0, 0, 16);

        // 요일별 목표와 달성 상태를 표시하는 레이아웃
        LinearLayout daysLayout = new LinearLayout(context);
        daysLayout.setOrientation(LinearLayout.HORIZONTAL);
        daysLayout.setPadding(0, 8, 0, 8);

        String[] daysOfWeek = {"월", "화", "수", "목", "금", "토", "일"};
        boolean[] dailyGoals = habit.getDailyGoals();
        boolean[] dailyAchievements = habit.getDailyAchievements();
        int currentDayOfWeek = TimeInfo.getCurrentDayOfWeek();

        for (int i = 0; i < daysOfWeek.length; i++) {
            TextView dayTextView = new TextView(context);
            dayTextView.setText(daysOfWeek[i]);
            dayTextView.setTextSize(14);
            dayTextView.setGravity(android.view.Gravity.CENTER);
            dayTextView.setPadding(12, 12, 12, 12);

            LinearLayout.LayoutParams dayParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
            );
            dayParams.setMargins(4, 0, 4, 0);
            dayTextView.setLayoutParams(dayParams);

            dayTextView.setBackground(new android.graphics.drawable.GradientDrawable() {{
                setCornerRadius(50);
                setColor(0xFFE0E6EB); // 기본 회색 배경
            }});

            if (dailyGoals[i]) { // 수행 요일이면 색상 변경
                if (dailyAchievements[i]) {
                    // 수행해야 하는 요일인데 완료한 경우
                    ((android.graphics.drawable.GradientDrawable) dayTextView.getBackground())
                            .setColor(0xFF4CAF50); // 초록색: 완료된 수행 요일
                    dayTextView.setTextColor(0xFFFFFFFF);
                    dayTextView.setText("✓");
                } else {
                    // 수행해야 하는 요일인데 미완료한 경우
                    ((android.graphics.drawable.GradientDrawable) dayTextView.getBackground())
                            .setColor(0xFFA5D6A7); // 파란색: 수행해야 하는 요일
                    dayTextView.setTextColor(0xFFFFFFFF);
                }
            } else {
                dayTextView.setTextColor(0xFF666666);
            }

            if (i == currentDayOfWeek) {
                dayTextView.setTextSize(16);
                dayTextView.setTypeface(null, android.graphics.Typeface.BOLD);
                ((android.graphics.drawable.GradientDrawable) dayTextView.getBackground())
                        .setColor(0xFF0D47A1);
            }

            daysLayout.addView(dayTextView);
        }

        // 완료 버튼 설정 및 클릭 이벤트
        Button achieveButton = new Button(context);
        achieveButton.setText("완료");
        achieveButton.setTextColor(0xFFFFFFFF);
        achieveButton.setBackground(new android.graphics.drawable.GradientDrawable() {{
            setCornerRadius(100);
            setColor(0xFF388E3C); // 녹색 배경 (활성화 상태)
        }});
        achieveButton.setPadding(32, 16, 32, 16);
        achieveButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        if (dailyAchievements[currentDayOfWeek]) {
            achieveButton.setEnabled(false);
            achieveButton.setText("✓ 완료됨");
            achieveButton.setBackground(new android.graphics.drawable.GradientDrawable() {{
                setCornerRadius(30);
                setColor(0xFFE0E0E0); // 옅은 회색 배경 (비활성화 상태)
            }});
            achieveButton.setTextColor(0xFF757575); // 짙은 회색 텍스트 색상
        } else {
            achieveButton.setOnClickListener(v -> {
                dailyAchievements[currentDayOfWeek] = true;
                habit.setDailyAchievements(dailyAchievements);
                dbHelper.updateHabit(habit);
                displayHabits(context, dbHelper, habitLayout);
            });
        }

        // 뷰 추가 및 카드 뷰 반환
        cardContentLayout.addView(titleLayout);
        cardContentLayout.addView(timeTextView);
        cardContentLayout.addView(daysLayout);
        cardContentLayout.addView(achieveButton);
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
