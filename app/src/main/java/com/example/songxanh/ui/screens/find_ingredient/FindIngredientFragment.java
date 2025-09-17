package com.example.songxanh.ui.screens.find_ingredient;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.R;
import com.example.songxanh.data.models.Ingredient;
import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.databinding.FragmentFindIngredientBinding;
import com.example.songxanh.ui.screens.add_meal.AddMealVM;
import com.example.songxanh.ui.screens.edit_meal.EditMealVM;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class FindIngredientFragment extends Fragment implements
        IngredientNameRecyclerViewAdapter.ViewIngredientInfoClickListener,
        IngredientNameRecyclerViewAdapter.IngredientInfoNameClickListener {

    private FindIngredientVM findIngredientVM;
    private String operation;
    private AddMealVM addMealVM;
    private EditMealVM editMealVM;
    private FragmentFindIngredientBinding binding;
    private IngredientNameRecyclerViewAdapter adapter;
    private IngredientNameRecyclerViewAdapter personalIngredientAdapter;

    public FindIngredientFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        findIngredientVM = provider.get(FindIngredientVM.class);            // 🔹 Share VM: nguồn dữ liệu tìm kiếm/ personal/ favorite
        addMealVM = provider.get(AddMealVM.class);                          // 🔹 VM dùng khi đang thao tác thêm bữa ăn
        editMealVM = provider.get(EditMealVM.class);                        // 🔹 VM dùng khi đang chỉnh sửa bữa ăn

        binding = FragmentFindIngredientBinding.inflate(inflater, container, false);
        binding.setViewModel(findIngredientVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        operation = requireArguments().getString("operation");              // 🔹 Phân biệt flow "add" hay "edit" bữa ăn

        // 🔹 Khởi tạo 2 RecyclerView: danh sách đề cử & danh sách cá nhân
        adapter = new IngredientNameRecyclerViewAdapter(
                this.getContext(),
                findIngredientVM.ingredientInfoArrayList.getValue(),
                this, this
        );
        binding.ingredientSearchResults.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.ingredientSearchResults.setAdapter(adapter);

        personalIngredientAdapter = new IngredientNameRecyclerViewAdapter(
                this.getContext(),
                findIngredientVM.personalIngredientInfoArrayList.getValue(),
                this, this
        );
        binding.personalIngredientSearchResults.setLayoutManager(new LinearLayoutManager(this.getContext()));
        binding.personalIngredientSearchResults.setAdapter(personalIngredientAdapter);

        // 🔹 Điều hướng
        binding.findIngredientBackButton.setOnClickListener(v ->
                GlobalMethods.backToPreviousFragment(FindIngredientFragment.this)
        );
        binding.addOwnIngredient.setOnClickListener(v ->
                NavHostFragment.findNavController(FindIngredientFragment.this)
                        .navigate(R.id.action_findIngredientFragment_to_addPersonalIngredientFragment)
        );

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🔹 Tải dữ liệu ban đầu: personal + đề cử + favorite (để fallback khi không có kết quả)
        findIngredientVM.loadAllPersonal();
        findIngredientVM.loadAllRecommended();
        findIngredientVM.fetchFavoriteIngredients();

        // 🔹 Tìm kiếm khi nhấn enter trên ô search
        binding.findIngredientSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String searchQuery = binding.findIngredientSearch.getText().toString();
                findIngredientVM.searchBoth(searchQuery);
                return true;
            }
        });

        // 🔹 Lắng nghe dữ liệu danh sách đề cử để hiển thị
        findIngredientVM.getIngredientInfoArrayList().observe(getViewLifecycleOwner(), new Observer<ArrayList<IngredientInfo>>() {
            @Override
            public void onChanged(ArrayList<IngredientInfo> ingredientInfoArrayList) {
                binding.searchResultsTv.setText("Nguyên liệu đề cử");
                binding.ingredientSearchResults.setVisibility(View.VISIBLE);
                adapter.setIngredientInfoArrayList(ingredientInfoArrayList);
                updateEmptyStates();
            }
        });

        // 🔹 Lắng nghe dữ liệu personal để hiển thị
        findIngredientVM.getPersonalIngredientInfoArrayList().observe(getViewLifecycleOwner(), new Observer<ArrayList<IngredientInfo>>() {
            @Override
            public void onChanged(ArrayList<IngredientInfo> ingredientInfoArrayList) {
                binding.personalIngredientTv.setVisibility(View.VISIBLE);
                binding.personalIngredientTv.setText("Nguyên liệu cá nhân");
                binding.personalIngredientSearchResults.setVisibility(View.VISIBLE);
                personalIngredientAdapter.setIngredientInfoArrayList(ingredientInfoArrayList);
                updateEmptyStates();
            }
        });

        // 🔹 Fallback: nếu danh sách đề cử đang rỗng thì hiển thị danh sách favorite đã fetch
        findIngredientVM.favoriteIngredient.observe(getViewLifecycleOwner(), new Observer<ArrayList<IngredientInfo>>() {
            @Override
            public void onChanged(ArrayList<IngredientInfo> ingredientInfoArrayList) {
                if (findIngredientVM.ingredientInfoArrayList.getValue() == null
                        || findIngredientVM.ingredientInfoArrayList.getValue().isEmpty()) {
                    binding.searchResultsTv.setText("Favorite Ingredients");
                    binding.ingredientSearchResults.setVisibility(View.VISIBLE);
                    adapter.setIngredientInfoArrayList(ingredientInfoArrayList);
                    updateEmptyStates();
                }
            }
        });

        // 🔹 Vuốt-xóa trên danh sách personal (xác nhận trước khi xóa)
        ItemTouchHelper.SimpleCallback swipeToDeletePersonal =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        ArrayList<IngredientInfo> list = findIngredientVM.getPersonalIngredientInfoArrayList().getValue();
                        if (list == null || position < 0 || position >= list.size()) {
                            personalIngredientAdapter.notifyItemChanged(position);
                            return;
                        }
                        IngredientInfo info = list.get(position);
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Xóa nguyên liệu cá nhân?")
                                .setMessage("Bạn có chắc muốn xóa \"" + safeName(info) + "\" khỏi danh sách cá nhân?")
                                .setPositiveButton("Xóa", (d, w) -> findIngredientVM.deletePersonalIngredient(info.getId(), position))
                                .setNegativeButton("Hủy", (d, w) -> personalIngredientAdapter.notifyItemChanged(position))
                                .setOnCancelListener(d -> personalIngredientAdapter.notifyItemChanged(position))
                                .show();
                    }
                };
        new ItemTouchHelper(swipeToDeletePersonal).attachToRecyclerView(binding.personalIngredientSearchResults);
    }

    private void showResult(String searchQuery) {
        findIngredientVM.searchBoth(searchQuery);                           // 🔹 Tìm đồng thời ở đề cử & personal
    }

    @Override
    public void onViewIngredientInfoClick(int position, int recyclerViewId) {
        // 🔹 Mở màn chi tiết IngredientInfo; tại đó người dùng mới bấm "Thêm vào yêu thích" để lưu personal
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        if (recyclerViewId == binding.ingredientSearchResults.getId()) {
            Log.d("globalId", "onViewIngredientInfoClick: " + recyclerViewId);
            bundle.putString("type", "global");                             // 🔹 Đề cử -> có nút "Thêm vào yêu thích"
        } else {
            Log.d("personalId", "onViewIngredientInfoClick: " + recyclerViewId);
            bundle.putString("type", "personal");                           // 🔹 Personal -> ẩn nút "Thêm vào yêu thích"
        }
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_findIngredientFragment_to_ingredientInfoFragment, bundle);
    }

    private void addToIngredients(Ingredient tempIngredient) {
        // 🔹 Thêm nguyên liệu vào list nguyên liệu của bữa ăn (tùy theo flow add/edit)
        ArrayList<Ingredient> tempList;
        if ("add".equals(operation)) {
            tempList = addMealVM.getIngredients().getValue();
        } else {
            tempList = editMealVM.getIngredients().getValue();
        }
        if (tempList != null) tempList.add(tempIngredient);
        else {
            tempList = new ArrayList<>();
            tempList.add(tempIngredient);
        }
        if ("add".equals(operation)) addMealVM.getIngredients().postValue(tempList);
        else editMealVM.getIngredients().postValue(tempList);
    }

    private Ingredient createTempIngredient(IngredientInfo selectedIngredientInfo) {
        // 🔹 Tạo bản tạm 100g để cộng dinh dưỡng nhanh khi thêm vào bữa ăn
        String name = safeName(selectedIngredientInfo);
        Ingredient tempIngredient = new Ingredient();
        tempIngredient.setWeight(100);
        tempIngredient.setName(name);
        tempIngredient.setProtein(selectedIngredientInfo.getProtein() * tempIngredient.getWeight() / 100);
        tempIngredient.setLipid(selectedIngredientInfo.getLipid() * tempIngredient.getWeight() / 100);
        tempIngredient.setCarb(selectedIngredientInfo.getCarbs() * tempIngredient.getWeight() / 100);
        tempIngredient.setCalories(selectedIngredientInfo.getCalories() * tempIngredient.getWeight() / 100);
        return tempIngredient;
    }

    @Override
    public void onIngredientInfoNameClick(int position, int recyclerViewId) {
        // 🔹 Click vào tên trong list: CHỈ thêm vào bữa ăn (nếu đang add/edit), KHÔNG lưu vào personal
        IngredientInfo selectedIngredientInfo;
        if (recyclerViewId == binding.ingredientSearchResults.getId()) {
            if (findIngredientVM.ingredientInfoArrayList.getValue() == null
                    || findIngredientVM.ingredientInfoArrayList.getValue().isEmpty()) {
                ArrayList<IngredientInfo> favs = findIngredientVM.favoriteIngredient.getValue();
                selectedIngredientInfo = (favs != null && position < favs.size()) ? favs.get(position) : null;
            } else {
                selectedIngredientInfo = findIngredientVM.ingredientInfoArrayList.getValue().get(position);
            }
        } else {
            selectedIngredientInfo = findIngredientVM.personalIngredientInfoArrayList.getValue().get(position);
        }
        if (selectedIngredientInfo == null) return;

        Ingredient tempIngredient = createTempIngredient(selectedIngredientInfo);
        addToIngredients(tempIngredient);

        GlobalMethods.backToPreviousFragment(FindIngredientFragment.this);  // 🔹 Quay lại sau khi chọn (tuỳ UX)
    }

    private void updateEmptyStates() {
        // 🔹 Đảm bảo các section luôn hiện khi có dữ liệu
        binding.personalIngredientTv.setVisibility(View.VISIBLE);
        binding.personalIngredientSearchResults.setVisibility(View.VISIBLE);
        binding.searchResultsTv.setVisibility(View.VISIBLE);
        binding.ingredientSearchResults.setVisibility(View.VISIBLE);
    }

    private String safeName(IngredientInfo info) {
        // 🔹 Tránh NPE khi lấy tên
        String name = info != null ? info.getShort_Description() : null;
        return name == null ? "" : name;
    }
}
