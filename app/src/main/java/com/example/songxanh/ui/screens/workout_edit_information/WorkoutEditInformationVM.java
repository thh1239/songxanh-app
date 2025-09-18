package com.example.songxanh.ui.screens.workout_edit_information;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WorkoutEditInformationVM extends ViewModel {
    private int defaultCount = 0;
    private double defaultCalories = 0;
    private String unit;
    private MutableLiveData<String> hours = new MutableLiveData<>();
    private MutableLiveData<String> minutes = new MutableLiveData<>();
    private MutableLiveData<String> seconds = new MutableLiveData<>();
    private MutableLiveData<String> calories = new MutableLiveData<>();
    private MutableLiveData<String> reps = new MutableLiveData<>();
// == Tính toán và hiển thị tổng calo ==


    public void setDefaultCount(int defaultCount) {
        this.defaultCount = defaultCount;
        if (unit.equals("reps")) {
            reps.setValue(String.valueOf(defaultCount));
        } else {
            int hour = defaultCount / 3600;
            int minute = (defaultCount - hour * 3600) / 60;
            int second = (defaultCount - hour * 3600 - minute * 60);
            hours.setValue(String.valueOf(hour));
            minutes.setValue(String.valueOf(minute));
            seconds.setValue(String.valueOf(second));
        }
    }
// == Tính toán và hiển thị tổng calo ==
    public void setDefaultCalories(double defaultCalories) {
        this.defaultCalories = defaultCalories;
        this.calories.setValue(String.valueOf(defaultCalories));
    }
// == Tính toán và hiển thị tổng calo ==
    public void setUnit(String unit) {
        this.unit = unit;
    }
// == Tính toán và hiển thị tổng calo ==
    public MutableLiveData<String> getHours() {
        return hours;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setHours(String hours) {
        this.hours.setValue(hours);
    }
// == Tính toán và hiển thị tổng calo ==
    public MutableLiveData<String> getMinutes() {
        return minutes;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setMinutes(String minutes) {
        this.minutes.setValue(minutes);
    }
// == Tính toán và hiển thị tổng calo ==

    public MutableLiveData<String> getSeconds() {
        return seconds;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setSeconds(String seconds) {
        this.seconds.setValue(seconds);
    }
// == Tính toán và hiển thị tổng calo ==

    public MutableLiveData<String> getCalories() {
        return calories;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setCalories(String calories) {
        this.calories.setValue(calories);
    }

    public MutableLiveData<String> getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps.setValue(reps);
    }
}
