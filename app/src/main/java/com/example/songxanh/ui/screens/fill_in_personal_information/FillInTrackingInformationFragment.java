package com.example.songxanh.ui.screens.fill_in_personal_information;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.songxanh.R;
import com.example.songxanh.data.models.User;
import com.example.songxanh.databinding.FragmentFillInTrackingInformationBinding;
import com.example.songxanh.ui.screens.MainActivity;
import com.example.songxanh.ui.screens.MainVM;

import java.util.Calendar;

public class FillInTrackingInformationFragment extends Fragment {

    private FragmentFillInTrackingInformationBinding binding;
    private FillInPersonalInformationVM viewModel;
    private int day;
    private int month;
    private int year;
    private MainVM mainVM;

    public FillInTrackingInformationFragment() {
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH) + 1;
        year = c.get(Calendar.YEAR);
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainVM = new ViewModelProvider(requireActivity()).get(MainVM.class);

    }

    @Override
// == Tạo hoặc xử lý Intent để chuyển màn hình ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFillInTrackingInformationBinding.inflate(inflater, container, false);

        viewModel =  new ViewModelProvider(requireActivity()).get(FillInPersonalInformationVM.class);

        binding.setPersonalInformationVM(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setOnClick();

        viewModel.getIsSuccess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
// == Tạo hoặc xử lý Intent để chuyển màn hình ==
            public void onChanged(Boolean isSuccess) {
                if (isSuccess == true) {
                    NavHostFragment.findNavController(FillInTrackingInformationFragment.this).navigate(R.id.homeFragment);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
// == Quản lý dữ liệu bằng ViewModel ==
            public void onChanged(String s) {
                if (s != null) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }

    private void setOnClick() {
        binding.goalTimeEdt.setOnClickListener(new View.OnClickListener() {
            @Override
// == Quản lý dữ liệu bằng ViewModel ==
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
// == Quản lý dữ liệu bằng ViewModel ==
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                                viewModel.setGoalTime(i2 + "/" + i1 + "/" + i);
                                day = i2;
                                month = i1;
                                year = i;
                            }
                        }, year, month, day
                );
                datePickerDialog.show();
            }
        });

        binding.currentWeightEdt.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavHostFragment.findNavController(FillInTrackingInformationFragment.this).navigate(R.id.currentWeightPickerBottomSheetFragment);
            }
        });

        binding.currentHeightEdt.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavHostFragment.findNavController(FillInTrackingInformationFragment.this).navigate(R.id.currentHeightPickerBottomSheetFragment);
            }
        });

        binding.goalWeightEdt.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavHostFragment.findNavController(FillInTrackingInformationFragment.this).navigate(R.id.goalWeightPickerBottomSheetFragment);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}