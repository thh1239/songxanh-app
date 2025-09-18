package com.example.songxanh.ui.screens.community_share_achievement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentCommunityShareAchievementBinding;
import com.example.songxanh.ui.screens.MainVM;
import com.example.songxanh.ui.screens.community.CommunityVM;
import com.example.songxanh.ui.screens.workout_exercise_practicing.PracticingOnBackDialogInterface;
import com.example.songxanh.utils.GlobalMethods;

import java.util.Date;

public class CommunityShareAchievementFragment extends Fragment {
    private FragmentCommunityShareAchievementBinding binding;
    private WorkoutShareAchievementVM viewModel;
    private CommunityVM communityVM;
    private MainVM mainVM;

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCommunityShareAchievementBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(WorkoutShareAchievementVM.class);
        mainVM = new ViewModelProvider(requireActivity()).get(MainVM.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        communityVM = new ViewModelProvider(requireActivity()).get(CommunityVM.class);

        binding.achievementLayout.nameTv.setText(mainVM.getUser().getValue().getName());
        binding.achievementLayout.dateTv.setText(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()));
        binding.achievementLayout.achievementMenuBtn.setVisibility(View.GONE);
        if (mainVM.getUserImageUrl() == null) {
            binding.achievementLayout.avatarImg.setImageResource(R.drawable.default_profile_image);
        } else {
            Glide.with(requireContext()).load(mainVM.getUser().getValue().getImageUrl()).into(binding.achievementLayout.avatarImg);
        }

        viewModel.getWarningDialogMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
// == Quản lý dữ liệu bằng ViewModel ==
            public void onChanged(String s) {
                if (!s.equals("")) {
                    PracticingOnBackDialogInterface action = new PracticingOnBackDialogInterface() {
                        @Override
// == Quản lý dữ liệu bằng ViewModel ==
                        public void onPositiveButton() {

                        }

                        @Override
// == Quản lý dữ liệu bằng ViewModel ==
                        public void onNegativeButton() {

                        }
                    };
                    GlobalMethods.showWarningDialog(requireContext(), s, action);
                }
            }
        });

        viewModel.getIsAddedSuccessfully().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isAdded) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Congratulate! You have added an achievement.", Toast.LENGTH_SHORT).show();
                    communityVM.loadAchievements();
                    GlobalMethods.backToPreviousFragment(CommunityShareAchievementFragment.this);
                }
            }
        });

        binding.achievementLayout.detailsBtn.setVisibility(View.GONE);

        setOnClick();

        return binding.getRoot();
    }

    private void setOnClick() {
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Quản lý dữ liệu bằng ViewModel ==
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(CommunityShareAchievementFragment.this);
            }
        });

        binding.submitBtn.setOnClick(new View.OnClickListener() {
            @Override
// == Quản lý dữ liệu bằng ViewModel ==
            public void onClick(View view) {
                viewModel.addAchievementToDb(mainVM.getUser().getValue());
            }
        });
    }
}