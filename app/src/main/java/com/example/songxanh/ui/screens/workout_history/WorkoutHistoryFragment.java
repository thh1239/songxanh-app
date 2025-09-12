package com.example.songxanh.ui.screens.workout_history;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;

import com.example.songxanh.R;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.databinding.FragmentWorkoutHistoryBinding;
import com.example.songxanh.utils.GlobalMethods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class WorkoutHistoryFragment extends Fragment {
    private FragmentWorkoutHistoryBinding binding;
    private WorkoutHistoryVM viewModel;
    private WorkoutHistoryExercisesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentWorkoutHistoryBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(WorkoutHistoryVM.class);
        binding.setWorkoutHistoryVM(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setOnClick();

        NavController navController = NavHostFragment.findNavController(this);
        adapter = new WorkoutHistoryExercisesAdapter(requireContext(), new ArrayList<>(), navController);
        binding.exercisesLst.setAdapter(adapter);
        binding.exercisesLst.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getExercisesToday().observe(getViewLifecycleOwner(), new Observer<List<Exercise>>() {
            @Override
            public void onChanged(List<Exercise> exercises) {
                adapter.setNewData(exercises);
                adapter.notifyDataSetChanged();
            }
        });

        return binding.getRoot();
    }

    private void setOnClick() {
        binding.appBar.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(WorkoutHistoryFragment.this);
            }
        });

        binding.calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                Date newDate = new GregorianCalendar(year, month, dayOfMonth).getTime();
                                viewModel.setSelectedDate(newDate);
                            }
                        }, year, month, day
                );

                dialog.show();
            }
        });
    }
}