package com.example.nutrisnap.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.SupabaseService;
import com.example.nutrisnap.data.model.DailySummary;
import com.example.nutrisnap.data.model.Meal;
import com.example.nutrisnap.databinding.FragmentHomeBinding;
import com.example.nutrisnap.ui.adapter.MealAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private MealAdapter mealAdapter;
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSwipeRefresh();

        boolean isDemo = requireActivity().getIntent().getBooleanExtra("IS_DEMO", false);
        if (isDemo) {
            showDemoData();
        } else {
            loadData();
        }
    }

    private void setupRecyclerView() {
        mealAdapter = new MealAdapter();
        binding.rvRecentMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvRecentMeals.setAdapter(mealAdapter);
    }

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadData();
        });
    }

    private void loadData() {
        fetchDailySummary();
        fetchRecentMeals();
    }

    private void showDemoData() {
        DailySummary demoSummary = new DailySummary();
        demoSummary.totalCalories = 1850.0;
        demoSummary.totalProtein = 120.5;
        demoSummary.totalCarbs = 200.0;
        demoSummary.totalFat = 65.0;
        updateUI(demoSummary);
        binding.tvNoMeals.setVisibility(View.VISIBLE);
        binding.tvNoMeals.setText("Demo Mode: Sign up to track meals");
    }

    private void fetchDailySummary() {
        String userId = requireActivity().getSharedPreferences("NutrisnapPrefs", requireContext().MODE_PRIVATE)
                .getString("user_id", null);
        String accessToken = requireActivity().getSharedPreferences("NutrisnapPrefs", requireContext().MODE_PRIVATE)
                .getString("access_token", null);

        if (userId == null || accessToken == null) {
            Log.e(TAG, "User ID or access token is null");
            binding.swipeRefresh.setRefreshing(false);
            updateUI(new DailySummary());
            return;
        }

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Use PostgREST eq. filter syntax
        String userIdFilter = "eq." + userId;
        String dateFilter = "eq." + today;

        Log.d(TAG, "Fetching daily summary for user: " + userId + ", date: " + today);

        service.getDailySummary(SUPABASE_KEY, "Bearer " + accessToken, userIdFilter, dateFilter, "*")
                .enqueue(new Callback<List<DailySummary>>() {
                    @Override
                    public void onResponse(Call<List<DailySummary>> call, Response<List<DailySummary>> response) {
                        binding.swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            DailySummary summary = response.body().get(0);
                            Log.d(TAG, "✅ Daily summary loaded: " + summary.totalCalories + " cal, " +
                                    summary.totalProtein + "g protein, " + summary.totalCarbs + "g carbs, " +
                                    summary.totalFat + "g fat");
                            updateUI(summary);
                        } else {
                            Log.d(TAG, "No daily summary found for today, showing zeros");
                            updateUI(new DailySummary());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<DailySummary>> call, Throwable t) {
                        Log.e(TAG, "Failed to fetch daily summary", t);
                        binding.swipeRefresh.setRefreshing(false);
                        updateUI(new DailySummary());
                    }
                });
    }

    private void fetchRecentMeals() {
        String userId = requireActivity().getSharedPreferences("NutrisnapPrefs", requireContext().MODE_PRIVATE)
                .getString("user_id", null);
        String accessToken = requireActivity().getSharedPreferences("NutrisnapPrefs", requireContext().MODE_PRIVATE)
                .getString("access_token", null);

        if (userId == null || accessToken == null) {
            Log.e(TAG, "User ID or access token is null");
            binding.tvNoMeals.setVisibility(View.VISIBLE);
            return;
        }

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);

        // Use PostgREST eq filter syntax
        String userIdFilter = "eq." + userId;

        service.getRecentMeals(SUPABASE_KEY, "Bearer " + accessToken, userIdFilter, "consumed_at.desc", "5", "*")
                .enqueue(new Callback<List<Meal>>() {
                    @Override
                    public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            mealAdapter.setMeals(response.body());
                            binding.tvNoMeals.setVisibility(View.GONE);
                            Log.d(TAG, "Loaded " + response.body().size() + " meals");
                        } else {
                            binding.tvNoMeals.setVisibility(View.VISIBLE);
                            Log.d(TAG, "No meals found or error: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Meal>> call, Throwable t) {
                        Log.e(TAG, "Failed to fetch recent meals", t);
                        binding.tvNoMeals.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void updateUI(DailySummary summary) {
        if (summary == null) {
            summary = new DailySummary();
            summary.totalCalories = 0.0;
            summary.totalProtein = 0.0;
            summary.totalCarbs = 0.0;
            summary.totalFat = 0.0;
        }

        binding.tvCalories.setText(String.format(Locale.getDefault(), "%.0f / 2500 kcal",
                summary.totalCalories != null ? summary.totalCalories : 0));
        binding.pbCalories.setProgress(summary.totalCalories != null ? summary.totalCalories.intValue() : 0);

        binding.tvProtein.setText(String.format(Locale.getDefault(), "%.1fg",
                summary.totalProtein != null ? summary.totalProtein : 0));
        binding.tvCarbs.setText(String.format(Locale.getDefault(), "%.1fg",
                summary.totalCarbs != null ? summary.totalCarbs : 0));
        binding.tvFat.setText(String.format(Locale.getDefault(), "%.1fg",
                summary.totalFat != null ? summary.totalFat : 0));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to fragment
        boolean isDemo = requireActivity().getIntent().getBooleanExtra("IS_DEMO", false);
        if (!isDemo) {
            loadData();
        }
    }
}
