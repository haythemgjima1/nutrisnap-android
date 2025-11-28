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

public class SplashActivity extends AppCompatActivity {

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
        // Mock Auth Check (Replace with real Supabase session check)
        SharedPreferences prefs = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(this, DashboardActivity.class);
        } else {
            intent = new Intent(this, WelcomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
