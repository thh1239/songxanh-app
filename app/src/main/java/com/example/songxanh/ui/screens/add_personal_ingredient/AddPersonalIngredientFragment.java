package com.example.songxanh.ui.screens.add_personal_ingredient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.songxanh.R;
import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.databinding.FragmentAddPersonalIngredientBinding;
import com.example.songxanh.utils.GlobalMethods;

public class AddPersonalIngredientFragment extends Fragment {

    private AddPersonalIngredientVM viewModel;
    private FragmentAddPersonalIngredientBinding binding;

    public AddPersonalIngredientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AddPersonalIngredientVM.class);
        binding = FragmentAddPersonalIngredientBinding.inflate(inflater, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.addNewIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etNewIngredientName.getText() == null || binding.etNewIngredientServingSize.getText() == null || binding.etNewIngredientCalories.getText() == null || binding.etNewIngredientProtein.getText() == null || binding.etNewIngredientLipid.getText() == null) {
                    return;
                }
                double weightRatio = Double.parseDouble(binding.etNewIngredientServingSize.getText().toString()) / 100;
                if (weightRatio <= 0) return;

                IngredientInfo temp = new IngredientInfo(binding.etNewIngredientName.getText().toString().toUpperCase(), Double.parseDouble(binding.etNewIngredientCalories.getText().toString()) / weightRatio, Double.parseDouble(binding.etNewIngredientCarbs.getText().toString()) / weightRatio, Double.parseDouble(binding.etNewIngredientLipid.getText().toString()) / weightRatio, Double.parseDouble(binding.etNewIngredientProtein.getText().toString()) / weightRatio);
                MutableLiveData<IngredientInfo> tempMutableLiveData = new MutableLiveData<>(temp);
                viewModel.setNewIngredient(tempMutableLiveData);
                viewModel.addPersonalIngredient();
                binding.etNewIngredientName.setText("");
                binding.etNewIngredientServingSize.setText("");
                binding.etNewIngredientCalories.setText("");
                binding.etNewIngredientProtein.setText("");
                binding.etNewIngredientCarbs.setText("");
                binding.etNewIngredientLipid.setText("");
                binding.createIngredientToolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GlobalMethods.backToPreviousFragment(AddPersonalIngredientFragment.this);
                    }
                });
                Toast.makeText(requireActivity(), "Ingredient Added Successfully", Toast.LENGTH_LONG).show();
                NavHostFragment.findNavController(AddPersonalIngredientFragment.this).navigate(R.id.action_addPersonalIngredientFragment_to_newIngredientAddedFragment);
            }

        });
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalMethods.backToPreviousFragment(AddPersonalIngredientFragment.this);
            }
        });
        return binding.getRoot();
    }
}