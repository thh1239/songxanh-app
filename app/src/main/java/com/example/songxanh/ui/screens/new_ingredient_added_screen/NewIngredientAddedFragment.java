package com.example.songxanh.ui.screens.new_ingredient_added_screen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.songxanh.databinding.FragmentNewIngredientAddedBinding;
import com.example.songxanh.ui.screens.add_personal_ingredient.AddPersonalIngredientVM;
import com.example.songxanh.utils.GlobalMethods;

public class NewIngredientAddedFragment extends Fragment {

    FragmentNewIngredientAddedBinding binding;
    AddPersonalIngredientVM viewModel;
    public NewIngredientAddedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AddPersonalIngredientVM.class);
        binding = FragmentNewIngredientAddedBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        binding.calories.setText(GlobalMethods.formatDoubleToString(viewModel.getNewIngredient().getValue().getCalories()));
        binding.protein.setText(GlobalMethods.formatDoubleToString(viewModel.getNewIngredient().getValue().getProtein()));
        binding.lipid.setText(GlobalMethods.formatDoubleToString(viewModel.getNewIngredient().getValue().getLipid()));
        binding.carbohydrate.setText(GlobalMethods.formatDoubleToString(viewModel.getNewIngredient().getValue().getCarbs()));
        binding.newIngredientAddedToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitScreen();
            }
        });
        binding.newIngredientConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               exitScreen();
            }
        });
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    public void exitScreen() {
        if (binding.submitCheckbox.isChecked()) {
            viewModel.addToPendingList();
        }
        GlobalMethods.backToPreviousFragment(NewIngredientAddedFragment.this);
    }
}