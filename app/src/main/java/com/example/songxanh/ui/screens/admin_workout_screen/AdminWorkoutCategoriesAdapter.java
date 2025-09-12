package com.example.songxanh.ui.screens.admin_workout_screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.adapters.WorkoutCategoriesAdapter;
import com.example.songxanh.data.models.WorkoutCategory;

import java.util.ArrayList;

public class AdminWorkoutCategoriesAdapter extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<WorkoutCategory> workoutCategories;
    OnCategoryDetailsClick onCategoryDetailsClick;

    public AdminWorkoutCategoriesAdapter (Context context, ArrayList<WorkoutCategory> workoutCategories, OnCategoryDetailsClick onCategoryDetailsClick) {
        this.context = context;
        this.workoutCategories = workoutCategories;
        this.onCategoryDetailsClick = onCategoryDetailsClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.workout_category_layout, parent, false);
        return new WorkoutCategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WorkoutCategoriesViewHolder viewHolder = (WorkoutCategoriesViewHolder) holder;
        viewHolder.categoryNameTv.setText(workoutCategories.get(position).getName());
        Glide.with(context).load(workoutCategories.get(position).getImageUrl()).into(viewHolder.workoutCategoryImg);
        viewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onCategoryDetailsClick != null) {
                    onCategoryDetailsClick.onCategoryDetailsClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return workoutCategories == null ? 0 : workoutCategories.size();
    }

    public void setWorkoutCategories(ArrayList<WorkoutCategory> workoutCategories) {
        this.workoutCategories = workoutCategories;
        notifyDataSetChanged();
    }

    public interface OnCategoryDetailsClick {
        public void onCategoryDetailsClick(int position);
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
