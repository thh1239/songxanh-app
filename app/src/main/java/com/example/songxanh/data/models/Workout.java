package com.example.songxanh.data.models;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Workout {
    private String id;
    private int calories;
    private Date date;
    private MutableLiveData<List<Exercise>> exercises;

    public Workout() {
         this.id = "";
         this.calories = 0;
         this.date = null;
         this.exercises.setValue(new ArrayList<>());
    }
// == Tính toán và hiển thị tổng calo ==
    public String getId() {
        return id;
    }
// == Tính toán và hiển thị tổng calo ==

    public int getCalories() {
        return calories;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public Date getDate() {
        return date;
    }

    public MutableLiveData<List<Exercise>> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises.setValue(exercises);
    }
}
