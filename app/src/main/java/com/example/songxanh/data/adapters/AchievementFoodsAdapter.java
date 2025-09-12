package com.example.songxanh.data.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.R;
import com.example.songxanh.data.models.Dish;

import java.util.List;

public class AchievementFoodsAdapter extends RecyclerView.Adapter<AchievementFoodsAdapter.ViewHolder> {
    private Context context;
    private List<Dish> dishes;

    public AchievementFoodsAdapter(Context context, List<Dish> dishes) {
        this.context = context;
        this.dishes = dishes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.achievement_food_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dish dish = dishes.get(position);
        holder.nameTv.setText(dish.getName());
        holder.caloriesTv.setText(String.valueOf(dish.getCalories()));
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView foodImage;
        private TextView nameTv;
        private TextView caloriesTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.achievement_food_img);
            nameTv = itemView.findViewById(R.id.achievement_food_name_tv);
            caloriesTv = itemView.findViewById(R.id.achievement_food_calories_tv);
        }
    }
}
