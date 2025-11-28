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

        // Navigate to Macro Calculation Activity
        Intent intent = new Intent(this, MacroCalculationActivity.class);

        // Pass all data
        intent.putExtra("full_name", viewModel.getFullName().getValue());
        intent.putExtra("age", viewModel.getAge().getValue());
        intent.putExtra("gender", viewModel.getGender().getValue());
        intent.putExtra("height", viewModel.getHeight().getValue());
        intent.putExtra("current_weight", viewModel.getCurrentWeight().getValue());
        intent.putExtra("fitness_goal", viewModel.getFitnessGoal().getValue());
        intent.putExtra("activity_level", viewModel.getActivityLevel().getValue());
        intent.putExtra("desired_weight", viewModel.getDesiredWeight().getValue());
        intent.putExtra("obstacles", viewModel.getObstacles().getValue());

        startActivity(intent);
        finish();
    }
}
