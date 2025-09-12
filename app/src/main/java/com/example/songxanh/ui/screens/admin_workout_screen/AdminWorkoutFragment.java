package com.example.songxanh.ui.screens.admin_workout_screen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.songxanh.R;
import com.example.songxanh.data.models.WorkoutCategory;
import com.example.songxanh.databinding.FragmentAdminWorkoutBinding;
import com.example.songxanh.ui.screens.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AdminWorkoutFragment extends Fragment implements AdminWorkoutCategoriesAdapter.OnCategoryDetailsClick {

    FragmentAdminWorkoutBinding binding;
    AdminWorkoutCategoriesAdapter adapter;
    AdminWorkoutVM viewModel;
    public AdminWorkoutFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(requireActivity());
        viewModel = viewModelProvider.get(AdminWorkoutVM.class);
        binding = FragmentAdminWorkoutBinding.inflate(inflater,container,false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        adapter = new AdminWorkoutCategoriesAdapter(this.getContext(), viewModel.workoutCategories.getValue(), this);
        binding.adminWorkoutMuscleCategory.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.adminWorkoutMuscleCategory.setLayoutManager(linearLayoutManager);

        binding.adminWorkoutAddCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminWorkoutFragment.this).navigate(R.id.action_adminWorkoutFragment_to_adminAddWorkoutCategoryFragment);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.workoutCategories.observe(getViewLifecycleOwner(), new Observer<ArrayList<WorkoutCategory>>() {
            @Override
            public void onChanged(ArrayList<WorkoutCategory> workoutCategories) {
                adapter.setWorkoutCategories(workoutCategories);
            }
        });
    }

    @Override
    public void onCategoryDetailsClick(int position) {
        viewModel.fetchExercisesInCategory(viewModel.getWorkoutCategories().getValue().get(position).getId());
        NavHostFragment.findNavController(AdminWorkoutFragment.this).navigate(R.id.action_adminWorkoutFragment_to_adminEditExercisesFragment);

    }
}