package com.example.songxanh.data.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    private String id;
    private String name;

    private double protein = 0;
    private double carb = 0;
    private  double lipid = 0;
    private double calories = 0;
    private String session;
    private List<Ingredient> ingredients = new ArrayList<>();
    public Dish() {
    }
// == Tính toán và hiển thị tổng calo ==

    public void setId(String id) {
        this.id = id;
    }
// == Tính toán và hiển thị tổng calo ==
    public Dish withId(@NonNull final String id) {
        this.id = id;
        return this;
    }
    @Exclude
// == Tính toán và hiển thị tổng calo ==
    public String getId() {
        return id;
    }
// == Tính toán và hiển thị tổng calo ==
    public String getName() {
        return name;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void setName(String name) {
        this.name = name;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public double getProtein() {
        return protein;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void setProtein(double protein) {
        this.protein = protein;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public double getCarb() {
        return carb;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void setCarb(double carb) {
        this.carb = carb;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public double getLipid() {
        return lipid;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void setLipid(double lipid) {
        this.lipid = lipid;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public double getCalories() {
        return calories;
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void setCalories(double calories) {
        this.calories = calories;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public String getSession() {
        return session;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void setSession(String session) {
        this.session = session;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
