package com.example.songxanh.ui.screens.profile_change_goals;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.songxanh.R;
import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.data.models.User;
import com.example.songxanh.databinding.FragmentProfileBinding;
import com.example.songxanh.databinding.FragmentProfileChangeGoalsBinding;
import com.example.songxanh.ui.screens.MainVM;
import com.example.songxanh.ui.screens.home.HomeVM;
import com.example.songxanh.ui.screens.profile_calories_history.ProfileCaloriesHistoryFragment;
import com.example.songxanh.ui.screens.profile_change_noti_time.ProfileChangeNotiTimeVM;
import com.example.songxanh.utils.FirebaseConstants;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileChangeGoalsFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int day = 1;
    private int month = 1;
    private int year = 2000;
    private FragmentProfileChangeGoalsBinding binding;

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private ProfileChangeGoalsVM profileChangeGoalsVM;
    private MainVM mainVM;

    public ProfileChangeGoalsFragment() {

    }


    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileChangeGoalsVM = new ViewModelProvider(requireActivity()).get(ProfileChangeGoalsVM.class);
        mainVM = new ViewModelProvider(requireActivity()).get(MainVM.class);
        profileChangeGoalsVM.setUser(mainVM.getUser());
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileChangeGoalsBinding.inflate(inflater,container,false);
        binding.setViewModel(profileChangeGoalsVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        loadData();

        binding.updateGoalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onClick(View view) {
                Map<String, Object> data = new HashMap<>();
                String dateString = binding.timeGoalEdt.getText().toString().trim();
                Date date;
                try {
                    date = formatter.parse(dateString);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                int goalWeight = Integer.parseInt(binding.weightEdt.getText().toString());
                data.put("goalWeight", goalWeight);
                data.put("dailySteps", Integer.parseInt(binding.stepEdt.getText().toString().trim()));
                data.put("goalTime",  new Timestamp(date));
                NormalUser cloneUser = (NormalUser) mainVM.getUser().getValue();
                data.put("dailyCalories", GlobalMethods.calculateDailyCalories(cloneUser.getGender(), cloneUser.getStartWeight(), cloneUser.getHeight(), cloneUser.getAge(), goalWeight, cloneUser.getStartTime(), date));

                FirebaseConstants.usersRef
                        .document(profileChangeGoalsVM.getUser().getValue().getUid())
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "User data updated successfully", Toast.LENGTH_SHORT).show();
                                mainVM.loadUser(new MainVM.UserLoadCallback() {
                                    @Override
// == Tải dữ liệu và hiển thị lên UI ==
                                    public void onUserLoaded(User user) {

                                    }

                                    @Override
                                    public void onUserNotHaveInformation() {

                                    }
                                });
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
        });

        binding.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(ProfileChangeGoalsFragment.this);
                navController.popBackStack();
            }
        });

        binding.timeGoalEdt.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                binding.timeGoalEdt.setText(i2 + "/" + i1 + "/" + i);
                                day = i2;
                                month = i1;
                                year = i;
                            }
                        }, year, month, day
                );
                datePickerDialog.show();
            }
        });

        return binding.getRoot();

    }

    private void loadData() {
        String goalWeight = String.valueOf(((NormalUser) profileChangeGoalsVM.getUser().getValue()).getGoalWeight());
        binding.weightEdt.setText(goalWeight);
        String dailySteps = String.valueOf(((NormalUser) profileChangeGoalsVM.getUser().getValue()).getDailySteps());
        binding.stepEdt.setText(dailySteps);
        String dateStr = formatter.format(((NormalUser) profileChangeGoalsVM.getUser().getValue()).getGoalTime());
        binding.timeGoalEdt.setText(dateStr);
    }
}