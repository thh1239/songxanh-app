package com.example.songxanh.ui.screens.admin_workout_screen;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.data.models.WorkoutCategory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class AdminWorkoutVM extends ViewModel {
    public MutableLiveData<ArrayList<WorkoutCategory>> getWorkoutCategories() {
        return workoutCategories;
    }
    private String currentCategoryId;

    public FirebaseFirestore getDb() {
        return db;
    }

    MutableLiveData<ArrayList<WorkoutCategory>> workoutCategories;

    public MutableLiveData<ArrayList<Exercise>> getExercises() {
        return exercises;
    }

    MutableLiveData<ArrayList<Exercise>> exercises;


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AdminWorkoutVM() {
        exercises = new MutableLiveData<>();
        workoutCategories = new MutableLiveData<>();
        fetchWorkoutCategories();
    }

    private void fetchWorkoutCategories() {
        db.collection("workout_categories").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<WorkoutCategory> temp = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    WorkoutCategory temp1 = document.toObject(WorkoutCategory.class);
                    temp.add(document.toObject(WorkoutCategory.class));
                    Log.d("Categories Id", "onSuccess: " + temp1.getId());
                }
                workoutCategories.postValue(temp);
            }
        });
    }

    public void addNewCategory(String name, Uri uri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageReference.child("workout_categories/" + name);
        imgRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        WorkoutCategory workoutCategory = new WorkoutCategory();
                        workoutCategory.setName(name);
                        workoutCategory.setImageUrl(uri.toString());

                        DocumentReference newDoc = db.collection("workout_categories").document();
                        String docId = newDoc.getId();

                        workoutCategory.setId(docId);

                        newDoc.set(workoutCategory);
                    }
                });
            }
        });
    }

    public void fetchExercisesInCategory(String id) {
        currentCategoryId = id;
        DocumentReference categoryDoc = db.collection("workout_categories").document(id);
        categoryDoc.collection("exercises").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Exercise> temp = new ArrayList<>();
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Exercise tempExercise = document.toObject(Exercise.class);
                        temp.add(tempExercise);
                    }
                    exercises.postValue(temp);
                } else {
                    exercises.postValue(new ArrayList<>());
                }
            }
        });
    }
    public void clearExerciseList() {
        exercises.postValue(new ArrayList<>());
    }

    public void deleteExercise(String id, int position) {
        DocumentReference categoryDoc = db.collection("workout_categories").document(currentCategoryId);
        categoryDoc.collection("exercises").document(id).delete();
        exercises.getValue().remove(position);
        exercises.postValue(exercises.getValue());
    }

    public void addNewExercise(Exercise exercise, Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageReference.child("exercises/" + exercise.getName());
        DocumentReference categoryDoc = db.collection("workout_categories").document(currentCategoryId);
        imgRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        exercise.setImageUrl(uri.toString());

                        DocumentReference newDoc = categoryDoc.collection("exercises").document();
                        String docId = newDoc.getId();

                        exercise.setId(docId);
                        exercise.setCategoryId(currentCategoryId);
                        newDoc.set(exercise).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                fetchExercisesInCategory(currentCategoryId);
                            }
                        });
                    }
                });
            }
        });
    }

    public void updateExercise(Exercise exercise, Uri newImageUri) {
        Log.d("Category id", "updateExercise: " + exercise.getCategoryId());
        Log.d("exercise id", "updateExercise: " + exercise.getId());
        Log.d("Exercise name", "updateExercise: " + exercise.getName());
        DocumentReference categoryDoc = db.collection("workout_categories").document(exercise.getCategoryId());
        DocumentReference exerciseDoc = categoryDoc.collection("exercises").document(exercise.getId());
        Log.d("imageUri", "updateExercise: " + newImageUri.toString());
        if (newImageUri.toString().equals(exercise.getImageUrl())) {
            // If the new image URI is the same as the old one, skip the image upload and just update the exercise document
            Log.d("Skiop image", "updateExercise: skip image");
            updateExerciseDocument(exercise, exerciseDoc);
        } else {
            // If the new image URI is different, upload the new image and then update the exercise document
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imgRef = storageReference.child("exercises/" + exercise.getName());

            imgRef.putFile(newImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            exercise.setImageUrl(uri.toString());
                            updateExerciseDocument(exercise, exerciseDoc);
                        }
                    });
                }
            });
        }
    }

    private void updateExerciseDocument(Exercise exercise, DocumentReference exerciseDoc) {
        Log.d("update", "updateExerciseDocument: update is called");
        DocumentReference categoryRef = db.collection("workout_categories").document(currentCategoryId);
        categoryRef.collection("exercises").document(exercise.getId()).set(exercise).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("update success", "onSuccess: update scucess");
                        fetchExercisesInCategory(exercise.getCategoryId());
            }
        });
//        exerciseDoc.update("name", exercise.getName(),
//                        "muscleGroup", exercise.getMuscleGroup(),
//                        "unit", exercise.getUnit(),
//                        "count", exercise.getCount(),
//                        "caloriesPerUnit", exercise.getCaloriesPerUnit(),
//                        "startingPosition", exercise.getStartingPosition(),
//                        "execution", exercise.getExecution(),
//                        "imageUrl", exercise.getImageUrl())
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d("update success", "onSuccess: update scucess");
//                        fetchExercisesInCategory(exercise.getCategoryId());
//                    }
//                });
    }


}
