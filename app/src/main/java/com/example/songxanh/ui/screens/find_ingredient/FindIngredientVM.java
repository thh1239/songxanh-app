package com.example.songxanh.ui.screens.find_ingredient;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.ui.screens.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class FindIngredientVM extends ViewModel {

    MutableLiveData<ArrayList<IngredientInfo>> ingredientInfoArrayList = new MutableLiveData<>();
    MutableLiveData<ArrayList<IngredientInfo>> personalIngredientInfoArrayList = new MutableLiveData<>();
    public MutableLiveData<ArrayList<IngredientInfo>> favoriteIngredient = new MutableLiveData<>();

    public FindIngredientVM() {
        ingredientInfoArrayList.setValue(new ArrayList<>());
        personalIngredientInfoArrayList.setValue(new ArrayList<>());
        favoriteIngredient.setValue(new ArrayList<>());
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public MutableLiveData<ArrayList<IngredientInfo>> getIngredientInfoArrayList() {
        return ingredientInfoArrayList;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public MutableLiveData<ArrayList<IngredientInfo>> getPersonalIngredientInfoArrayList() {
        return personalIngredientInfoArrayList;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void fetchFavoriteIngredients() {
        CollectionReference favoriteIngredientsRef = MainActivity.getDb()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("favorite_ingredients");
        favoriteIngredientsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<IngredientInfo> temp = new ArrayList<>();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    IngredientInfo tempIngredient = documentSnapshot.toObject(IngredientInfo.class).withId(documentSnapshot.getId());
                    temp.add(tempIngredient);
                }
                favoriteIngredient.postValue(temp);
            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void search(String query) {
        String qRaw = query == null ? "" : query.trim();
        String qUpper = qRaw.toUpperCase(Locale.ROOT);
        if (qRaw.isEmpty()) {
            ingredientInfoArrayList.postValue(new ArrayList<>());
            personalIngredientInfoArrayList.postValue(new ArrayList<>());
            fetchFavoriteIngredients();
            return;
        }
        CollectionReference ingredientInfoRef = MainActivity.getDb().collection("ingredient-data");
        Query searchQuery = ingredientInfoRef
                .whereGreaterThanOrEqualTo("Short_Description", qUpper)
                .whereLessThanOrEqualTo("Short_Description", qUpper + "\uf8ff")
                .limit(10);
        searchQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<IngredientInfo> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        IngredientInfo temp2 = document.toObject(IngredientInfo.class).withId(document.getId());
                        tempList.add(temp2);
                    }
                    ingredientInfoArrayList.setValue(tempList);
                } else {
                    Log.d("FAILURE", "onComplete: " + task.getException());
                }
            }
        });

        DocumentReference userDocument = MainActivity.getDb().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        CollectionReference personalIngredientsReference = userDocument.collection("personal_ingredient");
        Query personalIngredientQuery = personalIngredientsReference
                .whereGreaterThanOrEqualTo("shortDescription", qRaw)
                .whereLessThanOrEqualTo("shortDescription", qRaw + "\uf8ff")
                .limit(10);
        personalIngredientQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<IngredientInfo> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String shortDesc = document.getString("shortDescription");
                        Integer calories = document.get("calories", Integer.class);
                        Double carbs = document.get("carbs", Double.class);
                        Double lipid = document.get("lipid", Double.class);
                        Double protein = document.get("protein", Double.class);
                        IngredientInfo temp = new IngredientInfo(shortDesc, calories, carbs, lipid, protein);
                        temp.setId(document.getId());
                        tempList.add(temp);
                    }
                    personalIngredientInfoArrayList.setValue(tempList);
                } else {
                    Log.d("FAILURE", "onComplete: " + task.getException());
                }
            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void loadAllRecommended() {
        CollectionReference ingredientInfoRef = MainActivity.getDb().collection("ingredient-data");
        ingredientInfoRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<IngredientInfo> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        IngredientInfo info = doc.toObject(IngredientInfo.class).withId(doc.getId());
                        tempList.add(info);
                    }
                    ingredientInfoArrayList.postValue(tempList);
                } else {
                    Log.d("ING_ALL_FAIL", "onComplete: " + task.getException());
                }
            }
        });
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void loadAllPersonal() {
        DocumentReference userDocument = MainActivity.getDb().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        CollectionReference personalRef = userDocument.collection("personal_ingredient");
        personalRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<IngredientInfo> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String shortDesc = doc.getString("shortDescription");
                        Integer calories = doc.get("calories", Integer.class);
                        Double carbs = doc.get("carbs", Double.class);
                        Double lipid = doc.get("lipid", Double.class);
                        Double protein = doc.get("protein", Double.class);
                        IngredientInfo info = new IngredientInfo(shortDesc, calories, carbs, lipid, protein).withId(doc.getId());
                        tempList.add(info);
                    }
                    personalIngredientInfoArrayList.postValue(tempList);
                } else {
                    Log.d("PER_ALL_FAIL", "onComplete: " + task.getException());
                }
            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void searchBoth(String query) {
        String qRaw = query == null ? "" : query.trim();
        if (qRaw.isEmpty()) {
            loadAllRecommended();
            loadAllPersonal();
            return;
        }
        String upper = qRaw.toUpperCase(Locale.ROOT);

        CollectionReference ingredientInfoRef = MainActivity.getDb().collection("ingredient-data");
        Query searchQuery = ingredientInfoRef
                .whereGreaterThanOrEqualTo("Short_Description", upper)
                .whereLessThanOrEqualTo("Short_Description", upper + "\uf8ff")
                .limit(50);
        searchQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<IngredientInfo> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        IngredientInfo info = doc.toObject(IngredientInfo.class).withId(doc.getId());
                        tempList.add(info);
                    }
                    ingredientInfoArrayList.postValue(tempList);
                } else {
                    Log.d("ING_SEARCH_FAIL", "onComplete: " + task.getException());
                }
            }
        });

        DocumentReference userDocument = MainActivity.getDb().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        CollectionReference personalIngredientsReference = userDocument.collection("personal_ingredient");
        Query personalIngredientQuery = personalIngredientsReference
                .whereGreaterThanOrEqualTo("shortDescription", qRaw)
                .whereLessThanOrEqualTo("shortDescription", qRaw + "\uf8ff")
                .limit(50);
        personalIngredientQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<IngredientInfo> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        String shortDesc = doc.getString("shortDescription");
                        Integer calories = doc.get("calories", Integer.class);
                        Double carbs = doc.get("carbs", Double.class);
                        Double lipid = doc.get("lipid", Double.class);
                        Double protein = doc.get("protein", Double.class);
                        IngredientInfo info = new IngredientInfo(shortDesc, calories, carbs, lipid, protein).withId(doc.getId());
                        tempList.add(info);
                    }
                    personalIngredientInfoArrayList.postValue(tempList);
                } else {
                    Log.d("PER_SEARCH_FAIL", "onComplete: " + task.getException());
                }
            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void deletePersonalIngredient(String docId, int position) {
        DocumentReference userDocument = MainActivity.getDb()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userDocument.collection("personal_ingredient")
                .document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override public void onSuccess(Void unused) {
                        ArrayList<IngredientInfo> current = personalIngredientInfoArrayList.getValue();
                        if (current != null && position >= 0 && position < current.size()) {
                            current.remove(position);
                            personalIngredientInfoArrayList.postValue(new ArrayList<>(current));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override public void onFailure(@NonNull Exception e) {
                        Log.e("DELETE_PERSONAL_ING", "failed: " + e.getMessage());
                    }
                });
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void savePersonalIngredient(IngredientInfo info, String existingDocId,
                                       OnSuccessListener<Void> onSuccess,
                                       OnFailureListener onFailure) {
        DocumentReference userDocument = MainActivity.getDb()
                .collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        CollectionReference personalRef = userDocument.collection("personal_ingredient");

        Map<String, Object> data = new HashMap<>();
        data.put("shortDescription", info.getShort_Description());
        data.put("calories", info.getCalories());
        data.put("carbs", info.getCarbs());
        data.put("lipid", info.getLipid());
        data.put("protein", info.getProtein());

        if (existingDocId != null && !existingDocId.isEmpty()) {
            personalRef.document(existingDocId).set(data)
                    .addOnSuccessListener(v -> {
                        loadAllPersonal();
                        if (onSuccess != null) onSuccess.onSuccess(v);
                    })
                    .addOnFailureListener(onFailure);
        } else {
            personalRef.add(data)
                    .addOnSuccessListener(doc -> {
                        loadAllPersonal();
                        if (onSuccess != null) onSuccess.onSuccess(null);
                    })
                    .addOnFailureListener(onFailure);
        }
    }
}
