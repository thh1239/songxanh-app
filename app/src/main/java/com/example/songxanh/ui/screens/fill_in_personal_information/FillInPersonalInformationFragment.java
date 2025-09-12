package com.example.songxanh.ui.screens.fill_in_personal_information;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentFillInPersonalInformationBinding;

import java.util.Calendar;

public class FillInPersonalInformationFragment extends Fragment {
    private FragmentFillInPersonalInformationBinding binding;
    private FillInPersonalInformationVM viewModel;
    private int day = 1;
    private int month = 1;
    private int year = 2000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFillInPersonalInformationBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(FillInPersonalInformationVM.class);
        binding.setPersonalInformationVM(viewModel);
        binding.setLifecycleOwner(this);

        setOnClick();


        return binding.getRoot();
    }

    private void setOnClick() {
        binding.birthdateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();


                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                binding.birthdateEdt.setText(i2 + "/" + i1 + "/" + i);
                                day = i2;
                                month = i1;
                                year = i;
                            }
                        }, year, month, day
                );
                datePickerDialog.show();
            }
        });

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FillInPersonalInformationFragment.this).navigate(R.id.fillInTrackingInformationFragment);
            }
        });
    }
}