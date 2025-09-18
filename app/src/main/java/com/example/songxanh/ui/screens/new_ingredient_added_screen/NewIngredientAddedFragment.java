package com.example.songxanh.ui.screens.new_ingredient_added_screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.songxanh.databinding.FragmentNewIngredientAddedBinding;
import com.example.songxanh.ui.screens.add_personal_ingredient.AddPersonalIngredientVM;
import com.example.songxanh.utils.GlobalMethods;

public class NewIngredientAddedFragment extends Fragment {

    private FragmentNewIngredientAddedBinding binding;
    private AddPersonalIngredientVM viewModel;

    public NewIngredientAddedFragment() {

    }

    @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(requireActivity()).get(AddPersonalIngredientVM.class);

        binding = FragmentNewIngredientAddedBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);

        if (viewModel.getNewIngredient().getValue() != null) {
            binding.calories.setText(
                    GlobalMethods.formatDoubleToString(viewModel.getNewIngredient().getValue().getCalories()));
            binding.protein.setText(
                    GlobalMethods.formatDoubleToString(viewModel.getNewIngredient().getValue().getProtein()));
            binding.lipid.setText(
                    GlobalMethods.formatDoubleToString(viewModel.getNewIngredient().getValue().getLipid()));
            binding.carbohydrate.setText(
                    GlobalMethods.formatDoubleToString(viewModel.getNewIngredient().getValue().getCarbs()));
        }

        binding.newIngredientAddedToolbar.setOnClickListener(v -> exitScreen());

        binding.newIngredientConfirmButton.setOnClickListener(v -> exitScreen());

        return binding.getRoot();
    }

    
    private void exitScreen() {
        if (binding.submitCheckbox.isChecked()) {
            viewModel.addToPendingList();
        }
        GlobalMethods.backToPreviousFragment(NewIngredientAddedFragment.this);
    }
}
