package com.example.nutrisnap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.SupabaseService;
import com.example.nutrisnap.data.model.Meal;
import com.example.nutrisnap.databinding.ActivityMealApprovalBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealApprovalActivity extends AppCompatActivity {

    private static final String TAG = "MealApprovalActivity";
    private ActivityMealApprovalBinding binding;
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMealApprovalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String analysisResult = getIntent().getStringExtra("analysis_result");
        if (analysisResult != null) {
            parseAndDisplayResult(analysisResult);
        }

        binding.btnApproveMeal.setOnClickListener(v -> {
            boolean isDemo = getIntent().getBooleanExtra("IS_DEMO", false);
            if (isDemo) {
                Toast.makeText(this, "Demo Mode: Sign up to save meals!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                saveMeal();
            }
        });
    }

    private void parseAndDisplayResult(String result) {
        try {
            String foodName = "Unknown";
            int calories = 0;
            double protein = 0, carbs = 0, fat = 0;

            if (result.contains("food_name")) {
                int start = result.indexOf("food_name") + 13;
                int end = result.indexOf("\"", start);
                foodName = result.substring(start, end);
            }
            if (result.contains("calories")) {
                int start = result.indexOf("calories") + 11;
                int end = result.indexOf(",", start);
                if (end == -1)
                    end = result.indexOf("}", start);
                calories = Integer.parseInt(result.substring(start, end).trim());
            }
            if (result.contains("protein")) {
                int start = result.indexOf("protein") + 10;
                int end = result.indexOf(",", start);
                if (end == -1)
                    end = result.indexOf("}", start);
                protein = Double.parseDouble(result.substring(start, end).trim());
            }
            if (result.contains("carbs")) {
                int start = result.indexOf("carbs") + 8;
                int end = result.indexOf(",", start);
                if (end == -1)
                    end = result.indexOf("}", start);
                carbs = Double.parseDouble(result.substring(start, end).trim());
            }
            if (result.contains("fat")) {
                int start = result.indexOf("fat") + 6;
                int end = result.indexOf(",", start);
                if (end == -1)
                    end = result.indexOf("}", start);
                fat = Double.parseDouble(result.substring(start, end).trim());
            }

            binding.etFoodName.setText(foodName);
            binding.etCalories.setText(String.valueOf(calories));
            binding.etProtein.setText(String.valueOf(protein));
            binding.etCarbs.setText(String.valueOf(carbs));
            binding.etFat.setText(String.valueOf(fat));
        } catch (Exception e) {
            Log.e(TAG, "Error parsing meal data", e);
            Toast.makeText(this, "Error parsing meal data", Toast.LENGTH_SHORT).show();
        }
    }

    private double parseDoubleFromInput(String input) {
        try {
            String cleaned = input.replaceAll("[^0-9.-]", "");
            if (cleaned.isEmpty())
                return 0.0;
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing double from: " + input, e);
            return 0.0;
        }
    }

    private int parseIntFromInput(String input) {
        try {
            String cleaned = input.replaceAll("[^0-9-]", "");
            if (cleaned.isEmpty())
                return 0;
            return Integer.parseInt(cleaned);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing int from: " + input, e);
            return 0;
        }
    }

    private void saveMeal() {
        String userId = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("user_id", null);
        String accessToken = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("access_token", null);

        if (userId == null || accessToken == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User ID or access token is null");
            return;
        }

        try {
            String foodName = binding.etFoodName.getText().toString().trim();
            if (foodName.isEmpty()) {
                Toast.makeText(this, "Please enter food name", Toast.LENGTH_SHORT).show();
                return;
            }

            int calories = parseIntFromInput(binding.etCalories.getText().toString());
            double protein = parseDoubleFromInput(binding.etProtein.getText().toString());
            double carbs = parseDoubleFromInput(binding.etCarbs.getText().toString());
            double fat = parseDoubleFromInput(binding.etFat.getText().toString());

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getDefault());
            String now = isoFormat.format(new Date());

            Meal meal = new Meal(userId, foodName, calories, protein, carbs, fat, today, now, "Snack");

            Log.d(TAG, "Saving meal: " + foodName + ", " + calories + " cal");

            SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);
            service.saveMeal(SUPABASE_KEY, "Bearer " + accessToken, "return=minimal", meal)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "✅ Meal saved successfully!");
                                Toast.makeText(MealApprovalActivity.this, "Meal saved!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if (response.code() == 401 || response.code() == 403) {
                                    Log.e(TAG, "JWT token expired");
                                    Toast.makeText(MealApprovalActivity.this,
                                            "Session expired. Please login again.", Toast.LENGTH_LONG).show();
                                    handleTokenExpiration();
                                    return;
                                }

                                try {
                                    if (response.errorBody() != null) {
                                        String errorBody = response.errorBody().string();
                                        Log.e(TAG, "Error: " + errorBody);
                                        if (errorBody.contains("JWT") || errorBody.contains("expired")) {
                                            Toast.makeText(MealApprovalActivity.this,
                                                    "Session expired. Please login again.", Toast.LENGTH_LONG).show();
                                            handleTokenExpiration();
                                            return;
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error reading error body", e);
                                }
                                Toast.makeText(MealApprovalActivity.this, "Failed to save meal", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "Network error", t);
                            Toast.makeText(MealApprovalActivity.this, "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error preparing meal data", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleTokenExpiration() {
        getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
