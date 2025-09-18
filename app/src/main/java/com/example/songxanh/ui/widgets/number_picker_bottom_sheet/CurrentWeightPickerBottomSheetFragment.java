package com.example.songxanh.ui.widgets.number_picker_bottom_sheet;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.databinding.FragmentCurrentWeightPickerBottomSheetBinding;
import com.example.songxanh.ui.screens.fill_in_personal_information.FillInPersonalInformationVM;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CurrentWeightPickerBottomSheetFragment extends BottomSheetDialogFragment {
    private FragmentCurrentWeightPickerBottomSheetBinding binding;
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

        binding =  FragmentCurrentWeightPickerBottomSheetBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        personalInformationVM =  new ViewModelProvider(requireActivity()).get(FillInPersonalInformationVM.class);

        String currentValue = personalInformationVM.getCurrentWeight().getValue();

        binding.numberPicker.setMinValue(1);
        binding.numberPicker.setMaxValue(100);
        binding.numberPicker.setValue(Integer.valueOf(currentValue));
        binding.numberPicker.setWrapSelectorWheel(false);

        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                String newValue = String.valueOf(binding.numberPicker.getValue());
                personalInformationVM.setCurrentWeight(newValue);
                dismiss();
            }
        });

        return binding.getRoot();
    }
}