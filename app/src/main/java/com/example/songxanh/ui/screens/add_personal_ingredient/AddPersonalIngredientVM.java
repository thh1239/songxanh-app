package com.example.songxanh.ui.screens.add_personal_ingredient;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPersonalIngredientVM extends ViewModel {
    public GlobalMethods getGlobalMethods() {
        return globalMethods;
    }

    private GlobalMethods globalMethods;
    private MutableLiveData<IngredientInfo> newIngredient = new MutableLiveData<>();

    public MutableLiveData<IngredientInfo> getNewIngredient() {
        return newIngredient;
    }

    public AddPersonalIngredientVM() {
    }

    public void setNewIngredient(MutableLiveData<IngredientInfo> newIngredient) {
        this.newIngredient = newIngredient;
    }


    public AddPersonalIngredientVM(MutableLiveData<IngredientInfo> newIngredient) {
        this.newIngredient = newIngredient;
    }


    public void addPersonalIngredient() {
        DocumentReference userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        CollectionReference personalIngredientRef = userDocumentRef.collection("personal_ingredient");
        personalIngredientRef.add(this.newIngredient.getValue()).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ADDED NEW INGREDIENT failure", "Error adding document", e);
                    }
                });
    }

    public void addToPendingList() {
        CollectionReference user_ingredients = FirebaseFirestore.getInstance().collection("user_ingredients");
        Map<String, Object> data = new HashMap<>();
        IngredientInfo temp = this.newIngredient.getValue();
        data.put("Calories", temp.getCalories());
        data.put("Carbs", temp.getCarbs());
        data.put("Lipid", temp.getLipid());
        data.put("Protein", temp.getProtein());
        data.put("Short_Description", temp.getShort_Description());
        user_ingredients.add(data);
        FirebaseFirestore.getInstance().collection("count").document("pending_ingredients_count").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int count = documentSnapshot.getLong("count").intValue();
                int newCount = count + 1;
                FirebaseFirestore.getInstance().collection("count").document("pending_ingredients_count").update("count", newCount);
            }
        });
    }
}
