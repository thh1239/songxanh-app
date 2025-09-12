package com.example.songxanh.ui.screens.ingredient_info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.songxanh.databinding.FragmentIngredientInfoBinding;
import com.example.songxanh.ui.screens.find_ingredient.FindIngredientVM;
import com.example.songxanh.utils.GlobalMethods;

public class IngredientInfoFragment extends Fragment {
    IngredientInfoVM ingredientInfoVM;
    FragmentIngredientInfoBinding binding;
    FindIngredientVM findIngredientVM;
    int position;


    public IngredientInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ingredientInfoVM = new ViewModelProvider(requireActivity()).get(IngredientInfoVM.class);
        findIngredientVM = new ViewModelProvider(requireActivity()).get(FindIngredientVM.class);
        binding = FragmentIngredientInfoBinding.inflate(inflater, container, false);
        position = requireArguments().getInt("position");
        String type = requireArguments().getString("type");
        if (type == "global") {
            if (findIngredientVM.getIngredientInfoArrayList().getValue() == null || findIngredientVM.getIngredientInfoArrayList().getValue().isEmpty()) {
                ingredientInfoVM.setIngredientInfo(findIngredientVM.favoriteIngredient.getValue().get(position));
            } else {
                ingredientInfoVM.setIngredientInfo(findIngredientVM.getIngredientInfoArrayList().getValue().get(position));
            }
        } else {
            ingredientInfoVM.setIngredientInfo(findIngredientVM.getPersonalIngredientInfoArrayList().getValue().get(position));
        }
        binding.setViewModel(ingredientInfoVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalMethods.backToPreviousFragment(IngredientInfoFragment.this);
            }
        });

        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredientInfoVM.markAsFavorite();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ingredientInfoVM.toastMessage.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!s.isEmpty()) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}