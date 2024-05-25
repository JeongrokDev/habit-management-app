package com.example.habitmanagementapplication;

public class Diary {
    private int id; // 일기 객체의 고유한 식별자
    private String createdDate; // 일기가 작성된 날짜
    private String diaryContent; // 일기의 내용
    private String feedbackContent; // 일기에 대한 피드백 내용
    private boolean feedbackReceived; // 일기에 대한 피드백 여부

    public Diary(String createdDate, String diaryContent, String feedbackContent, boolean feedbackReceived) {
        this.id = id;
        this.createdDate = createdDate;
        this.diaryContent = diaryContent;
        this.feedbackContent = feedbackContent;
        this.feedbackReceived = feedbackReceived;
    }

    public int getId() { return id; }
    public String getCreatedDate() { return createdDate; }
    public String getDiaryContent() { return diaryContent; }
    public String getFeedbackContent() { return feedbackContent; }
    public boolean getFeedbackReceived() { return feedbackReceived; }

    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public void setDiaryContent(String diaryContent) { this.diaryContent = diaryContent; }
    public void setFeedbackContent(String feedbackContent) { this.feedbackContent = feedbackContent; }
    public void setFeedbackReceived(boolean feedbackReceived) { this.feedbackReceived = feedbackReceived; }
}
