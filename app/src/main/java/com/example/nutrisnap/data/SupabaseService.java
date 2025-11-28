package com.example.nutrisnap.data;

import com.example.nutrisnap.data.model.AuthRequest;
import com.example.nutrisnap.data.model.AuthResponse;
import com.example.nutrisnap.data.model.ChatMessage;
import com.example.nutrisnap.data.model.DailySummary;
import com.example.nutrisnap.data.model.Exercise;
import com.example.nutrisnap.data.model.Meal;
import com.example.nutrisnap.data.model.UserProfile;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseService {
        // Auth endpoints
        @POST("auth/v1/signup")
        Call<AuthResponse> signUp(
                        @Header("apikey") String apiKey,
                        @Body AuthRequest request);

        @POST("auth/v1/token?grant_type=password")
        Call<AuthResponse> signIn(
                        @Header("apikey") String apiKey,
                        @Body AuthRequest request);

        // User Profile endpoints
        @GET("rest/v1/user_profiles")
        Call<List<UserProfile>> getUserProfile(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Query("user_id") String userId,
                        @Query("select") String select);

        @POST("rest/v1/user_profiles")
        Call<Void> createUserProfile(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Header("Prefer") String prefer,
                        @Body UserProfile userProfile);

        @PATCH("rest/v1/user_profiles")
        Call<Void> updateUserProfile(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Header("Prefer") String prefer,
                        @Query("user_id") String userId,
                        @Body UserProfile userProfile);

        // Daily Summary endpoints
        @GET("rest/v1/daily_summaries")
        Call<List<DailySummary>> getDailySummary(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Query("user_id") String userId,
                        @Query("date") String date,
                        @Query("select") String select);

        // Meal endpoints
        @POST("rest/v1/meals")
        Call<Void> saveMeal(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Header("Prefer") String prefer,
                        @Body Meal meal);

        @GET("rest/v1/meals")
        Call<List<Meal>> getMealsByDate(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Query("user_id") String userIdFilter,
                        @Query("date") String dateFilter,
                        @Query("select") String select,
                        @Query("order") String order);

        @GET("rest/v1/meals")
        Call<List<Meal>> getRecentMeals(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Query("user_id") String userIdFilter,
                        @Query("order") String order,
                        @Query("limit") String limit,
                        @Query("select") String select);

        // Exercise endpoints
        @GET("rest/v1/exercises")
        Call<List<Exercise>> getExercises(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Query("user_id") String userId,
                        @Query("date_assigned") String date,
                        @Query("select") String select,
                        @Query("order") String order);

        @POST("rest/v1/exercises")
        Call<Void> saveExercises(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Header("Prefer") String prefer,
                        @Body List<Exercise> exercises);

        @PATCH("rest/v1/exercises")
        Call<Void> updateExercise(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Header("Prefer") String prefer,
                        @Query("id") String exerciseId,
                        @Body Exercise exercise);

        // Chat message endpoints
        @POST("rest/v1/chat_messages")
        Call<Void> saveChatMessage(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Header("Prefer") String prefer,
                        @Body ChatMessage message);

        @GET("rest/v1/chat_messages")
        Call<List<ChatMessage>> getChatHistory(
                        @Header("apikey") String apiKey,
                        @Header("Authorization") String authorization,
                        @Query("user_id") String userId,
                        @Query("order") String order,
                        @Query("limit") String limit,
                        @Query("select") String select);
}
