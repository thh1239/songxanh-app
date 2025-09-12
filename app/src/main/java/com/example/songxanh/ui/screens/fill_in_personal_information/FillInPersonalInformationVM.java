package com.example.songxanh.ui.screens.fill_in_personal_information;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.DailyActivity;
import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
    private MutableLiveData<String> gender = new MutableLiveData<>("Male");
    private MutableLiveData<String> goalWeight = new MutableLiveData<>("75");
    private MutableLiveData<String> goalTime = new MutableLiveData<>();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private MutableLiveData<Boolean> isSuccess = new MutableLiveData<>(false);


    private MutableLiveData<String> message = new MutableLiveData<>(null);

    public void pushUserDataToDB() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = goalTime.getValue();
        String birthdateString = birthdate.getValue();
        Date goalDate = null;
        Date birthdate = null;

        if (dateString == null || dateString.trim().isEmpty()) {
            message.setValue("Vui lòng chọn thời gian đạt mục tiêu");
            return;
        }

        if (birthdateString == null || birthdateString.trim().isEmpty()) {
            message.setValue("Vui lòng nhập ngày sinh");
            return;
        }
        try {
             goalDate = formatter.parse(dateString);
             birthdate = formatter.parse(birthdateString);
        } catch (ParseException e) {
            System.out.println("Failed to parse date: " + dateString);
            e.printStackTrace();
        }

        if (goalDate != null) {
            NormalUser newUser = new NormalUser(
                    "",
                    firebaseAuth.getCurrentUser().getEmail(),
                    name.getValue(),
                    "",
                    phone.getValue(),
                    birthdate,
                    address.getValue(),
                    gender.getValue(),
                    Integer.valueOf(currentWeight.getValue()),
                    Integer.valueOf(goalWeight.getValue()),
                    new Date(),
                    goalDate,
                    GlobalMethods.calculateDailyCalories(gender.getValue(), Integer.valueOf(currentWeight.getValue()), Double.valueOf(currentHeight.getValue()), Integer.valueOf(age.getValue()), Integer.valueOf(goalWeight.getValue()),new Date(), goalDate),
                    10000, // default as specialist recommend
                    new ArrayList<String>(),
                    new ArrayList<String>(),
                    new DailyActivity(),
                    GlobalMethods.generateKeyword(name.getValue())
                    );

            String uid = firebaseAuth.getCurrentUser().getUid();
            newUser.setUid(uid);
            firestore.collection("users").document(uid).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        isSuccess.setValue(true);
                        message.setValue("Sign up success!");
                    } else {
                        message.setValue("Sign up failed!");
                    }
                }
            });

//            firestore.collection("users").add(newUser).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentReference> task) {
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    message.setValue(e.getMessage());
//                }
//            });
        }
    }

    // Getters and Setters
    public MutableLiveData<String> getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(String currentWeight) {
        this.currentWeight.setValue(currentWeight);
        Log.i("CURRENT WEIGHT", this.currentWeight.getValue());
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public MutableLiveData<String> getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(MutableLiveData<String> birthdate) {
        this.birthdate = birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate.setValue(birthdate);
    }

    public MutableLiveData<String> getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.setValue(phone);
    }

    public MutableLiveData<String> getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address.setValue(address);
    }

    public MutableLiveData<String> getCurrentHeight() {
        return currentHeight;
    }

    public void setCurrentHeight(String currentHeight) {
        this.currentHeight.setValue(currentHeight);
    }

    public MutableLiveData<String> getAge() {
        return age;
    }

    public void setCurrentAge(String age) {
        this.age.setValue(age);
    }

    public MutableLiveData<String> getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.setValue(gender);
    }

    public MutableLiveData<String> getGoalTime() {
        return goalTime;
    }

    public void setGoalTime(String goalTime) {
        this.goalTime.setValue(goalTime);
    }

    public MutableLiveData<String> getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(String goalWeight) {
        this.goalWeight.setValue(goalWeight);
    }

    public MutableLiveData<Boolean> getIsSuccess() {
        return isSuccess;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
