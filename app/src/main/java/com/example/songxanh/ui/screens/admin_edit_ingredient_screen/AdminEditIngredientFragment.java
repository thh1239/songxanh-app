package com.example.songxanh.ui.screens.admin_edit_ingredient_screen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.databinding.FragmentAdminEditIngredientBinding;
import com.example.songxanh.ui.screens.admin_ingredient_screen.AdminIngredientVM;
import com.example.songxanh.utils.GlobalMethods;

public class AdminEditIngredientFragment extends Fragment {

    private AdminIngredientVM adminIngredientVM;
    private FragmentAdminEditIngredientBinding binding;

    private String operation;
    private boolean isSearchResult;
    private int ingredientPosition;

    public AdminEditIngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adminIngredientVM = new ViewModelProvider(requireActivity()).get(AdminIngredientVM.class);
        binding = FragmentAdminEditIngredientBinding.inflate(inflater, container, false);
        binding.setViewModel(adminIngredientVM);
        ingredientPosition = requireArguments().getInt("position");
        binding.setLifecycleOwner(getViewLifecycleOwner());
        operation = requireArguments().getString("operation");
        isSearchResult = requireArguments().getBoolean("isSearchResult");
        setUpView();


        return binding.getRoot();
    }

    private void setUpView() {
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalMethods.backToPreviousFragment(AdminEditIngredientFragment.this);
            }
        });
        if(operation.equals("edit")) {
            IngredientInfo temp = new IngredientInfo();

            if(isSearchResult) {
                temp = adminIngredientVM.getSearchResultList().getValue().get(ingredientPosition);
            } else {
                temp = adminIngredientVM.getDatabaseIngredientList().getValue().get(ingredientPosition);
            }
            binding.etUpdateIngredientName.setText(temp.getShort_Description());
            binding.etUpdateIngredientProtein.setText(GlobalMethods.formatDoubleToString(temp.getProtein()));
            binding.etUpdateIngredientCalories.setText(GlobalMethods.formatDoubleToString(temp.getCalories()));
            binding.etUpdateIngredientCarbs.setText(GlobalMethods.formatDoubleToString(temp.getCarbs()));
            binding.etUpdateIngredientLipid.setText(GlobalMethods.formatDoubleToString(temp.getLipid()));
            binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobalMethods.backToPreviousFragment(AdminEditIngredientFragment.this);
                }
            });
            IngredientInfo finalTemp = temp;
            binding.updateIngredientBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.etUpdateIngredientName.getText() != null && binding.etUpdateIngredientCalories.getText() != null && binding.etUpdateIngredientProtein.getText() != null && binding.etUpdateIngredientLipid.getText() != null || binding.etUpdateIngredientCarbs.getText() != null) {
                        IngredientInfo newIngredient = new IngredientInfo(binding.etUpdateIngredientName.getText().toString().toUpperCase(), Double.parseDouble(binding.etUpdateIngredientCalories.getText().toString()), Double.parseDouble(binding.etUpdateIngredientCarbs.getText().toString()), Double.parseDouble(binding.etUpdateIngredientLipid.getText().toString()), Double.parseDouble(binding.etUpdateIngredientProtein.getText().toString()));
                        newIngredient.setId(finalTemp.getId());
                        adminIngredientVM.updateIngredient(finalTemp.getId(), newIngredient);
                        GlobalMethods.backToPreviousFragment(AdminEditIngredientFragment.this);
                    }
                }
            });
        } else if (operation.equals("add")) {
            binding.updateIngredientBtn.setText("Add");
            binding.updateIngredientBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (binding.etUpdateIngredientName.getText() != null && binding.etUpdateIngredientCalories.getText() != null && binding.etUpdateIngredientProtein.getText() != null && binding.etUpdateIngredientLipid.getText() != null || binding.etUpdateIngredientCarbs.getText() != null) {
                        IngredientInfo newIngredient = new IngredientInfo(binding.etUpdateIngredientName.getText().toString().toUpperCase(), Double.parseDouble(binding.etUpdateIngredientCalories.getText().toString()), Double.parseDouble(binding.etUpdateIngredientCarbs.getText().toString()), Double.parseDouble(binding.etUpdateIngredientLipid.getText().toString()), Double.parseDouble(binding.etUpdateIngredientProtein.getText().toString()));
                        Log.d("NEW INGREDIENT", newIngredient.getShort_Description());
                        adminIngredientVM.addIngredient(newIngredient);

                        GlobalMethods.backToPreviousFragment(AdminEditIngredientFragment.this);
                    }
                }
            });
        }
    }
}