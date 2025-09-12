package com.example.songxanh.ui.widgets.number_picker_bottom_sheet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentCurrentHeightPickerBottomSheetBinding;
import com.example.songxanh.ui.screens.fill_in_personal_information.FillInPersonalInformationVM;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CurrentHeightPickerBottomSheetFragment extends BottomSheetDialogFragment {
    private FragmentCurrentHeightPickerBottomSheetBinding binding;
    private FillInPersonalInformationVM personalInformationVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentCurrentHeightPickerBottomSheetBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        personalInformationVM =  new ViewModelProvider(requireActivity()).get(FillInPersonalInformationVM.class);

        String currentValue = personalInformationVM.getCurrentHeight().getValue();

        // Set value for number picker
        binding.numberPicker.setMinValue(100);
        binding.numberPicker.setMaxValue(200);
        binding.numberPicker.setValue(Integer.valueOf(currentValue));
        binding.numberPicker.setWrapSelectorWheel(false);

        binding.doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newValue = String.valueOf(binding.numberPicker.getValue());
                personalInformationVM.setCurrentHeight(newValue);
                dismiss();
            }
        });

        return binding.getRoot();
    }
}