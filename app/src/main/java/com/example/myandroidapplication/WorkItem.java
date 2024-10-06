package com.example.myandroidapplication;

public class WorkItem {
    private String id;
    private String text;
    private String category;
    private long dueDate;

    public WorkItem() {

    }


    public WorkItem(String id, String text) {
        this.id = id;
        this.text = text;
        this.dueDate = 0;
    }


    public WorkItem(String id, String text, String category, long dueDate) {
        this.id = id;
        this.text = text;
        this.category = category;
        this.dueDate = dueDate;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }
}
