package com.example.nutrisnap.ui.onboarding;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.nutrisnap.databinding.FragmentHeightWeightBinding;

public class HeightWeightFragment extends Fragment {
    private FragmentHeightWeightBinding binding;
    private OnboardingViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentHeightWeightBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (viewModel.getHeight().getValue() != null) {
            binding.etHeight.setText(String.valueOf(viewModel.getHeight().getValue()));
        }
        if (viewModel.getCurrentWeight().getValue() != null) {
            binding.etWeight.setText(String.valueOf(viewModel.getCurrentWeight().getValue()));
        }

        binding.etHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().isEmpty()) {
                        viewModel.getHeight().setValue(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (!s.toString().isEmpty()) {
                        viewModel.getCurrentWeight().setValue(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
