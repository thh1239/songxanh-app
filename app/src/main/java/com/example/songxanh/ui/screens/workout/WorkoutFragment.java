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
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(WorkoutVM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentWorkoutBinding.inflate(inflater, container, false);
        binding.setWorkoutVM(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        navController = NavHostFragment.findNavController(WorkoutFragment.this);

        setUpExerciseList();
        setOnClick();


//        adapter = new WorkoutPageAdapter(this);
//        binding.viewPager.setAdapter(adapter);

//        setUpTabLayoutWithViewPager();

        return binding.getRoot();
    }

    private void setUpExerciseList() {
        adapter = new WorkoutCategorySelectedExercisesAdapter(requireContext(), viewModel.getSelectedExercises().getValue(), this);
        binding.selectedExercisesLst.setAdapter(adapter);
        binding.selectedExercisesLst.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.selectedExercisesLst.setItemAnimator(new SlideLeftAnimator());

        viewModel.getSelectedExercises().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
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
            public void onClick(View view) {
                NavHostFragment.findNavController(WorkoutFragment.this).navigate(R.id.action_workoutFragment_to_workoutCategoriesFragment);
            }
        });

        binding.startExercisesBtn.setOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WorkoutFragment.this).navigate(R.id.action_workoutFragment_to_workoutExercisePracticingFragment);
            }
        });

        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WorkoutFragment.this).navigate(R.id.action_workoutFragment_to_workoutFavoriteFragment);
            }
        });

        binding.historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(WorkoutFragment.this).navigate(R.id.action_workoutFragment_to_workoutHistoryFragment);
            }
        });
    }

    @Override
    public void onInformationBtn(Exercise exercise) {
        com.example.songxanh.ui.screens.workout.WorkoutFragmentDirections.ActionWorkoutFragmentToWorkoutExerciseDetailsFragment action =
                WorkoutFragmentDirections.actionWorkoutFragmentToWorkoutExerciseDetailsFragment(exercise);
        navController.navigate(action);
    }

    @Override
    public void onDelete(int position) {
        viewModel.removeSelectedExercise(position);
        adapter.removeItem(position);
        viewModel.recalculateSelectedExercisesCalories();
    }

//    private void setUpTabLayoutWithViewPager() {
//        new TabLayoutMediator(binding.tabLayout, binding.viewPager,  (tab, position) ->
//            tab.setText(tabTitles[position])
//        ).attach();
//
//        for (int i = 0; i < 3; i++) {
//            RelativeLayout tabView = (RelativeLayout) LayoutInflater.from(requireContext()).inflate(R.layout.workout_tab_title, null);
//
//            TabLayout.Tab  tab = binding.tabLayout.getTabAt(i);
//            if (tab != null) {
//                tabView.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//                tabView.setPadding(0, 0, 0, 0);
//                tab.setCustomView(tabView);
//            }
//        }
//    }
}