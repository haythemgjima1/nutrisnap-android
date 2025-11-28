package com.example.nutrisnap.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nutrisnap.data.GeminiService;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.SupabaseService;
import com.example.nutrisnap.data.model.GeminiRequest;
import com.example.nutrisnap.data.model.GeminiResponse;
import com.example.nutrisnap.data.model.MacroResponse;
import com.example.nutrisnap.data.model.UserProfile;
import com.example.nutrisnap.databinding.ActivityMacroCalculationBinding;
import com.example.nutrisnap.ui.DashboardActivity;
import com.google.gson.Gson;
import java.util.Collections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MacroCalculationActivity extends AppCompatActivity {

    private static final String TAG = "MacroCalculation";
    private static final String GEMINI_API_KEY = "AIzaSyAWG5j1U45Yqrj0cbQ7khJJoc10_K7vnSM";
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    private ActivityMacroCalculationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMacroCalculationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calculateMacros();
    }

    private void calculateMacros() {
        String fullName = getIntent().getStringExtra("full_name");
        int age = getIntent().getIntExtra("age", 0);
        String gender = getIntent().getStringExtra("gender");
        double height = getIntent().getDoubleExtra("height", 0);
        double currentWeight = getIntent().getDoubleExtra("current_weight", 0);
        String fitnessGoal = getIntent().getStringExtra("fitness_goal");
        String activityLevel = getIntent().getStringExtra("activity_level");
        double desiredWeight = getIntent().getDoubleExtra("desired_weight", 0);
        String obstacles = getIntent().getStringExtra("obstacles");

        String prompt = String.format(
                "You are a professional nutritionist. Based on the following user data, calculate the optimal daily macros to achieve their goal.\\n\\n"
                        +
                        "User Data:\\n" +
                        "- Age: %d\\n" +
                        "- Gender: %s\\n" +
                        "- Current Weight: %.1f kg\\n" +
                        "- Desired Weight: %.1f kg\\n" +
                        "- Height: %.1f cm\\n" +
                        "- Activity Level: %s\\n" +
                        "- Fitness Goal: %s\\n" +
                        "- Obstacles: %s\\n\\n" +
                        "Return ONLY a JSON object with these exact fields:\\n" +
                        "{\\n" +
                        "  \\\"daily_calories\\\": 0,\\n" +
                        "  \\\"protein_g\\\": 0,\\n" +
                        "  \\\"carbs_g\\\": 0,\\n" +
                        "  \\\"fats_g\\\": 0,\\n" +
                        "  \\\"explanation\\\": \\\"Brief explanation of the plan\\\"\\n" +
                        "}",
                age, gender, currentWeight, desiredWeight, height, activityLevel, fitnessGoal, obstacles);

        GeminiService service = RetrofitClient.getGeminiClient().create(GeminiService.class);

        GeminiRequest.Part textPart = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content();
        content.parts = Collections.singletonList(textPart);

        GeminiRequest request = new GeminiRequest();
        request.contents = Collections.singletonList(content);

        service.generateContent(GEMINI_API_KEY, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String resultText = response.body().candidates.get(0).content.parts.get(0).text;
                        resultText = resultText.replace("```json", "").replace("```", "").trim();

                        Gson gson = new Gson();
                        MacroResponse macroResponse = gson.fromJson(resultText, MacroResponse.class);

                        saveToDatabase(fullName, age, gender, height, currentWeight, fitnessGoal,
                                activityLevel, desiredWeight, obstacles, macroResponse);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(MacroCalculationActivity.this, "Error calculating macros", Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }
                } else {
                    Log.e(TAG, "API error: " + response.code());
                    Toast.makeText(MacroCalculationActivity.this, "Failed to calculate macros", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Log.e(TAG, "Network error", t);
                Toast.makeText(MacroCalculationActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveToDatabase(String fullName, int age, String gender, double height,
            double currentWeight, String fitnessGoal, String activityLevel,
            double desiredWeight, String obstacles, MacroResponse macros) {

        String userId = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("user_id", null);
        String accessToken = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("access_token", null);

        if (userId == null || accessToken == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        UserProfile profile = new UserProfile();
        profile.userId = userId;
        profile.fullName = fullName;
        profile.age = age;
        profile.gender = gender;
        profile.height = height;
        profile.currentWeight = currentWeight;
        profile.fitnessGoal = fitnessGoal;
        profile.activityLevel = activityLevel;
        profile.desiredWeight = desiredWeight;
        profile.obstacles = obstacles;
        profile.dailyCalorieLimit = macros.dailyCalories;
        profile.targetProtein = macros.proteinG;
        profile.targetCarbs = macros.carbsG;
        profile.targetFats = macros.fatsG;
        profile.onboardingComplete = true;

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);
        String userIdFilter = "eq." + userId;

        service.updateUserProfile(SUPABASE_KEY, "Bearer " + accessToken, "return=minimal", userIdFilter, profile)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Profile updated successfully");
                            navigateToDashboard();
                        } else {
                            Log.e(TAG, "Failed to update profile: " + response.code());
                            Toast.makeText(MacroCalculationActivity.this, "Failed to save profile", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Error saving profile", t);
                        Toast.makeText(MacroCalculationActivity.this, "Error saving profile", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
