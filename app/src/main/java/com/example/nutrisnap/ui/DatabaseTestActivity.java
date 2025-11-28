package com.example.nutrisnap.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nutrisnap.R;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.SupabaseService;
import com.example.nutrisnap.data.model.Meal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DatabaseTestActivity extends AppCompatActivity {

    private static final String TAG = "DatabaseTest";
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create simple layout programmatically
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        Button btnTestSave = new Button(this);
        btnTestSave.setText("Test Save Meal");
        btnTestSave.setOnClickListener(v -> testSaveMeal());

        Button btnTestFetch = new Button(this);
        btnTestFetch.setText("Test Fetch Meals");
        btnTestFetch.setOnClickListener(v -> testFetchMeals());

        tvResult = new TextView(this);
        tvResult.setText("Click buttons to test database");
        tvResult.setPadding(0, 32, 0, 0);

        layout.addView(btnTestSave);
        layout.addView(btnTestFetch);
        layout.addView(tvResult);

        setContentView(layout);
    }

    private void testSaveMeal() {
        String userId = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("user_id", null);
        String accessToken = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("access_token", null);

        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Access Token: " + (accessToken != null ? "Present" : "NULL"));

        if (userId == null || accessToken == null) {
            tvResult.setText("ERROR: Not logged in");
            return;
        }

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());

        Meal testMeal = new Meal(userId, "Test Meal", 500, 25.0, 50.0, 15.0, today, now, "Test");

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);
        service.saveMeal(SUPABASE_KEY, "Bearer " + accessToken, "return=minimal", testMeal)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        String result = "Save Response: " + response.code() + "\n";
                        if (response.isSuccessful()) {
                            result += "SUCCESS!";
                        } else {
                            result += "FAILED: " + response.message();
                            try {
                                if (response.errorBody() != null) {
                                    result += "\nError: " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                result += "\nError reading body";
                            }
                        }
                        Log.d(TAG, result);
                        tvResult.setText(result);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        String result = "NETWORK ERROR: " + t.getMessage();
                        Log.e(TAG, result, t);
                        tvResult.setText(result);
                    }
                });
    }

    private void testFetchMeals() {
        String userId = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("user_id", null);
        String accessToken = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("access_token", null);

        if (userId == null || accessToken == null) {
            tvResult.setText("ERROR: Not logged in");
            return;
        }

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);
        service.getMealsByDate(SUPABASE_KEY, "Bearer " + accessToken, userId, today, "*", "consumed_at.desc")
                .enqueue(new Callback<List<Meal>>() {
                    @Override
                    public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                        String result = "Fetch Response: " + response.code() + "\n";
                        if (response.isSuccessful() && response.body() != null) {
                            result += "Found " + response.body().size() + " meals";
                            for (Meal meal : response.body()) {
                                result += "\n- " + meal.foodName + ": " + meal.calories + " cal";
                            }
                        } else {
                            result += "FAILED: " + response.message();
                        }
                        Log.d(TAG, result);
                        tvResult.setText(result);
                    }

                    @Override
                    public void onFailure(Call<List<Meal>> call, Throwable t) {
                        String result = "NETWORK ERROR: " + t.getMessage();
                        Log.e(TAG, result, t);
                        tvResult.setText(result);
                    }
                });
    }
}
