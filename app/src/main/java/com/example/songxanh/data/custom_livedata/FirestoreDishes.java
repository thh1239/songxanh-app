package com.example.songxanh.data.custom_livedata;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.songxanh.data.models.Dish;
import com.example.songxanh.data.models.Ingredient;
import com.example.songxanh.utils.GlobalMethods;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreDishes extends LiveData<ArrayList<Dish>> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private CollectionReference dailyActivitiesRef;
    private DocumentReference dailyActivityRef;
    private ListenerRegistration listenerRegistration;

    private void queryDailyActivities() {
        queryDailyActivities(null);
    }

    private void queryDailyActivities(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date start = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date end = calendar.getTime();

        Timestamp startTimestamp = new Timestamp(start);
        Timestamp endTimestamp = new Timestamp(end);

        Date queryDate = new Date();

        if (date != null) {
            queryDate = date;
        }

        dailyActivityRef = dailyActivitiesRef.document(GlobalMethods.convertDateToHyphenSplittingFormat(queryDate));

        dailyActivityRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().getData() == null || task.getResult().getData().isEmpty()) {
                    Map<String, Object> newDailyActivity = new HashMap<>();
                    newDailyActivity.put("date", startTimestamp);
                    newDailyActivity.put("dishes", new ArrayList<Map<String, Object>>());
                    newDailyActivity.put("foodCalories", 0);
                    dailyActivityRef.set(newDailyActivity, SetOptions.merge());
                }
            }
        });
    }

    @Override
// == Xác thực người dùng với FirebaseAuth ==
    protected void onActive() {
        super.onActive();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null) {
            return;
        }
        userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dailyActivitiesRef = userRef.collection("daily_activities");
        queryDailyActivities();
        if (dailyActivityRef != null) {
            listenerRegistration = dailyActivityRef.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<Map<String, Object>> dishesMapList = (List<Map<String, Object>>) documentSnapshot.get("dishes");
                    ArrayList<Dish> dishes = new ArrayList<>();
                    if (dishesMapList == null) {
                        return;
                    }
                    for (Map<String, Object> dishMap : dishesMapList) {
                        Dish dish = new Dish();
                        dish.setName((String) dishMap.get("name"));
                        dish.setSession((String) dishMap.get("session"));

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

                        dishes.add(dish);
                    }

                    setValue(dishes);
                }
            });
        }
    }

    @Override
// == Tính toán và hiển thị tổng calo ==
    protected void onInactive() {
        super.onInactive();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
// == Tính toán và hiển thị tổng calo ==

    public void addDish(Dish newDish) {
        if (dailyActivityRef != null) {
            dailyActivityRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Object foodCaloriesObj = task.getResult().get("foodCalories");
                    double initialCalories = 0;
                    if (foodCaloriesObj instanceof Long) {
                        initialCalories = ((Long) foodCaloriesObj).doubleValue();
                    } else if (foodCaloriesObj instanceof Double) {
                        initialCalories = (Double) foodCaloriesObj;
                    }
                    dailyActivityRef.update("calories", initialCalories + newDish.getCalories());
                    dailyActivityRef.update("foodCalories", initialCalories + newDish.getCalories());

                    dailyActivityRef.update("dishes", FieldValue.arrayUnion(GlobalMethods.toKeyValuePairs(newDish)))
                            .addOnSuccessListener(aVoid -> Log.d("FIRESTOREDISHES", "addDish: Dish added successfully"))
                            .addOnFailureListener(e -> Log.e("FIRESTOREDISHES", "addDish: Error adding dish", e));
                } else {
                    Log.d("ERROR", "addDish: " + task.getException());
                }
            });
        }
    }
// == Tính toán và hiển thị tổng calo ==

    public void deleteDish(Dish dishToDelete) {
        if (dailyActivityRef != null) {
            dailyActivityRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    double initialCalories = (Double) task.getResult().get("foodCalories");
                    dailyActivityRef.update("calories", initialCalories - dishToDelete.getCalories());
                    dailyActivityRef.update("foodCalories", initialCalories - dishToDelete.getCalories());

                    dailyActivityRef.update("dishes", FieldValue.arrayRemove(GlobalMethods.toKeyValuePairs(dishToDelete)))
                            .addOnSuccessListener(aVoid -> Log.d("FIRESTOREDISHES", "deleteDish: Dish deleted successfully"))
                            .addOnFailureListener(e -> Log.e("FIRESTOREDISHES", "deleteDish: Error deleting dish", e));
                } else {
                    Log.d("ERROR", "deleteDish: " + task.getException());
                }
            });
        }
    }
// == Tính toán và hiển thị tổng calo ==

    public void updateDishes(List<Dish> newDishes) {
        if (dailyActivityRef != null) {
            double totalCalories = 0;
            List<Map<String, Object>> newDishesKeyValuePairs = new ArrayList<>();
            for (Dish dish : newDishes) {
                newDishesKeyValuePairs.add(GlobalMethods.toKeyValuePairs(dish));
                totalCalories += dish.getCalories();
            }
            final double finalTotalCalories = totalCalories;
            dailyActivityRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Object caloriesObj = task.getResult().get("calories");
                    Object foodCaloriesObj = task.getResult().get("foodCalories");
                    double initialCalories = 0;
                    double initialFoodCalories = 0;
                    if (caloriesObj instanceof Long) {
                        initialCalories = ((Long) caloriesObj).doubleValue();
                    } else if (caloriesObj instanceof Double) {
                        initialCalories = (Double) caloriesObj;
                    }
                    if (foodCaloriesObj instanceof Long) {
                        initialFoodCalories = ((Long) foodCaloriesObj).doubleValue();
                    } else if (foodCaloriesObj instanceof Double) {
                        initialFoodCalories = (Double) foodCaloriesObj;
                    }

                    dailyActivityRef.update("dishes", newDishesKeyValuePairs)
                            .addOnSuccessListener(aVoid -> Log.d("FIRESTOREDISHES", "updateDishes: Dishes updated successfully"))
                            .addOnFailureListener(e -> Log.e("FIRESTOREDISHES", "updateDishes: Error updating dishes", e));
                    dailyActivityRef.update("foodCalories", finalTotalCalories);
                    dailyActivityRef.update("calories", initialCalories + (finalTotalCalories - initialCalories));

                } else {
                    Log.d("ERROR", "updateDishes: " + task.getException());
                }
            });
        }
    }


}