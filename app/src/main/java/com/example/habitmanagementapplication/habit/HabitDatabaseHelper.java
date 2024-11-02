package com.example.habitmanagementapplication.habit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.habitmanagementapplication.habit.Habit;
import com.example.habitmanagementapplication.time.TimeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HabitDatabaseHelper extends SQLiteOpenHelper {
    // 데이터베이스 이름과 버전
    private static final String DATABASE_NAME = "habit.db";
    private static final int DATABASE_VERSION = 1;

    // 테이블 및 컬럼 이름
    public static final String TABLE_NAME = "habits";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_DAILY_GOALS = "daily_goals";
    public static final String COLUMN_DAILY_ACHIEVEMENTS = "daily_achievements";

    // 테이블 생성 쿼리
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_HOUR + " INTEGER, " +
                    COLUMN_MINUTE + " INTEGER, " +
                    COLUMN_DAILY_GOALS + " TEXT, " +
                    COLUMN_DAILY_ACHIEVEMENTS + " TEXT);";

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

    // 목표 습관 데이터를 데이터베이스에 삽입하는 메서드
    public void insertHabit(Habit habit) {
        SQLiteDatabase db = this.getWritableDatabase(); // 데이터베이스를 쓰기 모드로 열기
        ContentValues values = new ContentValues(); // 값을 저장할 객체 생성
        values.put(COLUMN_TITLE, habit.getTitle()); // 제목 삽입
        values.put(COLUMN_HOUR, habit.getHour()); // 시간 삽입
        values.put(COLUMN_MINUTE, habit.getMinute()); // 분 삽입
        values.put(COLUMN_DAILY_GOALS, booleanArrayToString(habit.getDailyGoals())); // 수행 요일 삽입
        values.put(COLUMN_DAILY_ACHIEVEMENTS, booleanArrayToString(habit.getDailyAchievements())); // 달성 정보 삽입

        db.insert(TABLE_NAME, null, values); // 테이블에 값 삽입
        db.close(); // 데이터베이스 닫기
    }

    // 목표 습관 데이터를 업데이트하는 메서드
    public void updateHabit(Habit habit) {
        SQLiteDatabase db = this.getWritableDatabase(); // 데이터베이스를 쓰기 모드로 열기
        ContentValues values = new ContentValues(); // 값을 저장할 객체 생성

        values.put(COLUMN_TITLE, habit.getTitle()); // 제목 갱신
        values.put(COLUMN_HOUR, habit.getHour()); // 시간 갱신
        values.put(COLUMN_MINUTE, habit.getMinute()); // 분 갱신
        values.put(COLUMN_DAILY_GOALS, booleanArrayToString(habit.getDailyGoals())); // 수행 요일 갱신
        values.put(COLUMN_DAILY_ACHIEVEMENTS, booleanArrayToString(habit.getDailyAchievements())); // 달성 정보 갱신

        // 테이블에 있는 특정 행을 업데이트
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(habit.getId())});
        db.close(); // 데이터베이스 닫기
    }

    // 목표 습관 데이터를 삭제하는 메서드
    public void deleteHabit(Habit habit) {
        SQLiteDatabase db = this.getWritableDatabase(); // 데이터베이스를 쓰기 모드로 열기
        // 특정 id를 가진 행을 삭제
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(habit.getId())});
        db.close(); // 데이터베이스 닫기
    }

    // boolean 배열을 문자열로 변환하는 메서드
    private String booleanArrayToString(boolean[] array) {
        StringBuilder sb = new StringBuilder();
        for (boolean b : array) {
            sb.append(b ? 1 : 0); // true는 1, false는 0으로 변환하여 문자열에 추가
        }
        return sb.toString(); // 결과 문자열 반환
    }

    // 문자열을 boolean 배열로 변환하는 메서드
    private boolean[] stringToBooleanArray(String str) {
        boolean[] array = new boolean[str.length()];
        for (int i = 0; i < str.length(); i++) {
            array[i] = str.charAt(i) == '1'; // '1'은 true, '0'은 false로 변환
        }
        return array;
    }

    // 습관 데이터를 데이터베이스에서 불러오는 메서드
    public Habit getHabit(int id) {
        SQLiteDatabase db = this.getReadableDatabase(); // 데이터베이스를 읽기 모드로 열기
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_HOUR, COLUMN_MINUTE, COLUMN_DAILY_GOALS, COLUMN_DAILY_ACHIEVEMENTS},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        // 커서를 첫 번째 레코드로 이동
        if (cursor != null) cursor.moveToFirst();

        // 커서에서 각 컬럼의 값을 추출하여 변수에 저장
        int habitId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
        int hour = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HOUR));
        int minute = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINUTE));
        boolean[] dailyGoals = stringToBooleanArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAILY_GOALS)));
        boolean[] dailyAchievements = stringToBooleanArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAILY_ACHIEVEMENTS)));
        cursor.close();

        // 추출된 값을 사용하여 새로운 Habit 객체를 생성하여 반환
        return new Habit(habitId, title, Integer.toString(hour), Integer.toString(minute), dailyGoals, dailyAchievements);
    }

    // 데이터베이스에 저장된 모든 습관 객체 정보를 리스트로 반환하는 메서드
    public List<Habit> getAllHabits() {
        List<Habit> habitList = new ArrayList<>(); // 습관 객체를 저장할 리스트 생성
        SQLiteDatabase db = this.getReadableDatabase(); // 데이터베이스를 읽기 모드로 열기

        // 모든 레코드를 검색하기 위한 쿼리 수행
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        // 검색된 각 레코드를 Habit 객체로 변환하여 리스트에 추가
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                int hour = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HOUR));
                int minute = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINUTE));
                boolean[] dailyGoals = stringToBooleanArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAILY_GOALS)));
                boolean[] dailyAchievements = stringToBooleanArray(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAILY_ACHIEVEMENTS)));

                // Habit 객체 생성 및 리스트에 추가
                Habit habit = new Habit(id, title, Integer.toString(hour), Integer.toString(minute), dailyGoals, dailyAchievements);
                habitList.add(habit);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // 시간 기준으로 정렬
        Collections.sort(habitList, new Comparator<Habit>() {
            @Override
            public int compare(Habit h1, Habit h2) {
                int hourComparison = Integer.compare(Integer.parseInt(h1.getHour()), Integer.parseInt(h2.getHour()));
                if (hourComparison == 0) {
                    return Integer.compare(Integer.parseInt(h1.getMinute()), Integer.parseInt(h2.getMinute()));
                }
                return hourComparison;
            }
        });

        return habitList; // 정렬된 습관 객체 리스트 반환
    }

    public List<Habit> getTodayHabitList() {
        List<Habit> habitList = getAllHabits();
        List<Habit> todayHabitList = new ArrayList<>();

        for (Habit habit : habitList) {
            if (habit.getDailyGoals()[TimeInfo.getCurrentDayOfWeek()]) {
                todayHabitList.add(habit);
            }
        }

        return todayHabitList;
    }

    public List<Habit> getTodayCompletedhabitList() {
        List<Habit> habitList = getAllHabits();
        List<Habit> completedhabitList = new ArrayList<>();

        for (Habit habit : habitList) {
            if (habit.getDailyGoals()[TimeInfo.getCurrentDayOfWeek()]) {
                if (habit.getDailyAchievements()[TimeInfo.getCurrentDayOfWeek()]) { completedhabitList.add(habit); }
            }
        }

        return completedhabitList;
    }

    public List<Habit> getTodayInCompletedhabitList() {
        List<Habit> habitList = getAllHabits();
        List<Habit> inCompletedhabitList = new ArrayList<>();

        for (Habit habit : habitList) {
            if (habit.getDailyGoals()[TimeInfo.getCurrentDayOfWeek()]) {
                if (!habit.getDailyAchievements()[TimeInfo.getCurrentDayOfWeek()]) { inCompletedhabitList.add(habit); }
            }
        }

        return inCompletedhabitList;
    }

    public HabitDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // 특정 습관의 누적 달성 횟수를 불러오는 함수
    public int getHabitAchievementCount(int habitId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_DAILY_ACHIEVEMENTS + " FROM " + TABLE_NAME +
                " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(habitId)});

        int achievementCount = 0;
        if (cursor.moveToFirst()) {
            String dailyAchievements = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAILY_ACHIEVEMENTS));
            // 문자열에서 '1'의 개수를 세서 누적 달성 횟수로 반환
            for (char c : dailyAchievements.toCharArray()) {
                if (c == '1') {
                    achievementCount++;
                }
            }
        }
        cursor.close();
        return achievementCount;
    }

    public void insertDummyHabits() {
        // 더미 습관 데이터 생성
        insertHabit(new Habit(
                0,
                "아침 운동",
                "06", "30",
                new boolean[]{false, true, false, true, false, true, false},
                new boolean[]{false, true, false, false, false, false, false}
        ));
        insertHabit(new Habit(
                0,
                "영어 회화 연습",
                "15", "00",
                new boolean[]{false, true, true, true, true, true, false},
                new boolean[]{false, true, false, true, true, false, false}
        ));
        insertHabit(new Habit(
                0,
                "배운 내용 복습",
                "20", "15",
                new boolean[]{true, true, true, true, true, true, true},
                new boolean[]{true, false, false, true, true, false, false}
        ));
//        insertHabit(new Habit(0,
//                "일기 작성",
//                "09", "30",
//                new boolean[]{false, true, false, true, false, true, false},
//                new boolean[]{false, true, false, true, false, true, false}
//        ));
//        insertHabit(new Habit(0,
//                "식단 관리",
//                "10", "30",
//                new boolean[]{false, true, false, true, false, true, false},
//                new boolean[]{false, true, false, true, false, false, false}
//        ));
    }
}
