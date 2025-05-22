package com.android.happ.medicine.data;

public class ScheduleModel {
    private String date;
    private String content;
    private boolean completed;

    public ScheduleModel() {}

    public ScheduleModel(String date, String content, boolean completed) {
        this.date = date;
        this.content = content;
        this.completed = completed;
    }

    public String getDate() { return date; }
    public String getContent() { return content; }
    public boolean isCompleted() { return completed; }

    public void setDate(String date) { this.date = date; }
    public void setContent(String content) { this.content = content; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
