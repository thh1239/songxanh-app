package com.example.songxanh.ui.screens.admin_ingredient_screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.R;
import com.example.songxanh.data.models.IngredientInfo;
import com.example.songxanh.utils.GlobalMethods;

import java.util.ArrayList;

public class AdminIngredientRecyclerViewAdapter extends RecyclerView.Adapter {
    public Context getContext() {
        return context;
    }

    Context context;
    ArrayList<IngredientInfo> ingredientInfoArrayList = new ArrayList<>();

    private OnItemDeleteListener onItemDeleteListener;
    private OnItemEditListener onItemEditListener;

    private boolean isSearchResult = false;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;


    public AdminIngredientRecyclerViewAdapter(Context context, ArrayList<IngredientInfo> ingredientInfoArrayList, OnItemDeleteListener onItemDeleteListener, OnItemEditListener onItemEditListener) {
        this.context = context;
        this.ingredientInfoArrayList = ingredientInfoArrayList;
        this.onItemDeleteListener = onItemDeleteListener;
        this.onItemEditListener = onItemEditListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.admin_ingredient_info_item_layout, parent, false);
            return new IngredientViewHolder(view);
        }
       else {
           LayoutInflater inflater = LayoutInflater.from(context);
           View view = inflater.inflate(R.layout.loading_item, parent, false);
           return new LoadingViewHolder(view);
        }
    }

    public void deleteItem(int position)  {
        if(onItemDeleteListener != null) {
            onItemDeleteListener.onItemDelete(position);
        }
    }

    public void updateItem(int position) {
        if(onItemEditListener != null) {
            onItemEditListener.onEditClick(position);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof IngredientViewHolder){
            IngredientViewHolder ingredientHolder = (IngredientViewHolder) holder;
            ingredientHolder.tvIngredientName.setText(ingredientInfoArrayList.get(position).getShort_Description());
            ingredientHolder.tvProteinNumber.setText(GlobalMethods.formatDoubleToString(ingredientInfoArrayList.get(position).getProtein()));
            ingredientHolder.tvCarbsNumber.setText(GlobalMethods.formatDoubleToString(ingredientInfoArrayList.get(position).getCarbs()));
            ingredientHolder.tvLipidNumber.setText(GlobalMethods.formatDoubleToString(ingredientInfoArrayList.get(position).getLipid()));
            ingredientHolder.tvCaloriesNumber.setText(GlobalMethods.formatDoubleToString(ingredientInfoArrayList.get(position).getCalories()));
            ingredientHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateItem(position);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            // Handle the loading ViewHolder
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }


    @Override
    public int getItemCount() {
        return ingredientInfoArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return ingredientInfoArrayList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setIngredientInfoArrayList(ArrayList<IngredientInfo> ingredientInfoArrayList, boolean isSearchResult) {
        this.ingredientInfoArrayList = ingredientInfoArrayList;
        this.isSearchResult = isSearchResult;
        notifyDataSetChanged();
    }

    public boolean isSearchResult() {
        return isSearchResult;
    }

    public void setSearchResult(boolean searchResult) {
        isSearchResult = searchResult;
    }

    public static class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView tvIngredientName, tvProteinNumber, tvCarbsNumber, tvLipidNumber, tvCaloriesNumber;
        AppCompatButton btnEdit;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.admin_ingredient_item_name);
            tvProteinNumber = itemView.findViewById(R.id.admin_protein_number);
            tvCarbsNumber = itemView.findViewById(R.id.admin_carbs_number);
            tvLipidNumber = itemView.findViewById(R.id.admin_lipid_number);
            tvCaloriesNumber = itemView.findViewById(R.id.admin_calories_number);
            btnEdit = itemView.findViewById(R.id.admin_edit_ingredient_button);
        }
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public interface OnItemEditListener {
        void onEditClick(int position);
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
