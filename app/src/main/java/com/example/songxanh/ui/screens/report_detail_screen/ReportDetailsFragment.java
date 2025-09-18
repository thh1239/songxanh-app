package com.example.songxanh.ui.screens.report_detail_screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.songxanh.R;

public class ReportDetailsFragment extends Fragment {
    public ReportDetailsFragment() {

    }
    @Override
// == Khởi tạo và thiết lập ban đầu cho màn hình ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_report_details, container, false);
    }
}