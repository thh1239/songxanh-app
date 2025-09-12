package com.example.songxanh.ui.screens.workout_exercise_details;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.databinding.FragmentWorkoutExerciseDetailsBinding;
import com.example.songxanh.utils.GlobalMethods;

public class WorkoutExerciseDetailsFragment extends Fragment {
    private FragmentWorkoutExerciseDetailsBinding binding;
    private WorkoutExerciseDetailsVM viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentWorkoutExerciseDetailsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(WorkoutExerciseDetailsFragment.this).get(WorkoutExerciseDetailsVM.class);
        binding.setExerciseDetailsVM(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        Exercise exercise = WorkoutExerciseDetailsFragmentArgs.fromBundle(getArguments()).getExercise();
        viewModel.setExercise(exercise);
        Glide.with(requireContext()).load(exercise.getImageUrl()).into(binding.exercisePracticingImage);
        binding.exerciseDetailsTimeOrRepTv.setText(GlobalMethods.formatTimeOrRep(exercise.getCount(), exercise.getUnit()));

        setOnClick();

        viewModel.getToastMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.isEmpty()) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }

    private void setOnClick() {
        binding.appBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(WorkoutExerciseDetailsFragment.this);
            }
        });
    }
}