package com.example.nutrisnap.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.nutrisnap.data.GeminiService;
import com.example.nutrisnap.data.RetrofitClient;
import com.example.nutrisnap.data.SupabaseService;
import com.example.nutrisnap.data.model.ChatMessage;
import com.example.nutrisnap.data.model.Exercise;
import com.example.nutrisnap.data.model.GeminiRequest;
import com.example.nutrisnap.data.model.GeminiResponse;
import com.example.nutrisnap.data.model.UserProfile;
import com.example.nutrisnap.databinding.FragmentAiBroBinding;
import com.example.nutrisnap.ui.adapters.ChatAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiBroFragment extends Fragment {

    private static final String TAG = "AiBroFragment";
    private static final String GEMINI_API_KEY = "AIzaSyC3R38lcvRVd1Ii3oUOLb6TysOyX0RShWo";
    private static final String SUPABASE_URL = "https://yveealpqkahlwipdwuau.supabase.co/";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl2ZWVhbHBxa2FobHdpcGR3dWF1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI3NjkzMDcsImV4cCI6MjA3ODM0NTMwN30.m_IgyNHOjwIkPcA1JnGWUf69vSMO-a-7xn0wCbTJ7l0";

    private FragmentAiBroBinding binding;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private UserProfile userProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentAiBroBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSendButton();
        loadUserProfile();

        // Add welcome message
        ChatMessage welcome = new ChatMessage(null,
                "Hi! I'm your AI fitness trainer. Ask me for workout recommendations like 'I want a chest workout' or 'Give me leg exercises'",
                false);
        messages.add(welcome);
        chatAdapter.notifyItemInserted(0);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messages, this::onExercisesSelected);
        binding.rvChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvChat.setAdapter(chatAdapter);
    }

    private void setupSendButton() {
        binding.btnSend.setOnClickListener(v -> {
            String message = binding.etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                binding.etMessage.setText("");
            }
        });
    }

    private void loadUserProfile() {
        String userId = getSharedPreferences().getString("user_id", null);
        String accessToken = getSharedPreferences().getString("access_token", null);

        if (userId == null || accessToken == null)
            return;

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);
        String userIdFilter = "eq." + userId;

        service.getUserProfile(SUPABASE_KEY, "Bearer " + accessToken, userIdFilter, "*")
                .enqueue(new Callback<List<UserProfile>>() {
                    @Override
                    public void onResponse(Call<List<UserProfile>> call, Response<List<UserProfile>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            userProfile = response.body().get(0);
                            Log.d(TAG, "Profile loaded");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserProfile>> call, Throwable t) {
                        Log.e(TAG, "Failed to load profile", t);
                    }
                });
    }

    private void sendMessage(String userMessage) {
        ChatMessage userMsg = new ChatMessage(getSharedPreferences().getString("user_id", ""), userMessage, true);
        messages.add(userMsg);
        chatAdapter.notifyItemInserted(messages.size() - 1);
        binding.rvChat.smoothScrollToPosition(messages.size() - 1);

        binding.progressBar.setVisibility(View.VISIBLE);

        String prompt = buildPrompt(userMessage);

        GeminiService service = RetrofitClient.getGeminiClient().create(GeminiService.class);

        GeminiRequest.Part textPart = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content();
        content.parts = Collections.singletonList(textPart);

        GeminiRequest request = new GeminiRequest();
        request.contents = Collections.singletonList(content);

        service.generateContent(GEMINI_API_KEY, request).enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                Log.d(TAG, "=== API RESPONSE START ===");
                Log.d(TAG, "Response successful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String resultText = response.body().candidates.get(0).content.parts.get(0).text;
                        Log.d(TAG, "RAW API RESPONSE:");
                        Log.d(TAG, resultText);

                        // Extract JSON array
                        int startIndex = resultText.indexOf("[");
                        int endIndex = resultText.lastIndexOf("]");
                        if (startIndex != -1 && endIndex != -1) {
                            resultText = resultText.substring(startIndex, endIndex + 1);
                            Log.d(TAG, "EXTRACTED JSON:");
                            Log.d(TAG, resultText);
                        } else {
                            Log.e(TAG, "NO JSON ARRAY FOUND!");
                        }

                        Gson gson = new Gson();
                        List<Exercise> exercises = gson.fromJson(resultText, new TypeToken<List<Exercise>>() {
                        }.getType());

                        Log.d(TAG, "PARSED EXERCISES: " + (exercises != null ? exercises.size() : "null"));
                        if (exercises != null) {
                            for (int i = 0; i < exercises.size(); i++) {
                                Exercise ex = exercises.get(i);
                                Log.d(TAG, "Exercise " + (i + 1) + ":");
                                Log.d(TAG, "  Name: " + ex.exerciseName);
                                Log.d(TAG, "  Sets: " + ex.sets);
                                Log.d(TAG, "  Reps: " + ex.reps);
                                Log.d(TAG, "  Duration: " + ex.duration);
                            }
                        }
                        Log.d(TAG, "=== API RESPONSE END ===");

                        ChatMessage aiMsg = new ChatMessage(null,
                                "Here are 4 exercises for you. Select the ones you want:", false);
                        messages.add(aiMsg);
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        chatAdapter.setCurrentExercises(exercises);

                        binding.rvChat.smoothScrollToPosition(messages.size() - 1);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing exercises", e);
                        Log.e(TAG, "Exception details: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string()
                                : "No error body";
                        Log.e(TAG, "API failed - Code: " + response.code());
                        Log.e(TAG, "Error body: " + errorBody);

                        String errorMessage;
                        if (response.code() == 429) {
                            errorMessage = "Rate limit exceeded. Please wait a few minutes and try again.";
                        } else if (response.code() == 403) {
                            errorMessage = "Invalid API key. Please check your configuration.";
                        } else if (response.code() == 400) {
                            errorMessage = "Bad request. The API request format is invalid.";
                        } else {
                            errorMessage = "API error: " + response.code();
                        }

                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();

                        // Add error message to chat
                        ChatMessage errorMsg = new ChatMessage(null,
                                "⚠️ " + errorMessage, false);
                        messages.add(errorMsg);
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                        Toast.makeText(requireContext(), "API error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Log.e(TAG, "API error", t);
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private String buildPrompt(String userMessage) {
        String basePrompt = "You are a professional fitness trainer. Provide 4 specific exercises.\\n\\n" +
                "User Request: \\\"" + userMessage + "\\\"\\n\\n" +
                "Return ONLY a JSON array:\\n" +
                "[{\\\"exerciseName\\\": \\\"Name\\\", \\\"reps\\\": 12, \\\"sets\\\": 3, \\\"duration\\\": \\\"45 seconds\\\"}]";

        if (userProfile != null && userProfile.fitnessGoal != null) {
            return String.format(
                    "You are a professional fitness trainer.\\n\\n" +
                            "User: Age %d, %s, Goal: %s, Activity: %s\\n" +
                            "Request: \\\"%s\\\"\\n\\n" +
                            "Provide 4 exercises. Return ONLY JSON array:\\n" +
                            "[{\\\"exerciseName\\\": \\\"Name\\\", \\\"reps\\\": 12, \\\"sets\\\": 3, \\\"duration\\\": \\\"45s\\\"}]",
                    userProfile.age != null ? userProfile.age : 25,
                    userProfile.gender != null ? userProfile.gender : "Unknown",
                    userProfile.fitnessGoal,
                    userProfile.activityLevel != null ? userProfile.activityLevel : "Moderate",
                    userMessage);
        }

        return basePrompt;
    }

    private void onExercisesSelected(List<Exercise> selectedExercises) {
        Log.d(TAG, "=== SAVE EXERCISES START ===");
        Log.d(TAG, "Selected exercises count: " + selectedExercises.size());

        if (selectedExercises.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = getSharedPreferences().getString("user_id", null);
        String accessToken = getSharedPreferences().getString("access_token", null);

        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Has access token: " + (accessToken != null));

        if (userId == null || accessToken == null) {
            Log.e(TAG, "Missing user ID or access token!");
            Toast.makeText(requireContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String today = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(new java.util.Date());
        Log.d(TAG, "Today's date: " + today);

        for (Exercise exercise : selectedExercises) {
            exercise.userId = userId;
            exercise.dateAssigned = today;
            exercise.isCompleted = false;
            Log.d(TAG, "Preparing exercise: " + exercise.exerciseName +
                    " | Sets: " + exercise.sets +
                    " | Reps: " + exercise.reps +
                    " | Duration: " + exercise.duration);
        }

        SupabaseService service = RetrofitClient.getSupabaseClient(SUPABASE_URL).create(SupabaseService.class);

        Log.d(TAG, "Calling Supabase saveExercises API...");
        service.saveExercises(SUPABASE_KEY, "Bearer " + accessToken, "return=minimal", selectedExercises)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d(TAG, "Save response code: " + response.code());
                        Log.d(TAG, "Save successful: " + response.isSuccessful());

                        if (response.isSuccessful()) {
                            Log.d(TAG, "✅ Exercises saved successfully!");
                            Log.d(TAG, "=== SAVE EXERCISES END ===");

                            Toast.makeText(requireContext(),
                                    "✅ Saved " + selectedExercises.size() + " exercises!",
                                    Toast.LENGTH_SHORT).show();
                            chatAdapter.setCurrentExercises(null);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string()
                                        : "No error body";
                                Log.e(TAG, "❌ Save failed!");
                                Log.e(TAG, "Response code: " + response.code());
                                Log.e(TAG, "Error body: " + errorBody);
                                Log.e(TAG, "=== SAVE EXERCISES END ===");

                                Toast.makeText(requireContext(),
                                        "Failed to save: " + response.code(),
                                        Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "❌ Save network error!", t);
                        Log.e(TAG, "Error message: " + t.getMessage());
                        Log.e(TAG, "=== SAVE EXERCISES END ===");

                        Toast.makeText(requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
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
