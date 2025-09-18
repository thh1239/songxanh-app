package com.example.songxanh.ui.screens.fill_in_personal_information;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.DailyActivity;
import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FillInPersonalInformationVM extends ViewModel {

    private MutableLiveData<String> name = new MutableLiveData<>();
    private MutableLiveData<String> birthdate = new MutableLiveData<>("");
    private MutableLiveData<String> phone = new MutableLiveData<>();
    private MutableLiveData<String> address = new MutableLiveData<>();
    private MutableLiveData<String> currentWeight = new MutableLiveData<>("60");
    private MutableLiveData<String> currentHeight = new MutableLiveData<>("175");
    private MutableLiveData<String> age = new MutableLiveData<>("18");
    private MutableLiveData<String> gender = new MutableLiveData<>("Nam");
    private MutableLiveData<String> goalWeight = new MutableLiveData<>("75");
    private MutableLiveData<String> goalTime = new MutableLiveData<>();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> isSuccess = new MutableLiveData<>(false);
    private MutableLiveData<String> message = new MutableLiveData<>(null);

    private Integer toInt(String s) {
        try { return s == null ? null : Integer.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
    private Double toDouble(String s) {
        try { return s == null ? null : Double.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
    private float nn(float v) {
        if (Float.isNaN(v) || Float.isInfinite(v)) return 0f;
        if (Math.abs(v) < 1e-6f) return 0f;
        return v < 0f ? 0f : v;
    }
    private String normGender(String g) {
        if (g == null) return "Male";
        String x = g.trim().toLowerCase();
        if (x.startsWith("m") || x.equals("nam")) return "Nam";
        if (x.startsWith("f") || x.equals("nữ") || x.equals("nu")) return "Nữ";
        return "Nam";
    }
    private boolean isFuture(Date d) {
        return d != null && d.after(new Date());
    }

    public void pushUserDataToDB() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        String nameStr = name.getValue();
        String phoneStr = phone.getValue();
        String addressStr = address.getValue();
        String birthdateStr = birthdate.getValue();
        String genderStr = normGender(gender.getValue());
        String goalTimeStr = goalTime.getValue();

        Integer curWeight = toInt(currentWeight.getValue());
        Double curHeight = toDouble(currentHeight.getValue());
        Integer ageYears = toInt(age.getValue());
        Integer goalWeightKg = toInt(goalWeight.getValue());

        if (goalTimeStr == null || goalTimeStr.trim().isEmpty()) {
            message.setValue("Vui lòng chọn thời gian đạt mục tiêu");
            return;
        }
        if (birthdateStr == null || birthdateStr.trim().isEmpty()) {
            message.setValue("Vui lòng nhập ngày sinh");
            return;
        }
        if (curWeight == null || curWeight <= 0) {
            message.setValue("Vui lòng nhập cân nặng hiện tại hợp lệ (> 0)");
            return;
        }
        if (curHeight == null || curHeight <= 0) {
            message.setValue("Vui lòng nhập chiều cao hợp lệ (> 0)");
            return;
        }
        if (ageYears == null || ageYears < 10 || ageYears > 100) {
            message.setValue("Vui lòng nhập tuổi hợp lệ (10–100)");
            return;
        }
        if (goalWeightKg == null || goalWeightKg <= 0) {
            message.setValue("Vui lòng nhập cân nặng mục tiêu hợp lệ (> 0)");
            return;
        }

        Date goalDate = null;
        Date birthdateParsed = null;
        try {
            goalDate = formatter.parse(goalTimeStr);
            birthdateParsed = formatter.parse(birthdateStr);
        } catch (ParseException e) {
            message.setValue("Sai định dạng ngày (dd/MM/yyyy)");
            return;
        }
        if (!isFuture(goalDate)) {
            message.setValue("Thời gian đạt mục tiêu phải ở tương lai");
            return;
        }

        double daily = 0d;
        try {
            daily = GlobalMethods.calculateDailyCalories(
                    genderStr,
                    curWeight,
                    curHeight,
                    ageYears,
                    goalWeightKg,
                    new Date(),
                    goalDate
            );
        } catch (Exception e) {
            Log.e("dailyCalories", "Lỗi tính dailyCalories", e);
            daily = 0d;
        }
        float dailyCaloriesVal = nn((float) daily);

        NormalUser newUser = new NormalUser(
                "",
                firebaseAuth.getCurrentUser().getEmail(),
                nameStr,
                "",
                phoneStr,
                birthdateParsed,
                addressStr,
                genderStr,
                curWeight,
                goalWeightKg,
                new Date(),
                goalDate,
                dailyCaloriesVal,
                10000,
                new ArrayList<>(),
                new ArrayList<>(),
                new DailyActivity(),
                com.example.songxanh.utils.GlobalMethods.generateKeyword(nameStr)
        );

        String uid = firebaseAuth.getCurrentUser().getUid();
        newUser.setUid(uid);

        firestore.collection("users").document(uid).set(newUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isSuccess.setValue(true);
                            message.setValue("Lưu thông tin thành công!");
                        } else {
                            isSuccess.setValue(false);
                            message.setValue("Lưu thông tin thất bại!");
                        }
                    }
                });
    }

    public MutableLiveData<String> getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(String currentWeight) { this.currentWeight.setValue(currentWeight); }

    public MutableLiveData<String> getName() { return name; }
    public void setName(String name) { this.name.setValue(name); }

    public MutableLiveData<String> getBirthdate() { return birthdate; }
    public void setBirthdate(MutableLiveData<String> birthdate) { this.birthdate = birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate.setValue(birthdate); }

    public MutableLiveData<String> getPhone() { return phone; }
    public void setPhone(String phone) { this.phone.setValue(phone); }
// == Thêm mới dữ liệu hoặc item ==

    public MutableLiveData<String> getAddress() { return address; }
// == Thêm mới dữ liệu hoặc item ==
    public void setAddress(String address) { this.address.setValue(address); }

    public MutableLiveData<String> getCurrentHeight() { return currentHeight; }
    public void setCurrentHeight(String currentHeight) { this.currentHeight.setValue(currentHeight); }

    public MutableLiveData<String> getAge() { return age; }
    public void setCurrentAge(String age) { this.age.setValue(age); }

    public MutableLiveData<String> getGender() { return gender; }
    public void setGender(String gender) { this.gender.setValue(gender); }

    public MutableLiveData<String> getGoalTime() { return goalTime; }
    public void setGoalTime(String goalTime) { this.goalTime.setValue(goalTime); }

    public MutableLiveData<String> getGoalWeight() { return goalWeight; }
    public void setGoalWeight(String goalWeight) { this.goalWeight.setValue(goalWeight); }

    public MutableLiveData<Boolean> getIsSuccess() { return isSuccess; }
    public MutableLiveData<String> getMessage() { return message; }
}
