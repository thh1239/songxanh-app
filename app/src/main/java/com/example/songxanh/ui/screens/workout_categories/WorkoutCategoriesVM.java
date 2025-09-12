package com.example.songxanh.ui.screens.workout_categories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.WorkoutCategory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkoutCategoriesVM extends ViewModel {
    private List<WorkoutCategory> categories = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> isLoadingData = new MutableLiveData<>( null);

    public List<WorkoutCategory> getCategories() {
        return categories;
    }

    public MutableLiveData<Boolean> getIsLoadingData() {
        return isLoadingData;
    }

    public void loadData() {
        isLoadingData.setValue(true);
        firestore.collection("workout_categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        WorkoutCategory newCategory = doc.toObject(WorkoutCategory.class);
                        categories.add(newCategory);
                        Log.d("Category",newCategory.getName());
                    }
                    isLoadingData.setValue(false);
                } else {
                    Log.d("Get categories error", "Error getting documents: ", task.getException());
                    isLoadingData.setValue(false);
                }
            }
        });
//        categories.add(new WorkoutCategory("1", "Chest", "https://firebasestorage.googleapis.com/v0/b/android-healthify.appspot.com/o/Barbell%20Bench%20Press.webp?alt=media&token=f7de1185-b59b-4600-858a-79410a460209"));
    }
}
