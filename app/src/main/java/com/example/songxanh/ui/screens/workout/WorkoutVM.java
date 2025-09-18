package com.example.songxanh.ui.screens.workout;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.utils.FirebaseConstants;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkoutVM extends ViewModel {
    private MutableLiveData<List<Exercise>> selectedExercises = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Integer> selectedTotalCalories = new MutableLiveData<Integer>(0);

    private MutableLiveData<String> addSelectedExercisesToDbMessage = new MutableLiveData<>(null);
    private MutableLiveData<Integer> exerciseCalories = new MutableLiveData<>(0);
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public WorkoutVM() {
        if(auth.getCurrentUser() != null) {
            loadSelectedExercises();
            loadExercisesCalories();
        }
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public void loadSelectedExercises() {
        firestore.collection("users").document(auth.getCurrentUser().getUid())
                .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                .collection("today_selected_exercises").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
// == Tính toán và hiển thị tổng calo ==
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Exercise> newList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Exercise newExercise = doc.toObject(Exercise.class);
                            newExercise.setId(doc.getId());
                            newList.add(newExercise);
                        }
                        selectedExercises.setValue(newList);
                        recalculateSelectedExercisesCalories();
                    }
                });
    }
// == Tính toán và hiển thị tổng calo ==

    public void loadExercisesCalories() {
        FirebaseConstants.dailyActivitiesRef.document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
// == Tính toán và hiển thị tổng calo ==
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc.exists()) {
                                Long inLong = doc.getLong("exerciseCalories");
                                if (inLong == null) {
                                    exerciseCalories.setValue(0);
                                } else {
                                    exerciseCalories.setValue(inLong.intValue());
                                }
                            }
                        } else {
                            Log.e("ERROR", "Load exercises calories failed", task.getException());
                        }
                    }
                });
    }
// == Tính toán và hiển thị tổng calo ==

    public void addExercisesToDb(List<Exercise> exercises) {
        WriteBatch batch = firestore.batch();

        for (Exercise newExercise : exercises) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", newExercise.getId());
            data.put("name", newExercise.getName());
            data.put("imageUrl", newExercise.getImageUrl());
            data.put("startingPosition", newExercise.getStartingPosition());
            data.put("execution", newExercise.getExecution());
            data.put("unit", newExercise.getUnit());
            data.put("count", newExercise.getCount());
            data.put("caloriesPerUnit", newExercise.getCaloriesPerUnit());
            data.put("categoryId", newExercise.getCategoryId());

           DocumentReference ref = firestore.collection("users").document(auth.getCurrentUser().getUid())
                    .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                    .collection("today_selected_exercises").document();

            batch.set(ref, data);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    addSelectedExercisesToDbMessage.setValue("Add your selected exercises successfully");
                    loadSelectedExercises();
                } else {
                    Log.e("Add exercise", "Error writing batch", task.getException());
                    addSelectedExercisesToDbMessage.setValue("Some errors occurred");
                }
            }
        });
    }

    public void finishSelectedExercises() {
        updateWorkoutListOnDb();

        firestore.collection("users").document(auth.getCurrentUser().getUid())
                .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                .collection("today_selected_exercises").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
// == Tính toán và hiển thị tổng calo ==
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int numberOfExercises = task.getResult().size();
                            AtomicInteger count = new AtomicInteger(0);

                            for (DocumentSnapshot doc : task.getResult()) {
                                doc.getReference().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
// == Tính toán và hiển thị tổng calo ==
                                    public void onComplete(@NonNull Task<Void> task) {
                                        count.incrementAndGet();
                                        if (count.get() == numberOfExercises) {
                                            updateExercisesCaloriesOnDb();
                                            clearSelectedExercises();
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.e("ERROR", "Delete selected exercises failed.", task.getException());
                        }
                    }
                });
    }
// == Tính toán và hiển thị tổng calo ==

    public void updateExercisesCaloriesOnDb() {
        DocumentReference ref = firestore.collection("users").document(auth.getCurrentUser().getUid())
                .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()));

        WriteBatch batch = firestore.batch();
        batch.update(ref, "exerciseCalories", FieldValue.increment(selectedTotalCalories.getValue()));
        batch.update(ref, "calories", FieldValue.increment(selectedTotalCalories.getValue()));
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadSelectedExercises();
                    loadExercisesCalories();
                } else {
                    Log.d("ERROR", "Error updating fields: ", task.getException());
                }
            }
        });
    }
// == Xóa dữ liệu hoặc item ==

    public void removeSelectedExercise(int position) {
        firestore.collection("users").document(auth.getCurrentUser().getUid())
                .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                .collection("today_selected_exercises").document(selectedExercises.getValue().get(position).getId())
                .delete();

    }
// == Tính toán và hiển thị tổng calo ==

    public void initDailyActivity() {
        firestore.collection("users").document(auth.getCurrentUser().getUid())
                .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
// == Tính toán và hiển thị tổng calo ==
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (!snapshot.exists()) {
                                Map<String, Object> newDailyActivity = new HashMap<>();
                                newDailyActivity.put("foodCalories", 0);
                                newDailyActivity.put("exerciseCalories", 0);
                                newDailyActivity.put("calories", 0);
                                newDailyActivity.put("steps", 0);
                                newDailyActivity.put("weight", 0);
                                snapshot.getReference().set(newDailyActivity)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
// == Tính toán và hiển thị tổng calo ==
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.i("Initial daily activity successfully", "");
                                                } else {
                                                    Log.e("ERROR", "Initial daily activity failed", task.getException());
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
// == Tính toán và hiển thị tổng calo ==

    public void recalculateSelectedExercisesCalories() {
        selectedTotalCalories.setValue(GlobalMethods.calculateTotalCalories(selectedExercises.getValue()));
    }
// == Tính toán và hiển thị tổng calo ==

    public void moveTempListToSelectedList(List<Exercise> tempList) {
        addExercisesToDb(tempList);
    }

    private void clearSelectedExercises() {
        selectedExercises.setValue(new ArrayList<>());
    }

    private void updateWorkoutListOnDb() {
        CollectionReference collection = firestore.collection("users").document(auth.getCurrentUser().getUid())
                .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date())).collection("workouts");

        final AtomicInteger numberOfAddedItem = new AtomicInteger(0);
        for (Exercise exercise : selectedExercises.getValue()) {
            collection.add(exercise);
        }
    }
// == Tính toán và hiển thị tổng calo ==

    public MutableLiveData<Integer> getSelectedTotalCalories() {
        return selectedTotalCalories;
    }
// == Tính toán và hiển thị tổng calo ==

    public MutableLiveData<List<Exercise>> getSelectedExercises() {
        return selectedExercises;
    }
// == Tính toán và hiển thị tổng calo ==


    public MutableLiveData<String> getAddSelectedExercisesToDbMessage() {
        return addSelectedExercisesToDbMessage;
    }
// == Tính toán và hiển thị tổng calo ==

    public void setAddSelectedExercisesToDbMessage(String newMessage) {
        addSelectedExercisesToDbMessage.setValue(newMessage);
    }
// == Tính toán và hiển thị tổng calo ==

    public int getSelectedExerciseSize() {
        return selectedExercises.getValue().size();
    }
// == Tính toán và hiển thị tổng calo ==

    public MutableLiveData<Integer> getExerciseCalories() {
        return exerciseCalories;
    }
}
