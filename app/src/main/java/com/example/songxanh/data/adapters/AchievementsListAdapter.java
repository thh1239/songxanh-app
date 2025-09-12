package com.example.songxanh.data.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.ui.interfaces.ActionOnAchievementMenu;
import com.example.songxanh.ui.screens.community.CommunityFragmentDirections;
import com.example.songxanh.utils.GlobalMethods;

import java.util.List;

public class AchievementsListAdapter extends RecyclerView.Adapter<AchievementsListAdapter.ViewHolder> {
    private Context context;
    private List<Achievement> achievements;
    private NavController navController;
    private ActionOnAchievementMenu menuAction;

    public AchievementsListAdapter(Context context, List<Achievement> achievements, NavController navController, ActionOnAchievementMenu menuAction) {
        this.context = context;
        this.achievements = achievements;
        this.navController = navController;
        this.menuAction = menuAction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.achievement_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.userNameTv.setText(achievement.getUserName());
        holder.dateTv.setText(GlobalMethods.convertDateToHyphenSplittingFormat(achievement.getCreatedTime()));
        holder.totalCaloriesTv.setText(String.valueOf(achievement.getCalories()));
        holder.stepsTv.setText(String.valueOf(achievement.getSteps()));
        holder.exercisesCaloriesTv.setText(String.valueOf(achievement.getExerciseCalories()));
        holder.foodCaloriesTv.setText(String.valueOf(achievement.getFoodCalories()));

        if (achievement.getUserImageUrl() == null || achievement.getUserImageUrl().isEmpty()) {
            holder.avatarImg.setImageResource(R.drawable.default_profile_image);
        } else {
            Glide.with(context).load(achievement.getUserImageUrl()).into(holder.avatarImg);
        }

        holder.detailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.songxanh.ui.screens.community.CommunityFragmentDirections.ActionCommunityFragmentToAchievementDetailsFragment action =
                        CommunityFragmentDirections.actionCommunityFragmentToAchievementDetailsFragment(achievement);
                navController.navigate(action);
            }
        });

        holder.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuAction.showPopupMenu(achievement, holder.menuBtn);
            }
        });
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userNameTv;
        private ImageView avatarImg;
        private TextView dateTv;
        private TextView totalCaloriesTv;
        private TextView stepsTv;
        private TextView exercisesCaloriesTv;
        private TextView foodCaloriesTv;
        private ImageView detailsBtn;
        private ImageView menuBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTv = itemView.findViewById(R.id.name_tv);
            avatarImg = itemView.findViewById(R.id.avatar_img);
            dateTv = itemView.findViewById(R.id.date_tv);
            totalCaloriesTv = itemView.findViewById(R.id.achievement_total_calories_tv);
            stepsTv = itemView.findViewById(R.id.steps_tv);
            exercisesCaloriesTv = itemView.findViewById(R.id.exercise_calories_tv);
            foodCaloriesTv = itemView.findViewById(R.id.food_calories_tv);
            detailsBtn = itemView.findViewById(R.id.details_btn);
            menuBtn = itemView.findViewById(R.id.achievement_menu_btn);
        }
    }

    public void setData(List<Achievement> newAchievements) {
        achievements.clear();
        achievements.addAll(newAchievements);
        notifyDataSetChanged();
    }
}
