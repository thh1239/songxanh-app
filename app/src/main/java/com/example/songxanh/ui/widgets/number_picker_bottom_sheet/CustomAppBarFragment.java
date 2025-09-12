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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentCustomAppBarBinding.inflate(inflater, container, true);
//        binding.setLifecycleOwner(this);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                NavHostFragment.findNavController(CustomAppBarFragment.this).popBackStack();
            }
        });

        return binding.getRoot();
    }
}