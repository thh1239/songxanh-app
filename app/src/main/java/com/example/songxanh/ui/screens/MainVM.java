package com.example.songxanh.ui.screens;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.data.models.User;
import com.example.songxanh.utils.FirebaseConstants;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

enum UserState { initial, loading, loaded, notHaveInformation, loadFailed }

public class MainVM extends ViewModel {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<UserState> state = new MutableLiveData<>(UserState.initial);

    public interface UserLoadCallback {
        void onUserLoaded(User user);
        void onUserNotHaveInformation();
    }

    public void loadUser(UserLoadCallback callback) {
        state.setValue(UserState.loading);

        FirebaseConstants.usersRef.document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user.setValue(document.toObject(NormalUser.class));
//                                if (callback != null) {
//                                    callback.onUserLoaded(user. getValue());
//                                }
                                state.setValue(UserState.loaded);
                            } else {
                                state.setValue(UserState.notHaveInformation);
//                                callback.onUserNotHaveInformation();
                            }
                        } else {
                            state.setValue(UserState.loadFailed);
                            Log.e("Load user failed", "", task.getException());
                        }
                    }
                });
    }

    public void updateUserProfileImage(Uri uri) {
        FirebaseConstants.usersRef.document(firebaseAuth.getCurrentUser().getUid()).update("imageUrl", uri)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            User copyUser = user.getValue();
                            copyUser.setImageUrl(uri.toString());
                            user.setValue(copyUser);
                        }
                    }
                });
    }

    public void updateKeyword(String newName) {
        FirebaseConstants.usersRef.document(firebaseAuth.getCurrentUser().getUid()).update("keyword", GlobalMethods.generateKeyword(newName));
    }

    public MutableLiveData<User> getUser() {
        return user;
    }

    public MutableLiveData<UserState> getState() {
        return state;
    }

    public String getUserImageUrl() {
        return user.getValue().getImageUrl();
    }
}