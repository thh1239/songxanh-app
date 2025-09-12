package com.example.songxanh.ui.screens.exercise_detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.R;
import com.example.songxanh.data.adapters.HomeStatisticsCategoryAdapter;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.data.models.ExerciseCategory;
import com.example.songxanh.databinding.FragmentExcerciseDetailBinding;
import com.example.songxanh.databinding.FragmentHomeBinding;
import com.example.songxanh.ui.screens.home.HomeFragment;
import com.example.songxanh.ui.screens.workout_categories.WorkoutCategoriesFragment;
import com.example.songxanh.utils.GlobalMethods;

import java.util.ArrayList;
import java.util.List;

public class ExcerciseDetailFragment extends Fragment {

    private FragmentExcerciseDetailBinding binding;

    private ExerciseDetailVM exerciseDetailVM;
    private HomeStatisticsCategoryAdapter adapter;

    public ExcerciseDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentExcerciseDetailBinding.inflate(inflater,container,false);
        exerciseDetailVM = new ViewModelProvider(this).get(ExerciseDetailVM.class);
        binding.setViewModel(exerciseDetailVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.exerciseDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(ExcerciseDetailFragment.this);
            }
        });

        adapter = new HomeStatisticsCategoryAdapter(requireContext(), new ArrayList<>(), new ArrayList<>());
        binding.exerciseCategoriesLst.setAdapter(adapter);

        exerciseDetailVM.getCategories().observe(getViewLifecycleOwner(), new Observer<List<ExerciseCategory>>() {
            @Override
            public void onChanged(List<ExerciseCategory> exerciseCategories) {
                adapter.setCategories(exerciseCategories);
            }
        });

        exerciseDetailVM.getWorkouts().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                adapter.setExercises(exercises);
            }
        });

        return binding.getRoot();
    }
}