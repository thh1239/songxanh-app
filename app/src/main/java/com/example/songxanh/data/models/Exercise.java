package com.example.songxanh.data.models;

import java.io.Serializable;

public class Exercise implements Serializable {
    private String id;
    private String name;
    private String imageUrl;
    private String startingPosition;
    private String execution;
    private String unit; // rep/second
    private int count;
    private double caloriesPerUnit;
    private String categoryId;

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    private String muscleGroup;

    public Exercise() {}

    public Exercise(String id, String name, String imageUrl, String startingPosition, String execution, String unit, int count, int caloriesPerUnit, String categoryId) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.startingPosition = startingPosition;
        this.execution = execution;
        this.unit = unit;
        this.count = count;
        this.caloriesPerUnit = caloriesPerUnit;
        this.categoryId = categoryId;
    }

    public Exercise(Exercise exercise) {
        this.id = exercise.id;
        this.name = exercise.name;
        this.imageUrl = exercise.imageUrl;
        this.startingPosition = exercise.startingPosition;
        this.execution = exercise.execution;
        this.unit = exercise.unit;
        this.count = exercise.count;
        this.caloriesPerUnit = exercise.caloriesPerUnit;
        this.categoryId = exercise.categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(String startingPosition) {
        this.startingPosition = startingPosition;
    }

    public String getExecution() {
        return execution;
    }

    public void setExecution(String execution) {
        this.execution = execution;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getCaloriesPerUnit() {
        return caloriesPerUnit;
    }

    public void setCaloriesPerUnit(double caloriesPerUnit) {
        this.caloriesPerUnit = caloriesPerUnit;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
