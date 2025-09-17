package com.example.songxanh.ui.screens.ingredient_info;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.databinding.FragmentIngredientInfoBinding;
import com.example.songxanh.ui.screens.find_ingredient.FindIngredientVM;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class IngredientInfoFragment extends Fragment {
    private IngredientInfoVM ingredientInfoVM;
    private FragmentIngredientInfoBinding binding;
    private FindIngredientVM findIngredientVM;
    private int position;
    private String type; // "global" | "personal"

    public IngredientInfoFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Lấy shared ViewModel từ Activity (dùng chung giữa các fragment)
        ingredientInfoVM = new ViewModelProvider(requireActivity()).get(IngredientInfoVM.class);
        findIngredientVM = new ViewModelProvider(requireActivity()).get(FindIngredientVM.class);

        // Inflate + DataBinding
        binding = FragmentIngredientInfoBinding.inflate(inflater, container, false);

        // Đọc tham số điều hướng: vị trí item và nguồn dữ liệu (global/personal)
        position = requireArguments().getInt("position", -1);
        type = requireArguments().getString("type", "global");

        // Chọn nguồn dữ liệu hiển thị theo 'type'
        IngredientInfo selected = null;
        if ("global".equalsIgnoreCase(type)) {
            // Ưu tiên list đề cử; nếu rỗng thì fallback sang list "favorite" đã fetch
            ArrayList<IngredientInfo> globals = findIngredientVM.getIngredientInfoArrayList().getValue();
            if (globals == null || globals.isEmpty()) {
                ArrayList<IngredientInfo> favs = findIngredientVM.favoriteIngredient.getValue();
                if (favs != null && position >= 0 && position < favs.size()) {
                    selected = favs.get(position);
                }
            } else if (position >= 0 && position < globals.size()) {
                selected = globals.get(position);
            }
        } else { // personal
            ArrayList<IngredientInfo> personals = findIngredientVM.getPersonalIngredientInfoArrayList().getValue();
            if (personals != null && position >= 0 && position < personals.size()) {
                selected = personals.get(position);
            }
        }

        // Gắn data vào VM để layout binding hiển thị
        if (selected != null) {
            ingredientInfoVM.setIngredientInfo(selected);
        }

        // Kết nối VM với layout (two-way binding, lifecycle aware)
        binding.setViewModel(ingredientInfoVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // Nút back trên app bar
        binding.appBar.backBtn.setOnClickListener(v ->
                GlobalMethods.backToPreviousFragment(IngredientInfoFragment.this));

        // Ẩn/hiện cụm "Thêm vào yêu thích" theo nguồn:
        // - global: HIỆN (được phép lưu sang personal)
        // - personal: ẨN (đã là nguyên liệu cá nhân, không cần nút)
        if ("personal".equalsIgnoreCase(type)) {
            binding.markAsFavorite.setVisibility(View.GONE);
        } else {
            binding.markAsFavorite.setVisibility(View.VISIBLE);
        }

        // Click "Thêm vào yêu thích" -> Lưu nguyên liệu hiện tại vào collection personal_ingredient
        binding.favoriteBtn.setOnClickListener(v -> {
            IngredientInfo info = ingredientInfoVM.getIngredientInfo();
            if (info == null) return;

            // Tránh tạo bản ghi trùng: tìm docId theo tên để quyết định 'set' (ghi đè) hay 'add' (tạo mới)
            String existingId = findPersonalDocIdByName(safeName(info));

            findIngredientVM.savePersonalIngredient(
                    info,
                    existingId, // null -> add; có id -> set overwrite
                    new OnSuccessListener<Void>() {
                        @Override public void onSuccess(Void unused) {
                            Toast.makeText(requireContext(),
                                    "Đã thêm vào danh sách cá nhân",
                                    Toast.LENGTH_SHORT).show();
                            // Refresh list personal cho các nơi khác đang observe
                            findIngredientVM.loadAllPersonal();
                        }
                    },
                    e -> Toast.makeText(requireContext(),
                            "Lỗi lưu: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show()
            );
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Lắng nghe thông điệp toast từ VM (nếu có logic khác phát sinh)
        ingredientInfoVM.toastMessage.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null && !s.isEmpty()) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ===== Helpers =====
    // Chuẩn hóa tên hiển thị, tránh NPE
    private String safeName(IngredientInfo info) {
        String name = (info != null) ? info.getShort_Description() : null;
        return name == null ? "" : name.trim();
    }

    // Tìm docId trong personal theo tên (so sánh không phân biệt hoa thường) để chống trùng
    private String findPersonalDocIdByName(String name) {
        ArrayList<IngredientInfo> list = findIngredientVM.getPersonalIngredientInfoArrayList().getValue();
        if (list == null || name == null) return null;
        for (IngredientInfo it : list) {
            String n = safeName(it);
            if (n.equalsIgnoreCase(name.trim())) {
                return it.getId();
            }
        }
        return null;
    }
}
