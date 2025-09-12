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

    public void setId(String id) {
        this.id = id;
    }
    public Dish withId(@NonNull final String id) {
        this.id = id;
        return this;
    }
    @Exclude
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarb() {
        return carb;
    }

    public void setCarb(double carb) {
        this.carb = carb;
    }

    public double getLipid() {
        return lipid;
    }

    public void setLipid(double lipid) {
        this.lipid = lipid;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
