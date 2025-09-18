package com.example.songxanh.ui.screens.profile_change_pass;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentProfileChangePassBinding;
import com.example.songxanh.ui.screens.home.HomeVM;
import com.example.songxanh.ui.screens.profile_calories_history.ProfileCaloriesHistoryFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileChangePassFragment extends Fragment {
    private FragmentProfileChangePassBinding binding;
    private TextInputEditText currentPasswordEditText;
    private TextInputEditText newPasswordEditText;
    private TextInputEditText confirmNewPasswordEditText;
    private AppCompatButton updateButton;
    private ProfileChangePassVM profileChangePassVM;

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileChangePassVM = new ViewModelProvider(requireActivity()).get(ProfileChangePassVM.class);
        profileChangePassVM.getUserLiveData();
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileChangePassBinding.inflate(inflater,container,false);
        binding.setViewModel(profileChangePassVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.updatePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                if (TextUtils.isEmpty(binding.currentPasswordEdt.getText().toString())) {
                    binding.currentPasswordEdt.setError("Please enter current password");
                    binding.currentPasswordEdt.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(binding.newpassEdt.getText().toString())) {
                    binding.newpassEdt.setError("Please enter new password");
                    binding.newpassEdt.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(binding.confirmNewpassEdt.getText().toString())) {
                    binding.confirmNewpassEdt.setError("Please enter confirm new password");
                    binding.confirmNewpassEdt.requestFocus();
                    return;
                }

                if (!binding.newpassEdt.getText().toString().equals(binding.confirmNewpassEdt.getText().toString())) {
                    binding.confirmNewpassEdt.setError("New password and confirm new password must match");
                    binding.confirmNewpassEdt.requestFocus();
                    Log.d("TAG",binding.newpassEdt.getText().toString() );
                    Log.d("TAG",binding.confirmNewpassEdt.getText().toString() );
                    return;
                }

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();

                String password = binding.currentPasswordEdt.getText().toString();
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String newPassword = binding.newpassEdt.getText().toString();
                        user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                DocumentReference userRef = db.collection("users").document(user.getUid());
                                userRef.update("password", newPassword)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Log.e("TAG", "Error updating password", updateTask.getException());
                            }
                        });
                    } else {
                        binding.currentPasswordEdt.setError("Wrong current password");
                        binding.currentPasswordEdt.requestFocus();
                    }
                });


            }
        });
        binding.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(ProfileChangePassFragment.this);
                navController.popBackStack();
            }
        });
        return binding.getRoot();
    }
}