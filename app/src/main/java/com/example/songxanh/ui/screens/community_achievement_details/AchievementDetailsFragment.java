package com.example.songxanh.ui.screens.community_achievement_details;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.adapters.AchievementExercisesAdapter;
import com.example.songxanh.data.adapters.AchievementFoodsAdapter;
import com.example.songxanh.data.models.Dish;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.databinding.FragmentAchievementDetailsBinding;
import com.example.songxanh.ui.screens.menu.DishRecycleViewAdapter;
import com.example.songxanh.utils.GlobalMethods;

import java.util.ArrayList;
import java.util.List;

public class AchievementDetailsFragment extends Fragment {
    private FragmentAchievementDetailsBinding binding;
    private AchievementDetailsVM viewModel;
    private AchievementFoodsAdapter foodsAdapter;
    private DishRecycleViewAdapter dishRecycleViewAdapter;
    private AchievementExercisesAdapter exercisesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAchievementDetailsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(AchievementDetailsVM.class);
        binding.setDetailsVM(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        viewModel.setAchievement(AchievementDetailsFragmentArgs.fromBundle(getArguments()).getAchievement());

        if (viewModel.getAchievement().getValue().getUserImageUrl() == null) {
            binding.achievementDetailUserAvatarImg.setImageResource(R.drawable.default_profile_image);
        } else {
            Glide.with(requireContext()).load(viewModel.getAchievement().getValue().getUserImageUrl()).into(binding.achievementDetailUserAvatarImg);
        }

        setUpFoodList();

        setUpExerciseList();

        setOnClick();

        return binding.getRoot();
    }

    private void setUpFoodList() {
        dishRecycleViewAdapter = new DishRecycleViewAdapter(requireContext(), viewModel.getDishes().getValue(), false, null);
        binding.foodLst.setAdapter(dishRecycleViewAdapter);
        binding.foodLst.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel.getDishes().observe(getViewLifecycleOwner(), new Observer<ArrayList<Dish>>() {
            @Override
            public void onChanged(ArrayList<Dish> dishArrayList) {
                Log.d("dishes", "onChanged: " + dishArrayList);
                dishRecycleViewAdapter.setDishes(dishArrayList);

            }
        });
    }

    private void setUpExerciseList() {
        exercisesAdapter = new AchievementExercisesAdapter(requireContext(), new ArrayList<>());
        binding.exerciseLst.setAdapter(exercisesAdapter);
        binding.exerciseLst.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getExercises().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                exercisesAdapter.addAll(exercises);
            }
        });
    }

    private void setOnClick() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(AchievementDetailsFragment.this);
            }
        });
    }
}