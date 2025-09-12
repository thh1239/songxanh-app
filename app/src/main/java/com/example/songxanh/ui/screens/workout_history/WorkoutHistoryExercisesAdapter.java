package com.example.songxanh.ui.screens.workout_history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.utils.GlobalMethods;

import java.util.List;

public class WorkoutHistoryExercisesAdapter extends RecyclerView.Adapter<WorkoutHistoryExercisesAdapter.ViewHolder> {
    private Context context;
    private List<Exercise> todayExercises;
    private NavController navController;

    public WorkoutHistoryExercisesAdapter(Context context, List<Exercise> todayExercises, NavController navController) {
        this.context = context;
        this.todayExercises = todayExercises;
        this.navController = navController;
    }

    public void setNewData(List<Exercise> newExercises) {
        todayExercises.clear();
        todayExercises.addAll(newExercises);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.exercise_list_item_layout, parent, false);
        return new ViewHolder(view);
    };

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = todayExercises.get(position);
        Glide.with(context).load(exercise.getImageUrl()).into(holder.image);
        holder.name.setText(exercise.getName().toUpperCase());
        holder.timeOrRep.setText(GlobalMethods.formatTimeOrRep(exercise.getCount(), exercise.getUnit()));
        holder.calories.setText(String.valueOf(exercise.getCaloriesPerUnit()) + " cal");
        holder.informationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.songxanh.ui.screens.workout_categories_exercises.WorkoutCategoryExercisesFragmentDirections.ActionWorkoutCategoryExercisesFragmentToWorkoutExerciseDetailsFragment action =
                        com.example.songxanh.ui.screens.workout_categories_exercises.WorkoutCategoryExercisesFragmentDirections.actionWorkoutCategoryExercisesFragmentToWorkoutExerciseDetailsFragment(exercise);
                navController.navigate(action);
            }
        });
    }

    @Override
    public int getItemCount() {
        return todayExercises.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView timeOrRep;
        private TextView calories;
        private ImageView informationBtn;
        private LinearLayoutCompat container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.exercise_image);
            name = itemView.findViewById(R.id.exercise_name_tv);
            timeOrRep = itemView.findViewById(R.id.exercise_time_or_rep_tv);
            calories = itemView.findViewById(R.id.exercise_calories_tv);
            informationBtn = itemView.findViewById(R.id.exercise_information_btn);
            container = itemView.findViewById(R.id.container);
        }
    }
}
