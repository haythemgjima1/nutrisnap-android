package com.example.nutrisnap.data.model;

import com.google.gson.annotations.SerializedName;

public class MacroResponse {
    @SerializedName("daily_calories")
    public Integer dailyCalories;

    @SerializedName("protein_g")
    public Double proteinG;

    @SerializedName("carbs_g")
    public Double carbsG;

    @SerializedName("fats_g")
    public Double fatsG;

    public String explanation;

    public MacroResponse() {
    }
}
