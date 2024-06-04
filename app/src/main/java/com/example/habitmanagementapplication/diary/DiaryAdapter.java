package com.example.habitmanagementapplication.diary;

import android.app.Activity;
import android.content.Context;
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

        TextView textViewCompletedhabitList = dialogView.findViewById(R.id.textViewCompletedhabitList);
        textViewCompletedhabitList.setText("완료한 습관: ");
        TextView textViewInCompletedhabitList = dialogView.findViewById(R.id.textViewInCompletedhabitList);
        textViewInCompletedhabitList.setText("미완료한 습관: ");

        for (Habit habit : habitList) {

            // 현재 요일에서 완료 및 미완료 습관 객체 가져와서 대화상자에 표시
            if (habit.getDailyGoals()[TimeInfo.getCurrentDayOfWeek()]) {
                if (habit.getDailyAchievements()[TimeInfo.getCurrentDayOfWeek()]) {
                    textViewCompletedhabitList.setText(textViewCompletedhabitList.getText().toString() + habit.getTitle() + ", ");
                } else {
                    textViewInCompletedhabitList.setText(textViewInCompletedhabitList.getText().toString() + habit.getTitle() + ", ");
                }
            }
        }

        dlg.setTitle("습관 일기 정보 입력");
        dlg.setView(dialogView);
        dlg.setPositiveButton("확인", (dialog, which) -> {
            EditText editDiaryContent = dialogView.findViewById(R.id.editDiaryContent);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            String createdDate = sdf.format(new Date());
            String diaryContent = editDiaryContent.getText().toString();
            String feedbackContent = "";

            Diary diary = new Diary(0, createdDate, diaryContent, feedbackContent, false); // 습관 일기 객체 생성

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
        // 카드 뷰 생성
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
        idTextView.setText("id: " + diary.getId());

        // 일기 작성 날짜를 표시하는 TextView 생성
        TextView createdDateTextView = new TextView(context);
        createdDateTextView.setText("createdDate: " + diary.getCreatedDate());

        // 일기 내용을 표시하는 TextView 생성
        TextView diaryContentTextView = new TextView(context);
        diaryContentTextView.setText("diaryContent: " + diary.getDiaryContent());

        // 일기에 대한 피드백 내용을 표시하는 TextView 생성
        TextView getFeedbackContentTextView = new TextView(context);
        getFeedbackContentTextView.setText("feedbackContent: " + diary.getFeedbackContent());

        // 일기에 대한 피드백 여부를 표시하는 TextView 생성
        TextView getFeedbackReceived = new TextView(context);
        getFeedbackReceived.setText("feedbackReceived: " + diary.getFeedbackReceived());

        // 피드백 버튼 추가
        Button feedbackButton = new Button(context);
        feedbackButton.setText("피드백");

        // 삭제 버튼 추가
        Button deleteButton = new Button(context);
        deleteButton.setText("삭제");

        // 삭제 버튼 클릭 이벤트 처리
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 데이터베이스에서 습관 객체 삭제
                dbDiaryHelper.deleteDiary(diary);
                // 레이아웃에서 해당 카드 뷰 삭제
                diaryLayout.removeView(cardView);
            }
        });

        // 피드백 버튼 클릭 이벤트 처리
        if (diary.getFeedbackReceived() == true) {
            feedbackButton.setEnabled(false);
        } else {
            feedbackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestFeedback(context, dbHabitHelper, dbDiaryHelper, diaryLayout, diary);
                }
            });
        }

        // 생성한 뷰들을 카드 뷰에 추가
        LinearLayout cardContentLayout = new LinearLayout(context);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(idTextView);
        cardContentLayout.addView(createdDateTextView);
        cardContentLayout.addView(diaryContentTextView);
        cardContentLayout.addView(getFeedbackContentTextView);
        cardContentLayout.addView(getFeedbackReceived);
        cardContentLayout.addView(feedbackButton); // 피드백 버튼 추가
        cardContentLayout.addView(deleteButton); // 삭제 버튼 추가
        cardView.addView(cardContentLayout);

        return cardView;
    }

    private static void requestFeedback(Context context, HabitDatabaseHelper dbHabitHelper, DiaryDatabaseHelper dbDiaryHelper, LinearLayout diaryLayout, Diary diary) {
        Retrofit retrofit = ApiClient.getClient();
        OpenAIApi apiService = retrofit.create(OpenAIApi.class);

        String savedMessage = "";
        List<Habit> completedhabitList = dbHabitHelper.getTodayCompletedhabitList();
        List<Habit> inCompletedhabitList = dbHabitHelper.getTodayInCompletedhabitList();
        savedMessage += "오늘 완료한 습관이 ";
        for (Habit habit : completedhabitList) {
            savedMessage += habit.getTitle() + ", ";
        }

        savedMessage += "이고 미완료한 습관이 ";
        for (Habit habit : inCompletedhabitList) {
            savedMessage += habit.getTitle() + ", ";
        }

        savedMessage += ("사용자가 작성한 습관 일기의 내용이 " + diary.getDiaryContent());
        savedMessage += "일 때 사용자를 격려할 수 있는 한 줄의 피드백을 작성해줘.";

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
}
