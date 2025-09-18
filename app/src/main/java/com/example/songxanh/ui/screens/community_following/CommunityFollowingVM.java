package com.example.songxanh.ui.screens.community_following;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.FollowingActivity;
import com.example.songxanh.utils.FirebaseConstants;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CommunityFollowingVM extends ViewModel {
    private List<String> followers;
    private MutableLiveData<List<FollowingActivity>> activities = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
// == Tính toán và hiển thị tổng calo ==

    public void loadActivities() {
        List<FollowingActivity> newActivities = new ArrayList<>();

        isLoading.setValue(true);
        for (String follower : followers) {
            FollowingActivity newActivity = new FollowingActivity();
            final AtomicBoolean isLoadedUserInfo = new AtomicBoolean(false);
            final AtomicBoolean isLoadedCaloriesInfo = new AtomicBoolean(false);
            final AtomicInteger numberOfLoadedItem = new AtomicInteger(0);

            FirebaseConstants.usersRef.document(follower).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    newActivity.setUserId(follower);
                                    newActivity.setUserName(document.get("name").toString());
                                    Object imageUrl = document.get("imageUrl");
                                    if (imageUrl != null) {
                                        newActivity.setUserAvatarUrl(imageUrl.toString());
                                    } else {
                                        newActivity.setUserAvatarUrl(null);
                                    }

                                    isLoadedUserInfo.set(true);
                                    if (isLoadedCaloriesInfo.equals(true)) { // if calories info is loaded, it means that 2 process is completed, so add this item to list
                                        newActivities.add(newActivity);
                                        numberOfLoadedItem.incrementAndGet();
                                    }

                                    if (numberOfLoadedItem.equals(followers.size())) { // if number of loaded item equal number of followers, it means that we loaded all item successfully, so set value for the activities property
                                        activities.setValue(newActivities);
                                        isLoading.setValue(false);
                                    }
                                }
                            }
                        }
                    });

            FirebaseConstants.usersRef.document(follower)
                    .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
// == Tính toán và hiển thị tổng calo ==
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Double steps = document.getDouble("steps");
                                    if (steps != null) {
                                        newActivity.setSteps(steps.intValue());
                                    } else {
                                        newActivity.setSteps(0);
                                    }
                                    newActivity.setExerciseCalories(document.getLong("exerciseCalories").intValue());
                                    newActivity.setFoodCalories(document.getLong("foodCalories").intValue());
                                    newActivity.setTotalCalories(document.getLong("calories").intValue());

                                    isLoadedCaloriesInfo.set(true);
                                    if (isLoadedUserInfo.get() == true) {
                                        newActivities.add(newActivity);
                                        numberOfLoadedItem.incrementAndGet();
                                    }

                                    if (numberOfLoadedItem.get() == (followers.size())) {
                                        activities.setValue(newActivities);
                                        isLoading.setValue(false);
                                    }
                                }
                            }
                        }
                    });
        }
    }

    public MutableLiveData<List<FollowingActivity>> getActivities() {
        return activities;
    }

    public void setActivities(MutableLiveData<List<FollowingActivity>> activities) {
        this.activities = activities;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
        loadActivities();
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
