package com.example.habitmanagementapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {
    // 상단에 위치한 4개의 버튼
    Button backToMainInProgressBtn;
    Button backToDiaryInProgressBtn;
    Button backToProgressInProgressBtn;
    Button addInProgressBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        backToMainInProgressBtn = findViewById(R.id.backToMainInProgressBtn);
        backToDiaryInProgressBtn = findViewById(R.id.backToDiaryInProgressBtn);
        backToProgressInProgressBtn = findViewById(R.id.backToProgressInProgressBtn);
        addInProgressBtn = findViewById(R.id.addInProgressBtn);

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

        addInProgressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "추가하기(현황 화면에서)", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
