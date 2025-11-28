package com.example.nutrisnap.ui.onboarding;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.nutrisnap.R;
import com.example.nutrisnap.databinding.FragmentAgeGenderBinding;

public class AgeGenderFragment extends Fragment {

    private FragmentAgeGenderBinding binding;
    private OnboardingViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentAgeGenderBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupGenderDropdown();

        if (viewModel.getAge().getValue() != null) {
            binding.etAge.setText(String.valueOf(viewModel.getAge().getValue()));
        }

        if (viewModel.getGender().getValue() != null) {
            binding.actvGender.setText(viewModel.getGender().getValue(), false);
        }

        binding.etAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().isEmpty()) {
                        viewModel.getAge().setValue(Integer.parseInt(s.toString()));
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.actvGender.setOnItemClickListener((parent, view1, position, id) -> {
            viewModel.getGender().setValue(binding.actvGender.getText().toString());
        });
    }

    private void setupGenderDropdown() {
        String[] genders = { "Male", "Female", "Other" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, genders);
        binding.actvGender.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
