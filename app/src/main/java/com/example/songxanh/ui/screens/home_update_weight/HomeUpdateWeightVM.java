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
import java.util.Locale;
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
    private Integer weight = 0;
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
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM", Locale.getDefault());
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
                    if (documentSnapshot.exists() && documentSnapshot.contains("weight")) {
                        int stepsValue = documentSnapshot.getLong("weight").intValue();
                        setWeight(stepsValue);
                    }
                })
                .addOnFailureListener(e -> Log.i("Lỗi", "Không thể tải cân nặng hôm nay"));
    }

    public void getUserLiveData() {
        isLoadingData.setValue(true);
        firestore.collection("users").whereEqualTo("email", firebaseAuth.getCurrentUser().getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            user = task.getResult().getDocuments().get(0).toObject(NormalUser.class);
                            isLoadingData.setValue(false);
                        } else {
                            Log.d("Get user data error", "Error getting user documents: ", task.getException());
                            isLoadingData.setValue(false);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    isLoadingData.setValue(false);
                    Log.i("Error", e.getMessage());
                });
    }

    /**
     * Lưu cân nặng hôm nay và TÍNH LẠI dailyCalories cho users/{uid}.
     */
    public void saveDailyWeight(Integer weight, Integer steps, Float exerciseCalories, Float calories, Float foodCalories) {
        Map<String, Object> dailyActivities = new HashMap<>();
        dailyActivities.put("weight", weight);

        firestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("daily_activities")
                .document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                .update(dailyActivities)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("success", "Lưu cân nặng thành công");
                        // Sau khi lưu weight -> tính lại dailyCalories
                        recalcAndUpdateDailyCaloriesAfterWeightChange(weight != null ? weight : 0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("fail", "Lưu cân nặng thất bại");
                        Log.i("bug", e.toString());
                    }
                });
    }

    /**
     * Đọc users/{uid}, tính lại dailyCalories theo cân nặng hiện tại.
     * - Nếu đủ dữ liệu lộ trình (startDate/goalDate/goalWeight): dùng calculateDailyCalories (đã có sàn BMR).
     * - Nếu thiếu: fallback = max(BMR, 90% TDEE).
     */
    private void recalcAndUpdateDailyCaloriesAfterWeightChange(int currentWeight) {
        final String uid = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(userDoc -> {
                    if (!userDoc.exists()) {
                        Log.w("kcal", "User doc không tồn tại, bỏ qua cập nhật dailyCalories");
                        return;
                    }

                    String gender = userDoc.getString("gender");
                    Double height = userDoc.getDouble("height");
                    Long ageL = userDoc.getLong("age");
                    Long goalW = userDoc.getLong("goalWeight");
                    Date startDate = userDoc.getDate("startDate");
                    Date goalDate  = userDoc.getDate("goalDate");

                    if (height == null || ageL == null || currentWeight <= 0) {
                        Log.w("kcal", "Thiếu height/age/weight -> bỏ qua cập nhật dailyCalories");
                        return;
                    }

                    final boolean isMale = normalizeGender(gender);
                    final double bmr  = GlobalMethods.bmrMifflin(isMale, currentWeight, height, ageL.intValue());
                    final double tdee = GlobalMethods.tdee(bmr, 1.375); // mức hoạt động mặc định: nhẹ

                    // Tính dailyCalories tạm
                    double dailyTmp;
                    boolean hasRoadmap = (goalW != null && startDate != null && goalDate != null);

                    if (hasRoadmap) {
                        // Dùng công thức đầy đủ (đã có sàn BMR bên trong)
                        dailyTmp = GlobalMethods.calculateDailyCalories(
                                gender,
                                currentWeight,
                                height,
                                ageL.intValue(),
                                goalW.intValue(),
                                startDate,
                                goalDate
                        );
                    } else {
                        // Fallback: không có lộ trình -> đề xuất an toàn tối thiểu
                        dailyTmp = Math.max(bmr, tdee * 0.90); // 90% TDEE nhưng >= BMR
                    }

                    // Giá trị cuối cùng (final) để dùng trong lambda lồng nhau
                    final long dailyRounded = Math.round(dailyTmp);

                    final Map<String, Object> upd = new HashMap<>();
                    upd.put("dailyCalories", dailyRounded);

                    firestore.collection("users").document(uid).update(upd)
                            .addOnSuccessListener(v -> Log.i("kcal", "Cập nhật dailyCalories = " + dailyRounded))
                            .addOnFailureListener(err -> Log.e("kcal", "Cập nhật dailyCalories thất bại", err));
                })
                .addOnFailureListener(e -> Log.e("kcal", "Không đọc được users/{uid}", e));
    }

    // Helper normalize local (độc lập với GlobalMethods.normalizeGender private)
    private static boolean normalizeGender(String gender) {
        if (gender == null) return true;
        String g = gender.trim().toLowerCase(java.util.Locale.ROOT);
        if (g.startsWith("m") || g.equals("nam")) return true;
        if (g.startsWith("f") || g.equals("nữ") || g.equals("nu")) return false;
        return true;
    }
}
