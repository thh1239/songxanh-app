package com.example.songxanh.ui.screens.profile_edit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.data.models.User;
import com.example.songxanh.databinding.FragmentEditProfileBinding;
import com.example.songxanh.databinding.FragmentProfilePersonalInfoBinding;
import com.example.songxanh.ui.screens.MainVM;
import com.example.songxanh.ui.screens.profile_calories_history.ProfileCaloriesHistoryFragment;
import com.example.songxanh.ui.screens.profile_personal_info.ProfilePersonalInfoVM;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditProfileFragment extends Fragment {

    Boolean isValidName = true;
    Boolean isValidDay = true;
    Boolean isValidPhone = true;
    Boolean isValidAddress = true;
    private FragmentEditProfileBinding binding;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private EditProfileVM editProfileVM;
    private MainVM mainVM;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public EditProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editProfileVM = new ViewModelProvider(this).get(EditProfileVM.class);
        editProfileVM.getUserLiveData();
        mainVM = new ViewModelProvider(requireActivity()).get(MainVM.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        binding.setViewModel(editProfileVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        loadData();

        binding.editnameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newName = binding.editnameEdt.getText().toString();
                if (!newName.equals(editProfileVM.getUser().getName())) {
                    if (newName.isEmpty()) {
                        binding.tickIcon1.setVisibility(View.GONE);
                        isValidName = false;
                    } else {
                        binding.tickIcon1.setVisibility(View.VISIBLE);
                        isValidName = true;
                    }
                } else {
                    binding.tickIcon1.setVisibility(View.GONE);
                    isValidName = true;
                }
            }

        });
//        binding.editemailEdt.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String newEmail = binding.editemailEdt.getText().toString();
//                if (!newEmail.equals(user.getEmail())) {
//                    if (binding.editemailEdt.getText().toString().isEmpty()) {
//                        binding.tickIcon2.setVisibility(View.GONE);
//                        isValidEmail = false;
//                    } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.editemailEdt.getText().toString()).matches()) {
//                        binding.tickIcon2.setVisibility(View.GONE);
//                        isValidEmail = false;
//                    } else {
//                        binding.tickIcon2.setVisibility(View.VISIBLE);
//                        isValidEmail = true;
//                    }
//                } else {
//                    binding.tickIcon2.setVisibility(View.GONE);
//                    isValidEmail = true;
//                }
//
//            }
//        });
        binding.editdateEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String dateStr = binding.editdateEdt.getText().toString().trim();
                String userDate = formatter.format(editProfileVM.getUser().getDateOfBirth()).toString();
                if(!userDate.equals(dateStr)) {
                    if (!isValidDateFormat(dateStr)) {
                        binding.tickIcon3.setVisibility(View.GONE);
                        isValidDay = false;
                        return;
                    }

                    if (!isValidDate(dateStr)) {
                        binding.tickIcon3.setVisibility(View.GONE);
                        isValidDay = false;
                        return;
                    }

                    binding.tickIcon3.setVisibility(View.VISIBLE);
                    isValidDay = true;
                }
                else {
                    binding.tickIcon3.setVisibility(View.GONE);
                    isValidDay = false;
                }

            }
        });
        binding.editphoneEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newPhone = binding.editphoneEdt.getText().toString();
                if (!newPhone.equals(editProfileVM.getUser().getPhone())) {
                    String phone = binding.editphoneEdt.getText().toString();
                    if (!Pattern.matches("[0-9]+", phone) || phone.length() != 10) {
                        binding.tickIcon4.setVisibility(View.GONE);
                        isValidPhone = false;
                    } else {
                        binding.tickIcon4.setVisibility(View.VISIBLE);
                        isValidPhone = true;
                    }
                } else {
                    binding.tickIcon4.setVisibility(View.GONE);
                    isValidPhone = true;
                }
            }
        });
        binding.editaddressEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newAddress = binding.editaddressEdt.getText().toString();
                if (!newAddress.equals(editProfileVM.getUser().getAddress())) {
                    if (binding.editaddressEdt.getText().toString().isEmpty()) {
                        binding.tickIcon5.setVisibility(View.GONE);
                        isValidAddress = false;
                    } else {
                        binding.tickIcon5.setVisibility(View.VISIBLE);
                        isValidAddress = true;
                    }
                } else {
                    binding.tickIcon5.setVisibility(View.GONE);
                    isValidAddress = true;
                }
            }
        });

        binding.editprofileSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((binding.tickIcon1.getVisibility() == View.GONE
//                        && binding.tickIcon2.getVisibility() == View.GONE
                        && binding.tickIcon3.getVisibility() == View.GONE
                        && binding.tickIcon4.getVisibility() == View.GONE
                        && binding.tickIcon5.getVisibility() == View.GONE)) {
                    Toast.makeText(requireContext(), "Vui lòng nhập thông tin cần thay đổi", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isValidName || isValidDay || isValidPhone || isValidAddress) {
                    String dateString = binding.editdateEdt.getText().toString().trim();
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date date;
                    try {
                        date = format.parse(dateString);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    updateUserData(editProfileVM.getUser().getUid(),
                            binding.editnameEdt.getText().toString().trim(),
//                            binding.editemailEdt.getText().toString().trim(),
                            date,
                            binding.editphoneEdt.getText().toString().trim(),
                            binding.editaddressEdt.getText().toString().trim());
                } else {
                    Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ và đúng thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


        binding.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(EditProfileFragment.this);
                navController.popBackStack();
            }
        });
        return binding.getRoot();
    }

    private void loadData() {
        editProfileVM.getIsLoadingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoadingData) {
                if (isLoadingData != null && !isLoadingData) {
                    binding.editnameEdt.setText(editProfileVM.getUser().getName());
//        binding.editemailEdt.setText(((NormalUser) user).getEmail());
                    String dateStr = formatter.format(editProfileVM.getUser().getDateOfBirth());
                    binding.editdateEdt.setText(dateStr);
                    binding.editphoneEdt.setText(editProfileVM.getUser().getPhone());
                    binding.editaddressEdt.setText(editProfileVM.getUser().getAddress());
                } else {
//                    binding.editnameEdt.setText("");
////        binding.editemailEdt.setText(((NormalUser) user).getEmail());
//                    String dateStr = formatter.format("");
//                    binding.editdateEdt.setText(dateStr);
//                    binding.editphoneEdt.setText("");
//                    binding.editaddressEdt.setText("");
                }
            }
        });

    }

    public void updateUserData(String userId, String name, Date dateOfBirth, String phone, String address) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
//        data.put("email", email);
        data.put("dateOfBirth", new Timestamp(dateOfBirth));
        data.put("phone", phone);
        data.put("address", address);

        db.collection("users")
                .document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "User data updated successfully", Toast.LENGTH_SHORT).show();
                        binding.tickIcon1.setVisibility(View.GONE);
//                        binding.tickIcon2.setVisibility(View.GONE);
                        binding.tickIcon3.setVisibility(View.GONE);
                        binding.tickIcon4.setVisibility(View.GONE);
                        binding.tickIcon5.setVisibility(View.GONE);

                        mainVM.loadUser(new MainVM.UserLoadCallback() {
                            @Override
                            public void onUserLoaded(User user) {

                            }

                            @Override
                            public void onUserNotHaveInformation() {

                            }
                        });

                        if (!name.equals(mainVM.getUser().getValue().getName())) {
                            mainVM.updateKeyword(name);
                        }

//                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                        FirebaseUser user = mAuth.getCurrentUser();


//                        UserInfo userInfo = (UserInfo) user.getProviderData();
//
//                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), userInfo.getEmail()); // Replace password with the user's current password
//                        user.reauthenticate(credential)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        // User is re-authenticated, can now update email
//                                        user.updateEmail(binding.editemailEdt.getText().toString())
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        // Email update successful
//                                                    }
//                                                })
//                                                .addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        // Handle email update failure
//                                                    }
//                                                });
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        // Handle re-authentication failure
//                                    }
//                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Error updating user data", e);
                        Toast.makeText(getContext(), "Failed to update user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidDateFormat(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);

        try {
            format.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);

        try {
            Date currentDate = new Date();
            Date inputDate = format.parse(dateStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(inputDate);
            int year = calendar.get(Calendar.YEAR);
            if (year < 1900) {
                return false;
            }

            return !inputDate.after(currentDate);
        } catch (ParseException e) {
            return false;
        }
    }
}