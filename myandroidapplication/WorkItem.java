package com.example.myandroidapplication;

public class WorkItem {
    private String id;
    private String text;
    private long dueDate; // Due date for the work item

    public WorkItem() {
        // Required empty public constructor for Firebase
    }

    // Constructor with id and text parameters
    public WorkItem(String id, String text) {
        this.id = id;
        this.text = text;
        this.dueDate = 0; // Initialize the due date to 0 by default
    }

    // Constructor with all parameters
    public WorkItem(String id, String text, long dueDate) {
        this.id = id;
        this.text = text;
        this.dueDate = dueDate;
    }

    // Getters and setters for the fields
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
