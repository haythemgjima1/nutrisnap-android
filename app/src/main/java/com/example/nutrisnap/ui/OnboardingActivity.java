package com.example.nutrisnap.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.nutrisnap.databinding.ActivityOnboardingBinding;
import com.example.nutrisnap.data.model.UserProfile;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnCompleteProfile.setOnClickListener(v -> {
            if (validateInputs()) {
                saveProfile();
            }
        });
    }

    private boolean validateInputs() {
        if (binding.etFullName.getText().toString().isEmpty()) {
            binding.etFullName.setError("Required");
            return false;
        }
        // Add more validation as needed
        return true;
    }

    private void saveProfile() {
        // TODO: Implement Supabase call to save profile
        // For now, just navigate to Dashboard
        Toast.makeText(this, "Profile Saved (Mock)", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
}
