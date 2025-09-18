package com.example.songxanh.data.models;

import java.sql.Timestamp;
import java.util.List;

public class DailyActivity {
   private Timestamp date;
   private double calories;
   private double foodCalories;
   private double exerciseCalories;
   private double exerciseTime;
   private double weight;
   private double step;
// == Tính toán và hiển thị tổng calo ==

   public void setDate(Timestamp date) {
      this.date = date;
   }

   private List<Dish> dishes;
   private List<Workout> workouts;

   public  DailyActivity(com.google.firebase.Timestamp startTimestamp) {

   }

   public DailyActivity(){}

   public DailyActivity(Timestamp date) {
      this.date = date;
   }
// == Tính toán và hiển thị tổng calo ==


   public Timestamp getDate() {
      return date;
   }
// == Tính toán và hiển thị tổng calo ==

   public double getCalories() {
      return calories;
   }
// == Tính toán và hiển thị tổng calo ==

   public void setCalories(double calories) {
      this.calories = calories;
   }
// == Tính toán và hiển thị tổng calo ==

   public double getFoodCalories() {
      return foodCalories;
   }
// == Tính toán và hiển thị tổng calo ==

   public void setFoodCalories(double foodCalories) {
      this.foodCalories = foodCalories;
   }
// == Tính toán và hiển thị tổng calo ==

   public double getExerciseCalories() {
      return exerciseCalories;
   }
// == Tính toán và hiển thị tổng calo ==

   public void setExerciseCalories(double exerciseCalories) {
      this.exerciseCalories = exerciseCalories;
   }

   public double getExerciseTime() {
      return exerciseTime;
   }

   public void setExerciseTime(double exerciseTime) {
      this.exerciseTime = exerciseTime;
   }

   public double getWeight() {
      return weight;
   }

   public void setWeight(double weight) {
      this.weight = weight;
   }

   public double getStep() {
      return step;
   }

   public void setStep(double step) {
      this.step = step;
   }

   public List<Dish> getDishes() {
      return dishes;
   }

   public void setDishes(List<Dish> dishes) {
      this.dishes = dishes;
   }

   public List<Workout> getWorkouts() {
      return workouts;
   }

   public void setWorkouts(List<Workout> workouts) {
      this.workouts = workouts;
   }
}
