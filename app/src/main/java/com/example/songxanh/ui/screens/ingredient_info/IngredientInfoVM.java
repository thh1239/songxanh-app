package com.example.songxanh.ui.screens.ingredient_info;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class IngredientInfoVM extends ViewModel {
    private GlobalMethods globalMethods;

    public GlobalMethods getGlobalMethods() {
        return globalMethods;
    }

    public MutableLiveData<String> toastMessage = new MutableLiveData<>("");
    public IngredientInfo getIngredientInfo() {
        return ingredientInfo;
    }

    public void setIngredientInfo(IngredientInfo ingredientInfo) {
        this.ingredientInfo = ingredientInfo;
    }

    public void markAsFavorite() {
        CollectionReference favoriteCollection = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("favorite_ingredients");
        Log.d("this id", "markAsFavorite: " + ingredientInfo.getId());
        favoriteCollection.whereEqualTo("id", ingredientInfo.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().isEmpty()) {
                        favoriteCollection.add(ingredientInfo).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful()) {
                                    toastMessage.postValue("Đã thêm nguyên liệu yêu thích của bạn thành công!");
                                    task.getResult().update("id", ingredientInfo.getId());
                                } else {
                                    toastMessage.postValue("Thêm thất bại, vui lòng thử lại!");
                                }
                            }
                        });
                    } else {
                        toastMessage.postValue("Nguyên liệu này đã có trong danh sách yêu thích của bạn.");
                    }
                }else {
                    toastMessage.postValue("Thêm thất bại, vui lòng thử lại!");

                }
            }
        });
    }

    IngredientInfo ingredientInfo = new IngredientInfo();




}
