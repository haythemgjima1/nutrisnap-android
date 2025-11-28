package com.example.nutrisnap.data.model;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    public String id;

    @SerializedName("user_id")
    public String userId;

    public String message;

    @SerializedName("is_user")
    public Boolean isUser;

    @SerializedName("created_at")
    public String createdAt;

    // Constructor for creating new message
    public ChatMessage() {
    }

    public ChatMessage(String userId, String message, Boolean isUser) {
        this.userId = userId;
        this.message = message;
        this.isUser = isUser;
    }
}
