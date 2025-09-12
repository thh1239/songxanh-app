package com.example.songxanh.ui.screens.profile_calories_history;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.ui.screens.home.CustomEntry;
import com.example.songxanh.ui.screens.home.CustomEntryLineChart;
import com.example.songxanh.utils.FirebaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ProfileCaloriesHistoryVM extends ViewModel {
    private MutableLiveData<Boolean> isLoadingData = new MutableLiveData<>(null);

    public MutableLiveData<Boolean> getIsLoadingData() {
        return isLoadingData;
    }

    public void setIsLoadingData(MutableLiveData<Boolean> isLoadingData) {
        this.isLoadingData = isLoadingData;
    }
    private MutableLiveData<Boolean> isLoadingLine = new MutableLiveData<>(null);

    public MutableLiveData<Boolean> getIsLoadingLine() {
        return isLoadingLine;
    }

    public void setIsLoadingLine(MutableLiveData<Boolean> isLoadingLine) {
        this.isLoadingLine = isLoadingLine;
    }
    ArrayList<CustomEntryLineChart> lineEntries;
    ArrayList<CustomEntryLineChart> lineEntries1;

    public ArrayList<CustomEntryLineChart> getLineEntries1() {
        return lineEntries1;
    }

    public void setLineEntries1(ArrayList<CustomEntryLineChart> lineEntries1) {
        this.lineEntries1 = lineEntries1;
    }

    private Integer x = 0;
    private Integer x1 = 0;

    public ArrayList<CustomEntryLineChart> getLineEntries() {
        return lineEntries;
    }

    public void setLineEntries(ArrayList<CustomEntryLineChart> lineEntries) {
        this.lineEntries = lineEntries;
    }

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private NormalUser user = new NormalUser();

    public ProfileCaloriesHistoryVM() {
        loadChart();
    }

    public void loadChart() {
        isLoadingLine.setValue(true);
        firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid()).collection("daily_activities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            lineEntries = new ArrayList<>();
                            lineEntries1 = new ArrayList<>();
                            x = 0;
                            x1 = 0;
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                if (document.contains("calories")) {
                                    int steps = document.getLong("calories").intValue();
                                    String date = document.getId();
                                    Log.i("calories line profile", String.valueOf(steps));
                                    Log.i("date line profile", date);

                                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM");
                                    try {
                                        Date parsedDate = inputFormat.parse(date);
                                        date = outputFormat.format(parsedDate);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    lineEntries.add(new CustomEntryLineChart(x, steps, date));
                                    x++;
                                }
                            }
                            Collections.sort(lineEntries);

                            for (CustomEntryLineChart customEntryLineChart : lineEntries) {
                                lineEntries1.add(new CustomEntryLineChart(
                                        x1,
                                        customEntryLineChart.getSteps(),
                                        customEntryLineChart.getDate()));
                                x1++;
                            }

                            isLoadingLine.setValue(false);
                        } else {
                            Exception e = task.getException();
                            Log.i("bugg", "Lỗi khi lấy dữ liệu steps");
                        }
                    }
                });
    }

    public void getUserLiveData() {
        isLoadingData.setValue(true);
        FirebaseConstants.usersRef.document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user = document.toObject(NormalUser.class);
                                isLoadingData.setValue(false);
                            }
                        } else {
                            Log.d("Get user data error", "Error getting user documents: ", task.getException());
                            isLoadingData.setValue(false);
                        }
                    }
                });

//        if (userLiveData == null) {
//            userLiveData = new MutableLiveData<>();
//            loadUser();
//        }
    }

}
