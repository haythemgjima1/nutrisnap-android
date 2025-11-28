package com.example.nutrisnap.data.model;

import com.google.gson.annotations.SerializedName;

public class Exercise {
    public String id;

    @SerializedName(value = "user_id", alternate = { "userId" })
    public String userId;

    @SerializedName(value = "exercise_name", alternate = { "exerciseName" })
    public String exerciseName;

    public Integer reps;
    public Integer sets;
    public String duration;

    @SerializedName(value = "is_completed", alternate = { "isCompleted" })
    public Boolean isCompleted;

    @SerializedName(value = "date_assigned", alternate = { "dateAssigned" })
    public String dateAssigned;

    @SerializedName(value = "created_at", alternate = { "createdAt" })
    public String createdAt;

    // Constructor for creating new exercise
    public Exercise() {
        this.isCompleted = false;
    }

    public Exercise(String userId, String exerciseName, Integer reps, Integer sets, String duration) {
        this.userId = userId;
        this.exerciseName = exerciseName;
        this.reps = reps;
        this.sets = sets;
        this.duration = duration;
        this.isCompleted = false;
    }
}
