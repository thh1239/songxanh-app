package com.example.songxanh.data.models;

import com.example.songxanh.utils.GlobalMethods;

import java.io.Serializable;
import java.util.Date;

public class Achievement implements Serializable {
    private String id;
    private int calories;
    private int steps;
    private int exerciseCalories;
    private int foodCalories;
    private String userId;
    private String userName;
    private String userImageUrl;

    private String date;
    private Date createdTime;

    public Achievement() {}

    public Achievement(int calories, int steps, int exerciseCalories, int foodCalories, String userId, String userName, String userImageUrl, String date, Date createdTime) {
        this.calories = calories;
        this.steps = steps;
        this.exerciseCalories = exerciseCalories;
        this.foodCalories = foodCalories;
        this.userId = userId;
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.date = date;
        this.createdTime = createdTime;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getExerciseCalories() {
        return exerciseCalories;
    }

    public void setExerciseCalories(int exerciseCalories) {
        this.exerciseCalories = exerciseCalories;
    }

    public int getFoodCalories() {
        return foodCalories;
    }

    public void setFoodCalories(int foodCalories) {
        this.foodCalories = foodCalories;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        if (this.userImageUrl == null || this.userImageUrl.isEmpty()) {
            return null;
        } else {
            return this.userImageUrl;
        }
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
