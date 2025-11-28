package com.example.nutrisnap.data.model;

import com.google.gson.annotations.SerializedName;

public class UserProfile {
    public String id;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("full_name")
    public String fullName;

    public Integer age;
    public String gender;
    public Double height;
    public Double weight;
    public String goal;

    @SerializedName("activity_level")
    public String activityLevel;

    @SerializedName("profile_complete")
    public Boolean profileComplete;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("updated_at")
    public String updatedAt;

    // New onboarding fields
    @SerializedName("current_weight")
    public Double currentWeight;

    @SerializedName("desired_weight")
    public Double desiredWeight;

    @SerializedName("fitness_goal")
    public String fitnessGoal;

    public String obstacles;

    // AI-generated macro targets
    @SerializedName("daily_calorie_limit")
    public Integer dailyCalorieLimit;

    @SerializedName("target_protein")
    public Double targetProtein;

    @SerializedName("target_carbs")
    public Double targetCarbs;

    @SerializedName("target_fats")
    public Double targetFats;

    @SerializedName("onboarding_complete")
    public Boolean onboardingComplete;

    // Constructor for creating new profile
    public UserProfile() {
        this.profileComplete = false;
        this.onboardingComplete = false;
    }
}
