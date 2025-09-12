package com.example.songxanh.data.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.DailyActivity;
import com.example.songxanh.data.models.FollowingActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityFollowingAdapter extends RecyclerView.Adapter<CommunityFollowingAdapter.ViewHolder> {
    private Context context;
    private List<FollowingActivity> activities;

    public CommunityFollowingAdapter(Context context, List<FollowingActivity> activities) {
        this.context = context;
        this.activities = activities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.following_today_activity_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FollowingActivity activity = activities.get(position);
        if (activity.getUserAvatarUrl() != null) {
            Glide.with(context).load(activity.getUserAvatarUrl()).into(holder.avatarImg);
        } else {
            holder.avatarImg.setImageResource(R.drawable.default_profile_image);
        }
        holder.nameTv.setText(activity.getUserName());
        holder.stepsTv.setText(String.valueOf(activity.getSteps()));
        holder.exerciseCaloriesTv.setText(String.valueOf(activity.getExerciseCalories()));
        holder.foodCaloriesTv.setText(String.valueOf(activity.getFoodCalories()));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarImg;
        private TextView nameTv;
        private TextView stepsTv;
        private TextView exerciseCaloriesTv;
        private  TextView foodCaloriesTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImg = itemView.findViewById(R.id.following_user_avatar_img);
            nameTv = itemView.findViewById(R.id.following_user_name_tv);
            stepsTv = itemView.findViewById(R.id.following_steps_tv);
            exerciseCaloriesTv  = itemView.findViewById(R.id.following_exercise_calories_tv);
            foodCaloriesTv  = itemView.findViewById(R.id.following_food_calories_tv);
        }
    }

    public void addAll(List<FollowingActivity> newActivities) {
        activities.clear();
        activities.addAll(newActivities);
        notifyDataSetChanged();
    }
}
