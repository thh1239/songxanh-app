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

    private GlobalMethods globalMethods;
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
    public GlobalMethods getGlobalMethods() { return globalMethods; }

    private MutableLiveData<IngredientInfo> newIngredient = new MutableLiveData<>();
// == Cập nhật nguyên liệu và tính lại tổng calo ==
    public MutableLiveData<IngredientInfo> getNewIngredient() { return newIngredient; }

    public AddPersonalIngredientVM() { }
    public AddPersonalIngredientVM(MutableLiveData<IngredientInfo> newIngredient) { this.newIngredient = newIngredient; }
// == Cập nhật nguyên liệu và tính lại tổng calo ==
    public void setNewIngredient(MutableLiveData<IngredientInfo> newIngredient) { this.newIngredient = newIngredient; }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void addPersonalIngredient() {
        IngredientInfo ing = this.newIngredient.getValue();
        if (ing == null) {
            Log.w("ADD_PERSONAL_ING", "newIngredient is null, abort");
            return;
        }

        String shortDesc = ing.getShort_Description();
        Double cal = ing.getCalories();
        Double carbs = ing.getCarbs();
        Double lipid = ing.getLipid();
        Double protein = ing.getProtein();

        Map<String, Object> data = new HashMap<>();
        data.put("shortDescription", shortDesc);
        data.put("calories", cal == null ? null : (int) Math.round(cal));
        data.put("carbs",   carbs);
        data.put("lipid",   lipid);
        data.put("protein", protein);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocumentRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid);
        CollectionReference personalIngredientRef = userDocumentRef.collection("personal_ingredient");

        personalIngredientRef.add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override public void onSuccess(DocumentReference documentReference) {
                        Log.i("ADD_PERSONAL_ING", "Added personal ingredient: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override public void onFailure(@NonNull Exception e) {
                        Log.w("ADD_PERSONAL_ING", "Error adding document", e);
                    }
                });
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void addToPendingList() {
        IngredientInfo temp = this.newIngredient.getValue();
        if (temp == null) {
            Log.w("ADD_PENDING_ING", "newIngredient is null, abort");
            return;
        }

        CollectionReference user_ingredients = FirebaseFirestore.getInstance().collection("user_ingredients");
        Map<String, Object> data = new HashMap<>();
        data.put("Calories",          temp.getCalories());
        data.put("Carbs",             temp.getCarbs());
        data.put("Lipid",             temp.getLipid());
        data.put("Protein",           temp.getProtein());
        data.put("Short_Description", temp.getShort_Description());

        user_ingredients.add(data)
                .addOnSuccessListener(docRef -> Log.i("ADD_PENDING_ING", "Added pending ingredient: " + docRef.getId()))
                .addOnFailureListener(e -> Log.w("ADD_PENDING_ING", "Error adding pending doc", e));

        FirebaseFirestore.getInstance()
                .collection("count")
                .document("pending_ingredients_count")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Long count = documentSnapshot.getLong("count");
                        int newCount = (count == null ? 0 : count.intValue()) + 1;
                        FirebaseFirestore.getInstance()
                                .collection("count")
                                .document("pending_ingredients_count")
                                .update("count", newCount);
                    }
                })
                .addOnFailureListener(e -> Log.w("ADD_PENDING_ING", "Error updating pending count", e));
    }
}
