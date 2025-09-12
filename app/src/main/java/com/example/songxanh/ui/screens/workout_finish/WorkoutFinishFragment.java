package com.example.songxanh.ui.screens.workout_finish;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentWorkoutFinishBinding;
import com.example.songxanh.ui.screens.workout.WorkoutVM;

public class WorkoutFinishFragment extends Fragment {
    private FragmentWorkoutFinishBinding binding;
    private WorkoutVM workoutVM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutFinishBinding.inflate(inflater, container, false);
        workoutVM = new ViewModelProvider(requireActivity()).get(WorkoutVM.class);
        binding.setWorkoutVM(workoutVM);

        binding.doneIcon.setAnimation(R.raw.lotties_done);

        setOnClick();

        return binding.getRoot();
    }

    private void setOnClick() {
        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NavHostFragment.findNavController(WorkoutFinishFragment.this).navigate(R.id.action_workoutFinishFragment_to_workoutFragment);
            }
        });
    }
}