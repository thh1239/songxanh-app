package com.example.songxanh.ui.screens.admin_edit_exercises_screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.utils.GlobalMethods;

import java.util.ArrayList;

public class AdminExerciseRecyclerViewAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Exercise> exerciseArrayList;

    OnDeleteClick onDeleteClick;
    OnInfoClick onInfoClick;
    OnEditClick onEditClick;

    public AdminExerciseRecyclerViewAdapter(Context context, ArrayList<Exercise> exerciseArrayList, OnDeleteClick onDeleteClick, OnInfoClick onInfoClick, OnEditClick onEditClick) {
        this.context = context;
        this.exerciseArrayList = exerciseArrayList;
        this.onDeleteClick = onDeleteClick;
        this.onInfoClick = onInfoClick;
        this.onEditClick = onEditClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.admin_edit_exercise_item_layout, parent, false);
        return new AdminExerciseRecyclerViewAdapterViewHolder(view);
    }

    @Override
// == Load ảnh bằng Glide và hiển thị ==
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdminExerciseRecyclerViewAdapterViewHolder viewHolder = (AdminExerciseRecyclerViewAdapterViewHolder) holder;
        Exercise temp = exerciseArrayList.get(position);
        Glide.with(context).load(temp.getImageUrl()).into(viewHolder.exerciseImg);

        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onClick(View v) {
                if(onDeleteClick != null) {
                    onDeleteClick.onDelete(position);
                }
            }
        });
        viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onClick(View v) {
                if(onEditClick!= null) {
                    onEditClick.onEdit(position);
                }
            }
        });
        viewHolder.exerciseNameTv.setText(temp.getName());
        String tempString;
        if(temp.getUnit().equals("reps")) {
            tempString = "x" + temp.getCount();
        } else {
            tempString = temp.getCount() + " seconds";
        }
        viewHolder.exerciseNumberTv.setText(tempString);
        viewHolder.exerciseCaloriesTv.setText(GlobalMethods.formatDoubleToString(temp.getCaloriesPerUnit()));
    }
// == Tính toán và hiển thị tổng calo ==

    public void setExerciseArrayList(ArrayList<Exercise> exercises) {
        this.exerciseArrayList = exercises;
        notifyDataSetChanged();
    }

    @Override
// == Tính toán và hiển thị tổng calo ==
    public int getItemCount() {
        return exerciseArrayList == null ? 0 : exerciseArrayList.size();
    }

    public static class AdminExerciseRecyclerViewAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView exerciseImg;
        ImageButton deleteBtn, editBtn;
        TextView exerciseNameTv, exerciseNumberTv, exerciseCaloriesTv;

        public AdminExerciseRecyclerViewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseImg = itemView.findViewById(R.id.exercise_image);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            editBtn = itemView.findViewById(R.id.edit_exercise_btn);
            exerciseNameTv = itemView.findViewById(R.id.exercise_name_tv);
            exerciseNumberTv = itemView.findViewById(R.id.exercise_time_or_rep_tv);
            exerciseCaloriesTv = itemView.findViewById(R.id.exercise_calories_tv);
        }
    }

    public interface OnDeleteClick {
        public void onDelete(int position);
    }

    public interface OnInfoClick {
        public void onInfo(int position);
    }

    public interface OnEditClick {
        public void onEdit(int position);
    }
}
