package com.example.habitmanagementapplication;

public class Diary {
    private String createdDate; // 작성 시각
    private String diaryContent; // 일기 내용
    private String feedbackContent; // 피드백 내용
    private boolean feedbackReceived; // 피드백 받았는지 여부

    public Diary(String createdDate, String diaryContent, String feedbackContent, boolean feedbackReceived) {
        this.createdDate = createdDate;
        this.diaryContent = diaryContent;
        this.feedbackContent = feedbackContent;
        this.feedbackReceived = feedbackReceived;
    }

    public String getCreatedDate() { return createdDate; }
    public String getDiaryContent() { return diaryContent; }
    public String getFeedbackContent() { return feedbackContent; }
    public boolean getFeedbackReceived() { return feedbackReceived; }

    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public void setDiaryContent(String diaryContent) { this.diaryContent = diaryContent; }
    public void setFeedbackContent(String feedbackContent) { this.feedbackContent = feedbackContent; }
    public void setFeedbackReceived(boolean feedbackReceived) { this.feedbackReceived = feedbackReceived; }
}
