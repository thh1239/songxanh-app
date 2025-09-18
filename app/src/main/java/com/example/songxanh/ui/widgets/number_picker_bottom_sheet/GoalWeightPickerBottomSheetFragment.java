package com.example.songxanh.ui.widgets.number_picker_bottom_sheet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentCurrentWeightPickerBottomSheetBinding;
import com.example.songxanh.databinding.FragmentGoalWeightPickerBottomSheetBinding;
import com.example.songxanh.ui.screens.fill_in_personal_information.FillInPersonalInformationVM;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class GoalWeightPickerBottomSheetFragment extends BottomSheetDialogFragment {
    private FragmentGoalWeightPickerBottomSheetBinding binding;
    private FillInPersonalInformationVM personalInformationVM;


    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentGoalWeightPickerBottomSheetBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        personalInformationVM =  new ViewModelProvider(requireActivity()).get(FillInPersonalInformationVM.class);

        String currentValue = personalInformationVM.getGoalWeight().getValue();

        binding.numberPicker.setMinValue(1);
        binding.numberPicker.setMaxValue(100);
        binding.numberPicker.setValue(Integer.valueOf(currentValue));
        binding.numberPicker.setWrapSelectorWheel(false);

        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                String newValue = String.valueOf(binding.numberPicker.getValue());
                personalInformationVM.setGoalWeight(newValue);
                dismiss();
            }
        });

        return binding.getRoot();
    }
}