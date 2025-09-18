package com.example.songxanh.ui.screens.admin_ingredient_screen;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AdminIngredientVM extends ViewModel {
    MutableLiveData<ArrayList<IngredientInfo>> databaseIngredientList;
    public MutableLiveData<ArrayList<IngredientInfo>> pendingIngredientList;

    MutableLiveData<ArrayList<IngredientInfo>> searchResultList;
    MutableLiveData<Integer> ingredientCount;
    public MutableLiveData<Integer> pendingIngredientCount;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Query query;
    ListenerRegistration registration;

    private int pageSize = 20;
    private DocumentSnapshot lastVisibleDocument;

    public AdminIngredientVM() {
        databaseIngredientList = new MutableLiveData<>(new ArrayList<>());
        pendingIngredientList = new MutableLiveData<>(new ArrayList<>());
        searchResultList = new MutableLiveData<>(new ArrayList<>());
        ingredientCount = new MutableLiveData<>(0);
        pendingIngredientCount = new MutableLiveData<>(0);
        fetchPendingIngredientsList();
        fetchIngredientCount();
        query = db.collection("ingredient-data").limit(pageSize);

        loadMore();
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public MutableLiveData<ArrayList<IngredientInfo>> getSearchResultList() {
        return searchResultList;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void setSearchResultList(MutableLiveData<ArrayList<IngredientInfo>> searchResultList) {
        this.searchResultList = searchResultList;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void loadMore() {
        Query newQuery;
        if (lastVisibleDocument != null) {
            newQuery = query.startAfter(lastVisibleDocument);
        } else {
            newQuery = query;
        }

        newQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<IngredientInfo> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        IngredientInfo ingredientInfo = doc.toObject(IngredientInfo.class).withId(doc.getId());
                        tempList.add(ingredientInfo);
                    }
                    if (!tempList.isEmpty()) {
                        lastVisibleDocument = task.getResult().getDocuments().get(tempList.size() - 1);
                        ArrayList<IngredientInfo> currentList = databaseIngredientList.getValue();
                        currentList.addAll(tempList);
                        databaseIngredientList.postValue(currentList);
                    }
                }
            }
        });
    }
// == Cập nhật nguyên liệu và tính lại tổng calo ==

    public void addIngredient(IngredientInfo ingredientInfo) {
        Log.d("NEW INGREDIENT", ingredientInfo.getShort_Description());
        Map<String, Object> data = new HashMap<>();
        data.put("Calories", ingredientInfo.getCalories());
        data.put("Carbs", ingredientInfo.getCarbs());
        data.put("Lipid", ingredientInfo.getLipid());
        data.put("Protein", ingredientInfo.getProtein());
        data.put("Short_Description", ingredientInfo.getShort_Description());
        db.collection("ingredient-data").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onSuccess(DocumentReference documentReference) {
                ArrayList<IngredientInfo> currentList = databaseIngredientList.getValue();
                ingredientInfo.withId(documentReference.getId()); //`withId` method to set the document ID
                Log.d("ID", "onSuccess: " + documentReference.getId());
                currentList.add(ingredientInfo);
                databaseIngredientList.postValue(currentList);
                updateCount(1);
            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void updateIngredient(String id, IngredientInfo updatedIngredient) {
        db.collection("ingredient-data").document(id).set(updatedIngredient).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onSuccess(Void unused) {
                ArrayList<IngredientInfo> currentList = databaseIngredientList.getValue();
                for (int i = 0; i < currentList.size(); i++) {
                    IngredientInfo ingredient = currentList.get(i);
                    if (ingredient.getId().equals(id)) {
                        currentList.set(i, updatedIngredient);
                    }
                }
                databaseIngredientList.postValue(currentList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onFailure(@NonNull Exception e) {
                ArrayList<IngredientInfo> currentList = databaseIngredientList.getValue();
                for (Iterator<IngredientInfo> it = currentList.iterator(); it.hasNext(); ) {
                    IngredientInfo ingredient = it.next();
                    if (ingredient.getId().equals(id)) {
                        it.remove();
                    }
                }
                databaseIngredientList.postValue(currentList);
            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void updateCount(int numIncreased) {
        ingredientCount.postValue(ingredientCount.getValue() + numIncreased);

        db.collection("count").document("ingredients_count").update("count",ingredientCount.getValue());
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void deleteIngredient(String id) {
        Log.d("DELETE ID", "deleteIngredient: " + id);
        db.collection("ingredient-data").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onSuccess(Void unused) {
                ArrayList<IngredientInfo> currentList = databaseIngredientList.getValue();
                for (Iterator<IngredientInfo> it = currentList.iterator(); it.hasNext(); ) {
                    IngredientInfo ingredient = it.next();
                    if (ingredient.getId().equals(id)) {
                        it.remove();
                    }
                }
                databaseIngredientList.postValue(currentList);
                updateCount(-1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void search(String query) {
        query = query.toUpperCase();
        if (query.isEmpty()) {
            searchResultList = new MutableLiveData<>(new ArrayList<>());
            return;
        }
        CollectionReference ingredientInfoRef = MainActivity.getDb().collection("ingredient-data");
        Query searchQuery = ingredientInfoRef.whereGreaterThanOrEqualTo("Short_Description", query).whereLessThanOrEqualTo("Short_Description", query + "\uf8ff").limit(10);
        searchQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    ArrayList<IngredientInfo> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Integer calories = document.get("Calories", int.class);
                        Double carbs = document.get("Carbs", double.class);
                        Double lipid = document.get("Lipid", double.class);
                        Double protein = document.get("Protein", double.class);

                        IngredientInfo temp = new IngredientInfo(document.get("Short_Description", String.class), calories, carbs, lipid, protein);
                        temp.withId(document.getId());
                        tempList.add(temp);
                    }
                    searchResultList.postValue(tempList);
                } else {
                    Log.d("FAILURE", "onComplete: " + task.getException());
                }
            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void fetchIngredientCount() {
        db.collection("count").document("ingredients_count").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long count = documentSnapshot.getLong("count");
                    if (count != null) {
                        ingredientCount.postValue(count.intValue());
                    }
                }
            }
        });
        db.collection("count").document("pending_ingredients_count").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long count = documentSnapshot.getLong("count");
                    if (count != null) {
                        pendingIngredientCount.postValue(count.intValue());
                    }
                }
            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void fetchPendingIngredientsList() {
        db.collection("user_ingredients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<IngredientInfo> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        IngredientInfo ingredientInfo = doc.toObject(IngredientInfo.class).withId(doc.getId());
                        tempList.add(ingredientInfo);
                    }
                    pendingIngredientList.postValue(tempList);
                }
            }
        });
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void approveIngredient(int position) {
        IngredientInfo ingredient = pendingIngredientList.getValue().get(position);
        addIngredient(ingredient);
        deleteFromPendingList(position);
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void deleteFromPendingList(int position) {
        db.collection("user_ingredients").document(pendingIngredientList.getValue().get(position).getId()).delete();

        ArrayList<IngredientInfo> temp = pendingIngredientList.getValue();
        temp.remove(position);
        pendingIngredientList.postValue(temp);
        decreasePendingCountByOne();
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==


    public void decreasePendingCountByOne(){
        int temp = pendingIngredientCount.getValue();
        pendingIngredientCount.postValue(temp -1 );
        db.collection("count").document("pending_ingredients_count").update("count",temp - 1);
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public MutableLiveData<ArrayList<IngredientInfo>> getDatabaseIngredientList() {
        return databaseIngredientList;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==


    public void setDatabaseIngredientList(MutableLiveData<ArrayList<IngredientInfo>> databaseIngredientList) {
        this.databaseIngredientList = databaseIngredientList;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public MutableLiveData<ArrayList<IngredientInfo>> getPendingIngredientList() {
        return pendingIngredientList;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void setPendingIngredientList(MutableLiveData<ArrayList<IngredientInfo>> pendingIngredientList) {
        this.pendingIngredientList = pendingIngredientList;
    }
}
