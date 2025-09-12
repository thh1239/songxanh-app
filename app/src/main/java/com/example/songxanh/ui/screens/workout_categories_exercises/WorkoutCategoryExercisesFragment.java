package com.example.songxanh.ui.screens.workout_categories_exercises;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.songxanh.data.adapters.MoveTempListToSelectedExerciseList;
import com.example.songxanh.data.adapters.WorkoutCategoryExercisesAdapter;
import com.example.songxanh.data.adapters.WorkoutCategorySelectedExercisesAdapter;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.databinding.FragmentWorkoutCategoryExercisesBinding;
import com.example.songxanh.ui.interfaces.ActionOnExerciseItem;
import com.example.songxanh.ui.screens.workout.WorkoutVM;
import com.example.songxanh.utils.GlobalMethods;

import java.util.List;

public class WorkoutCategoryExercisesFragment extends Fragment implements MoveTempListToSelectedExerciseList, ActionOnExerciseItem {
    private FragmentWorkoutCategoryExercisesBinding binding;
    private WorkoutCategoryExercisesVM viewModel;
    private WorkoutVM workoutVM;
    private WorkoutCategoryExercisesAdapter adapter;
    private WorkoutCategorySelectedExercisesAdapter selectedExercisesAdapter;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WorkoutCategoryExercisesVM.class);
        workoutVM = new ViewModelProvider(requireActivity()).get(WorkoutVM.class);
        String categoryId = getArguments().getString("categoryId");
        viewModel.setCategoryId(categoryId);
        viewModel.loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentWorkoutCategoryExercisesBinding.inflate(inflater, container, false);
        binding.setCategoryExerciseVM(viewModel);
        binding.setWorkoutVM(workoutVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        navController = NavHostFragment.findNavController(WorkoutCategoryExercisesFragment.this);

        setUpCategoryExercisesRecyclerView();
        setUpSelectedExercisesRecyclerView();

        setUpToastObserver();

        setOnClick();

        workoutVM.getSelectedTotalCalories().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer total) {
                binding.totalCaloriesTv.setText(String.valueOf(total) + " cal");
            }
        });

        return binding.getRoot();
    }

    private void setUpSelectedExercisesRecyclerView() {
        selectedExercisesAdapter = new WorkoutCategorySelectedExercisesAdapter(requireContext(), viewModel.getSelectedTempList().getValue(), this);
        binding.selectedExerciseLst.setAdapter(selectedExercisesAdapter);
        LinearLayoutManager selectedExercisesLayoutManager = new LinearLayoutManager(requireContext()) {};
        binding.selectedExerciseLst.setLayoutManager(selectedExercisesLayoutManager);

        viewModel.getSelectedTempList().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                selectedExercisesAdapter.setData(exercises);
                selectedExercisesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setUpCategoryExercisesRecyclerView() {
        viewModel.getExercises().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                if (!exercises.isEmpty()) {
                    adapter = new WorkoutCategoryExercisesAdapter(requireContext(), viewModel.getExercises().getValue(), navController, WorkoutCategoryExercisesFragment.this);
                    binding.exerciseLst.setAdapter(adapter);
                    binding.exerciseLst.setLayoutManager(new LinearLayoutManager(requireContext()));
                }
            }
        });
    }

    private void setUpToastObserver() {
        workoutVM.getAddSelectedExercisesToDbMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if (message != null) {
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    workoutVM.setAddSelectedExercisesToDbMessage(null);
                }
            }
        });
    }

    private void setOnClick() {
        binding.appBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(WorkoutCategoryExercisesFragment.this);
            }
        });

        binding.addToSelectedListBtn.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workoutVM.moveTempListToSelectedList(viewModel.getSelectedTempList().getValue());
                viewModel.clearTempList();
            }
        });
    }

    @Override
    public void addExerciseToTempList(Exercise exercise) {
        viewModel.addExerciseToTempList(exercise);
    }

    @Override
    public void onInformationBtn(Exercise exercise) {
        com.example.songxanh.ui.screens.workout_categories_exercises.WorkoutCategoryExercisesFragmentDirections.ActionWorkoutCategoryExercisesFragmentToWorkoutExerciseDetailsFragment action =
                com.example.songxanh.ui.screens.workout_categories_exercises.WorkoutCategoryExercisesFragmentDirections.actionWorkoutCategoryExercisesFragmentToWorkoutExerciseDetailsFragment(exercise);
        navController.navigate(action);
    }

    @Override
    public void onDelete(int position) {

    }
}