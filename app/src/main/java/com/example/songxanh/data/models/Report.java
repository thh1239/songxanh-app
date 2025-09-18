package com.example.songxanh.data.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Report {

    private String id;
    private String achievementId;


    private int achievementSteps;
    private double achievementCalories;
    private Date achievementCreatedTime;
    private double achievementExerciseCalories;
    private double achievementFoodCalories;
    private String achievementUserId;
    private String achievementUserImageUrl;
    private String achievementUserName;
    private String activitiesId;
    private String description;
    private String reportUserId;
    private String reportUserImageUrl;
    private String reportUserName;
    private String title;
// == Tính toán và hiển thị tổng calo ==
    public void setId(String id) {
        this.id = id;
    }
    public <T extends Report> T withId(@NonNull final String id) {
        this.id = id;
        return (T) this;
    }
    @Exclude
// == Tính toán và hiển thị tổng calo ==
    public String getId() {
        return id;
    }
// == Tính toán và hiển thị tổng calo ==

    public double getAchievementCalories() {
        return achievementCalories;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setAchievementCalories(double achievementCalories) {
        this.achievementCalories = achievementCalories;
    }
// == Tính toán và hiển thị tổng calo ==

    public Date getAchievementCreatedTime() {
        return achievementCreatedTime;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setAchievementCreatedTime(Date achievementCreatedTime) {
        this.achievementCreatedTime = achievementCreatedTime;
    }
// == Tính toán và hiển thị tổng calo ==

    public double getAchievementExerciseCalories() {
        return achievementExerciseCalories;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setAchievementExerciseCalories(double achievementExerciseCalories) {
        this.achievementExerciseCalories = achievementExerciseCalories;
    }
// == Tính toán và hiển thị tổng calo ==

    public double getAchievementFoodCalories() {
        return achievementFoodCalories;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setAchievementFoodCalories(double achievementFoodCalories) {
        this.achievementFoodCalories = achievementFoodCalories;
    }
    public int getAchievementSteps() {
        return achievementSteps;
    }

    public void setAchievementSteps(int achievementSteps) {
        this.achievementSteps = achievementSteps;
    }

    public String getAchievementUserId() {
        return achievementUserId;
    }

    public void setAchievementUserId(String achievementUserId) {
        this.achievementUserId = achievementUserId;
    }

    public String getAchievementUserImageUrl() {
        return achievementUserImageUrl;
    }

    public void setAchievementUserImageUrl(String achievementUserImageUrl) {
        this.achievementUserImageUrl = achievementUserImageUrl;
    }

    public String getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(String achievementId) {
        this.achievementId = achievementId;
    }


    public String getAchievementUserName() {
        return achievementUserName;
    }

    public void setAchievementUserName(String achievementUserName) {
        this.achievementUserName = achievementUserName;
    }

    public String getActivitiesId() {
        return activitiesId;
    }

    public void setActivitiesId(String activitiesId) {
        this.activitiesId = activitiesId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReportUserId() {
        return reportUserId;
    }

    public void setReportUserId(String reportUserId) {
        this.reportUserId = reportUserId;
    }

    public String getReportUserImageUrl() {
        return reportUserImageUrl;
    }

    public void setReportUserImageUrl(String reportUserImageUrl) {
        this.reportUserImageUrl = reportUserImageUrl;
    }

    public String getReportUserName() {
        return reportUserName;
    }

    public void setReportUserName(String reportUserName) {
        this.reportUserName = reportUserName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
