package com.example.songxanh.ui.screens.profile_change_noti_time;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentProfileChangeNotiTimeBinding;
import com.example.songxanh.ui.screens.MainActivity;
import com.example.songxanh.ui.screens.home.HomeVM;
import com.example.songxanh.ui.screens.notification.mealNotificationReceiver;
import com.example.songxanh.ui.screens.notification.workoutNotificationReceiver;
import com.example.songxanh.ui.screens.profile_calories_history.ProfileCaloriesHistoryFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ProfileChangeNotiTimeFragment extends Fragment {
    private FragmentProfileChangeNotiTimeBinding binding;
    private PendingIntent notificationIntent;
    private static final int REQUEST_CODE = 123;
    private AlarmManager alarmManager;

    private ProfileChangeNotiTimeVM profileChangeNotiTimeVM;

    private int getNotificationWorkoutHour() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getInt("notification_workout_hour", 17); // 17 là giá trị mặc định nếu không tìm thấy khóa
    }

    private int getNotificationWorkoutMinute() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getInt("notification_workout_minute", 10); // 0 là giá trị mặc định nếu không tìm thấy khóa
    }
    private int getNotificationMealHour() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getInt("notification_meal_hour", 7); // 17 là giá trị mặc định nếu không tìm thấy khóa
    }

    private int getNotificationMealMinute() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        return sharedPreferences.getInt("notification_meal_minute", 10); // 0 là giá trị mặc định nếu không tìm thấy khóa
    }

    private void saveWorkoutSelectedTime(int hourOfDay, int minute) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notification_workout_hour", hourOfDay);
        editor.putInt("notification_workout_minute", minute);
        editor.apply();
    }
    private void saveMealSelectedTime(int hourOfDay, int minute) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("notification_meal_hour", hourOfDay);
        editor.putInt("notification_meal_minute", minute);
        editor.apply();
    }


    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileChangeNotiTimeVM = new ViewModelProvider(requireActivity()).get(ProfileChangeNotiTimeVM.class);
        profileChangeNotiTimeVM.getUserLiveData();
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileChangeNotiTimeBinding.inflate(inflater,container,false);
        binding.setViewModel(profileChangeNotiTimeVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        loadData();

        binding.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(ProfileChangeNotiTimeFragment.this);
                navController.popBackStack();
            }
        });



















        binding.workoutEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.workoutEdt.getRight() - binding.workoutEdt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        final Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String time = String.format("%02d:%02d", hourOfDay, minute);
                                        binding.workoutEdt.setText(time);
                                    }
                                }, hour, minute, true);

                        timePickerDialog.show();
                    }
                }
                return false;
            }
        });



















        binding.mealEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (binding.mealEdt.getRight() - binding.mealEdt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        final Calendar calendar = Calendar.getInstance();
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String time = String.format("%02d:%02d", hourOfDay, minute);
                                        binding.mealEdt.setText(time);
                                    }
                                }, hour, minute, true);

                        timePickerDialog.show();
                    }
                }
                return false;
            }
        });

        binding.updateNotiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                try {
                    String workoutTimeString = binding.workoutEdt.getText().toString();
                    int workoutHour = 0;
                    int workoutMinute = 0;
                    try {
                        String[] timeParts = workoutTimeString.split(":");
                        workoutHour = Integer.parseInt(timeParts[0]);
                        workoutMinute = Integer.parseInt(timeParts[1]);
                        saveWorkoutSelectedTime(workoutHour, workoutMinute);
                        scheduleWorkoutNotification(workoutHour, workoutMinute,00);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    String mealTimeString = binding.mealEdt.getText().toString();
                    int mealHour = 0;
                    int mealMinute = 0;
                    try {
                        String[] timeParts = mealTimeString.split(":");
                        mealHour = Integer.parseInt(timeParts[0]);
                        mealMinute = Integer.parseInt(timeParts[1]);
                        saveMealSelectedTime(mealHour, mealMinute);
                        scheduleMealNotification(mealHour, mealMinute,00);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(requireContext(), "Notification times updated.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }

    private void loadData() {
        profileChangeNotiTimeVM.getIsLoadingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoadingData) {
                if (isLoadingData != null && !isLoadingData) {
                    String WorkoutHour = String.valueOf(getNotificationWorkoutHour());
                    String WorkoutMinute = String.valueOf(getNotificationWorkoutMinute());
                    String MealHour = String.valueOf(getNotificationMealHour());
                    String MealMinute = String.valueOf(getNotificationMealMinute());

                    binding.workoutEdt.setText(String.format("%s:%s", WorkoutHour, WorkoutMinute));
                    binding.mealEdt.setText(String.format("%s:%s", MealHour, MealMinute));

                } else {

                }
            }
        });

    }

















    private void scheduleWorkoutNotification(int hour, int minute, int second) {

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour); // Hour in 24-hour format
        calendar.set(java.util.Calendar.MINUTE, minute);
        calendar.set(java.util.Calendar.SECOND, second);

        Intent intent = new Intent(requireContext(), workoutNotificationReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationIntent = PendingIntent.getBroadcast(requireContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            notificationIntent = PendingIntent.getBroadcast(requireContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notificationIntent);
    }
    private void scheduleMealNotification(int hour, int minute, int second) {

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour); // Hour in 24-hour format
        calendar.set(java.util.Calendar.MINUTE, minute);
        calendar.set(java.util.Calendar.SECOND, second);

        Intent intent = new Intent(requireContext(), mealNotificationReceiver.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            notificationIntent = PendingIntent.getBroadcast(requireContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            notificationIntent = PendingIntent.getBroadcast(requireContext(), REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), notificationIntent);
    }
}