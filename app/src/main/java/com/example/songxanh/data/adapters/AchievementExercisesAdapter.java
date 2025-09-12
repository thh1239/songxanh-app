package com.example.songxanh.data.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.Exercise;

import java.util.List;

public class AchievementExercisesAdapter extends RecyclerView.Adapter<AchievementExercisesAdapter.ViewHolder>{
    private Context context;
    private List<Exercise> exercises;

    public AchievementExercisesAdapter(Context context, List<Exercise> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.achievement_exercise_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.nameTv.setText(exercise.getName());
        holder.caloriesTv.setText(String.valueOf(exercise.getCaloriesPerUnit()));
        Glide.with(context).load(exercise.getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTv;
        private TextView caloriesTv;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.achievement_exercise_name_tv);
            caloriesTv = itemView.findViewById(R.id.achievement_exercise_calories_tv);
            image = itemView.findViewById(R.id.achievement_exercise_img);
        }
    }

    public void addAll(List<Exercise> newExercises) {
        exercises.addAll(newExercises);
        notifyDataSetChanged();
    }
}
