package com.example.songxanh.data.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.WorkoutCategory;

import java.util.List;

public class WorkoutCategoriesAdapter extends RecyclerView.Adapter<WorkoutCategoriesAdapter.ViewHolder> {
    private Context context;
    private NavController navController;
    private List<WorkoutCategory> categories;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private ConstraintLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.workout_category_img);
            name = itemView.findViewById(R.id.category_name_tv);
            container = itemView.findViewById(R.id.workout_category_item);
        }
    }

    public WorkoutCategoriesAdapter(Context context, NavController navController, List<WorkoutCategory> categories) {
        this.context = context;
        this.navController = navController;
        this.categories = categories;
    }

    @NonNull
    @Override
// == Load ảnh bằng Glide và hiển thị ==
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_category_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
// == Load ảnh bằng Glide và hiển thị ==
    public void onBindViewHolder(@NonNull ViewHolder holder,  int position) {
        WorkoutCategory category = categories.get(position);
        Glide.with(context).load(category.getImageUrl()).into(holder.image);
        holder.name.setText(category.getName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
// == Điều hướng sang màn hình khác ==
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("categoryId",category.getId());
                navController.navigate(R.id.action_workoutCategoriesFragment_to_workoutCategoryExercisesFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
