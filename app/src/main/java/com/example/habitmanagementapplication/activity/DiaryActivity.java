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
import com.example.habitmanagementapplication.diary.DiaryDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {
    // 상단에 위치한 4개의 버튼
    Button backToMainInDiaryBtn;
    Button backToDiaryInDiaryBtn;
    Button backToProgressInDiaryBtn;
    Button addInDiaryBtn;

    // 습관 일기 데이터베이스 관련
    DiaryDatabaseHelper dbHelper;

    // 습관 일기를 표시할 레이아웃 관련
    private LinearLayout diaryLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // DiaryDatabaseHelper 초기화
        dbHelper = new DiaryDatabaseHelper(this);

        backToMainInDiaryBtn = findViewById(R.id.backToMainInDiaryBtn);
        backToDiaryInDiaryBtn = findViewById(R.id.backToDiaryInDiaryBtn);
        backToProgressInDiaryBtn = findViewById(R.id.backToProgressInDiaryBtn);
        addInDiaryBtn = findViewById(R.id.addInDiaryBtn);

        backToMainInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "메인 화면으로 돌아가기(일기 화면에서)", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        backToDiaryInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "일기 화면으로 돌아가기(일기 화면에서)", Toast.LENGTH_SHORT).show();
            }
        });

        backToProgressInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "현황 화면으로 돌아가기(일기 화면에서)", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(getApplicationContext(), ProgressActivity.class);
                startActivity(intent);
            }
        });

        addInDiaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "추가하기(일기 화면에서)", Toast.LENGTH_SHORT).show();
                showAddDiaryDialog();
            }
        });

        diaryLayout = findViewById(R.id.layout_diary);
        displayDiarys();
    }

    // 일기 정보를 입력받기 위한 대화상자
    private void showAddDiaryDialog() {
        View dialogView = (View) View.inflate(DiaryActivity.this, R.layout.dialog_diary, null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(DiaryActivity.this);

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
            displayDiarys();
        });
        dlg.setNegativeButton("취소", null);
        dlg.show();

    }

    // 데이터베이스로부터 모든 일기 객체 정보를 가져와서 레이아웃에 추가하는 메서드
    private void displayDiarys() {
        // 데이터베이스로부터 모든 일기 객체 정보를 가져옴
        List<Diary> diaryList = dbHelper.getAllDiaries();

        // 기존에 있는 카드 뷰들을 모두 제거
        diaryLayout.removeAllViews();

        // 각 일기 객체를 카드 뷰로 만들어서 레이아웃에 추가
        for (Diary diary : diaryList) {
            View diaryCardView = createDiaryCardView(diary);
            diaryLayout.addView(diaryCardView);
        }
    }

    // 일기 객체 정보를 받아서 카드 뷰로 만드는 메서드
    private View createDiaryCardView(Diary diary) {
        // 카드 뷰 생성
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
        idTextView.setText("id: " + diary.getId());

        // 일기 작성 날짜를 표시하는 TextView 생성
        TextView createdDateTextView = new TextView(this);
        createdDateTextView.setText("createdDate: " + diary.getCreatedDate());

        // 일기 내용을 표시하는 TextView 생성
        TextView diaryContentTextView = new TextView(this);
        diaryContentTextView.setText("diaryContent: " + diary.getDiaryContent());

        // 일기에 대한 피드백 내용을 표시하는 TextView 생성
        TextView getFeedbackContentTextView = new TextView(this);
        getFeedbackContentTextView.setText("feedbackContent: " + diary.getFeedbackContent());

        // 일기에 대한 피드백 여부를 표시하는 TextView 생성
        TextView getFeedbackReceived = new TextView(this);
        getFeedbackReceived.setText("feedbackReceived: " + diary.getFeedbackReceived());

        // 생성한 뷰들을 카드 뷰에 추가
        LinearLayout cardContentLayout = new LinearLayout(this);
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);
        cardContentLayout.addView(idTextView);
        cardContentLayout.addView(createdDateTextView);
        cardContentLayout.addView(diaryContentTextView);
        cardContentLayout.addView(getFeedbackContentTextView);
        cardContentLayout.addView(getFeedbackReceived);
        cardView.addView(cardContentLayout);

        return cardView;
    }
}
