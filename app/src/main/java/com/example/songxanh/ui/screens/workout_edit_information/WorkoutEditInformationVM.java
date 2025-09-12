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
    public void setDefaultCalories(double defaultCalories) {
        this.defaultCalories = defaultCalories;
        this.calories.setValue(String.valueOf(defaultCalories));
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public MutableLiveData<String> getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours.setValue(hours);
    }
    public MutableLiveData<String> getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes.setValue(minutes);
    }

    public MutableLiveData<String> getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds.setValue(seconds);
    }

    public MutableLiveData<String> getCalories() {
        return calories;
    }

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
