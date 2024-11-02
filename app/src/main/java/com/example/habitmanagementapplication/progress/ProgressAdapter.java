package com.example.habitmanagementapplication.progress;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.habitmanagementapplication.diary.DiaryAdapter;
import com.example.habitmanagementapplication.diary.DiaryDatabaseHelper;
import com.example.habitmanagementapplication.feedback.ApiClient;
import com.example.habitmanagementapplication.feedback.ChatRequest;
import com.example.habitmanagementapplication.feedback.ChatResponse;
import com.example.habitmanagementapplication.feedback.OpenAIApi;
import com.example.habitmanagementapplication.habit.Habit;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

// ProgressAdapter.java

public class ProgressAdapter {
    public static void displayProgressRate(Context context, HabitDatabaseHelper dbHelper, DiaryDatabaseHelper dbDiaryHelper, LinearLayout progressLayout) {
        ProgressManager progressManager = new ProgressManager();
        double[] pRate = progressManager.calculateProgressRate(dbHelper);

        progressLayout.removeAllViews();

        // 주간 달성률 카드 추가
        View progressRateView = createProgressCardView(context, pRate);
        progressLayout.addView(progressRateView);

        // 피드백 카드 추가
        displayFeedbackCard(context, dbDiaryHelper, progressLayout);

        displayKeywordCard(context, dbDiaryHelper, progressLayout);

        // 습관별 누적 달성 횟수 카드 추가
        displayHabitAchievementCards(context, dbHelper, progressLayout);
    }

    public static View createProgressCardView(Context context, double[] pRate) {
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int margin = 24;
        layoutParams.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(24); // 더 둥글게 처리
        cardView.setCardElevation(12); // 더 강한 그림자
        cardView.setContentPadding(32, 32, 32, 32);
        cardView.setCardBackgroundColor(0xFFE3F2FD); // 밝은 블루톤 배경

        // 타이틀 텍스트
        TextView titleTextView = new TextView(context);
        titleTextView.setText("주간 달성률");
        titleTextView.setTextSize(22);
        titleTextView.setPadding(0, 0, 0, 16);
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleTextView.setTextColor(0xFF0D47A1); // 진한 블루 색상

        // 주간 달성률 레이아웃
        LinearLayout horizontalProgressLayout = new LinearLayout(context);
        horizontalProgressLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalProgressLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        horizontalProgressLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        for (int i = 0; i < pRate.length; i++) {
            LinearLayout dayProgressLayout = new LinearLayout(context);
            dayProgressLayout.setOrientation(LinearLayout.VERTICAL);
            dayProgressLayout.setPadding(16, 0, 16, 0);
            dayProgressLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView dayLabel = new TextView(context);
            dayLabel.setText(days[i]);
            dayLabel.setTextSize(14);
            dayLabel.setTextColor(0xFF1E88E5); // 밝은 블루

            LinearLayout fullBarLayout = new LinearLayout(context);
            fullBarLayout.setOrientation(LinearLayout.VERTICAL);
            fullBarLayout.setGravity(Gravity.BOTTOM);
            LinearLayout.LayoutParams fullBarParams = new LinearLayout.LayoutParams(50, 200);
            fullBarParams.setMargins(0, 8, 0, 8);
            fullBarLayout.setLayoutParams(fullBarParams);
            fullBarLayout.setBackgroundColor(0xFFBBDEFB); // 연한 블루 배경

            View achievedBar = new View(context);
            int achievedHeight = (int) (pRate[i] * 200);
            LinearLayout.LayoutParams achievedParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, achievedHeight);
            achievedBar.setLayoutParams(achievedParams);
            achievedBar.setBackgroundColor(0xFF1976D2); // 진한 블루

            fullBarLayout.addView(achievedBar);

            TextView rateTextView = new TextView(context);
            rateTextView.setText(String.format("%.1f%%", pRate[i] * 100));
            rateTextView.setTextSize(7);
            rateTextView.setTextColor(0xFF0D47A1);

            dayProgressLayout.addView(dayLabel);
            dayProgressLayout.addView(fullBarLayout);
            dayProgressLayout.addView(rateTextView);
            horizontalProgressLayout.addView(dayProgressLayout);
        }

        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(titleTextView);
        cardContentLayout.addView(horizontalProgressLayout);
        cardView.addView(cardContentLayout);

        return cardView;
    }

    public static void displayHabitAchievementCards(Context context, HabitDatabaseHelper dbHelper, LinearLayout progressLayout) {
        List<Habit> habitList = dbHelper.getAllHabits();

        for (Habit habit : habitList) {
            int achievementCount = dbHelper.getHabitAchievementCount(habit.getId());
            View habitAchievementCard = createHabitAchievementCardView(context, habit.getTitle(), achievementCount);
            progressLayout.addView(habitAchievementCard);
        }
    }

    public static View createHabitAchievementCardView(Context context, String habitTitle, int achievementCount) {
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int margin = 24;
        layoutParams.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(20);
        cardView.setCardElevation(10);
        cardView.setContentPadding(24, 24, 24, 24);
        cardView.setCardBackgroundColor(0xFFF1F8E9); // 연한 초록색 배경

        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);

        TextView titleTextView = new TextView(context);
        titleTextView.setText("습관: " + habitTitle);
        titleTextView.setTextSize(18);
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleTextView.setTextColor(0xFF388E3C); // 진한 초록색

        TextView countTextView = new TextView(context);
        countTextView.setText("누적 달성 횟수: " + achievementCount + "회");
        countTextView.setTextSize(16);
        countTextView.setTextColor(0xFF66BB6A); // 밝은 초록색

        cardContentLayout.addView(titleTextView);
        cardContentLayout.addView(countTextView);
        cardView.addView(cardContentLayout);

        return cardView;
    }

    private static void displayFeedbackCard(Context context, DiaryDatabaseHelper dbDiaryHelper, LinearLayout progressLayout) {
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 16, 16, 16);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(16);
        cardView.setCardElevation(10);
        cardView.setContentPadding(32, 32, 32, 32);
        cardView.setCardBackgroundColor(0xFFFFF3E0); // 연한 오렌지색 배경

        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);

        TextView feedbackTextView = new TextView(context);
        feedbackTextView.setText("지금까지의 일기와 습관 수행 기록을 분석한\n맞춤 피드백을 받아보세요!");
        feedbackTextView.setTextSize(16);
        feedbackTextView.setPadding(0, 16, 0, 16);
        feedbackTextView.setTextColor(0xFFE65100);

        // GradientDrawable을 사용해 둥근 모서리 설정
        GradientDrawable buttonBackground = new GradientDrawable();
        Button feedbackButton = new Button(context);
        feedbackButton.setText("맞춤 분석");
        buttonBackground.setColor(0xFFF57C00); // 진한 오렌지색
        buttonBackground.setCornerRadius(30); // 둥근 모서리 반경 (40dp로 설정)
        feedbackButton.setBackground(buttonBackground);
        feedbackButton.setTextColor(0xFFFFFFFF); // 흰색 텍스트

        feedbackButton.setOnClickListener(v -> requestFeedback(context, dbDiaryHelper, feedbackTextView));

        cardContentLayout.addView(feedbackTextView);
        cardContentLayout.addView(feedbackButton);
        cardView.addView(cardContentLayout);

        progressLayout.addView(cardView);
    }

    private static void displayKeywordCard(Context context, DiaryDatabaseHelper dbDiaryHelper, LinearLayout progressLayout) {
        // 카드 뷰 생성 및 스타일 설정
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 16, 16, 16);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(16);
        cardView.setCardElevation(10);
        cardView.setContentPadding(32, 32, 32, 32);
        cardView.setCardBackgroundColor(0xFFEDE7F6); // 연한 보라색 배경

        // 카드 내용 레이아웃
        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);

        // 키워드 표시 텍스트 뷰
        TextView keywordTextView = new TextView(context);
        keywordTextView.setText("자주 언급된 일기 키워드로 일기 트렌드를 확인해보세요!");
        keywordTextView.setTextSize(16);
        keywordTextView.setPadding(0, 16, 0, 16);
        keywordTextView.setTextColor(0xFF6A1B9A);

        // 버튼 배경 설정
        GradientDrawable buttonBackground = new GradientDrawable();
        buttonBackground.setColor(0xFFD1C4E9); // 옅은 보라색
        buttonBackground.setCornerRadius(30); // 둥근 모서리 반경

        // 키워드 요청 버튼 생성
        Button keywordButton = new Button(context);
        keywordButton.setText("자주 등장하는 키워드 분석");
        keywordButton.setBackground(buttonBackground);
        keywordButton.setTextColor(0xFFFFFFFF); // 흰색 텍스트

        // 버튼 클릭 시 자주 등장하는 키워드 요청
        keywordButton.setOnClickListener(v -> requestFrequentKeywords(context, dbDiaryHelper, keywordTextView));

        // 레이아웃에 뷰 추가
        cardContentLayout.addView(keywordTextView);
        cardContentLayout.addView(keywordButton);
        cardView.addView(cardContentLayout);

        // 카드 뷰를 메인 레이아웃에 추가
        progressLayout.addView(cardView);
    }


    private static void requestFeedback(Context context, DiaryDatabaseHelper dbDiaryHelper, TextView feedbackTextView) {
        Retrofit retrofit = ApiClient.getClient();
        OpenAIApi apiService = retrofit.create(OpenAIApi.class);

        String feedbackMessage = DiaryAdapter.generateFeedbackMessage(dbDiaryHelper);
        ChatRequest.Message message = new ChatRequest.Message("user", feedbackMessage);
        ChatRequest request = new ChatRequest("gpt-3.5-turbo", Collections.singletonList(message));

        Call<ChatResponse> call = apiService.getChatResponse(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String feedback = response.body().getChoices().get(0).getMessage().getContent();
                    feedbackTextView.setText(feedback);
                    feedbackTextView.setTextColor(0xFFE65100); // 더 진한 오렌지색
                } else {
                    feedbackTextView.setText("피드백 요청에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                feedbackTextView.setText("피드백 요청에 실패했습니다.");
            }
        });
    }

    private static void requestFrequentKeywords(Context context, DiaryDatabaseHelper dbDiaryHelper, TextView keywordTextView) {
        Retrofit retrofit = ApiClient.getClient();
        OpenAIApi apiService = retrofit.create(OpenAIApi.class);

        // 일기 내용을 기반으로 키워드를 요청하는 메시지 생성
        String keywordMessage = DiaryAdapter.generateKeywordRequestMessage(dbDiaryHelper);
        ChatRequest.Message message = new ChatRequest.Message("user", keywordMessage);
        ChatRequest request = new ChatRequest("gpt-3.5-turbo", Collections.singletonList(message));

        Call<ChatResponse> call = apiService.getChatResponse(request);
        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 응답에서 키워드 내용 추출하여 TextView에 설정
                    String keywords = response.body().getChoices().get(0).getMessage().getContent();
                    keywordTextView.setText(keywords);
                    keywordTextView.setTextColor(0xFF6A1B9A); // 진한 보라색
                } else {
                    keywordTextView.setText("키워드 요청에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                keywordTextView.setText("키워드 요청에 실패했습니다.");
            }
        });
    }
}

