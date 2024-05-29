package com.example.habitmanagementapplication.diary;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.example.habitmanagementapplication.R;
import com.example.habitmanagementapplication.activity.DiaryActivity;
import com.example.habitmanagementapplication.habit.Habit;
import com.example.habitmanagementapplication.habit.HabitDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter {
    // 일기 정보를 입력받기 위한 대화상자
    public static void showAddDiaryDialog(Context context, DiaryDatabaseHelper dbHelper, LinearLayout diaryLayout) {
        View dialogView = (View) View.inflate(context, R.layout.dialog_diary, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(context);

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
            dbHelper.insertDiary(diary);
            displayDiarys(context, dbHelper, diaryLayout);
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();

    }

    // 데이터베이스로부터 모든 일기 객체 정보를 가져와서 레이아웃에 추가하는 메서드
    public static void displayDiarys(Context context, DiaryDatabaseHelper dbHelper, LinearLayout diaryLayout) {
        // 데이터베이스로부터 모든 일기 객체 정보를 가져옴
        List<Diary> diaryList = dbHelper.getAllDiaries();

        // 기존에 있는 카드 뷰들을 모두 제거
        diaryLayout.removeAllViews();

        // 각 일기 객체를 카드 뷰로 만들어서 레이아웃에 추가
        for (Diary diary : diaryList) {
            View diaryCardView = createDiaryCardView(context, dbHelper, diaryLayout, diary);
            diaryLayout.addView(diaryCardView);
        }
    }

    // 일기 객체 정보를 받아서 카드 뷰로 만드는 메서드
    public static View createDiaryCardView(Context context, DiaryDatabaseHelper dbHelper, LinearLayout diaryLayout, Diary diary) {
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

        // 만약 해당 요일에 이미 달성 여부가 true이면 버튼을 비활성화
        if (diary.getFeedbackReceived() == true) {
            feedbackButton.setEnabled(false);
        } else {
            feedbackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    diary.setFeedbackReceived(true);
                    diary.setFeedbackContent(diary.getDiaryContent() + ": temp feedback");
                    // 데이터베이스에서 습관 정보 업데이트
                    dbHelper.updateDiary(diary);
                    displayDiarys(context, dbHelper, diaryLayout);
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
        cardContentLayout.addView(feedbackButton); // 달성 버튼 추가
        cardView.addView(cardContentLayout);

        return cardView;
    }
}
