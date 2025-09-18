package com.example.songxanh.ui.screens.add_exercise_screen;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private boolean formattingTime;

    public AddExerciseFragment() {}

    @Override
// == Load ảnh bằng Glide và hiển thị ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        operation = requireArguments().getString("operation");
        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
        adminWorkoutVM = viewModelProvider.get(AdminWorkoutVM.class);

        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            if (result == null) return;
            imageUri = result;
            Glide.with(requireActivity()).load(result).centerCrop().into(binding.exerciseImagePicker);
        });

        binding = FragmentAddExerciseBinding.inflate(inflater, container, false);
        binding.setViewModel(adminWorkoutVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        binding.exerciseImagePicker.setOnClickListener(v -> getContent.launch("image/*"));
        binding.appBar.backBtn.setOnClickListener(v -> GlobalMethods.backToPreviousFragment(AddExerciseFragment.this));

        setupUnitBehavior();
        setUpFragment();

        return binding.getRoot();
    }

    private void setupUnitBehavior() {
        binding.unitRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.secondRb.getId()) {
                binding.countEt.setInputType(InputType.TYPE_CLASS_TEXT);
                binding.countEt.setText("00:00");
                binding.countEt.setSelection(binding.countEt.getText().length());
            } else if (checkedId == binding.repsRb.getId()) {
                binding.countEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                binding.countEt.setText("0");
                binding.countEt.setSelection(binding.countEt.getText().length());
            }
        });

        binding.countEt.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                if (formattingTime) return;
                if (binding.secondRb.isChecked()) {
                    formattingTime = true;
                    String formatted = enforceMMSS(s.toString());
                    binding.countEt.setText(formatted);
                    binding.countEt.setSelection(formatted.length());
                    formattingTime = false;
                }
            }
        });
    }

    private void setUpFragment() {
        if (operation.equals("add")) {
            binding.addExerciseBtn.setOnClickListener(v -> {
                if (imageUri == null
                        || binding.exerciseNameEt.getText().toString().trim().isEmpty()
                        || (!binding.chestRb.isChecked() && !binding.backRb.isChecked()
                        && !binding.bicepRb.isChecked() && !binding.legRb.isChecked()
                        && !binding.abdominalsRb.isChecked() && !binding.calvlesRb.isChecked()
                        && !binding.shoulderRb.isChecked() && !binding.tricepsRb.isChecked())
                        || (!binding.secondRb.isChecked() && !binding.repsRb.isChecked())
                        || binding.countEt.getText().toString().trim().isEmpty()
                        || binding.caloriesEt.getText().toString().trim().isEmpty()
                        || binding.startingPointEt.getText().toString().trim().isEmpty()
                        || binding.executionEt.getText().toString().trim().isEmpty()) {
                    return;
                }

                Exercise temp = new Exercise();
                temp.setName(binding.exerciseNameEt.getText().toString().trim());

                String muscle;
                if (binding.chestRb.isChecked()) muscle = "Chest";
                else if (binding.backRb.isChecked()) muscle = "Back";
                else if (binding.bicepRb.isChecked()) muscle = "Bicep";
                else if (binding.legRb.isChecked()) muscle = "Legs";
                else if (binding.abdominalsRb.isChecked()) muscle = "Abdominals";
                else if (binding.calvlesRb.isChecked()) muscle = "Calves";
                else if (binding.shoulderRb.isChecked()) muscle = "Shoulders";
                else if (binding.tricepsRb.isChecked()) muscle = "Triceps";
                else return;
                temp.setMuscleGroup(muscle);

                String unit = binding.secondRb.isChecked() ? "seconds" : binding.repsRb.isChecked() ? "reps" : null;
                if (unit == null) return;
                temp.setUnit(unit);

                int count = unit.equals("seconds")
                        ? parseMMSS(binding.countEt.getText().toString())
                        : safeParseInt(binding.countEt.getText().toString(), 0);
                temp.setCount(count);

                temp.setCaloriesPerUnit(safeParseInt(binding.caloriesEt.getText().toString(), 0));
                temp.setStartingPosition(binding.startingPointEt.getText().toString().trim());
                temp.setExecution(binding.executionEt.getText().toString().trim());

                adminWorkoutVM.addNewExercise(temp, imageUri);
                GlobalMethods.backToPreviousFragment(AddExerciseFragment.this);
            });
        } else if (operation.equals("edit")) {
            int position = requireArguments().getInt("position");
            Exercise temp = adminWorkoutVM.getExercises().getValue().get(position);

            Glide.with(requireActivity()).load(temp.getImageUrl()).centerCrop().into(binding.exerciseImagePicker);
            imageUri = Uri.parse(temp.getImageUrl());
            binding.exerciseNameEt.setText(temp.getName());

            switch (temp.getMuscleGroup()) {
                case "Chest": binding.chestRb.setChecked(true); break;
                case "Back": binding.backRb.setChecked(true); break;
                case "Bicep": binding.bicepRb.setChecked(true); break;
                case "Legs": binding.legRb.setChecked(true); break;
                case "Abdominals": binding.abdominalsRb.setChecked(true); break;
                case "Calves": binding.calvlesRb.setChecked(true); break;
                case "Shoulders": binding.shoulderRb.setChecked(true); break;
                case "Triceps": binding.tricepsRb.setChecked(true); break;
                default: binding.chestRb.setChecked(true); break;
            }

            switch (String.valueOf(temp.getUnit()).toLowerCase()) {
                case "seconds":
                    binding.secondRb.setChecked(true);
                    binding.countEt.setInputType(InputType.TYPE_CLASS_TEXT);
                    binding.countEt.setText(formatSecondsToMMSS(temp.getCount()));
                    break;
                case "reps":
                    binding.repsRb.setChecked(true);
                    binding.countEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    binding.countEt.setText(String.valueOf(temp.getCount()));
                    break;
                default:
                    binding.repsRb.setChecked(true);
                    binding.countEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                    binding.countEt.setText(String.valueOf(temp.getCount()));
                    break;
            }

            binding.caloriesEt.setText(String.valueOf(temp.getCaloriesPerUnit()));
            binding.startingPointEt.setText(temp.getStartingPosition());
            binding.executionEt.setText(temp.getExecution());
            binding.addExerciseBtn.setText("Edit");

            binding.addExerciseBtn.setOnClickListener(v -> {
                Exercise newExercise = new Exercise();
                newExercise.setName(binding.exerciseNameEt.getText().toString().trim());

                String muscle;
                if (binding.chestRb.isChecked()) muscle = "Chest";
                else if (binding.backRb.isChecked()) muscle = "Back";
                else if (binding.bicepRb.isChecked()) muscle = "Bicep";
                else if (binding.legRb.isChecked()) muscle = "Legs";
                else if (binding.abdominalsRb.isChecked()) muscle = "Abdominals";
                else if (binding.calvlesRb.isChecked()) muscle = "Calves";
                else if (binding.shoulderRb.isChecked()) muscle = "Shoulders";
                else if (binding.tricepsRb.isChecked()) muscle = "Triceps";
                else return;
                newExercise.setMuscleGroup(muscle);

                newExercise.setImageUrl(temp.getImageUrl());
                newExercise.setId(temp.getId());
                newExercise.setCategoryId(temp.getCategoryId());

                String unit = binding.secondRb.isChecked() ? "seconds" : binding.repsRb.isChecked() ? "reps" : null;
                if (unit == null) return;
                newExercise.setUnit(unit);

                int count = unit.equals("seconds")
                        ? parseMMSS(binding.countEt.getText().toString())
                        : safeParseInt(binding.countEt.getText().toString(), 0);
                newExercise.setCount(count);

                try {
                    newExercise.setCaloriesPerUnit(Double.parseDouble(binding.caloriesEt.getText().toString().trim()));
                } catch (Exception e) {
                    newExercise.setCaloriesPerUnit(0.0);
                }

                newExercise.setStartingPosition(binding.startingPointEt.getText().toString().trim());
                newExercise.setExecution(binding.executionEt.getText().toString().trim());

                adminWorkoutVM.updateExercise(newExercise, imageUri);
                GlobalMethods.backToPreviousFragment(AddExerciseFragment.this);
            });
        }
    }

    private String enforceMMSS(String raw) {
        String digitsOnly = raw.replaceAll("[^0-9]", "");
        if (digitsOnly.length() > 4) digitsOnly = digitsOnly.substring(0, 4);
        while (digitsOnly.length() < 4) digitsOnly = "0" + digitsOnly;
        String mm = digitsOnly.substring(0, 2);
        String ss = digitsOnly.substring(2, 4);
        return mm + ":" + ss;
    }

    private String formatSecondsToMMSS(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int mm = totalSeconds / 60;
        int ss = totalSeconds % 60;
        if (mm > 99) mm = 99;
        return String.format("%02d:%02d", mm, ss);
    }

    private int parseMMSS(String mmss) {
        if (mmss == null || mmss.trim().isEmpty()) return 0;
        String s = mmss.trim();
        if (s.contains(":")) {
            String[] parts = s.split(":");
            String mmStr = parts.length > 0 ? parts[0] : "0";
            String ssStr = parts.length > 1 ? parts[1] : "0";
            int mm = safeParseInt(mmStr, 0);
            int ss = safeParseInt(ssStr, 0);
            if (mm < 0) mm = 0;
            if (ss < 0) ss = 0;
            if (ss > 59) ss = 59;
            if (mm > 99) mm = 99;
            return mm * 60 + ss;
        } else {
            String digits = s.replaceAll("[^0-9]", "");
            if (digits.isEmpty()) return 0;
            if (digits.length() <= 2) {
                int ss = safeParseInt(digits, 0);
                if (ss > 59) ss = 59;
                return ss;
            } else {
                if (digits.length() > 4) digits = digits.substring(0, 4);
                while (digits.length() < 4) digits = "0" + digits;
                int mm = safeParseInt(digits.substring(0, 2), 0);
                int ss = safeParseInt(digits.substring(2, 4), 0);
                if (mm > 99) mm = 99;
                if (ss > 59) ss = 59;
                return mm * 60 + ss;
            }
        }
    }

    private int safeParseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }
}
