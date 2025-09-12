package com.example.songxanh.ui.screens.admin_pending_ingredients_screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.databinding.FragmentAdminPendingIngredientsBinding;
import com.example.songxanh.ui.screens.admin_ingredient_screen.AdminIngredientVM;
import com.example.songxanh.utils.GlobalMethods;

import java.util.ArrayList;

public class AdminPendingIngredientsFragment extends Fragment implements PendingIngredientRecyclerviewAdapter.OnItemDeleteListener, PendingIngredientRecyclerviewAdapter.OnItemApproveListener {

    AdminIngredientVM adminIngredientVM;
    FragmentAdminPendingIngredientsBinding binding;

    PendingIngredientRecyclerviewAdapter adapter;

    public AdminPendingIngredientsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        binding = FragmentAdminPendingIngredientsBinding.inflate(inflater, container, false);

        adminIngredientVM = provider.get(AdminIngredientVM.class);
        adapter = new PendingIngredientRecyclerviewAdapter(this.getContext(), adminIngredientVM.getPendingIngredientList().getValue(), this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.pendingIngredientsRecyclerView.setAdapter(adapter);
        binding.pendingIngredientsRecyclerView.setLayoutManager(linearLayoutManager);
        binding.setViewModel(adminIngredientVM);
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalMethods.backToPreviousFragment(AdminPendingIngredientsFragment.this);
            }
        });


        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adminIngredientVM.pendingIngredientList.observe(getViewLifecycleOwner(), new Observer<ArrayList<IngredientInfo>>() {
            @Override
            public void onChanged(ArrayList<IngredientInfo> ingredientInfoArrayList) {
                adapter.setIngredientInfoArrayList(ingredientInfoArrayList);
            }
        });
        adminIngredientVM.pendingIngredientCount.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                binding.unverifiedCount.setText(String.valueOf(integer));
            }
        });
    }

    @Override
    public void onItemApprove(int position) {
        adminIngredientVM.approveIngredient(position);

    }

    @Override
    public void onItemDelete(int position) {
        adminIngredientVM.deleteFromPendingList(position);
    }

}