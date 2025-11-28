package com.example.nutrisnap.data.model;

public class JournalEntry {
    public String id;
    public String userId;
    public String date;
    public String content;
    public String mood;
    public String createdAt;
    public String updatedAt;

    public JournalEntry() {
    }

    public JournalEntry(String userId, String date, String content, String mood) {
        this.userId = userId;
        this.date = date;
        this.content = content;
        this.mood = mood;
    }
}
