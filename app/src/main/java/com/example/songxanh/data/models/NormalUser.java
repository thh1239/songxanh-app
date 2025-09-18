package com.example.songxanh.data.models;


import android.util.Log;

import androidx.annotation.NonNull;

import com.example.songxanh.utils.FirebaseConstants;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class NormalUser extends User {
    private String phone;
    private Date dateOfBirth;
    private String address;
    private String gender;
    private int startWeight;
    private int goalWeight;
    private int height = 160;
    private Date startTime;
    private Date goalTime;
    private double dailyCalories;
    private int dailySteps;
    private List<String> following;
    private List<String> followers;
    private List<String> keyword;
    private DailyActivity dailyActivity;


    public NormalUser() {}

    public NormalUser(String uid, String email, String name, String imageUrl, String phone, Date dateOfBirth, String address, String gender, int startWeight, int goalWeight, Date startTime, Date goalTime, double dailyCalories, int dailySteps, List<String> following, List<String> followers, DailyActivity dailyActivity, List<String> keyword) {
        super(uid, email, name, imageUrl, "NORMAL_USER");
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.gender = gender;
        this.startWeight = startWeight;
        this.goalWeight = goalWeight;
        this.startTime = startTime;
        this.goalTime = goalTime;
        this.dailyCalories = dailyCalories;
        this.dailySteps = dailySteps;
        this.following = following;
        this.followers = followers;
        this.dailyActivity = dailyActivity;
        this.keyword = keyword;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
// == Thêm mới dữ liệu hoặc item ==

    public String getAddress() {
        return address;
    }
// == Thêm mới dữ liệu hoặc item ==

    public void setAddress(String address) {
        this.address = address;
    }
// == Tính toán và hiển thị tổng calo ==

    public String getGender() {
        return gender;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setGender(String gender) {
        this.gender = gender;
    }
// == Tính toán và hiển thị tổng calo ==

    public int getStartWeight() {
        return startWeight;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setStartWeight(int startWeight) {
        this.startWeight = startWeight;
    }
// == Tính toán và hiển thị tổng calo ==

    public int getGoalWeight() {
        return goalWeight;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setGoalWeight(int goalWeight) {
        this.goalWeight = goalWeight;
    }
// == Tính toán và hiển thị tổng calo ==

    public Date getStartTime() {
        return startTime;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
// == Tính toán và hiển thị tổng calo ==

    public Date getGoalTime() {
        return goalTime;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setGoalTime(Date goalTime) {
        this.goalTime = goalTime;
    }
// == Tính toán và hiển thị tổng calo ==

    public double getDailyCalories() {
        return dailyCalories;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setDailyCalories(int dailyCalories) {
        this.dailyCalories = dailyCalories;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }
// == Tính toán và hiển thị tổng calo ==


    public List<String> getKeyword() {
        return keyword;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }
// == Tính toán và hiển thị tổng calo ==

    public DailyActivity getDailyActivity() {
        return dailyActivity;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setDailyActivity(DailyActivity dailyActivity) {
        this.dailyActivity = dailyActivity;
    }
// == Tính toán và hiển thị tổng calo ==

    public int getDailySteps() {
        return dailySteps;
    }
// == Tính toán và hiển thị tổng calo ==

    public int getAge() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        int yearOfBirth = calendar.get(Calendar.YEAR);
        calendar.setTime(new Date());
        int currentYear = calendar.get(Calendar.YEAR);
        return currentYear - yearOfBirth;
    }


    private void updateDailyCaloriesOnDb() {
        FirebaseConstants.usersRef.document(this.uid).update("dailyCalories", GlobalMethods.calculateDailyCalories(this.getGender(), this.getStartWeight(), this.getHeight(), this.getAge(), this.getGoalWeight(), this.getStartTime(), this.getGoalTime()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
// == Tính toán và hiển thị tổng calo ==
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        } else {
                            Log.e("Cập nhật Calories hàng ngày thất bại", "", task.getException());
                        }
                    }
                });
    }
}
