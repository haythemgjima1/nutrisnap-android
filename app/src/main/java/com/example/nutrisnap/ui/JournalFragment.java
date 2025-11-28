package com.example.nutrisnap.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.SupabaseService;
import com.example.nutrisnap.data.model.Meal;
import com.example.nutrisnap.databinding.FragmentJournalBinding;
import com.example.nutrisnap.ui.adapter.MealAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JournalFragment extends Fragment {

    private static final String TAG = "JournalFragment";
    private FragmentJournalBinding binding;
    private MealAdapter mealAdapter;
    private String selectedDate;
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentJournalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupCalendar();

        // Load today's meals by default
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        binding.tvSelectedDate.setText("Today's Meals");
        loadMealsForDate(selectedDate);
    }

    private void setupRecyclerView() {
        mealAdapter = new MealAdapter();
        binding.rvMeals.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMeals.setAdapter(mealAdapter);
    }

    private void setupCalendar() {
        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());

                SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                binding.tvSelectedDate.setText(displayFormat.format(calendar.getTime()) + "'s Meals");

                Log.d(TAG, "Selected date: " + selectedDate);
                loadMealsForDate(selectedDate);
            }
        });
    }

    private void loadMealsForDate(String date) {
        String userId = requireActivity().getSharedPreferences("NutrisnapPrefs", requireContext().MODE_PRIVATE)
                .getString("user_id", null);
        String accessToken = requireActivity().getSharedPreferences("NutrisnapPrefs", requireContext().MODE_PRIVATE)
                .getString("access_token", null);

        if (userId == null || accessToken == null) {
            Log.e(TAG, "User ID or access token is null");
            binding.tvNoMeals.setVisibility(View.VISIBLE);
            binding.tvNoMeals.setText("Please login to view meal history");
            return;
        }

        Log.d(TAG, "Loading meals for date: " + date + ", userId: " + userId);

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);

        // Use PostgREST eq filter syntax: ?user_id=eq.{userId}&date=eq.{date}
        String userIdFilter = "eq." + userId;
        String dateFilter = "eq." + date;

        service.getMealsByDate(SUPABASE_KEY, "Bearer " + accessToken, userIdFilter, dateFilter, "*", "consumed_at.desc")
                .enqueue(new Callback<List<Meal>>() {
                    @Override
                    public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Meal> meals = response.body();
                            Log.d(TAG, "Received " + meals.size() + " meals for date " + date);

                            if (!meals.isEmpty()) {
                                mealAdapter.setMeals(meals);
                                binding.tvNoMeals.setVisibility(View.GONE);
                                binding.rvMeals.setVisibility(View.VISIBLE);
                            } else {
                                binding.tvNoMeals.setVisibility(View.VISIBLE);
                                binding.rvMeals.setVisibility(View.GONE);
                                binding.tvNoMeals.setText("No meals logged for this date");
                            }
                        } else {
                            Log.e(TAG, "Failed to load meals: " + response.code() + " - " + response.message());
                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, "Error body: " + response.errorBody().string());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                            binding.tvNoMeals.setVisibility(View.VISIBLE);
                            binding.rvMeals.setVisibility(View.GONE);
                            binding.tvNoMeals.setText("Error loading meals");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Meal>> call, Throwable t) {
                        Log.e(TAG, "Failed to load meals", t);
                        binding.tvNoMeals.setVisibility(View.VISIBLE);
                        binding.rvMeals.setVisibility(View.GONE);
                        binding.tvNoMeals.setText("Error: " + t.getMessage());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh when returning to fragment
        if (selectedDate != null) {
            loadMealsForDate(selectedDate);
        }
    }
}
