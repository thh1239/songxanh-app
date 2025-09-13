package com.example.songxanh.ui.screens.admin_workout_screen;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.data.models.WorkoutCategory;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminWorkoutVM extends ViewModel {

    public interface OnError { void onError(String message); }

    private String currentCategoryId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<ArrayList<WorkoutCategory>> workoutCategories = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArrayList<Exercise>> exercises = new MutableLiveData<>(new ArrayList<>());

    public AdminWorkoutVM() {
        fetchWorkoutCategories();
    }

    public MutableLiveData<ArrayList<WorkoutCategory>> getWorkoutCategories() {
        return workoutCategories;
    }

    public MutableLiveData<ArrayList<Exercise>> getExercises() {
        return exercises;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void fetchWorkoutCategories() {
        db.collection("workout_categories").get().addOnSuccessListener(snap -> {
            ArrayList<WorkoutCategory> temp = new ArrayList<>();
            for (DocumentSnapshot doc : snap) {
                WorkoutCategory c = doc.toObject(WorkoutCategory.class);
                if (c == null) continue;
                if (c.getId() == null || c.getId().isEmpty()) c.setId(doc.getId());
                temp.add(c);
            }
            workoutCategories.postValue(temp);
        });
    }

    public void addNewCategory(String name, Uri imageUri) {
        addNewCategory(name, imageUri, null, null);
    }

    public void addNewCategory(String name, Uri imageUri, @Nullable Runnable onSuccess, @Nullable OnError onError) {
        if (name == null || name.trim().isEmpty() || imageUri == null) {
            if (onError != null) onError.onError("Thiếu tên hoặc ảnh danh mục");
            return;
        }
        DocumentReference newDoc = db.collection("workout_categories").document();
        String docId = newDoc.getId();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageReference.child("workout_categories/" + docId + ".jpg");
        imgRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    WorkoutCategory category = new WorkoutCategory();
                    category.setId(docId);
                    category.setName(name.trim());
                    category.setImageUrl(uri.toString());
                    newDoc.set(category).addOnSuccessListener(unused -> {
                        fetchWorkoutCategories();
                        if (onSuccess != null) onSuccess.run();
                    }).addOnFailureListener(e -> {
                        if (onError != null) onError.onError(e.getMessage());
                    });
                }).addOnFailureListener(e -> {
                    if (onError != null) onError.onError(e.getMessage());
                })
        ).addOnFailureListener(e -> {
            if (onError != null) onError.onError(e.getMessage());
        });
    }

    public void deleteCategoryById(String categoryId, Runnable onSuccess, @Nullable OnError onError) {
        if (categoryId == null || categoryId.isEmpty()) {
            if (onError != null) onError.onError("Thiếu ID danh mục");
            return;
        }
        DocumentReference catRef = db.collection("workout_categories").document(categoryId);
        catRef.collection("exercises").get().addOnSuccessListener(snap -> {
            List<com.google.android.gms.tasks.Task<Void>> deletes = new ArrayList<>();
            for (DocumentSnapshot d : snap.getDocuments()) {
                deletes.add(catRef.collection("exercises").document(d.getId()).delete());
            }
            Tasks.whenAllComplete(deletes).addOnSuccessListener(all ->
                    catRef.delete().addOnSuccessListener(unused -> {
                        ArrayList<WorkoutCategory> list = workoutCategories.getValue() == null ? new ArrayList<>() : new ArrayList<>(workoutCategories.getValue());
                        int idx = -1;
                        for (int i = 0; i < list.size(); i++) if (categoryId.equals(list.get(i).getId())) { idx = i; break; }
                        if (idx >= 0) list.remove(idx);
                        workoutCategories.postValue(list);
                        if (onSuccess != null) onSuccess.run();
                    }).addOnFailureListener(e -> {
                        if (onError != null) onError.onError(e.getMessage());
                    })
            ).addOnFailureListener(e -> {
                if (onError != null) onError.onError(e.getMessage());
            });
        }).addOnFailureListener(e -> {
            if (onError != null) onError.onError(e.getMessage());
        });
    }

    public void fetchExercisesInCategory(String id) {
        currentCategoryId = id;
        DocumentReference categoryDoc = db.collection("workout_categories").document(id);
        categoryDoc.collection("exercises").get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Exercise> temp = new ArrayList<>();
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Exercise e = document.toObject(Exercise.class);
                    if (e != null) temp.add(e);
                }
            }
            exercises.postValue(temp);
        });
    }

    public void clearExerciseList() {
        exercises.postValue(new ArrayList<>());
    }

    public void deleteExercise(String id, int position) {
        if (currentCategoryId == null || id == null) return;
        DocumentReference categoryDoc = db.collection("workout_categories").document(currentCategoryId);
        categoryDoc.collection("exercises").document(id).delete().addOnSuccessListener(unused -> {
            ArrayList<Exercise> list = exercises.getValue();
            if (list != null && position >= 0 && position < list.size()) {
                list.remove(position);
                exercises.postValue(list);
            } else {
                fetchExercisesInCategory(currentCategoryId);
            }
        });
    }

    public void addNewExercise(Exercise exercise, Uri imageUri) {
        if (currentCategoryId == null || exercise == null || imageUri == null) return;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storageReference.child("exercises/" + currentCategoryId + "_" + System.currentTimeMillis() + ".jpg");
        DocumentReference categoryDoc = db.collection("workout_categories").document(currentCategoryId);
        imgRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    exercise.setImageUrl(uri.toString());
                    DocumentReference newDoc = categoryDoc.collection("exercises").document();
                    String docId = newDoc.getId();
                    exercise.setId(docId);
                    exercise.setCategoryId(currentCategoryId);
                    newDoc.set(exercise).addOnSuccessListener(unused -> fetchExercisesInCategory(currentCategoryId));
                })
        );
    }

    public void updateExercise(Exercise exercise, Uri newImageUri) {
        if (exercise == null || exercise.getCategoryId() == null || exercise.getId() == null) return;
        DocumentReference categoryDoc = db.collection("workout_categories").document(exercise.getCategoryId());
        DocumentReference exerciseDoc = categoryDoc.collection("exercises").document(exercise.getId());
        if (newImageUri == null || newImageUri.toString().equals(exercise.getImageUrl())) {
            updateExerciseDocument(exercise, exerciseDoc);
        } else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference imgRef = storageReference.child("exercises/" + exercise.getCategoryId() + "_" + exercise.getId() + ".jpg");
            imgRef.putFile(newImageUri).addOnSuccessListener(taskSnapshot ->
                    imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        exercise.setImageUrl(uri.toString());
                        updateExerciseDocument(exercise, exerciseDoc);
                    })
            );
        }
    }

    private void updateExerciseDocument(Exercise exercise, DocumentReference exerciseDoc) {
        DocumentReference categoryRef = db.collection("workout_categories").document(exercise.getCategoryId());
        categoryRef.collection("exercises").document(exercise.getId()).set(exercise).addOnSuccessListener(unused ->
                fetchExercisesInCategory(exercise.getCategoryId())
        );
    }
}
