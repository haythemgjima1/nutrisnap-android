package com.example.nutrisnap.data.model;

import com.google.gson.annotations.SerializedName;

public class Meal {
    public String id;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("food_name")
    public String foodName;

    public Integer calories;
    public Double protein;
    public Double carbs;
    public Double fat;
    public String date;

    @SerializedName("consumed_at")
    public String consumedAt;

    @SerializedName("meal_type")
    public String mealType;

    @SerializedName("created_at")
    public String createdAt;

    public Meal() {
    }

    public Meal(String userId, String foodName, Integer calories, Double protein, Double carbs, Double fat, String date,
            String consumedAt, String mealType) {
        this.userId = userId;
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.date = date;
        this.consumedAt = consumedAt;
        this.mealType = mealType;
    }
}
