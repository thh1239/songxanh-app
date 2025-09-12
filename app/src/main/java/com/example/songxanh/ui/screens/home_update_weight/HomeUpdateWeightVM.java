package com.example.songxanh.ui.screens.home_update_weight;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.ui.screens.home.CustomEntry;
import com.example.songxanh.ui.screens.home.HomeVM;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeUpdateWeightVM extends ViewModel {
    private MutableLiveData<Boolean> isLoadingData = new MutableLiveData<>(null);
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private NormalUser user = new NormalUser();

    public NormalUser getUser() {
        return user;
    }

    private Integer x = 0;
    private MutableLiveData<Boolean> isAddingValue = null;
    private Integer weight = new Integer(0);
    private HomeVM homeVM;

    List<CustomEntry> barEntries;

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }

    public MutableLiveData<Boolean> getIsAddingValue() {
        return isAddingValue;
    }

    public void setIsAddingValue(MutableLiveData<Boolean> isAddingValue) {
        this.isAddingValue = isAddingValue;
    }

    public HomeUpdateWeightVM(Integer weight) {
        this.weight = weight;
    }

    public HomeUpdateWeightVM() {
//        getUserLiveData();
        loadDailyWeight();
        loadBarData();
    }

    public List<CustomEntry> getBarEntries() {
        return barEntries;
    }

    public MutableLiveData<Boolean> getIsLoadingData() {
        return isLoadingData;
    }

    public void loadBarData() {
        isLoadingData.setValue(true);
        firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("daily_activities")
                .limit(7)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            barEntries = new ArrayList<>();
                            x = 0;
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                if (document.contains("weight")) {
                                    int steps = document.getLong("weight").intValue();
                                    String date = document.getId();
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM");
                                    try {
                                        Date parsedDate = inputFormat.parse(date);
                                        date = outputFormat.format(parsedDate);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    barEntries.add(new CustomEntry(x, steps, date));
                                    x++;
                                }
                            }
                            isLoadingData.setValue(false);
                        } else {
                            Exception e = task.getException();
                            Log.i("bugg", "Lỗi khi lấy dữ liệu bar");
                        }
                    }
                });

    }

    public void loadDailyWeight() {

        firestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("daily_activities")
                .document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int stepsValue = documentSnapshot.getLong("weight").intValue();
                        setWeight(stepsValue);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.i("Lỗi", "abcdxyz");
                    // Xử lý khi không thể tải dữ liệu từ Firestore
                });
    }

    public void getUserLiveData() {
        isLoadingData.setValue(true);
        firestore.collection("users").whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            user = task.getResult().getDocuments().get(0).toObject(NormalUser.class);
                            isLoadingData.setValue(false);

                        } else {
                            Log.d("Get user data error", "Error getting user documents: ", task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isLoadingData.setValue(false);
                        Log.i("Error", e.getMessage());
                    }
                });
    }

    public void saveDailyWeight(Integer weight, Integer steps, Float exerciseCalories, Float calories, Float foodCalories) {
        Map<String, Object> dailyActivities = new HashMap<>();
//        dailyActivities.put("steps", steps);
        dailyActivities.put("weight", weight);
//        dailyActivities.put("exerciseCalories", exerciseCalories);
//        dailyActivities.put("calories", calories);
//        dailyActivities.put("foodCalories", foodCalories);

        firestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                .update(dailyActivities)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("success", "Lưu giá trị thành công");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("success", "Lưu giá trị thất bại");
                        Log.i("bug", e.toString());
                    }
                });
    }

}