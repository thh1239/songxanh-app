package com.example.songxanh.ui.screens.admin_add_workout_category_screen;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.songxanh.databinding.FragmentAdminAddWorkoutCategoryBinding;
import com.example.songxanh.ui.screens.admin_workout_screen.AdminWorkoutVM;
import com.example.songxanh.utils.GlobalMethods;

public class AdminAddWorkoutCategoryFragment extends Fragment {

    private FragmentAdminAddWorkoutCategoryBinding binding;
    private AdminWorkoutVM viewModel;
    private ActivityResultLauncher<String> pickImage;
    private Uri imageUri;

    public AdminAddWorkoutCategoryFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminAddWorkoutCategoryBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminWorkoutVM.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri == null) return;
            imageUri = uri;
            Glide.with(requireActivity()).load(uri).centerCrop().into(binding.categoryImagePicker);
        });

        setupClicks();
        return binding.getRoot();
    }

    private void setupClicks() {
        binding.appBar.backBtn.setOnClickListener(v -> {
            binding.categoryNameEt.setText("");
            imageUri = null;
            GlobalMethods.backToPreviousFragment(this);
        });

        binding.categoryImagePicker.setOnClickListener(v -> pickImage.launch("image/*"));

        binding.adminAddNewCategoryButton.setOnClickListener(v -> {
            String name = binding.categoryNameEt.getText() == null ? "" : binding.categoryNameEt.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(requireContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
            }
            if (imageUri == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn ảnh danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            setLoading(true);
            viewModel.addNewCategory(name, imageUri,
                    () -> {
                        setLoading(false);
                        binding.categoryNameEt.setText("");
                        imageUri = null;
                        NavHostFragment.findNavController(this).popBackStack();
                    },
                    msg -> {
                        setLoading(false);
                        String m = (msg == null || msg.isEmpty()) ? "Thêm thất bại, vui lòng thử lại." : msg;
                        Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void setLoading(boolean loading) {
        binding.adminAddNewCategoryButton.setEnabled(!loading);
        binding.categoryImagePicker.setEnabled(!loading);
        binding.appBar.backBtn.setEnabled(!loading);
    }
}
