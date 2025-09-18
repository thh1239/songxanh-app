package com.example.songxanh.ui.screens.find_ingredient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.R;
import com.example.songxanh.data.models.IngredientInfo;

import java.util.ArrayList;

public class IngredientNameRecyclerViewAdapter extends RecyclerView.Adapter<IngredientNameRecyclerViewAdapter.IngredientNameViewHolder> {

    Context context;
    ArrayList<IngredientInfo> ingredientInfoArrayList = new ArrayList<>();

    private ViewIngredientInfoClickListener ingredientInfoClickListener;
    private IngredientInfoNameClickListener ingredientInfoNameClickListener;
    private OnFavoriteClickListener favoriteClickListener;

    IngredientNameRecyclerViewAdapter(Context context,
                                      ArrayList<IngredientInfo> ingredientInfoArrayList,
                                      ViewIngredientInfoClickListener viewIngredientInfoClickListener,
                                      IngredientInfoNameClickListener ingredientInfoNameClickListener) {
        this.context = context;
        this.ingredientInfoArrayList = ingredientInfoArrayList;
        this.ingredientInfoClickListener = viewIngredientInfoClickListener;
        this.ingredientInfoNameClickListener = ingredientInfoNameClickListener;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void setIngredientInfoArrayList(ArrayList<IngredientInfo> ingredientInfoArrayList) {
        this.ingredientInfoArrayList = ingredientInfoArrayList;
        notifyDataSetChanged();
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public IngredientInfo getItem(int position) {
        if (ingredientInfoArrayList == null || position < 0 || position >= ingredientInfoArrayList.size()) return null;
        return ingredientInfoArrayList.get(position);
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void setOnFavoriteClickListener(OnFavoriteClickListener l) {
        this.favoriteClickListener = l;
    }

    @NonNull
    @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
    public IngredientNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ingredient_info_row_layout, parent, false);
        return new IngredientNameViewHolder(view);
    }

    @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
    public void onBindViewHolder(@NonNull IngredientNameViewHolder holder, int position) {
        IngredientInfo item = ingredientInfoArrayList.get(position);
        holder.tvIngredientName.setText(item.getShort_Description());

        holder.btnViewIngredientInfo.setOnClickListener(v -> {
            if (ingredientInfoClickListener != null) {
                int p = holder.getAdapterPosition();
                if (p == RecyclerView.NO_POSITION) return;
                int recyclerViewId = getParentRecyclerId(holder.itemView);
                ingredientInfoClickListener.onViewIngredientInfoClick(p, recyclerViewId);
            }
        });

        holder.tvIngredientName.setOnClickListener(v -> {
            if (ingredientInfoNameClickListener != null) {
                int p = holder.getAdapterPosition();
                if (p == RecyclerView.NO_POSITION) return;
                int recyclerViewId = getParentRecyclerId(holder.itemView);
                ingredientInfoNameClickListener.onIngredientInfoNameClick(p, recyclerViewId);
            }
        });

        if (holder.favoriteBtn != null) {
            holder.favoriteBtn.setOnClickListener(v -> {
                if (favoriteClickListener != null) {
                    int p = holder.getAdapterPosition();
                    if (p == RecyclerView.NO_POSITION) return;
                    int recyclerViewId = getParentRecyclerId(holder.itemView);
                    favoriteClickListener.onFavoriteClick(p, recyclerViewId);
                }
            });
        }
    }

    @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
    public int getItemCount() {
        return ingredientInfoArrayList == null ? 0 : ingredientInfoArrayList.size();
    }

    private @IdRes int getParentRecyclerId(View itemView) {
        ViewGroup parent = (ViewGroup) itemView.getParent();
        return parent != null ? parent.getId() : View.NO_ID;
    }

    public static class IngredientNameViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredientName;
        AppCompatButton btnViewIngredientInfo;
        ImageButton favoriteBtn;

        public IngredientNameViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.ingredient_name_info);
            btnViewIngredientInfo = itemView.findViewById(R.id.view_ingredient_info_button);
            favoriteBtn = itemView.findViewById(R.id.favorite_btn);
        }
    }

    public interface ViewIngredientInfoClickListener {
        void onViewIngredientInfoClick(int position, int recyclerViewId);
    }

    public interface IngredientInfoNameClickListener {
        void onIngredientInfoNameClick(int position, int recyclerViewId);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(int position, int recyclerViewId);
    }
}
