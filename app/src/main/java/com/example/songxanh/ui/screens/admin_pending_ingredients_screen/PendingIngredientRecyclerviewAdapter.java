package com.example.songxanh.ui.screens.admin_pending_ingredients_screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.R;
import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.utils.GlobalMethods;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PendingIngredientRecyclerviewAdapter extends RecyclerView.Adapter {

    Context context;

    ArrayList<IngredientInfo> ingredientInfoArrayList = new ArrayList<>();

    private OnItemDeleteListener onItemDeleteListener;
    private OnItemApproveListener onItemApproveListener;

    public PendingIngredientRecyclerviewAdapter(Context context, ArrayList<IngredientInfo> ingredientInfoArrayList, OnItemApproveListener onItemApproveListener, OnItemDeleteListener onItemDeleteListener) {
        this.context = context;
        this.ingredientInfoArrayList = ingredientInfoArrayList;
        this.onItemDeleteListener = onItemDeleteListener;
        this.onItemApproveListener = onItemApproveListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pending_ingredient_layout, parent, false);
        return new PendingIngredientViewHolder(view);
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void deleteItem(int position) {
        if (onItemDeleteListener != null) {
            onItemDeleteListener.onItemDelete(position);
        }
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void approveItem(int position) {
        if (onItemApproveListener != null) {
            onItemApproveListener.onItemApprove(position);
        }
    }

    @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PendingIngredientViewHolder pendingIngredientViewHolder = (PendingIngredientViewHolder) holder;
        pendingIngredientViewHolder.tvIngredientName.setText(ingredientInfoArrayList.get(position).getShort_Description());
        pendingIngredientViewHolder.tvProteinNumber.setText(GlobalMethods.formatDoubleToString(ingredientInfoArrayList.get(position).getProtein()));
        pendingIngredientViewHolder.tvCarbsNumber.setText(GlobalMethods.formatDoubleToString(ingredientInfoArrayList.get(position).getCarbs()));
        pendingIngredientViewHolder.tvLipidNumber.setText(GlobalMethods.formatDoubleToString(ingredientInfoArrayList.get(position).getLipid()));
        pendingIngredientViewHolder.tvCaloriesNumber.setText(GlobalMethods.formatDoubleToString(ingredientInfoArrayList.get(position).getCalories()));
        pendingIngredientViewHolder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
            public void onClick(View v) {
                approveItem(position);
            }
        });
        pendingIngredientViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
            public void onClick(View v) {
                deleteItem(position);
            }
        });

    }

    @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
    public int getItemCount() {
        return ingredientInfoArrayList.size();
    }

    public static class PendingIngredientViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredientName, tvProteinNumber, tvCarbsNumber, tvLipidNumber, tvCaloriesNumber;

        AppCompatButton btnApprove, btnDelete;
        public PendingIngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.admin_pending_ingredient_item_name);
            tvProteinNumber = itemView.findViewById(R.id.admin_pending_protein_number);
            tvCarbsNumber = itemView.findViewById(R.id.admin_pending_carbs_number);
            tvLipidNumber = itemView.findViewById(R.id.admin_pending_lipid_number);
            tvCaloriesNumber = itemView.findViewById(R.id.admin_pending_calories_number);
            btnApprove = itemView.findViewById(R.id.approve_pending_ingredient_button);
            btnDelete = itemView.findViewById(R.id.delete_pending_ingredient_button);
        }
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void setIngredientInfoArrayList(ArrayList<IngredientInfo> ingredientInfoArrayList) {
        this.ingredientInfoArrayList = ingredientInfoArrayList;
        notifyDataSetChanged();
    }

    public interface OnItemApproveListener {
        void onItemApprove(int position);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

}
