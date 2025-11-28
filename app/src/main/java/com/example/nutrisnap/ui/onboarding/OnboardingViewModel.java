package com.example.nutrisnap.ui.onboarding;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OnboardingViewModel extends ViewModel {

    // User data
    private final MutableLiveData<String> fullName = new MutableLiveData<>();
    private final MutableLiveData<Integer> age = new MutableLiveData<>();
    private final MutableLiveData<String> gender = new MutableLiveData<>();
    private final MutableLiveData<Double> height = new MutableLiveData<>();
    private final MutableLiveData<Double> currentWeight = new MutableLiveData<>();
    private final MutableLiveData<String> fitnessGoal = new MutableLiveData<>();
    private final MutableLiveData<String> activityLevel = new MutableLiveData<>();
    private final MutableLiveData<Double> desiredWeight = new MutableLiveData<>();
    private final MutableLiveData<String> obstacles = new MutableLiveData<>();

    // Getters
    public MutableLiveData<String> getFullName() {
        return fullName;
    }

    public MutableLiveData<Integer> getAge() {
        return age;
    }

    public MutableLiveData<String> getGender() {
        return gender;
    }

    public MutableLiveData<Double> getHeight() {
        return height;
    }

    public MutableLiveData<Double> getCurrentWeight() {
        return currentWeight;
    }

    public MutableLiveData<String> getFitnessGoal() {
        return fitnessGoal;
    }

    public MutableLiveData<String> getActivityLevel() {
        return activityLevel;
    }

    public MutableLiveData<Double> getDesiredWeight() {
        return desiredWeight;
    }

    public MutableLiveData<String> getObstacles() {
        return obstacles;
    }

    // Validation
    public boolean isStep2Valid() {
        return fullName.getValue() != null && !fullName.getValue().trim().isEmpty();
    }

    public boolean isStep3Valid() {
        return age.getValue() != null && age.getValue() > 0 &&
                gender.getValue() != null && !gender.getValue().isEmpty();
    }

    public boolean isStep4Valid() {
        return height.getValue() != null && height.getValue() > 0 &&
                currentWeight.getValue() != null && currentWeight.getValue() > 0;
    }

    public boolean isStep5Valid() {
        return fitnessGoal.getValue() != null && !fitnessGoal.getValue().isEmpty() &&
                activityLevel.getValue() != null && !activityLevel.getValue().isEmpty();
    }

    public boolean isStep6Valid() {
        return desiredWeight.getValue() != null && desiredWeight.getValue() > 0;
    }

    public boolean isStep7Valid() {
        return obstacles.getValue() != null && !obstacles.getValue().trim().isEmpty();
    }
}
