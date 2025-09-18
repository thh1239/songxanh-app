package com.example.songxanh.ui.screens.workout_exercise_details;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Exercise;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class WorkoutExerciseDetailsVM extends ViewModel {
    private MutableLiveData<Exercise> exercise = new MutableLiveData<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private MutableLiveData<String> toastMessage = new MutableLiveData<>("");

    public MutableLiveData<Exercise> getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise.setValue(exercise);
    }

    public void addToFavorite() {
        CollectionReference collection = firestore.collection("users").document(auth.getCurrentUser().getUid())
                .collection("favorite_exercises");
        // Check if this exercise is already existed favorite list
        collection.whereEqualTo("id", exercise.getValue().getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        // if is empty, add this exercise to favorite list
                        collection.add(exercise.getValue())
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            toastMessage.setValue("Added to your favorite workout.");
                                        } else {
                                            toastMessage.setValue("Add failed. Please try again.");
                                        }
                                    }
                                });
                    } else {
                        toastMessage.setValue("This exercise is already in favorite list.");
                    }
                } else {
                    toastMessage.setValue("Add failed. Please try again.");
                }
            }
        });

    }


    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

}
