package com.example.songxanh.ui.screens.forgot_password;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordVM extends ViewModel {
    private MutableLiveData<String> email = new MutableLiveData<>("");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private MutableLiveData<Boolean> isSent = new MutableLiveData<>(false);

    public void sendResetPasswordEmail() {
        if (!email.getValue().isEmpty()) {
            firebaseAuth.sendPasswordResetEmail(email.getValue())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                isSent.setValue(true);
                            } else {
                            }
                        }
                    });
        }
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public MutableLiveData<Boolean> getIsSent() {
        return isSent;
    }

    public void setIsSent(MutableLiveData<Boolean> isSent) {
        this.isSent = isSent;
    }
}
