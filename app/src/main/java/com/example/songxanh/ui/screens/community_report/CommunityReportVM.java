package com.example.songxanh.ui.screens.community_report;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.data.models.User;
import com.example.songxanh.utils.FirebaseConstants;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CommunityReportVM extends ViewModel {
    private MutableLiveData<String> title = new MutableLiveData<>("");
    private MutableLiveData<String> description = new MutableLiveData<>("");
    private Achievement achievement;
    private User user;
    private MutableLiveData<String> message = new MutableLiveData<>("");

    public CommunityReportVM() {
        achievement = null;
        user = null;
    }
// == Tính toán và hiển thị tổng calo ==

    public void sendReport() {
        if (user != null && achievement != null) {
            HashMap<String, Object> newReport = new HashMap<>();
            newReport.put("achievementId", achievement.getId());
            newReport.put("achievementUserId", achievement.getUserId());
            newReport.put("achievementUserName", achievement.getUserName());
            newReport.put("achievementUserImageUrl", achievement.getUserImageUrl());
            newReport.put("achievementSteps", achievement.getSteps());
            newReport.put("achievementExerciseCalories", achievement.getExerciseCalories());
            newReport.put("achievementFoodCalories", achievement.getFoodCalories());
            newReport.put("achievementCalories", achievement.getCalories());
            newReport.put("achievementCreatedTime", achievement.getCreatedTime());
            newReport.put("reportUserId", user.getUid());
            newReport.put("reportUserName", user.getName());
            newReport.put("reportUserImageUrl", user.getImageUrl());
            newReport.put("title", title.getValue());
            newReport.put("description", description.getValue());
            newReport.put("activitiesId", GlobalMethods.convertDateToHyphenSplittingFormat(achievement.getCreatedTime()));

            FirebaseConstants.reportsRef.add(newReport)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
// == Tương tác với dịch vụ Firebase ==
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                message.setValue("Chúng tôi đã gửi báo cáo này cho quản trị viên. Cảm ơn bạn đã đóng góp!");
                                title.setValue("");
                                description.setValue("");

                                updatePendingReportCount();
                            } else {
                                message.setValue("Something went wrong. Please try again");
                            }
                        }
                    });
        } else {
            message.setValue("Something went wrong. Please try again");
        }
    }
// == Tương tác với dịch vụ Firebase ==

    
    public void updatePendingReportCount() {
        final DocumentReference countDocumentRef =
                FirebaseFirestore.getInstance().collection("count").document("reports_count");

        countDocumentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    Map<String, Object> init = new HashMap<>();
                    init.put("count", 1);
                    countDocumentRef.set(init, SetOptions.merge());
                    return;
                }

                Long current = documentSnapshot.getLong("count");
                if (current == null) current = 0L;

                if (current < 0L) {

                    Map<String, Object> fix = new HashMap<>();
                    fix.put("count", 0);
                    countDocumentRef.set(fix, SetOptions.merge())
                            .addOnSuccessListener(unused -> countDocumentRef.update("count", FieldValue.increment(1)));
                } else {

                    countDocumentRef.update("count", FieldValue.increment(1));
                }
            }
        });
    }


    public MutableLiveData<String> getTitle() {
        return title;
    }

    public void setTitle(MutableLiveData<String> title) {
        this.title = title;
    }

    public MutableLiveData<String> getDescription() {
        return description;
    }

    public void setDescription(MutableLiveData<String> description) {
        this.description = description;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
}
