package com.example.nutrisnap.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.example.nutrisnap.R;
import com.example.nutrisnap.databinding.ActivityOnboardingWizardBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class OnboardingWizardActivity extends AppCompatActivity {

    private ActivityOnboardingWizardBinding binding;
    private OnboardingViewModel viewModel;
    private ViewPager2 viewPager;
    private LinearProgressIndicator progressIndicator;
    private MaterialButton btnNext;
    private MaterialButton btnBack;

    private static final int TOTAL_STEPS = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingWizardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);

        setupViews();
        setupViewPager();
        setupButtons();
    }

    private void setupViews() {
        viewPager = binding.viewPager;
        progressIndicator = binding.progressIndicator;
        btnNext = binding.btnNext;
        btnBack = binding.btnBack;

        progressIndicator.setMax(TOTAL_STEPS);
        progressIndicator.setProgress(1);
    }

    private void setupViewPager() {
        OnboardingPagerAdapter adapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(false); // Disable swipe

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateProgress(position + 1);
                updateButtons(position);
            }
        });
    }

    private void setupButtons() {
        btnNext.setOnClickListener(v -> {
            int currentPage = viewPager.getCurrentItem();

            if (currentPage == TOTAL_STEPS - 1) {
                // Last page - complete onboarding
                completeOnboarding();
            } else if (validateCurrentPage(currentPage)) {
                viewPager.setCurrentItem(currentPage + 1);
            }
        });

        btnBack.setOnClickListener(v -> {
            int currentPage = viewPager.getCurrentItem();
            if (currentPage > 0) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        });
    }

    private boolean validateCurrentPage(int page) {
        switch (page) {
            case 0:
                return true; // Welcome screen
            case 1:
                if (!viewModel.isStep2Valid()) {
                    Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            case 2:
                if (!viewModel.isStep3Valid()) {
                    Toast.makeText(this, "Please enter your age and gender", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            case 3:
                if (!viewModel.isStep4Valid()) {
                    Toast.makeText(this, "Please enter your height and weight", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            case 4:
                if (!viewModel.isStep5Valid()) {
                    Toast.makeText(this, "Please select your goal and activity level", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            case 5:
                if (!viewModel.isStep6Valid()) {
                    Toast.makeText(this, "Please enter your desired weight", Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    private void updateProgress(int step) {
        progressIndicator.setProgress(step);
    }

    private void updateButtons(int position) {
        btnBack.setEnabled(position > 0);

        if (position == TOTAL_STEPS - 1) {
            btnNext.setText("Complete");
        } else {
            btnNext.setText("Next");
        }
    }

    private void completeOnboarding() {
        if (!viewModel.isStep7Valid()) {
            Toast.makeText(this, "Please tell us what's stopping you", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save profile to database and navigate to dashboard
        saveProfileAndNavigate();
    }

    private void saveProfileAndNavigate() {
        String userId = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("user_id", null);
        String accessToken = getSharedPreferences("NutrisnapPrefs", MODE_PRIVATE).getString("access_token", null);

        if (userId == null || accessToken == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Create profile with ALL onboarding data
        com.example.nutrisnap.data.model.UserProfile profile = new com.example.nutrisnap.data.model.UserProfile();
        profile.userId = userId;
        profile.fullName = viewModel.getFullName().getValue();
        profile.age = viewModel.getAge().getValue();
        profile.gender = viewModel.getGender().getValue();
        profile.height = viewModel.getHeight().getValue();
        profile.weight = viewModel.getCurrentWeight().getValue(); // Also save to weight field
        profile.currentWeight = viewModel.getCurrentWeight().getValue();
        profile.goal = viewModel.getFitnessGoal().getValue(); // Also save to goal field
        profile.fitnessGoal = viewModel.getFitnessGoal().getValue();
        profile.activityLevel = viewModel.getActivityLevel().getValue();
        profile.desiredWeight = viewModel.getDesiredWeight().getValue();
        profile.obstacles = viewModel.getObstacles().getValue();
        profile.onboardingComplete = true;
        profile.profileComplete = true; // Mark profile as complete

        // Save to Supabase
        String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
        String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

        com.example.nutrisnap.data.SupabaseService service = com.example.nutrisnap.data.RetrofitClient
                .getSupabaseClient(SUPABASE_URL)
                .create(com.example.nutrisnap.data.SupabaseService.class);

        // Try to create profile first (for new users)
        service.createUserProfile(SUPABASE_KEY, "Bearer " + accessToken, "return=minimal", profile)
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Profile created successfully
                            Toast.makeText(OnboardingWizardActivity.this, "Profile saved successfully!",
                                    Toast.LENGTH_SHORT).show();
                            navigateToDashboard();
                        } else if (response.code() == 409) {
                            // Profile already exists, update it instead
                            updateExistingProfile(service, SUPABASE_KEY, accessToken, userId, profile);
                        } else {
                            // Try update as fallback
                            updateExistingProfile(service, SUPABASE_KEY, accessToken, userId, profile);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        // If create fails, try update
                        updateExistingProfile(service, SUPABASE_KEY, accessToken, userId, profile);
                    }
                });
    }

    private void updateExistingProfile(com.example.nutrisnap.data.SupabaseService service,
            String apiKey, String accessToken, String userId,
            com.example.nutrisnap.data.model.UserProfile profile) {
        String userIdFilter = "eq." + userId;

        service.updateUserProfile(apiKey, "Bearer " + accessToken, "return=minimal", userIdFilter, profile)
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(OnboardingWizardActivity.this, "Profile updated successfully!",
                                    Toast.LENGTH_SHORT).show();
                            navigateToDashboard();
                        } else {
                            Toast.makeText(OnboardingWizardActivity.this, "Failed to save profile. Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                        Toast.makeText(OnboardingWizardActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(OnboardingWizardActivity.this, com.example.nutrisnap.ui.DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
