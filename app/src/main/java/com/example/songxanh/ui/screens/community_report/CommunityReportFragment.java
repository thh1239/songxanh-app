package com.example.songxanh.ui.screens.community_report;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentCommunityReportBinding;
import com.example.songxanh.ui.screens.MainVM;
import com.example.songxanh.utils.GlobalMethods;

public class CommunityReportFragment extends Fragment {
    private FragmentCommunityReportBinding binding;
    private CommunityReportVM viewModel;
    private MainVM mainVM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentCommunityReportBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(CommunityReportVM.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        mainVM = new ViewModelProvider(requireActivity()).get(MainVM.class);
        viewModel.setUser(mainVM.getUser().getValue());
        viewModel.setAchievement(CommunityReportFragmentArgs.fromBundle(getArguments()).getAchievement());

        handleToast();

        setOnClick();

        return binding.getRoot();
    }

    private void handleToast() {
        viewModel.getMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.isEmpty()) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setOnClick() {
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(CommunityReportFragment.this);
            }
        });
    }
}