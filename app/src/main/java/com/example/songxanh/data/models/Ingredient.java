package com.example.songxanh.data.models;

public class Ingredient {
    private String name;
    private double weight = 0;
    private double calories = 0;
    private double protein = 0;
    private double carb = 0;
    private double lipid = 0;

    public Ingredient() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void updateWeight(double weight) {
        double oldWeight = this.weight;
        this.weight = weight;
        double weightRatio = weight / oldWeight;

        this.calories = this.calories * weightRatio;
        this.protein = this.protein * weightRatio;
        this.carb = this.carb * weightRatio;
        this.lipid = this.lipid * weightRatio;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
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
}
