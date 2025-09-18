package com.example.songxanh.ui.screens.workout;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.songxanh.R;
import com.example.songxanh.data.adapters.WorkoutCategorySelectedExercisesAdapter;
import com.example.songxanh.data.adapters.WorkoutPageAdapter;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.databinding.FragmentWorkoutBinding;
import com.example.songxanh.ui.animations.SlideLeftAnimator;
import com.example.songxanh.ui.interfaces.ActionOnExerciseItem;
import com.example.songxanh.ui.screens.workout.WorkoutFragmentDirections;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class WorkoutFragment extends Fragment implements ActionOnExerciseItem {
    private FragmentWorkoutBinding binding;
    private WorkoutVM viewModel;
    private WorkoutCategorySelectedExercisesAdapter adapter;
    private NavController navController;

    public WorkoutFragment() {

    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WorkoutVM.class);
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentWorkoutBinding.inflate(inflater, container, false);
        binding.setWorkoutVM(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        navController = NavHostFragment.findNavController(WorkoutFragment.this);

        setUpExerciseList();
        setOnClick();




        return binding.getRoot();
    }

    private void setUpExerciseList() {
        adapter = new WorkoutCategorySelectedExercisesAdapter(requireContext(), viewModel.getSelectedExercises().getValue(), this);
        binding.selectedExercisesLst.setAdapter(adapter);
        binding.selectedExercisesLst.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.selectedExercisesLst.setItemAnimator(new SlideLeftAnimator());

        viewModel.getSelectedExercises().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
            public void onChanged(List<Exercise> exercises) {
                if (!exercises.isEmpty()) {
                    adapter.setData(exercises);
                }
            }
        });
    }

    private void setOnClick() {
        binding.addExerciseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavHostFragment.findNavController(WorkoutFragment.this).navigate(R.id.action_workoutFragment_to_workoutCategoriesFragment);
            }
        });

        binding.startExercisesBtn.setOnClick(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavHostFragment.findNavController(WorkoutFragment.this).navigate(R.id.action_workoutFragment_to_workoutExercisePracticingFragment);
            }
        });

        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavHostFragment.findNavController(WorkoutFragment.this).navigate(R.id.action_workoutFragment_to_workoutFavoriteFragment);
            }
        });

        binding.historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onClick(View view) {
                NavHostFragment.findNavController(WorkoutFragment.this).navigate(R.id.action_workoutFragment_to_workoutHistoryFragment);
            }
        });
    }

    @Override
// == Tính toán và hiển thị tổng calo ==
    public void onInformationBtn(Exercise exercise) {
        com.example.songxanh.ui.screens.workout.WorkoutFragmentDirections.ActionWorkoutFragmentToWorkoutExerciseDetailsFragment action =
                WorkoutFragmentDirections.actionWorkoutFragmentToWorkoutExerciseDetailsFragment(exercise);
        navController.navigate(action);
    }

    @Override
// == Tính toán và hiển thị tổng calo ==
    public void onDelete(int position) {
        viewModel.removeSelectedExercise(position);
        adapter.removeItem(position);
        viewModel.recalculateSelectedExercisesCalories();
    }
















}