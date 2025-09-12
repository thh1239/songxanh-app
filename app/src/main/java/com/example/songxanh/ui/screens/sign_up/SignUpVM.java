package com.example.songxanh.ui.screens.sign_up;

import static android.provider.Settings.System.getString;

import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignUpVM extends ViewModel {
    private MutableLiveData<String> email = new MutableLiveData<>();
    private MutableLiveData<String> password = new MutableLiveData<>();
    private MutableLiveData<String> confirmPassword = new MutableLiveData<>();
    private MutableLiveData<String> emailError = new MutableLiveData<>(null);
    private MutableLiveData<String> passwordError = new MutableLiveData<>(null);
    private MutableLiveData<String> confirmPasswordError = new MutableLiveData<>(null);
    private MutableLiveData<String> toastMessage = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> isSuccessful = new MutableLiveData<>(false);
    private static final String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";
    private Pattern pattern = Pattern.compile(passwordRegex);
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public void signUpWithEmailAndPassword() {
        validate();

        if (emailError.getValue() == null && passwordError.getValue() == null && confirmPasswordError.getValue() == null) {
            isLoading.setValue(true);
            firebaseAuth.createUserWithEmailAndPassword(email.getValue(), password.getValue()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        isLoading.setValue(false);
                        toastMessage.setValue("Sign up successfully");
                        isSuccessful.setValue(true);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isLoading.setValue(false);
                    toastMessage.setValue(e.getMessage());
                }
            });
        }
    }

    public void validateEmail() {
        if (email.getValue() == null || email.getValue().isEmpty()) {
            emailError.setValue("Email is not allowed empty");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getValue()).matches()) {
            emailError.setValue("Email is invalid.");
        } else {
            emailError.setValue(null);
        }
    }

    public void validatePassword() {
        if (password.getValue() == null || password.getValue().isEmpty()) {
            passwordError.setValue("Password is not allowed empty");
        } else {
            if (pattern.matcher(password.getValue()).matches()) {
                passwordError.setValue(null);
            } else {
                passwordError.setValue("Password must have more than 6 characters, includes char and number");
            }
        }
    }

    private void validateConfirmPassword() {
        if ( confirmPassword.getValue() == null || confirmPassword.getValue().isEmpty()) {
            confirmPasswordError.setValue("You must confirm password.");
        } else if (!confirmPassword.getValue().equals(password.getValue())) {
//            Log.i("Password", confirmPassword.getValue())
            confirmPasswordError.setValue("Confirm password must same with password");
        } else {
            confirmPasswordError.setValue(null);
        }
    }
    public void validate() {
        validateEmail();
        validatePassword();
        validateConfirmPassword();

    }

    public MutableLiveData<Boolean> getIsSuccessful() {
        return isSuccessful;
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

    public MutableLiveData<String> getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword.setValue(confirmPassword);
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

    public MutableLiveData<String> getConfirmPasswordError() {
        return confirmPasswordError;
    }

    public void setConfirmPasswordError(String confirmPasswordError) {
        this.confirmPasswordError.setValue(confirmPasswordError);
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
