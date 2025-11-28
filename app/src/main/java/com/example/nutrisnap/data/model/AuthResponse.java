package com.example.nutrisnap.data.model;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access_token")
    public String accessToken;
    @SerializedName("user")
    public User user;

    public static class User {
        @SerializedName("id")
        public String id;
        @SerializedName("email")
        public String email;
    }
}
