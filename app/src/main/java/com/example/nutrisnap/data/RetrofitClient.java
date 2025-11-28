package com.example.nutrisnap.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit geminiRetrofit = null;
    private static Retrofit supabaseRetrofit = null;

    public static Retrofit getGeminiClient() {
        if (geminiRetrofit == null) {
            geminiRetrofit = new Retrofit.Builder()
                    .baseUrl("https://generativelanguage.googleapis.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return geminiRetrofit;
    }

    public static Retrofit getSupabaseClient(String baseUrl) {
        if (supabaseRetrofit == null) {
            supabaseRetrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return supabaseRetrofit;
    }
}
