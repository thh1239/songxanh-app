package com.example.songxanh.ui.screens.sign_in;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInVM extends ViewModel {
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> emailError = new MutableLiveData<>(null);
    private MutableLiveData<String> passwordError = new MutableLiveData<>(null);
    private MutableLiveData<String> toastMessage = new MutableLiveData<>(null);
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private MutableLiveData<Boolean> signInSuccess = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public void signInWithEmailAndPassword() {
        String emailInput = email.getValue();
        String passwordInput = password.getValue();

        boolean hasError = false;

        if (emailInput == null || emailInput.trim().isEmpty()) {
            emailError.setValue("Email không được để trống");
            hasError = true;
        } else {
            emailError.setValue(null);
        }

        if (passwordInput == null || passwordInput.trim().isEmpty()) {
            passwordError.setValue("Mật khẩu không được để trống");
            hasError = true;
        } else {
            passwordError.setValue(null);
        }

        if (hasError) {
            toastMessage.setValue("Vui lòng kiểm tra lại thông tin đăng nhập");
            return;
        }

        isLoading.setValue(true);
        firebaseAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(task -> {
                    isLoading.setValue(false);
                    if (task.isSuccessful()) {
                        toastMessage.setValue("Đăng nhập thành công");
                        signInSuccess.setValue(true);
                    }
                })
                .addOnFailureListener(e -> {
                    isLoading.setValue(false);
                    toastMessage.setValue("Đăng nhập thất bại: " + e.getMessage());
                });
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email.setValue(email);
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public MutableLiveData<String> getEmailError() {
        return emailError;
    }

    public void setEmailError(String emailError) {
        this.emailError.setValue(emailError);
    }

    public MutableLiveData<String> getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError.setValue(passwordError);
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public MutableLiveData<Boolean> getSignInSuccess() {
        return signInSuccess;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
