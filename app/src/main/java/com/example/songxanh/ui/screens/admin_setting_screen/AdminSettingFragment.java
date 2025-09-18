package com.example.songxanh.ui.screens.admin_setting_screen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.songxanh.databinding.FragmentAdminSettingBinding;
import com.example.songxanh.ui.screens.MainActivity;
import com.example.songxanh.ui.screens.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

public class AdminSettingFragment extends Fragment {

    FragmentAdminSettingBinding binding;

    public AdminSettingFragment() {

    }

    @Override
// == Lưu hoặc lấy dữ liệu từ SharedPreferences ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminSettingBinding.inflate(inflater, container, false);
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Lưu hoặc lấy dữ liệu từ SharedPreferences ==
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        binding.switchModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Lưu hoặc lấy dữ liệu từ SharedPreferences ==
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(HomeFragment.PREF_FILE_NAME, Context.MODE_PRIVATE);
                boolean isDarkTheme = !sharedPreferences.getBoolean(HomeFragment.THEME_KEY, true);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(HomeFragment.THEME_KEY, isDarkTheme);
                editor.apply();
                getActivity().recreate();
            }
        });
        return binding.getRoot();
    }
}