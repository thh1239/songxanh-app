package com.example.songxanh.data.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.songxanh.ui.screens.workout.WorkoutAdvanceFragment;
import com.example.songxanh.ui.screens.workout.WorkoutBeginnerFragment;
import com.example.songxanh.ui.screens.workout.WorkoutIntermediateFragment;

public class WorkoutPageAdapter extends FragmentStateAdapter {
    public WorkoutPageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new WorkoutBeginnerFragment();
            case 1:
                return new WorkoutIntermediateFragment();
            case 2:
                return new WorkoutAdvanceFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
