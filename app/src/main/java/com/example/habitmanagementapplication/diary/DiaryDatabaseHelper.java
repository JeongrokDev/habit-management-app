package com.example.habitmanagementapplication.diary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.habitmanagementapplication.diary.Diary;
import com.example.habitmanagementapplication.habit.Habit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryDatabaseHelper extends SQLiteOpenHelper {
    // 데이터베이스 이름과 버전
    private static final String DATABASE_NAME = "diary.db";
    private static final int DATABASE_VERSION = 1;

    // 테이블 및 컬럼 이름
    public static final String TABLE_NAME = "diarys";
    public static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "createdDate";
    private static final String COLUMN_CONTENT = "diaryContent";
    private static final String COLUMN_FEEDBACK_CONTENT = "feedbackContent";
    private static final String COLUMN_FEEDBACK_RECEIVED = "feedbackReceived";

    private static final String COLUMN_COMPLETED_HABITS = "completedHabits";
    private static final String COLUMN_INCOMPLETED_HABITS = "incompletedHabits";

   // 테이블 생성 쿼리
   private static final String TABLE_CREATE =
           "CREATE TABLE " + TABLE_NAME + "("
                   + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + COLUMN_DATE + " TEXT,"
                   + COLUMN_CONTENT + " TEXT,"
                   + COLUMN_FEEDBACK_CONTENT + " TEXT,"
                   + COLUMN_FEEDBACK_RECEIVED + " INTEGER,"
                   + COLUMN_COMPLETED_HABITS + " TEXT,"
                   + COLUMN_INCOMPLETED_HABITS + " TEXT" + ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 데이터베이스 테이블 생성 쿼리 실행
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 버전이 변경될 때의 동작
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // 기존 테이블 삭제
        onCreate(db); // 새로운 버전의 테이블 생성
    }

    // 습관 일기 데이터를 데이터베이스에 삽입하는 메서드
    public void insertDiary(Diary diary) {
        SQLiteDatabase db = this.getWritableDatabase(); // 데이터베이스를 쓰기 모드로 열기
        ContentValues values = new ContentValues(); // 값을 저장할 객체 생성
        values.put(COLUMN_DATE, diary.getCreatedDate()); // 일기가 작성된 날짜 삽입
        values.put(COLUMN_CONTENT, diary.getDiaryContent()); // 일기의 내용 삽입
        values.put(COLUMN_FEEDBACK_CONTENT, diary.getFeedbackContent()); // 일기에 대한 피드백 내용 삽입
        values.put(COLUMN_FEEDBACK_RECEIVED, diary.getFeedbackReceived() ? 1 : 0); // 일기에 대한 피드백 여부 삽입

        values.put(COLUMN_COMPLETED_HABITS, diary.getCompletedHabits());
        values.put(COLUMN_INCOMPLETED_HABITS, diary.getIncompletedHabits());

        db.insert(TABLE_NAME, null, values); // 테이블에 값 삽입
        db.close(); // 데이터베이스 닫기
    }

    // 습관 일기 데이터를 업데이트하는 메서드
    public void updateDiary(Diary diary) {
        SQLiteDatabase db = this.getWritableDatabase(); // 데이터베이스를 쓰기 모드로 열기
        ContentValues values = new ContentValues(); // 값을 저장할 객체 생성

        values.put(COLUMN_DATE, diary.getCreatedDate()); // 일기의 작성 일자 갱신
        values.put(COLUMN_CONTENT, diary.getDiaryContent()); // 일기의 내용 갱신
        values.put(COLUMN_FEEDBACK_CONTENT, diary.getFeedbackContent()); // 일기에 대한 피드백 내용 갱신
        values.put(COLUMN_FEEDBACK_RECEIVED, diary.getFeedbackReceived()); // 일기에 대한 피드백 여부 갱신

        values.put(COLUMN_COMPLETED_HABITS, diary.getCompletedHabits());
        values.put(COLUMN_INCOMPLETED_HABITS, diary.getIncompletedHabits());

        // 테이블에 있는 특정 행을 업데이트
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(diary.getId())});
        db.close(); // 데이터베이스 닫기
    }

    // 습관 일기 데이터를 삭제하는 메서드
    public void deleteDiary(Diary diary) {
        SQLiteDatabase db = this.getWritableDatabase(); // 데이터베이스를 쓰기 모드로 열기
        // 특정 id를 가진 행을 삭제
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(diary.getId())});
        db.close(); // 데이터베이스 닫기
    }

    // 일기 데이터를 데이터베이스에서 불러오는 메서드
    public Diary getDiary(int id) {
        SQLiteDatabase db = this.getReadableDatabase(); // 데이터베이스를 읽기 모드로 열기
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_DATE, COLUMN_CONTENT, COLUMN_FEEDBACK_CONTENT, COLUMN_FEEDBACK_RECEIVED},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        // 커서를 첫 번째 레코드로 이동
        if (cursor != null) cursor.moveToFirst();

        // 커서에서 각 컬럼의 값을 추출하여 변수에 저장
        int diaryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String createdDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
        String diaryContent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
        String feedbackContent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_CONTENT));
        boolean feedbackReceived = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_RECEIVED)) == 1;
        String completedHabits = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_HABITS));
        String incompletedHabits = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INCOMPLETED_HABITS));
        cursor.close();

        return new Diary(diaryId, createdDate, diaryContent, feedbackContent, feedbackReceived, completedHabits, incompletedHabits);
    }

    // 데이터베이스에 저장된 모든 습관 객체 정보를 리스트로 반환하는 메서드
    public List<Diary> getAllDiaries() {
        List<Diary> diaryList = new ArrayList<>(); // 일기 객체를 저장할 리스트 생성
        SQLiteDatabase db = this.getReadableDatabase(); // 데이터베이스를 읽기 모드로 열기

        // 모든 레코드를 검색하기 위한 쿼리 수행
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_DATE + " DESC");

        // 검색된 각 레코드를 Diary 객체로 변환하여 리스트에 추가
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String createdDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                String diaryContent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
                String feedbackContent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_CONTENT));
                boolean feedbackReceived = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_RECEIVED)) == 1;

                String completedHabits = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_HABITS));
                String incompletedHabits = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INCOMPLETED_HABITS));

                // Diary 객체 생성 및 리스트에 추가
                Diary diary = new Diary(id, createdDate, diaryContent, feedbackContent, feedbackReceived, completedHabits, incompletedHabits);
                diaryList.add(diary);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // createDate를 기준으로 정렬
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Collections.sort(diaryList, new Comparator<Diary>() {
            @Override
            public int compare(Diary d1, Diary d2) {
                try {
                    Date date1 = dateFormat.parse(d1.getCreatedDate());
                    Date date2 = dateFormat.parse(d2.getCreatedDate());
                    return date2.compareTo(date1); // 내림차순 정렬 (최신 날짜가 맨 위로)
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        return diaryList; // 일기 객체 리스트 반환
    }

    public DiaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // DiaryDatabaseHelper.java
    public void insertDummyDiaryData() {
        // 더미 일기 데이터 생성
        insertDiary(new Diary(
                0,
                "2024-10-28",
                "새로운 주가 시작되어 힘차게 복습으로 하루를 시작했습니다. 처음 배운 내용들이 쌓여 가는 만큼 복습을 통해 기억이 다시 선명해지고, 작은 성취감도 느껴져요. 계획했던 첫 날을 무사히 보낸 것 같아 뿌듯해요. 내일은 아침 운동과 영어 회화 연습도 함께 챙기면서 좀 더 체계적으로 하루를 보내고 싶어요.",
                "",
                false,
                "배운 내용 복습",
                ""
        ));

        insertDiary(new Diary(
                0,
                "2024-10-29",
                "오늘은 아침에 일찍 일어나 운동으로 시작했어요. 오랜만에 운동을 하고 나니 개운했지만, 하루를 보내면서 약간의 피로도 느껴졌습니다. 그래도 영어 회화 연습도 해내면서 계획한 일들을 충실히 지켰다는 생각에 뿌듯했어요. 다만, 시간이 부족해 복습을 미뤄야 했던 점이 아쉬웠어요. 내일은 꼭 복습까지 완료해서 하루를 온전히 채워 보려 합니다.",
                "",
                false,
                "아침 운동@영어 회화 연습",
                "배운 내용 복습"
        ));

        insertDiary(new Diary(
                0,
                "2024-10-30",
                "오늘은 정말 최악의 하루였다. 원래 해야 할 영어 회화 연습과 배운 내용 복습을 못 했는데, 모든 게 나를 가로막는 느낌이었다. 하루 종일 무기력하게 시간을 흘려보냈고, 아무리 노력해도 제대로 집중할 수가 없었다. 영어 회화 연습도 시작해 보려 했지만, 내 생각보다 실력도 떨어지고 흥미조차 잃어버린 것 같아 스스로가 너무 한심하게 느껴졌다.\n" +
                        "\n" +
                        "마지막까지 배운 내용을 복습해 보려 했지만, 이미 무너진 집중력은 돌아오지 않았다. 이렇게 하루를 흘려보내고 나니 좌절감과 죄책감이 밀려와서 정말 우울했다. 과연 내가 목표한 이 습관들을 계속 이어갈 수 있을지조차 의문이다.",
                "",
                false,
                "",
                "영어 회화 연습@배운 내용 복습"
        ));

        insertDiary(new Diary(
                0,
                "2024-10-31",
                "오늘은 아침 운동을 하려 했지만, 알람을 무시하고 더 자버렸어요. 조금 아쉽긴 하지만, 영어 회화 연습과 복습은 끝까지 챙겼습니다. 특히 영어 연습을 할 때 문장을 조금 더 자연스럽게 말할 수 있었던 것 같아 성취감을 느꼈어요. 복습할 때는 지난 내용이 다시 기억나는 게 공부에 큰 도움이 되었어요. 내일은 아침 운동을 다시 한번 시도하면서 모든 계획을 다 지켜보려고 합니다.",
                "",
                false,
                "영어 회화 연습@배운 내용 복습",
                "아침 운동"
        ));

        insertDiary(new Diary(
                0,
                "2024-11-1",
                "오늘 아침도 몸이 무거웠지만, 영어 회화 연습과 복습을 마쳤다. 그런데 지금 이 정도로 연습한다고 해서 내가 원하는 수준의 영어 실력을 가질 수 있을까? 예전부터 생각했던 외국계 회사 취업이 과연 가능할까 싶다. 목표를 향해 가고 있는지 아니면 제자리걸음을 하고 있는 건지 점점 불안해진다. 이번 주에 아침 운동도 놓친 날이 많았고, 작은 습관조차 꾸준히 지키지 못하면서 이렇게 큰 꿈을 이룰 수 있을지 의심이 든다.",
                "",
                false,
                "영어 회화 연습@배운 내용 복습",
                ""
        ));
    }
}
