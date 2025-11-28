package com.example.nutrisnap.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.nutrisnap.R;
import com.example.nutrisnap.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;
        navView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_scan) {
                selectedFragment = new ScanFragment();
            } else if (itemId == R.id.navigation_exercises) {
                selectedFragment = new ExercisesFragment();
            } else if (itemId == R.id.navigation_ai_bro) {
                selectedFragment = new AiBroFragment();
            } else if (itemId == R.id.navigation_journal) {
                selectedFragment = new JournalFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new HomeFragment())
                    .commit();
        }
    }
}
