package com.example.songxanh.ui.screens.admin_workout_screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.WorkoutCategory;

import java.util.ArrayList;
import java.util.Objects;

public class AdminWorkoutCategoriesAdapter extends RecyclerView.Adapter<AdminWorkoutCategoriesAdapter.WorkoutCategoriesViewHolder> {

    private final Context context;
    private final ArrayList<WorkoutCategory> workoutCategories;
    private OnCategoryDetailsClick onCategoryDetailsClick;

    public AdminWorkoutCategoriesAdapter(@NonNull Context context,
                                         @NonNull ArrayList<WorkoutCategory> workoutCategories,
                                         OnCategoryDetailsClick onCategoryDetailsClick) {
        this.context = context;
        this.workoutCategories = workoutCategories == null ? new ArrayList<>() : workoutCategories;
        this.onCategoryDetailsClick = onCategoryDetailsClick;
        setHasStableIds(true);
    }

    public void setOnCategoryDetailsClick(OnCategoryDetailsClick listener) {
        this.onCategoryDetailsClick = listener;
    }

    @NonNull
    @Override
    public WorkoutCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.workout_category_layout, parent, false);
        return new WorkoutCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutCategoriesViewHolder holder, int position) {
        WorkoutCategory item = workoutCategories.get(position);
        holder.categoryNameTv.setText(item.getName() == null ? "" : item.getName());
        Glide.with(context).load(item.getImageUrl()).centerCrop().into(holder.workoutCategoryImg);

        holder.item.setOnClickListener(v -> {
            if (onCategoryDetailsClick == null) return;
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) onCategoryDetailsClick.onCategoryDetailsClick(pos);
        });

        holder.categoryDetail.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(v.getContext(), v);
            menu.getMenu().add(0, 1, 0, "Chi tiết");
            menu.getMenu().add(0, 2, 1, "Xóa");
            menu.setOnMenuItemClickListener(mi -> {
                if (onCategoryDetailsClick == null) return false;
                int pos = holder.getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return false;
                if (mi.getItemId() == 1) {
                    onCategoryDetailsClick.onCategoryDetailsClick(pos);
                    return true;
                } else if (mi.getItemId() == 2) {
                    if (onCategoryDetailsClick instanceof OnCategoryActionListener) {
                        ((OnCategoryActionListener) onCategoryDetailsClick)
                                .onCategoryDeleteClick(pos, workoutCategories.get(pos));
                    }
                    return true;
                }
                return false;
            });
            menu.show();
        });
    }

    @Override
    public int getItemCount() {
        return workoutCategories == null ? 0 : workoutCategories.size();
    }

    @Override
    public long getItemId(int position) {
        WorkoutCategory c = workoutCategories.get(position);
        if (c.getId() != null) return c.getId().hashCode();
        return (c.getName() == null ? position : c.getName().hashCode());
    }

    public void setWorkoutCategories(ArrayList<WorkoutCategory> newList) {
        workoutCategories.clear();
        if (newList != null) workoutCategories.addAll(newList);
        notifyDataSetChanged();
    }

    public void addOrUpdate(@NonNull WorkoutCategory item) {
        int idx = indexOfId(item.getId());
        if (idx >= 0) {
            workoutCategories.set(idx, item);
            notifyItemChanged(idx);
        } else {
            workoutCategories.add(item);
            notifyItemInserted(workoutCategories.size() - 1);
        }
    }

    public boolean removeById(String id) {
        int idx = indexOfId(id);
        if (idx >= 0) {
            workoutCategories.remove(idx);
            notifyItemRemoved(idx);
            return true;
        }
        return false;
    }

    private int indexOfId(String id) {
        if (id == null) return -1;
        for (int i = 0; i < workoutCategories.size(); i++) {
            if (Objects.equals(workoutCategories.get(i).getId(), id)) return i;
        }
        return -1;
    }

    public interface OnCategoryDetailsClick {
        void onCategoryDetailsClick(int position);
    }

    public interface OnCategoryActionListener extends OnCategoryDetailsClick {
        void onCategoryDeleteClick(int position, @NonNull WorkoutCategory category);
    }

    public static class WorkoutCategoriesViewHolder extends RecyclerView.ViewHolder {
        ImageView workoutCategoryImg, categoryDetail;
        TextView categoryNameTv;
        ConstraintLayout item;

        public WorkoutCategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.workout_category_item);
            workoutCategoryImg = itemView.findViewById(R.id.workout_category_img);
            categoryDetail = itemView.findViewById(R.id.category_detail);
            categoryNameTv = itemView.findViewById(R.id.category_name_tv);
        }
    }
}
