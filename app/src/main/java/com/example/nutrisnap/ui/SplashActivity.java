package com.example.nutrisnap.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nutrisnap.R;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.SupabaseService;
import com.example.nutrisnap.data.model.UserProfile;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    // TODO: Move to secure storage
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logo_image);

        // Animations
        Animation fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(1500);
        Animation scale = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scale.setDuration(1500);

        logo.startAnimation(fade);
        logo.startAnimation(scale);

        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuth, 2000);
    }

    private void checkAuth() {
        // Always go to Welcome page first
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }

    private void checkProfileCompleteness(String accessToken, String userId) {
        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);
        String userIdFilter = "eq." + userId;

        service.getUserProfile(SUPABASE_KEY, "Bearer " + accessToken, userIdFilter, "*")
                .enqueue(new Callback<List<UserProfile>>() {
                    @Override
                    public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            UserProfile profile = response.body().get(0);

                            Intent intent;
                            // Check if onboarding is complete
                            if (profile.onboardingComplete != null && profile.onboardingComplete) {
                                intent = new Intent(SplashActivity.this, DashboardActivity.class);
                            } else {
                                intent = new Intent(SplashActivity.this,
                                        com.example.nutrisnap.ui.onboarding.OnboardingWizardActivity.class);
                            }
                            startActivity(intent);
                            finish();
                        } else {
                            // No profile found, go to onboarding
                            startActivity(new Intent(SplashActivity.this,
                                    com.example.nutrisnap.ui.onboarding.OnboardingWizardActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                        // On error, go to dashboard as fallback
                        startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                        finish();
                    }
                });
    }
}
