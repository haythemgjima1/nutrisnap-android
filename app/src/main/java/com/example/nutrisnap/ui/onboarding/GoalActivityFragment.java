package com.example.nutrisnap.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.nutrisnap.databinding.FragmentGoalActivityBinding;

public class GoalActivityFragment extends Fragment {
    private FragmentGoalActivityBinding binding;
    private OnboardingViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentGoalActivityBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupDropdowns();

        if (viewModel.getFitnessGoal().getValue() != null) {
            binding.actvGoal.setText(viewModel.getFitnessGoal().getValue(), false);
        }
        if (viewModel.getActivityLevel().getValue() != null) {
            binding.actvActivityLevel.setText(viewModel.getActivityLevel().getValue(), false);
        }

        binding.actvGoal.setOnItemClickListener((parent, view1, position, id) -> {
            viewModel.getFitnessGoal().setValue(binding.actvGoal.getText().toString());
        });

        binding.actvActivityLevel.setOnItemClickListener((parent, view1, position, id) -> {
            viewModel.getActivityLevel().setValue(binding.actvActivityLevel.getText().toString());
        });
    }

    private void setupDropdowns() {
        String[] goals = { "Lose Weight", "Maintain Weight", "Gain Weight", "Build Muscle" };
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, goals);
        binding.actvGoal.setAdapter(goalAdapter);

        String[] activityLevels = { "Sedentary", "Lightly Active", "Moderately Active", "Very Active",
                "Extremely Active" };
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, activityLevels);
        binding.actvActivityLevel.setAdapter(activityAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
