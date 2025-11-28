package com.example.nutrisnap.data.model;

import com.google.gson.annotations.SerializedName;

public class DailySummary {
    @SerializedName("user_id")
    public String userId;
    
    public String date;
    
    @SerializedName("total_calories")
    public Double totalCalories;
    
    @SerializedName("total_protein")
    public Double totalProtein;
    
    @SerializedName("total_carbs")
    public Double totalCarbs;
    
    @SerializedName("total_fat")
    public Double totalFat;
    
    @SerializedName("meal_count")
    public Integer mealCount;
}
