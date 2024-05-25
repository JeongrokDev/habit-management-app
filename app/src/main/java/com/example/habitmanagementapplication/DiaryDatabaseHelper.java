package com.example.habitmanagementapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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

   // 테이블 생성 쿼리
   private static final String TABLE_CREATE =
           "CREATE TABLE " + TABLE_NAME + "("
                   + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + COLUMN_DATE + " TEXT,"
                   + COLUMN_CONTENT + " TEXT,"
                   + COLUMN_FEEDBACK_CONTENT + " TEXT,"
                   + COLUMN_FEEDBACK_RECEIVED + " INTEGER" + ")";

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
        values.put(COLUMN_DATE, diary.getCreatedDate()); // 작성 시간 삽입
        values.put(COLUMN_CONTENT, diary.getDiaryContent()); // 일기 내용 삽입
        values.put(COLUMN_FEEDBACK_CONTENT, diary.getFeedbackContent()); // 피드백 내용 삽입
        values.put(COLUMN_FEEDBACK_RECEIVED, diary.getFeedbackReceived() ? 1 : 0); // 피드백 받았는지 여부 삽입

        db.insert(TABLE_NAME, null, values); // 테이블에 값 삽입
        db.close(); // 데이터베이스 닫기
    }

    // 일기 데이터를 데이터베이스에서 불러오는 메서드
    public Diary getDiary(int id) {
        SQLiteDatabase db = this.getReadableDatabase(); // 데이터베이스를 읽기 모드로 열기
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_DATE, COLUMN_CONTENT, COLUMN_FEEDBACK_CONTENT, COLUMN_FEEDBACK_RECEIVED},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        String createdDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
        String diaryContent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT));
        String feedbackContent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_CONTENT));
        boolean feedbackReceived = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEEDBACK_RECEIVED)) == 1;
        cursor.close();

        return new Diary(createdDate, diaryContent, feedbackContent, feedbackReceived);
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

                // Diary 객체 생성 및 리스트에 추가
                Diary diary = new Diary(createdDate, diaryContent, feedbackContent, feedbackReceived);
                diaryList.add(diary);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return diaryList; // 일기 객체 리스트 반환
    }

    public DiaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
