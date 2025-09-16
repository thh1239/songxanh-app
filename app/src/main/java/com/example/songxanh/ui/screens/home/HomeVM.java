package com.example.songxanh.ui.screens.home;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.User;
import com.example.songxanh.ui.screens.home_update_weight.HomeUpdateWeightVM;
import com.example.songxanh.ui.screens.workout.WorkoutVM;
import com.example.songxanh.utils.GlobalMethods;
import com.github.mikephil.charting.data.PieEntry;
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
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class HomeVM extends ViewModel {
    private MutableLiveData<Boolean> isLoadingDocument = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> isLoadingLine = new MutableLiveData<>(null);
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private MutableLiveData<User> user = new MutableLiveData<>();

    private Integer steps = 0;
    private Float exerciseCalories = 0f;
    private Float foodCalories = 0f;
    private Float calories = 0f;
    private Float remaining = 1000f;
    private Float goal = 1000f;
    private Float startWeight = 0f;
    private Float goalWeight = 0f;
    private Float dailyCalories = 0f;
    private Integer weight = 0;

    // NEW: kcal tối thiểu theo mức vận động
    private Float activityLight = 0f;
    private Float activityModerate = 0f;
    private Float activityHeavy = 0f;

    ArrayList<CustomEntryLineChart> lineEntries;
    ArrayList<CustomEntryLineChart> lineEntries1;

    List<PieEntry> pieEntries;

    private WorkoutVM workoutVM;
    private final HomeUpdateWeightVM homeUpdateWeightVM = new HomeUpdateWeightVM(100);

    private Integer x = 0;
    private Integer x1 = 0;

    // ===== Hàm tiện ích =====
    private float num(DocumentSnapshot doc, String key) {
        if (!doc.contains(key)) return 0f;
        Object v = doc.get(key);
        if (v instanceof Number) return ((Number) v).floatValue();
        return 0f;
    }

    private float nn(float v) {
        if (Float.isNaN(v) || Float.isInfinite(v)) return 0f;
        if (Math.abs(v) < 1e-6f) return 0f;
        return v < 0f ? 0f : v;
    }

    // ===== Getter/Setter =====
    public ArrayList<CustomEntryLineChart> getLineEntries1() { return lineEntries1; }
    public void setLineEntries1(ArrayList<CustomEntryLineChart> lineEntries1) { this.lineEntries1 = lineEntries1; }

    public Float getCalories() { return calories; }
    public void setCalories(Float calories) { this.calories = nn(calories); }

    public Float getStartWeight() { return startWeight; }
    public void setStartWeight(Float startWeight) { this.startWeight = startWeight; }

    public Float getGoalWeight() { return goalWeight; }
    public void setGoalWeight(Float goalWeight) { this.goalWeight = goalWeight; }

    public Float getDailyCalories() { return dailyCalories; }
    public void setDailyCalories(Float dailyCalories) { this.dailyCalories = nn(dailyCalories); }

    public void setWeight(Integer weight) { this.weight = weight == null ? 0 : weight; }
    public Integer getWeight() { return weight; }

    public void getIsLoadingLine(MutableLiveData<Boolean> isLoadingLine) { this.isLoadingLine = isLoadingLine; }
    public MutableLiveData<Boolean> getIsLoadingLine() { return isLoadingLine; }

    public Float getRemaining() { return remaining; }
    public void setRemaining(Float remaining) { this.remaining = nn(remaining); }

    public List<PieEntry> getPieEntries() { return pieEntries; }
    public void setPieEntries(List<PieEntry> pieEntries) { this.pieEntries = pieEntries; }

    public ArrayList<CustomEntryLineChart> getLineEntries() { return lineEntries; }
    public void setLineEntries(ArrayList<CustomEntryLineChart> entries) { this.lineEntries = entries; }

    public Float getGoal() { return goal; }
    public void setGoal(Float goal) { this.goal = nn(goal); }

    public MutableLiveData<Boolean> getIsLoadingDocument() { return isLoadingDocument; }
    public void setIsLoadingDocument(MutableLiveData<Boolean> isLoadingDocument) { this.isLoadingDocument = isLoadingDocument; }

    public Float getExerciseCalories() { return exerciseCalories; }
    public Float getFoodCalories() { return foodCalories; }
    public void setExerciseCalories(Float exerciseCalories) { this.exerciseCalories = nn(exerciseCalories); }
    public void setFoodCalories(Float foodCalories) { this.foodCalories = nn(foodCalories); }

    public Integer getSteps() { return steps; }
    public void setSteps(Integer steps) { this.steps = steps == null ? 0 : steps; }

    // NEW: getters cho 3 mức vận động
    public Float getActivityLight() { return activityLight; }
    public Float getActivityModerate() { return activityModerate; }
    public Float getActivityHeavy() { return activityHeavy; }

    public HomeVM() {}

    // ===== Lưu dữ liệu bước chân hàng ngày =====
    public void saveDailySteps(int stepCount, Date previousDate) {
        Map<String, Object> dailyActivities = new HashMap<>();
        dailyActivities.put("steps", stepCount);

        String dateString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(previousDate);
        firestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("daily_activities").document(dateString)
                .update(dailyActivities)
                .addOnSuccessListener(aVoid -> Log.i("success", "Lưu bước chân thành công"))
                .addOnFailureListener(e -> Log.i("fail", "Lưu bước chân thất bại"));
    }

    // ===== Tải dữ liệu hoạt động hôm nay =====
    public void loadDocument() {
        isLoadingDocument.setValue(true);
        firestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("daily_activities")
                .document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        setSteps(0);
                        setWeight(0);
                        setCalories(0f);
                        setExerciseCalories(0f);
                        setFoodCalories(0f);
                        loadGoal();
                        return;
                    }

                    setSteps((int) num(documentSnapshot, "steps"));
                    setWeight((int) num(documentSnapshot, "weight"));
                    setCalories(num(documentSnapshot, "calories"));
                    setExerciseCalories(num(documentSnapshot, "exerciseCalories"));
                    setFoodCalories(num(documentSnapshot, "foodCalories"));
                    loadGoal();
                })
                .addOnFailureListener(e -> isLoadingDocument.setValue(false));
    }

    // ===== Tải mục tiêu và tính toán lượng calo =====
    public void loadGoal() {
        User u = user.getValue();
        if (u != null && u.getEmail() != null) {
            Log.i("User", String.valueOf(u.getEmail()));
        }

        firestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        setGoal(0f);
                        setDailyCalories(0f);
                        setStartWeight(0f);
                        setGoalWeight(0f);

                        // reset 3 mức vận động
                        activityLight = 0f;
                        activityModerate = 0f;
                        activityHeavy = 0f;

                        buildPieEntries();
                        isLoadingDocument.setValue(false);
                        return;
                    }

                    float g  = num(documentSnapshot, "dailyCalories");
                    float sw = num(documentSnapshot, "startWeight");
                    float gw = num(documentSnapshot, "goalWeight");
                    setGoal(g);
                    setDailyCalories(g);
                    setStartWeight(sw);
                    setGoalWeight(gw);

                    // NEW: tính kcal theo 3 mức vận động dựa trên cân nặng hiện tại
                    String gender = documentSnapshot.getString("gender");
                    Double height = documentSnapshot.getDouble("height");
                    Long ageL = documentSnapshot.getLong("age");

                    if (height != null && ageL != null && getWeight() > 0) {
                        boolean isMale = GlobalMethods.normalizeGenderPublic(gender);
                        double bmr = GlobalMethods.bmrMifflin(isMale, getWeight(), height, ageL.intValue());

                        // Các hệ số hoạt động thường dùng
                        double light    = GlobalMethods.tdee(bmr, 1.375); // nhẹ
                        double moderate = GlobalMethods.tdee(bmr, 1.55);  // vừa
                        double heavy    = GlobalMethods.tdee(bmr, 1.725); // nặng

                        activityLight = Math.max(0f, (float) Math.round(light));
                        activityModerate = Math.max(0f, (float) Math.round(moderate));
                        activityHeavy = Math.max(0f, (float) Math.round(heavy));
                    } else {
                        activityLight = 0f;
                        activityModerate = 0f;
                        activityHeavy = 0f;
                    }

                    float food = nn(getFoodCalories());
                    float ex   = nn(getExerciseCalories());
                    float rem  = nn(getGoal() - food + ex);
                    setRemaining(rem);

                    buildPieEntries();
                    isLoadingDocument.setValue(false);
                })
                .addOnFailureListener(e -> isLoadingDocument.setValue(false));
    }

    // ===== Xây dựng dữ liệu PieChart =====
    private void buildPieEntries() {
        float rem  = nn(getRemaining());
        float food = nn(getFoodCalories());
        float ex   = nn(getExerciseCalories());
        float g = nn(getGoal());

        if (g > 0f) {
            food = Math.min(food, g);
            ex   = Math.min(ex, g);
            rem  = Math.min(rem, g);
        }

        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(rem,  "Remaining"));
        pieEntries.add(new PieEntry(food, "Food"));
        pieEntries.add(new PieEntry(ex,   "Exercise"));
    }

    // ===== Tải dữ liệu vẽ LineChart bước chân =====
    public void loadLineData() {
        isLoadingLine.setValue(true);
        firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("daily_activities")
                .limit(7)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            isLoadingLine.setValue(false);
                            return;
                        }
                        QuerySnapshot qs = task.getResult();
                        lineEntries = new ArrayList<>();
                        lineEntries1 = new ArrayList<>();
                        x = 0; x1 = 0;

                        for (DocumentSnapshot document : qs.getDocuments()) {
                            if (document.contains("steps")) {
                                int steps = (int) num(document, "steps");
                                String date = document.getId();

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

                        for (CustomEntryLineChart celc : lineEntries) {
                            lineEntries1.add(new CustomEntryLineChart(x1, celc.getSteps(), celc.getDate()));
                            x1++;
                        }

                        isLoadingLine.setValue(false);
                    }
                });
    }

    // ===== Quản lý thông tin người dùng =====
    public MutableLiveData<User> getUser() { return user; }
    public void setUser(MutableLiveData<User> user) { this.user = user; }
}
