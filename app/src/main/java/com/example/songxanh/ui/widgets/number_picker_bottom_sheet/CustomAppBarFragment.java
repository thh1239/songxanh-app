package com.example.songxanh.ui.widgets.number_picker_bottom_sheet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentCustomAppBarBinding;

public class CustomAppBarFragment extends Fragment {
    private FragmentCustomAppBarBinding binding;

    @Override
// == Khởi tạo và thiết lập ban đầu cho màn hình ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentCustomAppBarBinding.inflate(inflater, container, true);


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {

            }
        });

        return binding.getRoot();
    }
}