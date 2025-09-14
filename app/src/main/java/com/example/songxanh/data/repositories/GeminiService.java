package com.example.songxanh.data.repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.songxanh.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void callGeminiAPI(String prompt, OnApiResponseListener listener) {
        executor.execute(() -> {
            try {
                JSONObject part = new JSONObject().put("text", prompt);
                JSONArray partsArray = new JSONArray().put(part);
                JSONObject content = new JSONObject().put("parts", partsArray);
                JSONArray contentsArray = new JSONArray().put(content);
                JSONObject jsonBody = new JSONObject().put("contents", contentsArray);

                RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
                Request request = new Request.Builder().url(ENDPOINT).post(body).build();

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
                Log.e("GeminiService", "Lỗi gọi Gemini API", e);
            }
        });
    }

    public static void callGeminiAPIWithImage(Context ctx, Uri imageUri, String prompt, OnApiResponseListener listener) {
        executor.execute(() -> {
            try {
                ContentResolver cr = ctx.getContentResolver();
                String mime = cr.getType(imageUri);
                if (mime == null || mime.trim().isEmpty()) mime = "image/jpeg";

                byte[] bytes = readAllBytes(cr, imageUri);
                if (bytes == null || bytes.length == 0) {
                    listener.onError("Không đọc được dữ liệu ảnh từ Uri");
                    return;
                }
                String b64 = Base64.encodeToString(bytes, Base64.NO_WRAP);

                JSONObject inline = new JSONObject()
                        .put("mime_type", mime)
                        .put("data", b64);

                JSONArray parts = new JSONArray()
                        .put(new JSONObject().put("inline_data", inline))
                        .put(new JSONObject().put("text", prompt));

                JSONObject content = new JSONObject().put("parts", parts);
                JSONArray contents = new JSONArray().put(content);
                JSONObject jsonBody = new JSONObject().put("contents", contents);

                RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
                Request request = new Request.Builder().url(ENDPOINT).post(body).build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        listener.onError("Kết nối thất bại: " + e.getMessage());
                        Log.e("GeminiService", "Lỗi kết nối gọi Gemini API (image)", e);
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
                            Log.e("GeminiService", "Nhận câu trả lời từ Gemini thất bại (image)", e);
                        }
                    }
                });

            } catch (Exception e) {
                listener.onError("Lỗi không mong muốn: " + e.getMessage());
                Log.e("GeminiService", "Lỗi gọi Gemini API (image)", e);
            }
        });
    }

    private static byte[] readAllBytes(ContentResolver cr, Uri uri) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (InputStream is = cr.openInputStream(uri)) {
            if (is == null) return null;
            byte[] buf = new byte[8192];
            int r;
            while ((r = is.read(buf)) != -1) bos.write(buf, 0, r);
            return bos.toByteArray();
        } catch (Exception e) {
            Log.e("GeminiService", "readAllBytes failed", e);
            return null;
        }
    }

    public static String getDisplayName(Context ctx, Uri uri) {
        String result = null;
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) result = cursor.getString(idx);
            }
        } catch (Exception e) {
            Log.w("GeminiService", "getDisplayName failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }
}
