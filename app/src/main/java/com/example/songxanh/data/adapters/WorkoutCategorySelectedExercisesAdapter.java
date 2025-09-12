package com.example.songxanh.data.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.ui.interfaces.ActionOnExerciseItem;
import com.example.songxanh.utils.GlobalMethods;

import java.util.List;

public class WorkoutCategorySelectedExercisesAdapter extends RecyclerView.Adapter<WorkoutCategorySelectedExercisesAdapter.ViewHolder> {
    private List<Exercise> selectedExercises;
    private Context context;
//    private NavController navController;
    private ActionOnExerciseItem action;

    public WorkoutCategorySelectedExercisesAdapter(Context context, List<Exercise> selectedExercises, ActionOnExerciseItem actionOnExerciseItem) {
        this.selectedExercises = selectedExercises;
        this.context = context;
//        this.navController = navController;
        this.action = actionOnExerciseItem;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView timeOrRep;
        private TextView calories;
        private ImageButton informationBtn;
        private ImageButton deleteBtn;
        private LinearLayoutCompat container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.exercise_image);
            name = itemView.findViewById(R.id.exercise_name_tv);
            timeOrRep = itemView.findViewById(R.id.exercise_time_or_rep_tv);
            calories = itemView.findViewById(R.id.exercise_calories_tv);
            informationBtn = itemView.findViewById(R.id.exercise_information_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            container = itemView.findViewById(R.id.container);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.exercise_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Exercise exercise = selectedExercises.get(position);

        Glide.with(context).load(exercise.getImageUrl()).into(holder.image);
        holder.name.setText(exercise.getName().toUpperCase());
        holder.timeOrRep.setText(GlobalMethods.formatTimeOrRep(exercise.getCount(), exercise.getUnit()));
        holder.calories.setText(String.valueOf(exercise.getCaloriesPerUnit()) + " cal");
        holder.informationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.onInformationBtn(exercise);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.onDelete(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedExercises.size();
    }

    public void setData(List<Exercise> exercises) {
        selectedExercises = exercises;
    }

    public void removeItem(int position) {
        selectedExercises.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, selectedExercises.size() - position);
    }
}
