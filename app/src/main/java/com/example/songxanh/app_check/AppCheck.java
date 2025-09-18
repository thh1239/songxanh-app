package com.example.songxanh.app_check;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.example.songxanh.BuildConfig;

/**
 * Khởi tạo Firebase App Check cho toàn bộ ứng dụng.
 * - Debug build: dùng DebugAppCheckProvider (dễ test trên máy dev).
 * - Release build: dùng Play Integrity (bảo vệ thật).
 */
public class AppCheck extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);

        // Cài đặt App Check provider theo build type
        FirebaseAppCheck appCheck = FirebaseAppCheck.getInstance();

        if (BuildConfig.DEBUG) {
            // Dùng debug provider khi chạy debug để test nhanh
            appCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
            );
        } else {
            // Production: Play Integrity
            appCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
            );
        }
    }
}
