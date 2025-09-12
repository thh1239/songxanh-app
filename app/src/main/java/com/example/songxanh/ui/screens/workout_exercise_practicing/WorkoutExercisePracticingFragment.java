package com.example.songxanh.ui.screens.workout_exercise_practicing;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.R;
import com.example.songxanh.data.adapters.ExercisePracticingPageAdapter;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.databinding.FragmentWorkoutExercisePracticingBinding;
import com.example.songxanh.ui.screens.workout.WorkoutVM;
import com.example.songxanh.utils.GlobalMethods;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WorkoutExercisePracticingFragment extends Fragment {
    private FragmentWorkoutExercisePracticingBinding binding;
    private WorkoutVM workoutVM;
    private ExercisePracticingPageAdapter pageAdapter;
    private List<Exercise> selectedExercises;
    private CountDownTimer timer;
    private long remainingTimeInMilliseconds = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWorkoutExercisePracticingBinding.inflate(inflater, container, false);
        workoutVM = new ViewModelProvider(requireActivity()).get(WorkoutVM.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Get selected exercises from view model
        selectedExercises = workoutVM.getSelectedExercises().getValue();
        pageAdapter = new ExercisePracticingPageAdapter(requireContext(), selectedExercises);
        binding.exerciseList.setAdapter(pageAdapter);
        pageAdapter.setViewPager(binding.exerciseList);

        // Handle timer for the first page separately because addOnPageChangeListener method does not apply on the first page
        handleTimer(0);

        binding.exerciseList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pauseTimer(); // reset timer if it existed
                handleTimer(position);
                Log.i("Position", String.valueOf(position));
                Log.i("UNIT", selectedExercises.get(position).getUnit());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setOnClick();

        return binding.getRoot();
    }

    private void setOnClick() {
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PracticingOnBackDialogInterface onBackDialogInterface = new PracticingOnBackDialogInterface() {
                    @Override
                    public void onPositiveButton() {
                        GlobalMethods.backToPreviousFragment(WorkoutExercisePracticingFragment.this);
                    }

                    @Override
                    public void onNegativeButton() {

                    }
                };
                GlobalMethods.showWarningDialog(requireContext(), "If you back, we will cancel result of previous exercises.", onBackDialogInterface);
            }
        });

        binding.previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.exerciseList.setCurrentItem(binding.exerciseList.getCurrentItem() - 1);
            }
        });

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.exerciseList.setCurrentItem(binding.exerciseList.getCurrentItem() + 1);
            }
        });
    }

    private void handleRestTime() {

    }

    private void handleTimer(int position) {
        handlePlayBtn(position);
        if (selectedExercises.get(position).getUnit().equals("seconds")) {
            createTimer(selectedExercises.get(position).getCount() * 1000, position);
        } else {
            binding.exercisePracticingTimeOrRepTv.setText("x" + String.valueOf(selectedExercises.get(position).getCount()));
        }
        handleVisibility(position);
    }

    private void handlePlayBtn(int position) {
        if (selectedExercises.get(position).getUnit().equals("seconds")) {
            binding.playBtn.setImageResource(R.drawable.ic_pause);
            binding.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (timer != null) {
                        pauseTimer();
                        binding.playBtn.setImageResource(R.drawable.ic_play);
                    } else {
                        resumeTimer();
                        binding.playBtn.setImageResource(R.drawable.ic_pause);
                    }
                }
            });
        } else {
            binding.playBtn.setImageResource(R.drawable.ic_done);
            binding.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (binding.exerciseList.getCurrentItem() < selectedExercises.size() - 1) {
                        binding.exerciseList.setCurrentItem(binding.exerciseList.getCurrentItem() + 1);
                    } else {
                        onFinishWorkout();
                    }
                }
            });
        }
    }

    private void createTimer(int timeInMilliseconds, int currentPosition) {
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.exercisePracticingTimeOrRepTv.setText(GlobalMethods.convertTimeInSeconds((int) millisUntilFinished / 1000));
                remainingTimeInMilliseconds = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                if (currentPosition < selectedExercises.size() - 1) {
                    binding.exerciseList.setCurrentItem(currentPosition + 1);
                } else {
                    onFinishWorkout();
                }
            }
        };
        timer.start();
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void resumeTimer() {
        timer = new CountDownTimer(remainingTimeInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.exercisePracticingTimeOrRepTv.setText(GlobalMethods.convertTimeInSeconds((int) millisUntilFinished / 1000));
                remainingTimeInMilliseconds = millisUntilFinished / 1000;
            }

            @Override
            public void onFinish() {
                if (binding.exerciseList.getCurrentItem() < selectedExercises.size() - 1) {
                    binding.exerciseList.setCurrentItem(binding.exerciseList.getCurrentItem() + 1);
                } else {
                    onFinishWorkout();
                }
            }
        };
        timer.start();
    }

    private void handleVisibility(int position) {
        if (position == 0) {
            binding.previousBtn.setVisibility(View.INVISIBLE);
        }
        if (position == selectedExercises.size() - 1) {
            binding.nextBtn.setVisibility(View.INVISIBLE);
            return;
        } else {
            binding.previousBtn.setVisibility(View.VISIBLE);
            binding.nextBtn.setVisibility(View.VISIBLE);
        }
    }

    private void onFinishWorkout() {
        workoutVM.finishSelectedExercises();
        NavHostFragment.findNavController(WorkoutExercisePracticingFragment.this).navigate(R.id.action_workoutExercisePracticingFragment_to_workoutFinishFragment);
    }
}
