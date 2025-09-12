package com.example.songxanh.data.models;

public class FollowingActivity {
    private String userId;
    private String userName;
    private String userAvatarUrl;
    private int steps;
    private int exerciseCalories;
    private int foodCalories;
    private int totalCalories;

//    public FollowingActivity() {
//        this.userId = "";
//        this.userName = "";
//        this.userAvatarUrl = null;
//        this.steps = 0;
//        this.exerciseCalories = 0;
//        this.foodCalories = 0;
//        this.totalCalories = 0;
//    }

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

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
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

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }
}
