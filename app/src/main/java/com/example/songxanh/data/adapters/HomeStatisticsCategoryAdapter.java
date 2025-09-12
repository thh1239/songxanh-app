package com.example.songxanh.data.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.songxanh.R;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.data.models.ExerciseCategory;

import java.util.List;

public class HomeStatisticsCategoryAdapter extends BaseAdapter {
    private Context context;
    private List<ExerciseCategory> categories;
    private List<Exercise> exercises;

    public HomeStatisticsCategoryAdapter(Context context, List<ExerciseCategory> categories, List<Exercise> exercises) {
        this.context = context;
        this.categories = categories;
        this.exercises = exercises;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(R.layout.home_statistics_category_item, viewGroup, false);
        TextView caloriesTv = view.findViewById(R.id.category_burned_calories_tv);
        TextView nameTv = view.findViewById(R.id.category_name_tv);

        ExerciseCategory category = categories.get(i);
        nameTv.setText(category.getName());
        int textColor = context.getResources().getColor(R.color.primaryTextColor, null);
        nameTv.setTextColor(textColor);
        int count = 0;
        for (int index = 0; index < exercises.size(); index ++) {
            if (exercises.get(index).getCategoryId().equals(category.getId())) {
                count++;
            }
        }
        caloriesTv.setText(String.valueOf(count));
        caloriesTv.setTextColor(textColor);
        return view;
    }

    public void setCategories(List<ExerciseCategory> newCategories) {
        this.categories.clear();
        this.categories.addAll(newCategories);
        notifyDataSetChanged();

    }

    public void setExercises(List<Exercise> newExercises) {
        this.exercises.clear();
        this.exercises.addAll(newExercises);
        notifyDataSetChanged();
    }
}
