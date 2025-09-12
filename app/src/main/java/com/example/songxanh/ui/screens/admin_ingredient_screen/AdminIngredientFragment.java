package com.example.songxanh.ui.screens.admin_ingredient_screen;

import android.os.Bundle;
import android.os.Handler;
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
import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.databinding.FragmentAdminIngredientBinding;

import java.util.ArrayList;

public class AdminIngredientFragment extends Fragment implements AdminIngredientRecyclerViewAdapter.OnItemDeleteListener, AdminIngredientRecyclerViewAdapter.OnItemEditListener {

    FragmentAdminIngredientBinding binding;
    AdminIngredientVM adminIngredientVM;
    AdminIngredientRecyclerViewAdapter ingredientRecyclerViewAdapter;
    private boolean isLoading = false;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean enableLoadOnScroll = true;

    public AdminIngredientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        adminIngredientVM = provider.get(AdminIngredientVM.class);
        ingredientRecyclerViewAdapter = new AdminIngredientRecyclerViewAdapter(this.getContext(), adminIngredientVM.databaseIngredientList.getValue(), this, this);

        binding = FragmentAdminIngredientBinding.inflate(inflater, container, false);
        binding.setViewModel(adminIngredientVM);

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(ingredientRecyclerViewAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(binding.adminIngredientRecyclerView);

        binding.adminIngredientRecyclerView.setAdapter(ingredientRecyclerViewAdapter);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.adminIngredientRecyclerView.setLayoutManager(linearLayoutManager);

        binding.adminIngredientRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (enableLoadOnScroll) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });

        binding.adminFindIngredientSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String searchQuery = binding.adminFindIngredientSearch.getText().toString().trim();
                if (searchQuery.length() == 0) {
                    ingredientRecyclerViewAdapter.setIngredientInfoArrayList(adminIngredientVM.databaseIngredientList.getValue(), false);
                    adminIngredientVM.searchResultList.postValue(new ArrayList<>());
                    enableLoadOnScroll = true;
                } else {
                    showResult(searchQuery);
                }
                return true;
            }
        });

        binding.adminPendingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AdminIngredientFragment.this).navigate(R.id.action_adminIngredientFragment_to_adminPendingIngredientsFragment);
            }
        });

        binding.adminAddNewIngredientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("operation", "add");
                NavHostFragment.findNavController(AdminIngredientFragment.this).navigate(R.id.action_adminIngredientFragment_to_adminEditIngredientFragment, bundle);
            }
        });


        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();

    }

    private void showResult(String searchQuery) {
        adminIngredientVM.search(searchQuery);
    }

    private void loadMore() {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adminIngredientVM.loadMore();
                isLoading = false;
            }
        }, 2000);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminIngredientVM.databaseIngredientList.observe(getViewLifecycleOwner(), new Observer<ArrayList<IngredientInfo>>() {
            @Override
            public void onChanged(ArrayList<IngredientInfo> ingredientInfoArrayList) {
                ingredientRecyclerViewAdapter.setIngredientInfoArrayList(ingredientInfoArrayList, false);
            }
        });

        adminIngredientVM.searchResultList.observe(getViewLifecycleOwner(), new Observer<ArrayList<IngredientInfo>>() {
            @Override
            public void onChanged(ArrayList<IngredientInfo> ingredientInfoArrayList) {
                if (ingredientInfoArrayList == null || ingredientInfoArrayList.isEmpty()) {
                    enableLoadOnScroll = true;
                    ingredientRecyclerViewAdapter.setIngredientInfoArrayList(adminIngredientVM.databaseIngredientList.getValue(), true);

                } else {
                    enableLoadOnScroll = false;
                    ingredientRecyclerViewAdapter.setIngredientInfoArrayList(ingredientInfoArrayList, true);
                }
            }
        });

        adminIngredientVM.ingredientCount.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.adminCurrentIngredientCountNumber.setText(String.valueOf(integer));
            }
        });

        adminIngredientVM.pendingIngredientCount.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.adminPendingCountNumber.setText(String.valueOf(integer));
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adminIngredientVM.registration != null) {
            adminIngredientVM.registration.remove();
        }
    }

    @Override
    public void onItemDelete(int position) {
        if (ingredientRecyclerViewAdapter.isSearchResult()) {
            IngredientInfo temp = adminIngredientVM.searchResultList.getValue().get(position);
            adminIngredientVM.deleteIngredient(temp.getId());

        } else {
            IngredientInfo temp = adminIngredientVM.databaseIngredientList.getValue().get(position);
            adminIngredientVM.deleteIngredient(temp.getId());
        }
    }

    @Override
    public void onEditClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("operation", "edit");
        if (adminIngredientVM.searchResultList.getValue() == null || adminIngredientVM.searchResultList.getValue().isEmpty()) {
            bundle.putBoolean("isSearchResult", false);
        } else {
            bundle.putBoolean("isSearchResult", true);
        }
        NavHostFragment.findNavController(AdminIngredientFragment.this).navigate(R.id.action_adminIngredientFragment_to_adminEditIngredientFragment, bundle);
    }
}