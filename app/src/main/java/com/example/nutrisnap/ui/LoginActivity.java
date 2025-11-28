package com.example.nutrisnap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.SupabaseService;
import com.example.nutrisnap.data.model.AuthRequest;
import com.example.nutrisnap.data.model.AuthResponse;
import com.example.nutrisnap.databinding.ActivityLoginBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    // TODO: Move to secure storage
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v -> login());
        binding.tvGoToSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });
    }

    private void login() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);
        AuthRequest request = new AuthRequest(email, password);

        service.signIn(SUPABASE_KEY, request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Save token and user info
                    AuthResponse authResponse = response.body();
                    getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("is_logged_in", true)
                            .putString("access_token", authResponse.accessToken)
                            .putString("user_id", authResponse.user.id)
                            .apply();

                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed: " + response.message(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
