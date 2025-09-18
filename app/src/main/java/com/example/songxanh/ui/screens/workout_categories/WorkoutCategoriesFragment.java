package com.example.songxanh.ui.screens.workout_categories;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.data.adapters.WorkoutCategoriesAdapter;
import com.example.songxanh.databinding.FragmentWorkoutCategoriesBinding;
import com.example.songxanh.utils.GlobalMethods;

public class WorkoutCategoriesFragment extends Fragment {
    private FragmentWorkoutCategoriesBinding binding;
    private WorkoutCategoriesVM viewModel;
    private WorkoutCategoriesAdapter adapter;

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(WorkoutCategoriesVM.class);
        viewModel.loadData();
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWorkoutCategoriesBinding.inflate(inflater, container, false);
        binding.setWorkoutCategoriesVM(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setLoading();

        setOnClick();

        return binding.getRoot();
    }

    private void setOnClick() {
        binding.appBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
// == Quản lý dữ liệu bằng ViewModel ==
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(WorkoutCategoriesFragment.this);
            }
        });
    }

    private void setLoading() {
        viewModel.getIsLoadingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
// == Quản lý dữ liệu bằng ViewModel ==
            public void onChanged(Boolean isLoadingData) {
                if (isLoadingData != null && !isLoadingData) {
                    binding.progressBarCyclic.setVisibility(View.GONE);

                    adapter = new WorkoutCategoriesAdapter(requireContext(), NavHostFragment.findNavController(WorkoutCategoriesFragment.this),  viewModel.getCategories());
                    binding.categoriesLst.setAdapter(adapter);
                    binding.categoriesLst.setLayoutManager(new LinearLayoutManager(requireContext()));
                } else {
                    binding.progressBarCyclic.setVisibility(View.VISIBLE);
                }
            }
        });
    };
}