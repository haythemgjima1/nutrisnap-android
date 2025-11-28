package com.example.nutrisnap.ui;

import android.content.SharedPreferences;
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
import com.example.nutrisnap.data.model.Exercise;
import com.example.nutrisnap.databinding.FragmentExercisesBinding;
import com.example.nutrisnap.ui.adapters.ExerciseAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExercisesFragment extends Fragment {

    private static final String TAG = "ExercisesFragment";
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    private FragmentExercisesBinding binding;
    private ExerciseAdapter adapter;
    private List<Exercise> exercises = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        loadTodaysExercises();
    }

    private void setupRecyclerView() {
        adapter = new ExerciseAdapter(exercises, this::onExerciseChecked);
        binding.rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvExercises.setAdapter(adapter);
    }

    private void loadTodaysExercises() {
        Log.d(TAG, "=== LOAD EXERCISES START ===");

        String userId = getSharedPreferences().getString("user_id", null);
        String accessToken = getSharedPreferences().getString("access_token", null);

        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Has access token: " + (accessToken != null));

        if (userId == null || accessToken == null) {
            Log.e(TAG, "Missing credentials!");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        String userIdFilter = "eq." + userId;
        String dateFilter = "eq." + today;

        Log.d(TAG, "Fetching exercises for date: " + today);
        Log.d(TAG, "User filter: " + userIdFilter);
        Log.d(TAG, "Date filter: " + dateFilter);

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);

        service.getExercises(SUPABASE_KEY, "Bearer " + accessToken, userIdFilter, dateFilter, "*", "created_at.asc")
                .enqueue(new Callback<List<Exercise>>() {
                    @Override
                    public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.d(TAG, "Response code: " + response.code());
                        Log.d(TAG, "Response successful: " + response.isSuccessful());

                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Received " + response.body().size() + " exercises");

                            exercises.clear();
                            exercises.addAll(response.body());

                            for (int i = 0; i < exercises.size(); i++) {
                                Exercise ex = exercises.get(i);
                                Log.d(TAG, "Exercise " + (i + 1) + ": " + ex.exerciseName +
                                        " | Completed: " + ex.isCompleted);
                            }

                            adapter.notifyDataSetChanged();
                            updateProgress();

                            if (exercises.isEmpty()) {
                                Log.d(TAG, "No exercises found - showing empty state");
                                binding.tvEmpty.setVisibility(View.VISIBLE);
                                binding.rvExercises.setVisibility(View.GONE);
                            } else {
                                Log.d(TAG, "Showing exercises list");
                                binding.tvEmpty.setVisibility(View.GONE);
                                binding.rvExercises.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.e(TAG, "Failed to load exercises: " + response.code());
                        }
                        Log.d(TAG, "=== LOAD EXERCISES END ===");
                    }

                    @Override
                    public void onFailure(Call<List<Exercise>> call, Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        Log.e(TAG, "Failed to load exercises", t);
                        Log.e(TAG, "Error: " + t.getMessage());
                        Log.e(TAG, "=== LOAD EXERCISES END ===");
                    }
                });
    }

    private void onExerciseChecked(Exercise exercise, boolean isChecked) {
        String accessToken = getSharedPreferences().getString("access_token", null);
        if (accessToken == null)
            return;

        exercise.isCompleted = isChecked;

        String exerciseIdFilter = "eq." + exercise.id;

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);

        service.updateExercise(SUPABASE_KEY, "Bearer " + accessToken, "return=minimal", exerciseIdFilter, exercise)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            updateProgress();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Failed to update exercise", t);
                    }
                });
    }

    private void updateProgress() {
        int total = exercises.size();
        int completed = 0;

        for (Exercise exercise : exercises) {
            if (exercise.isCompleted != null && exercise.isCompleted) {
                completed++;
            }
        }

        if (total > 0) {
            int percentage = (completed * 100) / total;
            binding.progressBar.setProgress(percentage);
            binding.tvProgress.setText(String.format(Locale.US, "%d%% Complete (%d/%d)", percentage, completed, total));
        } else {
            binding.progressBar.setProgress(0);
            binding.tvProgress.setText("0% Complete");
        }
    }

    private SharedPreferences getSharedPreferences() {
        return requireActivity().getSharedPreferences("NutrisnapPrefs", requireContext().MODE_PRIVATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTodaysExercises();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
