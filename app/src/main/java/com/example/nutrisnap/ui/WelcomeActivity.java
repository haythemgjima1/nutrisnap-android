package com.example.nutrisnap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nutrisnap.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnSignup = findViewById(R.id.btnSignup);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvDemo = findViewById(R.id.tvDemo);

        btnSignup.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
        btnLogin.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        tvDemo.setOnClickListener(v -> {
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("IS_DEMO", true);
            startActivity(intent);
            finish(); // Optional: keep welcome on back stack? No, better to finish.
        });
    }
}
