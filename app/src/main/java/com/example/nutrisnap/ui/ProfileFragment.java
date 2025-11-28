package com.example.nutrisnap.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.nutrisnap.R;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.SupabaseService;
import com.example.nutrisnap.data.model.UserProfile;
import com.example.nutrisnap.databinding.FragmentProfileBinding;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    private UserProfile currentProfile;
    private boolean isNewProfile = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDropdowns();
        setupButtons();
        loadUserProfile();
    }

    private void setupDropdowns() {
        // Gender dropdown
        String[] genders = { "Male", "Female", "Other" };
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, genders);
        binding.actvGender.setAdapter(genderAdapter);

        // Goal dropdown
        String[] goals = { "Lose Weight", "Maintain Weight", "Gain Weight", "Build Muscle" };
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, goals);
        binding.actvGoal.setAdapter(goalAdapter);

        // Activity Level dropdown
        String[] activityLevels = { "Sedentary", "Lightly Active", "Moderately Active", "Very Active",
                "Extremely Active" };
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, activityLevels);
        binding.actvActivityLevel.setAdapter(activityAdapter);
    }

    private void setupButtons() {
        binding.btnSaveProfile.setOnClickListener(v -> saveProfile());
        binding.btnSignOut.setOnClickListener(v -> signOut());
    }

    private void loadUserProfile() {
        String userId = getSharedPreferences().getString("user_id", null);
        String accessToken = getSharedPreferences().getString("access_token", null);

        if (userId == null || accessToken == null) {
            Log.e(TAG, "User ID or access token is null");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);
        String userIdFilter = "eq." + userId;

        service.getUserProfile(SUPABASE_KEY, "Bearer " + accessToken, userIdFilter, "*")
                .enqueue(new Callback<List<UserProfile>>() {
                    @Override
                    public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            currentProfile = response.body().get(0);
                            isNewProfile = false;
                            populateFields(currentProfile);
                            Log.d(TAG, "Profile loaded successfully");
                        } else {
                            // No profile exists, create new one
                            isNewProfile = true;
                            currentProfile = new UserProfile();
                            currentProfile.userId = userId;
                            Log.d(TAG, "No profile found, will create new one");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Failed to load profile", t);
                        Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateFields(UserProfile profile) {
        if (profile.fullName != null)
            binding.etFullName.setText(profile.fullName);
        if (profile.age != null)
            binding.etAge.setText(String.valueOf(profile.age));
        if (profile.gender != null)
            binding.actvGender.setText(profile.gender, false);
        if (profile.height != null)
            binding.etHeight.setText(String.valueOf(profile.height));
        if (profile.weight != null)
            binding.etWeight.setText(String.valueOf(profile.weight));
        if (profile.goal != null)
            binding.actvGoal.setText(profile.goal, false);
        if (profile.activityLevel != null)
            binding.actvActivityLevel.setText(profile.activityLevel, false);
    }

    private void saveProfile() {
        String userId = getSharedPreferences().getString("user_id", null);
        String accessToken = getSharedPreferences().getString("access_token", null);

        if (userId == null || accessToken == null) {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate required fields
        String fullName = binding.etFullName.getText().toString().trim();
        if (fullName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your full name", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build profile object
        UserProfile profile = new UserProfile();
        profile.userId = userId;
        profile.fullName = fullName;

        String ageStr = binding.etAge.getText().toString().trim();
        if (!ageStr.isEmpty())
            profile.age = Integer.parseInt(ageStr);

        profile.gender = binding.actvGender.getText().toString().trim();

        String heightStr = binding.etHeight.getText().toString().trim();
        if (!heightStr.isEmpty())
            profile.height = Double.parseDouble(heightStr);

        String weightStr = binding.etWeight.getText().toString().trim();
        if (!weightStr.isEmpty())
            profile.weight = Double.parseDouble(weightStr);

        profile.goal = binding.actvGoal.getText().toString().trim();
        profile.activityLevel = binding.actvActivityLevel.getText().toString().trim();

        // Check if profile is complete
        profile.profileComplete = profile.age != null && profile.gender != null && !profile.gender.isEmpty() &&
                profile.height != null && profile.weight != null && profile.goal != null && !profile.goal.isEmpty() &&
                profile.activityLevel != null && !profile.activityLevel.isEmpty();

        binding.progressBar.setVisibility(View.VISIBLE);

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);

        if (isNewProfile) {
            // Create new profile
            service.createUserProfile(SUPABASE_KEY, "Bearer " + accessToken, "return=minimal", profile)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            binding.progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Profile created!", Toast.LENGTH_SHORT).show();
                                isNewProfile = false;
                                currentProfile = profile;
                            } else {
                                Log.e(TAG, "Failed to create profile: " + response.code());
                                Toast.makeText(requireContext(), "Failed to create profile", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            binding.progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Error creating profile", t);
                            Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Update existing profile
            String userIdFilter = "eq." + userId;
            service.updateUserProfile(SUPABASE_KEY, "Bearer " + accessToken, "return=minimal", userIdFilter, profile)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            binding.progressBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {
                                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                                currentProfile = profile;
                            } else {
                                Log.e(TAG, "Failed to update profile: " + response.code());
                                Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            binding.progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Error updating profile", t);
                            Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void signOut() {
        getSharedPreferences().edit().clear().apply();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private SharedPreferences getSharedPreferences() {
        return requireActivity().getSharedPreferences("NutrisnapPrefs", requireContext().MODE_PRIVATE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
