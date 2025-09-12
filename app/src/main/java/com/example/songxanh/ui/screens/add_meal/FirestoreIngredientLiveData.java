package com.example.songxanh.ui.screens.add_meal;

import androidx.lifecycle.LiveData;

import com.example.songxanh.data.models.Dish;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class FirestoreIngredientLiveData extends LiveData<ArrayList<Dish>> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference ingredientInfoRef = db.collection("ingredient-data");
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onActive() {
        super.onActive();

    }
}
