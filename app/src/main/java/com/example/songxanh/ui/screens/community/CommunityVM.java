package com.example.songxanh.ui.screens.community;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.utils.FirebaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommunityVM extends ViewModel {
    private MutableLiveData<List<Achievement>> achievements = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingAchievements = new MutableLiveData<>(false);

    public CommunityVM() {
        loadAchievements();
    }
// == Tương tác với dịch vụ Firebase ==

    public void loadAchievements() {
        isLoadingAchievements.setValue(true);

        FirebaseConstants.achievementsRef.orderBy("createdTime", Query.Direction.DESCENDING).limit(20).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Achievement> newAchievements = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                Achievement newAchievement;
                                newAchievement = doc.toObject(Achievement.class);
                                newAchievement.setId(doc.getId());
                                newAchievements.add(newAchievement);
                            }
                            achievements.setValue(newAchievements);

                            isLoadingAchievements.setValue(false);
                        }
                    }
                });
    }


    public MutableLiveData<List<Achievement>> getAchievements() {
        return achievements;
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public MutableLiveData<Boolean> getIsLoadingAchievements() {
        return isLoadingAchievements;
    }

}
