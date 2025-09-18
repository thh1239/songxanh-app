package com.example.songxanh.ui.screens.workout_edit_information;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.databinding.FragmentWorkoutEditInformationBinding;
import com.example.songxanh.ui.screens.workout_categories_exercises.WorkoutCategoryExercisesVM;
import com.example.songxanh.utils.GlobalMethods;

public class WorkoutEditInformationFragment extends Fragment {
    private FragmentWorkoutEditInformationBinding binding;
    private WorkoutEditInformationVM viewModel;
    private WorkoutCategoryExercisesVM workoutCategoryExercisesVM;
    private Exercise exercise;
    private int defaultCount = 0;
    private double defaultCalories = 0;

    @Override
// == Tính toán và hiển thị tổng calo ==
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(WorkoutEditInformationVM.class);
        workoutCategoryExercisesVM = new ViewModelProvider(requireActivity()).get(WorkoutCategoryExercisesVM.class);
    }

    @Override
// == Tính toán và hiển thị tổng calo ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentWorkoutEditInformationBinding.inflate(inflater, container, false);
        binding.setWorkoutEditInformationVM(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        exercise = new Exercise(WorkoutEditInformationFragmentArgs.fromBundle(getArguments()).getExercise());
        defaultCalories = exercise.getCaloriesPerUnit();
        defaultCount = exercise.getCount();
        viewModel.setUnit(exercise.getUnit());
        viewModel.setDefaultCalories(defaultCalories);
        viewModel.setDefaultCount(defaultCount);

        binding.exerciseNameTv.setText(exercise.getName().toUpperCase());
        Glide.with(this).load(exercise.getImageUrl()).into(binding.exerciseImage);

        if (exercise.getUnit().equals("seconds")) {
            setUpTimeSetter();
        } else {
            setUpRepsSetter();
        }

        binding.caloriesEdt.setText(String.valueOf(exercise.getCaloriesPerUnit()));

        setOnClick();





        return binding.getRoot();
    }

    private void setUpRepsSetter() {
        binding.timeSetterLayout.setVisibility(View.GONE); // hide time setter
        binding.repEdt.setText(String.valueOf(exercise.getCount()));

        viewModel.getReps().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onChanged(String reps) {
                if (reps != null) {
                    double newCalories = reps.isEmpty() ? 0 : Integer.parseInt(reps) / defaultCount * defaultCalories;
                    viewModel.setCalories(String.valueOf(newCalories));
                }
            }
        });
    }

    private void setUpTimeSetter() {
        binding.repSetterLayout.setVisibility(View.GONE); // hide rep setter
        int hour = exercise.getCount() / 3600;
        int minute = (exercise.getCount() - hour * 3600) / 60;
        int second = (exercise.getCount() - hour * 3600 - minute * 60);
        if (hour != 0) {
            binding.hourEdt.setText(String.valueOf(hour));
        }
        if (minute != 0) {
            binding.minutesEdt.setText(String.valueOf(minute));
        }
        if (second != 0) {
            binding.secondsEdt.setText(String.valueOf(second));
        }

        viewModel.getSeconds().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onChanged(String second) {
                int hourInSecond = viewModel.getHours().getValue().isEmpty() ? 0 :  Integer.parseInt(viewModel.getHours().getValue()) * 3600;
                int minuteInSecond = viewModel.getMinutes().getValue().isEmpty() ? 0 : Integer.parseInt(viewModel.getMinutes().getValue()) * 60;
                int secondValidated = viewModel.getSeconds().getValue().isEmpty() ? 0 : Integer.parseInt(viewModel.getSeconds().getValue());
                double total = hourInSecond + minuteInSecond + secondValidated;
                String formatted = String.format("%.2f", total / defaultCount * defaultCalories);
                viewModel.setCalories(formatted);
            }
        });

        viewModel.getMinutes().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onChanged(String minute) {
                int hourInSecond = viewModel.getHours().getValue().isEmpty() ? 0 :  Integer.parseInt(viewModel.getHours().getValue()) * 3600;
                int minuteInSecond = viewModel.getMinutes().getValue().isEmpty() ? 0 : Integer.parseInt(viewModel.getMinutes().getValue()) * 60;
                int second = viewModel.getSeconds().getValue().isEmpty() ? 0 : Integer.parseInt(viewModel.getSeconds().getValue());
                double total = hourInSecond + minuteInSecond + second;
                String formatted = String.format("%.2f", total / defaultCount * defaultCalories);
                viewModel.setCalories(formatted);
            }
        });

        viewModel.getHours().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onChanged(String hour) {
                int hourInSecond = viewModel.getHours().getValue().isEmpty() ? 0 :  Integer.parseInt(viewModel.getHours().getValue()) * 3600;
                int minuteInSecond = viewModel.getMinutes().getValue().isEmpty() ? 0 : Integer.parseInt(viewModel.getMinutes().getValue()) * 60;
                int second = viewModel.getSeconds().getValue().isEmpty() ? 0 : Integer.parseInt(viewModel.getSeconds().getValue());
                double total = hourInSecond + minuteInSecond + second;
                String formatted = String.format("%.2f", total / defaultCount * defaultCalories);
                viewModel.setCalories(formatted);
            }
        });
    }

    private void setOnClick() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(WorkoutEditInformationFragment.this);
            }
        }
    );

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onClick(View view) {
                if (viewModel.getCalories().getValue() != "0") {

                    int newCount = 0;
                    if (viewModel.getReps().getValue() != null) {
                        newCount = viewModel.getReps().getValue().isEmpty() ? 0 : Integer.parseInt(viewModel.getReps().getValue());
                    } else {
                        int hourInSecond = viewModel.getHours().getValue().isEmpty() ? 0 :  Integer.parseInt(viewModel.getHours().getValue()) * 3600;
                        int minuteInSecond = viewModel.getMinutes().getValue().isEmpty() ? 0 : Integer.parseInt(viewModel.getMinutes().getValue()) * 60;
                        int second = viewModel.getSeconds().getValue().isEmpty() ? 0 : Integer.parseInt(viewModel.getSeconds().getValue());
                        newCount = hourInSecond + minuteInSecond + second;
                    }
                    double newCalories = Double.parseDouble(viewModel.getCalories().getValue());
                    exercise.setCount(newCount);
                    exercise.setCaloriesPerUnit(newCalories);

                    workoutCategoryExercisesVM.addExerciseToTempList(exercise);

                    GlobalMethods.backToPreviousFragment(WorkoutEditInformationFragment.this);
                }
            }
        });
    }
}