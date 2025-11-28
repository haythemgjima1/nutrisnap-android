package com.example.nutrisnap.ui.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OnboardingPagerAdapter extends FragmentStateAdapter {

    public OnboardingPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WelcomeFragment();
            case 1:
                return new NameFragment();
            case 2:
                return new AgeGenderFragment();
            case 3:
                return new HeightWeightFragment();
            case 4:
                return new GoalActivityFragment();
            case 5:
                return new DesiredWeightFragment();
            case 6:
                return new ObstaclesFragment();
            default:
                return new WelcomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
