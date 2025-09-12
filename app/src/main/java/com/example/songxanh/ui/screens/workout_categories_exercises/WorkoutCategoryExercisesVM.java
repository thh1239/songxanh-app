package com.example.songxanh.ui.screens.workout_categories_exercises;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutCategoryExercisesVM extends ViewModel {
    private String categoryId;
    private MutableLiveData<List<Exercise>> exercises = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<List<Exercise>> selectedTempList = new MutableLiveData<>(new ArrayList<>());
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public void loadData() {
        exercises.setValue(new ArrayList<>());

        List<Exercise> newList = new ArrayList<>();
        firestore.collection("workout_categories").document(categoryId).collection("exercises").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                Exercise newExercise = doc.toObject(Exercise.class);
                                newList.add(newExercise);
                            }
                            exercises.setValue(newList);
                        } else {
                            Log.i("Load exercise error","Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void addTempExercisesToDb() {
        Log.i("Start add selected", "");
        WriteBatch batch = firestore.batch();

        for (Exercise newExercise : selectedTempList.getValue()) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", newExercise.getId());
            data.put("name", newExercise.getName());
            data.put("imageUrl", newExercise.getImageUrl());
            data.put("startPosition", newExercise.getStartingPosition());
            data.put("execution", newExercise.getExecution());
            data.put("unit", newExercise.getUnit());
            data.put("count", newExercise.getCount());
            data.put("caloriesPerUnit", newExercise.getCaloriesPerUnit());
            data.put("categoryId", newExercise.getCategoryId());

            DocumentReference ref = firestore.collection("users").document(auth.getCurrentUser().getUid())
                    .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                    .collection("today_selected_exercises").document();

            batch.set(ref, data);
            Log.i("Add", "");
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("Add done", "Done");
//                    loadSelectedExercises();
                } else {
                    Log.e("Add exercise", "Error writing batch", task.getException());
                }
            }
        });
    }

    public void addExerciseToTempList(Exercise exercise) {
        List<Exercise> newList =  selectedTempList.getValue();
        newList.add(exercise);
        selectedTempList.setValue(newList);
        Log.i("New selected list size", String.valueOf(selectedTempList.getValue().size()));
    }

    // Getters and setters
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public MutableLiveData<List<Exercise>> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises.setValue(exercises);
    }

    public MutableLiveData<List<Exercise>> getSelectedTempList() {
        return selectedTempList;
    }

    public void clearTempList() {
        selectedTempList.setValue(new ArrayList<Exercise>());
    }

}
