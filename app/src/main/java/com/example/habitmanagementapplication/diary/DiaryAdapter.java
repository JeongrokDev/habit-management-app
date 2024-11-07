package com.example.habitmanagementapplication.diary;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.habitmanagementapplication.R;
//import com.example.habitmanagementapplication.feedback.ChatGptManager;
import com.example.habitmanagementapplication.feedback.ApiClient;
import com.example.habitmanagementapplication.feedback.ChatRequest;
import com.example.habitmanagementapplication.feedback.ChatResponse;
import com.example.habitmanagementapplication.feedback.OpenAIApi;
import com.example.habitmanagementapplication.habit.Habit;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;
import com.example.habitmanagementapplication.time.TimeInfo;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DiaryAdapter {
    private static final int MAX_RETRY_COUNT = 3;

    // 일기 정보를 입력받기 위한 대화상자
    public static void showAddDiaryDialog(Context context, HabitDatabaseHelper dbHabitHelper, DiaryDatabaseHelper dbDiaryHelper, LinearLayout diaryLayout) {
        View dialogView = (View) View.inflate(context, R.layout.dialog_diary, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);

        // 완료 및 미완료 습관 표시 설정 관련
        List<Habit> habitList = dbHabitHelper.getAllHabits();

        // `@`로 구분할 문자열을 생성할 StringBuilder 초기화
        StringBuilder completedHabitsBuilder = new StringBuilder();
        StringBuilder incompletedHabitsBuilder = new StringBuilder();

        TextView textViewCompletedhabitList = dialogView.findViewById(R.id.textViewCompletedHabitList);
        textViewCompletedhabitList.setText("완료한 습관: ");
        TextView textViewInCompletedhabitList = dialogView.findViewById(R.id.textViewInCompletedHabitList);
        textViewInCompletedhabitList.setText("미완료한 습관: ");

        TextView completedHabitList = dialogView.findViewById(R.id.completedHabitList);
        TextView inCompletedHabitList = dialogView.findViewById(R.id.inCompletedHabitList);

        for (Habit habit : habitList) {

            // 현재 요일에서 완료 및 미완료 습관 객체 가져와서 대화상자에 표시
            if (habit.getDailyGoals()[TimeInfo.getCurrentDayOfWeek()]) {
                if (habit.getDailyAchievements()[TimeInfo.getCurrentDayOfWeek()]) {
                    completedHabitList.setText(completedHabitList.getText().toString() + habit.getTitle() + ", ");

                    completedHabitsBuilder.append(habit.getTitle()).append("@"); // `@` 추가
                } else {
                    inCompletedHabitList.setText(inCompletedHabitList.getText().toString() + habit.getTitle() + ", ");

                    incompletedHabitsBuilder.append(habit.getTitle()).append("@"); // `@` 추가
                }
            }
        }

        // 미자막의 추가되는 ", " 제거 로직
        String completedText = completedHabitList.getText().toString();
        if (completedText.endsWith(", ")) {
            completedText = completedText.substring(0, completedText.length() - 2); // 마지막 ", " 제거
        }
        completedHabitList.setText(completedText);

        // 미자막의 추가되는 ", " 제거 로직
        String incompletedText = inCompletedHabitList.getText().toString();
        if (incompletedText.endsWith(", ")) {
            incompletedText = incompletedText.substring(0, incompletedText.length() - 2); // 마지막 ", " 제거
        }
        inCompletedHabitList.setText(incompletedText);

        // 생성한 문자열에서 마지막 `@` 제거
        String completedHabits = completedHabitsBuilder.length() > 0
                ? completedHabitsBuilder.substring(0, completedHabitsBuilder.length() - 1)
                : "";
        String incompletedHabits = incompletedHabitsBuilder.length() > 0
                ? incompletedHabitsBuilder.substring(0, incompletedHabitsBuilder.length() - 1)
                : "";

        dlg.setTitle("습관 일기 정보 입력");
        dlg.setView(dialogView);
        dlg.setPositiveButton("확인", (dialog, which) -> {
            EditText editDiaryContent = dialogView.findViewById(R.id.editDiaryContent);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            String createdDate = sdf.format(new Date());
            String diaryContent = editDiaryContent.getText().toString();
            String feedbackContent = "";

            Diary diary = new Diary(0, createdDate, diaryContent, feedbackContent, false, completedHabits, incompletedHabits); // 습관 일기 객체 생성

            // 생성한 습관 일기 객체를 데이터베이스에 저장
            dbDiaryHelper.insertDiary(diary);
            displayDiarys(context, dbHabitHelper, dbDiaryHelper, diaryLayout);
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();

    }

    // 데이터베이스로부터 모든 일기 객체 정보를 가져와서 레이아웃에 추가하는 메서드
    public static void displayDiarys(Context context, HabitDatabaseHelper dbHabitHelper, DiaryDatabaseHelper dbDiaryHelper, LinearLayout diaryLayout) {
        // 데이터베이스로부터 모든 일기 객체 정보를 가져옴
        List<Diary> diaryList = dbDiaryHelper.getAllDiaries();

        // 기존에 있는 카드 뷰들을 모두 제거
        diaryLayout.removeAllViews();

        // 각 일기 객체를 카드 뷰로 만들어서 레이아웃에 추가
        for (Diary diary : diaryList) {
            View diaryCardView = createDiaryCardView(context, dbHabitHelper, dbDiaryHelper, diaryLayout, diary);
            diaryLayout.addView(diaryCardView);
        }
    }

    // 일기 객체 정보를 받아서 카드 뷰로 만드는 메서드
    public static View createDiaryCardView(Context context, HabitDatabaseHelper dbHabitHelper, DiaryDatabaseHelper dbDiaryHelper, LinearLayout diaryLayout, Diary diary) {
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = 16;
        layoutParams.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(20);
        cardView.setCardElevation(10);
//        cardView.setContentPadding(32, 32, 32, 32);
//        cardView.setBackgroundColor(0xFFFFFFFF);

        // 카드 콘텐츠 레이아웃
        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(0xFFEDE7F6); // 연한 초록색 배경
        backgroundDrawable.setCornerRadius(24); // 모서리 둥글게
        cardContentLayout.setBackground(backgroundDrawable);
        cardContentLayout.setPadding(24, 24, 24, 24);

        // 상단 레이아웃: 작성 날짜와 삭제 버튼
        LinearLayout topLayout = new LinearLayout(context);
        topLayout.setOrientation(LinearLayout.HORIZONTAL);
        topLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // 작성 날짜
        TextView createdDateText = new TextView(context);
        createdDateText.setText(diary.getCreatedDate());
        createdDateText.setTextColor(0xFF3F51B5);
        createdDateText.setTextSize(18);
        createdDateText.setTypeface(null, android.graphics.Typeface.BOLD);
        createdDateText.setPadding(0, 0, 16, 0);
        createdDateText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        // 삭제 버튼 (오른쪽 상단에 작게 배치)
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
            dbDiaryHelper.deleteDiary(diary);
            diaryLayout.removeView(cardView);
        });

        // 상단 레이아웃에 추가
        topLayout.addView(createdDateText);
        topLayout.addView(deleteButton);

        // 일기 내용 섹션
        TextView diaryContentLabel = new TextView(context);
        diaryContentLabel.setText("일기 내용");
        diaryContentLabel.setTextColor(0xFF757575);
        diaryContentLabel.setTextSize(14);
        diaryContentLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        diaryContentLabel.setPadding(0, 16, 0, 8);

        TextView diaryContentText = new TextView(context);
        diaryContentText.setText(diary.getDiaryContent());
        diaryContentText.setTextColor(0xFF444444);
        diaryContentText.setTextSize(16);
        diaryContentText.setBackground(new android.graphics.drawable.GradientDrawable() {{
            setColor(0xFFF5F5F5);
            setCornerRadius(10);
        }});
        diaryContentText.setPadding(20, 20, 20, 20);

        // 습관 달성 현황 섹션 (가로 배치)
        TextView habitStatusLabel = new TextView(context);
        habitStatusLabel.setText("습관 달성 현황");
        habitStatusLabel.setTextColor(0xFF757575);
        habitStatusLabel.setTextSize(14);
        habitStatusLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        habitStatusLabel.setPadding(0, 16, 0, 8);

        LinearLayout habitStatusLayout1 = new LinearLayout(context);
        habitStatusLayout1.setOrientation(LinearLayout.HORIZONTAL);
        habitStatusLayout1.setPadding(0, 8, 0, 8);
        habitStatusLayout1.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        LinearLayout habitStatusLayout2 = new LinearLayout(context);
        habitStatusLayout2.setOrientation(LinearLayout.HORIZONTAL);
        habitStatusLayout2.setPadding(0, 8, 0, 8);
        habitStatusLayout2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // 완료한 습관 리스트 가져오기
        String[] completedHabits = diary.getCompletedHabits().split("@");

        // 미완료한 습관 리스트 가져오기
        String[] incompletedHabits = diary.getIncompletedHabits().split("@");

        // 완료한 습관 표시
        for (String habitTitle : completedHabits) {
            if (!habitTitle.isEmpty()) { // 비어있지 않은 경우만 표시
                TextView habitStatusText = new TextView(context);
                habitStatusText.setText(habitTitle);
                habitStatusText.setTextSize(14);
                habitStatusText.setPadding(20, 10, 20, 10);
                habitStatusText.setBackground(new android.graphics.drawable.GradientDrawable() {{
                    setColor(0xFFA5D6A7); // 연한 초록색 배경 (달성한 습관)
                    setCornerRadius(30);
                }});
                habitStatusText.setTextColor(0xFF2E7D32); // 짙은 초록색 텍스트
                habitStatusText.setGravity(android.view.Gravity.CENTER);
                habitStatusLayout1.addView(habitStatusText);
            }
        }

        // 미완료한 습관 표시
        for (String habitTitle : incompletedHabits) {
            if (!habitTitle.isEmpty()) { // 비어있지 않은 경우만 표시
                TextView habitStatusText = new TextView(context);
                habitStatusText.setText(habitTitle);
                habitStatusText.setTextSize(14);
                habitStatusText.setPadding(20, 10, 20, 10);
                habitStatusText.setBackground(new android.graphics.drawable.GradientDrawable() {{
                    setColor(0xFFEF9A9A); // 연한 빨간색 배경 (달성하지 못한 습관)
                    setCornerRadius(30);
                }});
                habitStatusText.setTextColor(0xFFC62828); // 짙은 빨간색 텍스트
                habitStatusText.setGravity(android.view.Gravity.CENTER);
                habitStatusLayout2.addView(habitStatusText);
            }
        }

        // 피드백 내용 섹션
        TextView feedbackLabel = new TextView(context);
        feedbackLabel.setText("피드백");
        feedbackLabel.setTextColor(0xFF757575);
        feedbackLabel.setTextSize(14);
        feedbackLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        feedbackLabel.setPadding(0, 16, 0, 8);

        TextView feedbackText = new TextView(context);
        feedbackText.setText(diary.getFeedbackContent() != null ? diary.getFeedbackContent() : "피드백이 없습니다");
        feedbackText.setTextColor(0xFF666666);
        feedbackText.setTextSize(16);
        feedbackText.setBackground(new android.graphics.drawable.GradientDrawable() {{
            setColor(0xFFF5F5F5);
            setCornerRadius(10);
        }});
        feedbackText.setPadding(20, 20, 20, 20);

        // 피드백 버튼 (하단 중앙에 배치)
        Button feedbackButton = new Button(context);
        feedbackButton.setText("피드백 요청");
        feedbackButton.setTextColor(0xFFFFFFFF);
        feedbackButton.setBackground(new android.graphics.drawable.GradientDrawable() {{
            setCornerRadius(50);
            setColor(0xFF3F51B5);
        }});
        feedbackButton.setPadding(40, 20, 40, 20);
        feedbackButton.setTextSize(16);

        // 하단 중앙 정렬을 위한 레이아웃 설정
        LinearLayout.LayoutParams feedbackButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        feedbackButtonParams.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        feedbackButtonParams.setMargins(0, 24, 0, 0);
        feedbackButton.setLayoutParams(feedbackButtonParams);

        // 피드백 버튼 클릭 이벤트 처리
        if (diary.getFeedbackReceived()) {
            feedbackButton.setEnabled(false);
            feedbackButton.setText("피드백 완료");

            // 둥근 모서리를 위해 GradientDrawable 설정
            GradientDrawable completedButtonBackground = new GradientDrawable();
            completedButtonBackground.setColor(0xFF888888); // 완료된 버튼의 회색 배경색
            completedButtonBackground.setCornerRadius(40); // 모서리 둥글게 설정

            feedbackButton.setBackground(completedButtonBackground);
            feedbackButton.setTextColor(0xFFFFFFFF); // 흰색 텍스트
        } else {
            feedbackButton.setOnClickListener(v -> requestFeedback(context, dbHabitHelper, dbDiaryHelper, diaryLayout, diary));
        }

        // 뷰 추가 및 카드 뷰 반환
        cardContentLayout.addView(topLayout);           // 상단 레이아웃
        cardContentLayout.addView(diaryContentLabel);   // 일기 내용 레이블
        cardContentLayout.addView(diaryContentText);    // 일기 내용
        cardContentLayout.addView(habitStatusLabel);    // 습관 달성 현황 레이블
        cardContentLayout.addView(habitStatusLayout1);   // 습관 달성 현황 (가로 배치)
        cardContentLayout.addView(habitStatusLayout2);   // 습관 달성 현황 (가로 배치)
        cardContentLayout.addView(feedbackLabel);       // 피드백 레이블
        cardContentLayout.addView(feedbackText);        // 피드백 내용
        cardContentLayout.addView(feedbackButton);      // 피드백 버튼
        cardView.addView(cardContentLayout);

        return cardView;
    }


    private static void requestFeedback(Context context, HabitDatabaseHelper dbHabitHelper, DiaryDatabaseHelper dbDiaryHelper, LinearLayout diaryLayout, Diary diary) {
        Retrofit retrofit = ApiClient.getClient();
        OpenAIApi apiService = retrofit.create(OpenAIApi.class);

        String savedMessage = "안녕하세요! 오늘의 습관 성취도와 일기 내용을 기반으로 사용자에게 도움이 될 수 있는 피드백을 작성해주세요. 사용자는 오늘 ";

        // 완료한 습관 리스트 가져오기
        String[] completedHabits = diary.getCompletedHabits().split("@");

        // 미완료한 습관 리스트 가져오기
        String[] incompletedHabits = diary.getIncompletedHabits().split("@");

        // 완료한 습관 목록 작성
        if (completedHabits.length == 0 || (completedHabits.length == 1 && completedHabits[0].isEmpty())) {
            savedMessage += "완료한 습관이 없고 ";
        } else {
            savedMessage += "완료한 습관으로";
            for (String habitTitle : completedHabits) {
                if (!habitTitle.isEmpty()) {
                    savedMessage += habitTitle + ", ";
                }
            }
            savedMessage = savedMessage.substring(0, savedMessage.length() - 2) + "을 완료했고, ";  // 마지막 ", " 제거 후 "이고" 추가
        }

        savedMessage += "미완료한 습관으로는 ";

        // 미완료한 습관 목록 작성
        if (incompletedHabits.length == 0 || (incompletedHabits.length == 1 && incompletedHabits[0].isEmpty())) {
            savedMessage += "없습니다.";
        } else {
            for (String habitTitle : incompletedHabits) {
                if (!habitTitle.isEmpty()) {
                    savedMessage += habitTitle + ", ";
                }
            }
            savedMessage = savedMessage.substring(0, savedMessage.length() - 2) +  "이 있습니다. ";  // 마지막 ", " 제거 후 "입니다" 추가
        }

        savedMessage += (" 오늘 사용자가 작성한 일기는 다음과 같습니다: \"" + diary.getDiaryContent() + "\".\n\n");
//        savedMessage += "이 정보를 바탕으로, 사용자가 불안감을 완화할 수 있는 색다른 방법이나 일상에서 쉽게 실천할 수 있는 행동을 제안해 주세요. ";
//        savedMessage += "예를 들어, 쇼핑, 야외 산책, 혹은 새로운 환경에서 간단한 활동을 시도하는 등 구체적이고 실질적인 조언을 부탁드립니다. ";
//        savedMessage += "특히 사용자가 예상하지 못한 방식으로 불안을 줄일 수 있는 창의적인 활동을 추천해 주세요. 한 문단 이내, 3줄 이하로 작성해 주시고 존댓말로 부탁드립니다.";
        // 일관된 양식 요청
//        savedMessage += "이 정보를 바탕으로 다음과 같은 형식으로 피드백을 작성해 주세요.\n";
//        savedMessage += "1. 피드백 요약: 사용자의 오늘 성취도와 일기 내용을 바탕으로 한 간단한 요약\n";
//        savedMessage += "2. 추천 활동: 사용자가 어려움을 완화할 수 있는 색다른 방법이나 일상에서 쉽게 실천할 수 있는 행동을 제안\n";
//        savedMessage += "피드백은 한 문단 이내, 3줄 이하로 작성해 주세요. 존댓말을 사용해 주세요.";

//        savedMessage += "이 정보를 바탕으로 다음과 같은 형식으로 피드백을 작성해 주세요.\n";
//        savedMessage += "1. 격려의 메시지: 오늘의 성취와 일기 내용을 바탕으로 격려 및 응원\n";
//        savedMessage += "2. 추천 활동: 사용자가 어려움을 완화할 수 있는 색다른 방법이나 일상에서 쉽게 실천할 수 있는 행동을 제안\n";
//        savedMessage += "피드백은 한 문단 이내, 3줄 이하로 작성해 주시고, 존댓말로 부탁드립니다.";
        savedMessage += "이 정보를 바탕으로 피드백을 작성해 주세요.\n";
        savedMessage += "1. 격려의 메시지: 오늘의 성취와 일기 내용을 바탕으로, 사용자가 예상치 못한 방식으로 자부심을 느낄 수 있도록 유머와 비유를 섞어 격려해 주세요.\n";
        savedMessage += "2. 추천 활동: 사용자가 생활에서 쉽게 실천할 수 있으면서도 색다르고 창의적인 행동을 제안해 주세요. 예를 들어, 종이접기로 평온함을 찾거나 일기를 향초를 켜고 작성하는 것처럼 독특한 방법을 추천해 주세요.\n";
        savedMessage += "피드백은 한 문단 이내, 3줄 이하로 작성해 주시고, 재미와 신선함이 느껴질 수 있도록 부탁드립니다.";

        ChatRequest.Message message = new ChatRequest.Message("user", savedMessage);
        ChatRequest request = new ChatRequest("gpt-3.5-turbo", Collections.singletonList(message));

        Call<ChatResponse> call = apiService.getChatResponse(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful()) {
                    ChatResponse chatResponse = response.body();
                    if (chatResponse != null && !chatResponse.getChoices().isEmpty()) {
                        String feedback = chatResponse.getChoices().get(0).getMessage().getContent();
                        diary.setFeedbackContent(feedback);
                        diary.setFeedbackReceived(true);
                        dbDiaryHelper.updateDiary(diary);

                        // UI 업데이트는 메인 스레드에서 수행
                        ((Activity) context).runOnUiThread(() -> {
                            displayDiarys(context, dbHabitHelper, dbDiaryHelper, diaryLayout);
                        });
                    }
                } else {
                    Log.e("DiaryAdapter", "API call failed with response code: " + response.code());
                    Toast.makeText(context, "API 호출 실패: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Log.e("DiaryAdapter", "API call failed", t);
                Toast.makeText(context, "API 호출 실패: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String generateFeedbackMessage(DiaryDatabaseHelper dbDiaryHelper) {
        List<Diary> diaryList = dbDiaryHelper.getAllDiaries();
        StringBuilder savedMessage = new StringBuilder("지금까지 작성된 모든 일기를 바탕으로 사용자에게 도움이 될 피드백을 제공해주세요.\n\n");

        for (Diary diary : diaryList) {
            savedMessage.append("일기 작성일: ").append(diary.getCreatedDate()).append("\n");
            savedMessage.append("일기 내용: ").append(diary.getDiaryContent()).append("\n");

            String[] completedHabits = diary.getCompletedHabits().split("@");
            if (completedHabits.length > 0 && !completedHabits[0].isEmpty()) {
                savedMessage.append("완료한 습관: ");
                for (String habit : completedHabits) savedMessage.append(habit).append(", ");
                savedMessage.setLength(savedMessage.length() - 2); // 마지막 ", " 제거
                savedMessage.append("\n");
            } else {
                savedMessage.append("완료한 습관이 없습니다.\n");
            }

            String[] incompletedHabits = diary.getIncompletedHabits().split("@");
            if (incompletedHabits.length > 0 && !incompletedHabits[0].isEmpty()) {
                savedMessage.append("미완료한 습관: ");
                for (String habit : incompletedHabits) savedMessage.append(habit).append(", ");
                savedMessage.setLength(savedMessage.length() - 2);
                savedMessage.append("\n");
            } else {
                savedMessage.append("미완료한 습관이 없습니다.\n");
            }
            savedMessage.append("\n");
        }
//        savedMessage.append("위 내용을 종합하여, 사용자가 더 나은 습관 형성을 지속할 수 있도록 격려와 개선 팁을 포함한 피드백을 작성해 주세요. 특히, 미완료한 습관을 꾸준히 수행할 수 있도록 간단한 실천 방법이나 유용한 제안을 포함해 주세요.");
        // 일관된 양식으로 종합 피드백 작성 요청
        savedMessage.append("위 데이터를 기반으로, 다음과 같은 형식으로 종합 피드백을 작성해 주세요.\n\n");
        savedMessage.append("1. 장기적인 성과 분석: 사용자가 지금까지 기록한 일기와 습관 달성 내용을 바탕으로 주요 성과와 반복되는 실천 패턴을 요약해 주세요.\n");
        savedMessage.append("2. 개선 방향성 제안: 지속적인 습관 형성과 목표 달성을 위한 실천 방안과 장기적인 개선 팁을 제공해 주세요.\n\n");
        savedMessage.append("각 항목은 한 문단 이내로 간결하게 작성해 주시고, 존댓말로 부탁드립니다.");
        return savedMessage.toString();
    }

    public static String generateKeywordRequestMessage(DiaryDatabaseHelper dbDiaryHelper) {
        List<Diary> diaryList = dbDiaryHelper.getAllDiaries();
        StringBuilder savedMessage = new StringBuilder("아래는 지금까지 작성된 일기 내용입니다. 각 일기에서 자주 등장하는 키워드를 통해 사용자의 감정 및 심리 상태를 분석해 주세요.\n\n");

        for (Diary diary : diaryList) {
            savedMessage.append("일기 작성일: ").append(diary.getCreatedDate()).append("\n");
            savedMessage.append("일기 내용: ").append(diary.getDiaryContent()).append("\n");
        }
        // 일관된 양식 요청
        savedMessage.append("\n위의 일기들을 종합하여, 다음과 같은 형식으로 분석 결과를 작성해 주세요:\n\n");
        savedMessage.append("1. 주요 키워드: 사용자의 감정 및 심리 상태를 반영하는 주요 키워드 목록 (최대 5개)\n");
        savedMessage.append("2. 감정 상태 요약: 일기들에서 나타난 감정적 경향을 바탕으로 사용자의 최근 감정 상태나 심리적 경향을 한 문단 이내로 간결하게 요약\n\n");
        savedMessage.append("각 항목은 간결하게 작성해 주시고, 존댓말을 사용해 주세요.");

        return savedMessage.toString();
    }
}
