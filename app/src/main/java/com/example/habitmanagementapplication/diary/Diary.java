package com.example.habitmanagementapplication.diary;

public class Diary {
    private int id; // 일기 객체의 고유한 식별자
    private String createdDate; // 일기가 작성된 날짜
    private String diaryContent; // 일기의 내용
    private String feedbackContent; // 일기에 대한 피드백 내용
    private boolean feedbackReceived; // 일기에 대한 피드백 여부

    private String completedHabits;
    private String incompletedHabits;

    public Diary(int id, String createdDate, String diaryContent, String feedbackContent, boolean feedbackReceived, String completedHabits, String incompletedHabits) {
        this.id = id;
        this.createdDate = createdDate;
        this.diaryContent = diaryContent;
        this.feedbackContent = feedbackContent;
        this.feedbackReceived = feedbackReceived;
        this.completedHabits = completedHabits;
        this.incompletedHabits = incompletedHabits;
    }

    public int getId() { return id; }
    public String getCreatedDate() { return createdDate; }
    public String getDiaryContent() { return diaryContent; }
    public String getFeedbackContent() { return feedbackContent; }
    public boolean getFeedbackReceived() { return feedbackReceived; }

    public String getCompletedHabits() { return completedHabits; }
    public String getIncompletedHabits() { return incompletedHabits; }

    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public void setDiaryContent(String diaryContent) { this.diaryContent = diaryContent; }
    public void setFeedbackContent(String feedbackContent) { this.feedbackContent = feedbackContent; }
    public void setFeedbackReceived(boolean feedbackReceived) { this.feedbackReceived = feedbackReceived; }

    public void setCompletedHabits(String completedHabits) { this.completedHabits = completedHabits; }
    public void setIncompletedHabits(String incompletedHabits) { this.incompletedHabits = incompletedHabits; }
}
