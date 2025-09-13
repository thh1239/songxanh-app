package com.example.songxanh.ui.screens.admin_workout_screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.R;
import com.example.songxanh.data.models.WorkoutCategory;
import com.example.songxanh.databinding.FragmentAdminWorkoutBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminWorkoutFragment extends Fragment implements AdminWorkoutCategoriesAdapter.OnCategoryActionListener {

    private FragmentAdminWorkoutBinding binding;
    private AdminWorkoutCategoriesAdapter adapter;
    private AdminWorkoutVM viewModel;

    public AdminWorkoutFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminWorkoutVM.class);
        binding = FragmentAdminWorkoutBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);

        ArrayList<WorkoutCategory> initial = viewModel.getWorkoutCategories().getValue();
        if (initial == null) initial = new ArrayList<>();

        adapter = new AdminWorkoutCategoriesAdapter(requireContext(), initial, this);
        binding.adminWorkoutMuscleCategory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.adminWorkoutMuscleCategory.setAdapter(adapter);

        // Attach swipe-to-delete
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                int pos = vh.getAdapterPosition();
                List<WorkoutCategory> list = viewModel.getWorkoutCategories().getValue();
                if (list == null || pos < 0 || pos >= list.size()) {
                    adapter.notifyItemChanged(pos);
                    return;
                }
                WorkoutCategory category = list.get(pos);
                if (category == null || category.getId() == null || category.getId().isEmpty()) {
                    Toast.makeText(requireContext(), "Danh mục chưa có ID – không thể xóa.", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(pos);
                    return;
                }
                viewModel.deleteCategoryById(
                        category.getId(),
                        () -> {
                            adapter.removeById(category.getId());
                            Toast.makeText(requireContext(), "Đã xóa danh mục.", Toast.LENGTH_SHORT).show();
                        },
                        msg -> {
                            String m = (msg == null || msg.isEmpty()) ? "Xóa thất bại. Vui lòng thử lại." : msg;
                            Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show();
                            adapter.notifyItemChanged(pos);
                        }
                );
            }
        });
        helper.attachToRecyclerView(binding.adminWorkoutMuscleCategory);

        binding.adminWorkoutAddCategoryBtn.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_adminWorkoutFragment_to_adminAddWorkoutCategoryFragment)
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getWorkoutCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories == null) categories = new ArrayList<>();
            adapter.setWorkoutCategories(new ArrayList<>(categories));
        });
    }

    @Override
    public void onCategoryDetailsClick(int position) {
        List<WorkoutCategory> list = viewModel.getWorkoutCategories().getValue();
        if (list == null || position < 0 || position >= list.size()) return;
        WorkoutCategory category = list.get(position);
        if (category == null || category.getId() == null) {
            Toast.makeText(requireContext(), "Không tìm thấy danh mục.", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.fetchExercisesInCategory(category.getId());
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_adminWorkoutFragment_to_adminEditExercisesFragment);
    }

    @Override
    public void onCategoryDeleteClick(int position, @NonNull WorkoutCategory category) {
        if (category.getId() == null || category.getId().isEmpty()) {
            Toast.makeText(requireContext(), "Danh mục chưa có ID – không thể xóa.", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.deleteCategoryById(
                category.getId(),
                () -> {
                    adapter.removeById(category.getId());
                    Toast.makeText(requireContext(), "Đã xóa danh mục.", Toast.LENGTH_SHORT).show();
                },
                msg -> {
                    String m = (msg == null || msg.isEmpty()) ? "Xóa thất bại. Vui lòng thử lại." : msg;
                    Toast.makeText(requireContext(), m, Toast.LENGTH_SHORT).show();
                }
        );
    }
}
