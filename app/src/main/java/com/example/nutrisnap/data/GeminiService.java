package com.example.nutrisnap.data;

import com.example.nutrisnap.data.model.GeminiRequest;
import com.example.nutrisnap.data.model.GeminiResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiService {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    Call<GeminiResponse> generateContent(
            @Query("key") String apiKey,
            @Body GeminiRequest request);
}
