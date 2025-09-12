package com.example.songxanh.data.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.songxanh.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiService {

    public interface OnApiResponseListener {
        void onResponse(String response);
        void onError(String errorMessage);
    }

    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    // thời gian app đợi AI trả lời-------------------------
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();
//------------------------------------------------------
    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void callGeminiAPI(String prompt, OnApiResponseListener listener) {
        executor.execute(() -> {
            try {
                JSONObject part = new JSONObject();
                part.put("text", prompt);

                JSONArray partsArray = new JSONArray();
                partsArray.put(part);

                JSONObject content = new JSONObject();
                content.put("parts", partsArray);

                JSONArray contentsArray = new JSONArray();
                contentsArray.put(content);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("contents", contentsArray);

                RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

                Request request = new Request.Builder()
                        .url(ENDPOINT)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        listener.onError("Kết nối thất bại: " + e.getMessage());
                        Log.e("GeminiService", "Lỗi kết nối gọi Gemini API", e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        try {
                            if (response.isSuccessful()) {
                                String responseBody = response.body() != null ? response.body().string() : "";
                                listener.onResponse(responseBody);
                            } else {
                                String errorBody = response.body() != null ? response.body().string() : "No error body";
                                listener.onError("API error: " + response.code() + " " + response.message() + " - " + errorBody);
                            }
                        } catch (IOException e) {
                            listener.onError("Nhận câu trả lời từ Gemini thất bại: " + e.getMessage());
                            Log.e("GeminiService", "Nhận câu trả lời từ Gemini thất bại", e);
                        }
                    }
                });
            } catch (Exception e) {
                listener.onError("Lỗi không mong muốn: " + e.getMessage());
                Log.e("GeminiService", "Lỗi kết nối gọi Gemini API", e);
            }
        });
    }
}