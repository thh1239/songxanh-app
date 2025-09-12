package com.example.songxanh.ui.screens.admin_add_workout_category_screen;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.songxanh.databinding.FragmentAdminAddWorkoutCategoryBinding;
import com.example.songxanh.ui.screens.admin_workout_screen.AdminWorkoutVM;
import com.example.songxanh.utils.GlobalMethods;

public class AdminAddWorkoutCategoryFragment extends Fragment {
    FragmentAdminAddWorkoutCategoryBinding binding;
    AdminWorkoutVM viewModel;
    private ActivityResultLauncher<String> getContent;
    private Uri imageUri;


    public AdminAddWorkoutCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminAddWorkoutCategoryBinding.inflate(inflater, container, false);
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        viewModel = provider.get(AdminWorkoutVM.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        getContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result == null) return;
                imageUri = result;
                Glide.with(requireActivity()).load(result).centerCrop().into(binding.categoryImagePicker);
            }
        });
        setUpOnClick();
        return binding.getRoot();
    }

    private void setUpOnClick() {
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.categoryNameEt.setText("");
                GlobalMethods.backToPreviousFragment(AdminAddWorkoutCategoryFragment.this);
            }
        });

        binding.categoryImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContent.launch("image/*");
            }
        });

        binding.adminAddNewCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.categoryNameEt.getText().toString().equals("") || imageUri == null) return;
                viewModel.addNewCategory(binding.categoryNameEt.getText().toString(), imageUri);
            }
        });
    }
}