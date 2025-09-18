package com.example.songxanh.ui.screens.community_user_profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.utils.FirebaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommunityUserProfileVM extends ViewModel {
    private MutableLiveData<NormalUser> user = new MutableLiveData<>();
    private MutableLiveData<List<Achievement>> achievements = new MutableLiveData<>();
    private MutableLiveData<Integer> numberOfAchievements = new MutableLiveData<>(0);
    private FirebaseAuth auth = FirebaseAuth.getInstance();
// == Tương tác với dịch vụ Firebase ==

    public void loadUser(String uid) {
        FirebaseConstants.usersRef.document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
// == Tương tác với dịch vụ Firebase ==
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            user.setValue(task.getResult().toObject(NormalUser.class));
                        }
                    }
                });
    }
// == Tương tác với dịch vụ Firebase ==

    public void loadAchievements(String uid) {
        FirebaseConstants.achievementsRef.whereEqualTo("userId", uid).get()
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
                            numberOfAchievements.setValue(achievements.getValue().size());
                        }
                    }
                });
    }
// == Tương tác với dịch vụ Firebase ==

    public void follow() {
        if (user.getValue().getFollowers().contains(auth.getCurrentUser().getUid())) {
            FirebaseConstants.usersRef.document(auth.getCurrentUser().getUid()).update("followers", FieldValue.arrayRemove(auth.getCurrentUser().getUid()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
// == Tương tác với dịch vụ Firebase ==
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                NormalUser temp = user.getValue();
                                List<String> followers = temp.getFollowers();
                                followers.remove(auth.getCurrentUser().getUid());
                                temp.setFollowers(followers);
                                user.setValue(temp);
                            }
                        }
                    });
        } else {
            FirebaseConstants.usersRef.document(auth.getCurrentUser().getUid()).update("followers", FieldValue.arrayUnion(auth.getCurrentUser().getUid()))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                NormalUser temp = user.getValue();
                                List<String> followers = temp.getFollowers();
                                followers.add(auth.getCurrentUser().getUid());
                                temp.setFollowers(followers);
                                user.setValue(temp);
                            }
                        }
                    });
        }
    }

    public MutableLiveData<NormalUser> getUser() {
        return user;
    }

    public void setUser(MutableLiveData<NormalUser> user) {
        this.user = user;
    }

    public MutableLiveData<List<Achievement>> getAchievements() {
        return achievements;
    }

    public void setAchievements(MutableLiveData<List<Achievement>> achievements) {
        this.achievements = achievements;
    }

    public MutableLiveData<Integer> getNumberOfAchievements() {
        return numberOfAchievements;
    }

    public void setNumberOfAchievements(MutableLiveData<Integer> numberOfAchievements) {
        this.numberOfAchievements = numberOfAchievements;
    }
}
