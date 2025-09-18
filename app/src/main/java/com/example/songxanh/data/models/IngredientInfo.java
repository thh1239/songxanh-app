package com.example.songxanh.data.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class IngredientInfo {



    private String id;
    private String Short_Description;
    private double Calories = 0;
    private double Carbs = 0;
    private double Lipid = 0;

    private double Protein = 0;


    public IngredientInfo(String Short_Description, double Calories, double Carbs, double Lipid, double Protein) {
        this.Short_Description = Short_Description;
        this.Calories = Calories;
        this.Carbs = Carbs;
        this.Lipid = Lipid;
        this.Protein = Protein;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void setId(String id) {
        this.id = id;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==


    public String getId() {
        return id;
    }

    public <T extends IngredientInfo> T withId(@NonNull final String id) {
        this.id = id;
        return (T) this;
    }

    public IngredientInfo() {

    }
// == Tính toán và hiển thị tổng calo ==

    public String getShort_Description() {
        return Short_Description;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setShort_Description(String Short_Description) {
        this.Short_Description = Short_Description;
    }
// == Tính toán và hiển thị tổng calo ==

    public double getCalories() {
        return Calories;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setCalories(double Calories) {
        this.Calories = Calories;
    }

    public double getCarbs() {
        return Carbs;
    }

    public void setCarbs(double Carbs) {
        this.Carbs = Carbs;
    }

    public double getLipid() {
        return Lipid;
    }

    public void setLipid(double Lipid) {
        this.Lipid = Lipid;
    }

    public double getProtein() {
        return Protein;
    }

    public void setProtein(double Protein) {
        this.Protein = Protein;
    }
}
