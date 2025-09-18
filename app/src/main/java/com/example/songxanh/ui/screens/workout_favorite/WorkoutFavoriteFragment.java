package com.example.songxanh.ui.screens.workout_favorite;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.R;
import com.example.songxanh.data.adapters.WorkoutCategoryExercisesAdapter;
import com.example.songxanh.data.adapters.WorkoutCategorySelectedExercisesAdapter;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.databinding.FragmentWorkoutFavoriteBinding;
import com.example.songxanh.ui.animations.SlideLeftAnimator;
import com.example.songxanh.ui.interfaces.ActionOnExerciseItem;
import com.example.songxanh.ui.screens.workout_categories_exercises.WorkoutCategoryExercisesFragmentDirections;
import com.example.songxanh.utils.GlobalMethods;

import java.util.ArrayList;
import java.util.List;

public class WorkoutFavoriteFragment extends Fragment implements ActionOnExerciseItem {
    private FragmentWorkoutFavoriteBinding binding;
    private WorkoutFavoriteVM viewModel;
    private WorkoutCategorySelectedExercisesAdapter adapter;
    private NavController navController;

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWorkoutFavoriteBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(WorkoutFavoriteFragment.this).get(WorkoutFavoriteVM.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        navController = NavHostFragment.findNavController(this);

        adapter = new WorkoutCategorySelectedExercisesAdapter(requireContext(), new ArrayList<>(), this);
        binding.favoriteExercisesLst.setAdapter(adapter);
        binding.favoriteExercisesLst.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.favoriteExercisesLst.setItemAnimator(new SlideLeftAnimator());

        viewModel.getFavoriteList().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
            public void onChanged(List<Exercise> exercises) {
                adapter.setData(exercises);
                adapter.notifyDataSetChanged();
            }
        });

        setOnClick();

        return binding.getRoot();
    }

    private void setOnClick() {
        binding.appBarLayout.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
// == Điều hướng sang màn hình khác ==
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(WorkoutFavoriteFragment.this);
            }
        });
    }

    @Override
// == Điều hướng sang màn hình khác ==
    public void onInformationBtn(Exercise exercise) {
        WorkoutFavoriteFragmentDirections.ActionWorkoutFavoriteFragmentToWorkoutExerciseDetailsFragment action = WorkoutFavoriteFragmentDirections.actionWorkoutFavoriteFragmentToWorkoutExerciseDetailsFragment(exercise);
        navController.navigate(action);
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onDelete(int position) {
        viewModel.removeFavoriteExercise(position);
        adapter.removeItem(position);

    }
}