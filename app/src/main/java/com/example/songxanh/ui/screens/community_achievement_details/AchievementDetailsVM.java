package com.example.songxanh.ui.screens.community_achievement_details;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.data.models.Dish;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.data.models.Ingredient;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AchievementDetailsVM extends ViewModel {
    private MutableLiveData<Achievement> achievement = new MutableLiveData<>();
    private MutableLiveData<List<Exercise>> exercises = new MutableLiveData<>();

    private MutableLiveData<ArrayList<Dish>> dishes = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingFoods = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLoadingExercises = new MutableLiveData<>(false);

    private void loadFoods() {
        CollectionReference dailyActivitiesRef = FirebaseFirestore.getInstance().collection("users").document(achievement.getValue().getUserId()).collection("daily_activities");

        DocumentReference dailyActivityRef = dailyActivitiesRef.document(GlobalMethods.convertDateToHyphenSplittingFormat(achievement.getValue().getCreatedTime()));
        ArrayList<Dish> temp = new ArrayList<>();
        isLoadingFoods.setValue(true);

        dailyActivityRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()) {

                        List<Map<String, Object>> dishesMapList = (List<Map<String, Object>>) documentSnapshot.get("dishes");
                        if (dishesMapList != null) {

                            for (Map<String, Object> dishMap : dishesMapList) {
                                Dish dish = new Dish();
                                dish.setName((String) dishMap.get("name"));
                                dish.setSession((String) dishMap.get("session"));
                                Log.d("Dishname", "onComplete:  " + dish.getName());
                                List<Map<String, Object>> ingredientsMapList = (List<Map<String, Object>>) dishMap.get("ingredients");
                                if (ingredientsMapList == null || ingredientsMapList.isEmpty()) {
                                    continue;
                                }
                                List<Ingredient> ingredients = new ArrayList<>();
                                double dishTotalCalories = 0;
                                double dishTotalCarb = 0;
                                double dishTotalLipid = 0;
                                double dishTotalProtein = 0;
                                for (Map<String, Object> ingredientMap : ingredientsMapList) {
                                    Ingredient ingredient = new Ingredient();
                                    ingredient.setName((String) ingredientMap.get("name"));
                                    ingredient.setCalories(((Number) ingredientMap.get("calories")).doubleValue());
                                    ingredient.setCarb(((Number) ingredientMap.get("carb")).doubleValue());
                                    ingredient.setLipid(((Number) ingredientMap.get("lipid")).doubleValue());
                                    ingredient.setProtein(((Number) ingredientMap.get("protein")).doubleValue());
                                    ingredient.setWeight(((Number) ingredientMap.get("weight")).doubleValue());
                                    dishTotalCalories += ingredient.getCalories();
                                    dishTotalCarb += ingredient.getCarb();
                                    dishTotalLipid += ingredient.getLipid();
                                    dishTotalProtein += ingredient.getProtein();
                                    ingredients.add(ingredient);
                                }
                                dish.setIngredients(ingredients);
                                dish.setCalories(dishTotalCalories);
                                dish.setCarb(dishTotalCarb);
                                dish.setLipid(dishTotalLipid);
                                dish.setProtein(dishTotalProtein);
                                temp.add(dish);
                            }
                            dishes.postValue(temp);
                        } else {
                        }
                    }
                    isLoadingFoods.setValue(false);
                }
            }
        });
    }

    private void loadExercises() {
        isLoadingExercises.setValue(true);
        CollectionReference dailyActivitiesRef = FirebaseFirestore.getInstance().collection("users").document(achievement.getValue().getUserId()).collection("daily_activities");
        dailyActivitiesRef.document(GlobalMethods.convertDateToHyphenSplittingFormat(achievement.getValue().getCreatedTime()))
                .collection("workouts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Exercise> newExercises = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                newExercises.add(doc.toObject(Exercise.class));
                            }
                            exercises.setValue(newExercises);

                            isLoadingExercises.setValue(false);
                        }
                    }
                });
    }


    // GETTER AND SETTERS
    public MutableLiveData<Achievement> getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement.setValue(achievement);
        loadFoods();
        loadExercises();
    }

    public MutableLiveData<List<Exercise>> getExercises() {
        return exercises;
    }

    public void setExercises(MutableLiveData<List<Exercise>> exercises) {
        this.exercises = exercises;
    }

    public MutableLiveData<Boolean> getIsLoadingFoods() {
        return isLoadingFoods;
    }

    public MutableLiveData<Boolean> getIsLoadingExercises() {
        return isLoadingExercises;
    }

    public MutableLiveData<ArrayList<Dish>> getDishes() {
        return dishes;
    }

    public void setDishes(MutableLiveData<ArrayList<Dish>> dishes) {
        this.dishes = dishes;
    }
}
