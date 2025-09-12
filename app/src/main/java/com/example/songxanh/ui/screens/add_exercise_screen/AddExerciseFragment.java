package com.example.songxanh.ui.screens.add_exercise_screen;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.databinding.FragmentAddExerciseBinding;
import com.example.songxanh.ui.screens.admin_workout_screen.AdminWorkoutVM;
import com.example.songxanh.utils.GlobalMethods;

public class AddExerciseFragment extends Fragment {
    FragmentAddExerciseBinding binding;
    AdminWorkoutVM adminWorkoutVM;
    String operation;

    private ActivityResultLauncher<String> getContent;

    private Uri imageUri;


    public AddExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        operation = requireArguments().getString("operation");
        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
        adminWorkoutVM = viewModelProvider.get(AdminWorkoutVM.class);
        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result == null) return;
                imageUri = result;
                Glide.with(requireActivity()).load(result).centerCrop().into(binding.exerciseImagePicker);
            }
        });
        binding = FragmentAddExerciseBinding.inflate(inflater, container, false);
        binding.setViewModel(adminWorkoutVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.exerciseImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContent.launch("image/*");
            }
        });
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalMethods.backToPreviousFragment(AddExerciseFragment.this);
            }
        });

        setUpFragment();


        return binding.getRoot();
    }
    private void setUpFragment() {
        if (operation.equals("add")) {
            binding.addExerciseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageUri == null || binding.exerciseNameEt.getText().toString().equals("") || binding.muscleRadioGroup.getCheckedRadioButtonId() == -1 || binding.unitRadioGroup.getCheckedRadioButtonId() == -1 || binding.countEt.getText().toString().equals("") || binding.caloriesEt.getText().toString().equals("") || binding.startingPointEt.getText().toString().equals("") || binding.executionEt.getText().toString().equals("")) {
                        return;
                    }
                    Exercise temp = new Exercise();
                    temp.setName(binding.exerciseNameEt.getText().toString());
                    RadioButton selectedMuscleGroupButton = (RadioButton) binding.getRoot().findViewById(binding.muscleRadioGroup.getCheckedRadioButtonId());
                    temp.setMuscleGroup(selectedMuscleGroupButton.getText().toString());
                    RadioButton selectedUnitGroupButton = (RadioButton) binding.getRoot().findViewById(binding.unitRadioGroup.getCheckedRadioButtonId());
                    temp.setUnit(selectedUnitGroupButton.getText().toString().toLowerCase());
                    temp.setCount(Integer.parseInt(binding.countEt.getText().toString()));
                    temp.setCaloriesPerUnit(Integer.parseInt(binding.caloriesEt.getText().toString()));
                    temp.setStartingPosition(binding.startingPointEt.getText().toString());
                    temp.setExecution(binding.executionEt.getText().toString());
                    adminWorkoutVM.addNewExercise(temp, imageUri);
                    GlobalMethods.backToPreviousFragment(AddExerciseFragment.this);
                }
            });
        } else if (operation.equals("edit")) {
            int position = requireArguments().getInt("position");
            Exercise temp = adminWorkoutVM.getExercises().getValue().get(position);
            Glide.with(requireActivity()).load(temp.getImageUrl()).centerCrop().into(binding.exerciseImagePicker);
            imageUri = Uri.parse(temp.getImageUrl());
            binding.exerciseNameEt.setText(temp.getName());
            switch (temp.getMuscleGroup()) {
                case "Chest":
                    binding.chestRb.setChecked(true);
                    break;

                case "Back":
                    binding.backRb.setChecked(true);
                    break;
                case "Bicep":
                    binding.bicepRb.setChecked(true);
                    break;
                case "Legs":
                    binding.legRb.setChecked(true);
                    break;
                case "Abdominals":
                    binding.abdominalsRb.setChecked(true);
                    break;
                case "Calves":
                    binding.calvlesRb.setChecked(true);
                    break;
                case "Shoulders":
                    binding.shoulderRb.setChecked(true);
                    break;
                case "Triceps":
                    binding.tricepsRb.setChecked(true);
                    break;
            }
            switch (temp.getUnit()) {
                case "seconds":
                    binding.secondRb.setChecked(true);
                    break;
                case "reps":
                    binding.repsRb.setChecked(true);
            }
            binding.countEt.setText(String.valueOf(temp.getCount()));
            binding.caloriesEt.setText(String.valueOf(temp.getCaloriesPerUnit()));
            binding.startingPointEt.setText(temp.getStartingPosition());
            binding.executionEt.setText(temp.getExecution());
            binding.addExerciseBtn.setText("Edit");

            binding.addExerciseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Exercise newExercise = new Exercise();
                    newExercise.setName(binding.exerciseNameEt.getText().toString());
                    RadioButton selectedMuscleGroupButton = (RadioButton) binding.getRoot().findViewById(binding.muscleRadioGroup.getCheckedRadioButtonId());
                    newExercise.setMuscleGroup(selectedMuscleGroupButton.getText().toString());
                    newExercise.setImageUrl(temp.getImageUrl());
                    newExercise.setId(temp.getId());
                    newExercise.setCategoryId(temp.getCategoryId());
                    RadioButton selectedUnitGroupButton = (RadioButton) binding.getRoot().findViewById(binding.unitRadioGroup.getCheckedRadioButtonId());
                    newExercise.setUnit(selectedUnitGroupButton.getText().toString().toLowerCase());
                    newExercise.setCount(Integer.parseInt(binding.countEt.getText().toString()));
                    newExercise.setCaloriesPerUnit(Double.parseDouble(binding.caloriesEt.getText().toString()));
                    newExercise.setStartingPosition(binding.startingPointEt.getText().toString());
                    newExercise.setExecution(binding.executionEt.getText().toString());
                    adminWorkoutVM.updateExercise(newExercise, imageUri);
                    GlobalMethods.backToPreviousFragment(AddExerciseFragment.this);
                }
            });
        }

    }
}