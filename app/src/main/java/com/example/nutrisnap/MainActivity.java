package com.example.nutrisnap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nutrisnap.data.GeminiService;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.model.GeminiRequest;
import com.example.nutrisnap.data.model.GeminiResponse;
import com.example.nutrisnap.ui.LoginActivity;
import com.example.nutrisnap.ui.OnboardingActivity;
import java.util.Collections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    // TODO: Replace with secure key management in production
    private static final String GEMINI_API_KEY = "AIzaSyC3R38lcvRVd1Ii3oUOLb6TysOyX0RShWo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Test Gemini API
        testGeminiApi();

        // Navigate to Login
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void testGeminiApi() {
        GeminiService service = RetrofitClient.getGeminiClient().create(GeminiService.class);

        GeminiRequest.Part part = new GeminiRequest.Part("Explain how AI works in less than 50 words.");
        GeminiRequest.Content content = new GeminiRequest.Content();
        content.parts = Collections.singletonList(part);

        GeminiRequest request = new GeminiRequest();
        request.contents = Collections.singletonList(content);

        service.generateContent(GEMINI_API_KEY, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Gemini Success: " + response.body().candidates.get(0).content.parts.get(0).text);
                } else {
                    Log.e(TAG, "Gemini Error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Log.e(TAG, "Gemini Failure", t);
            }
        });
    }
}