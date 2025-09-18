package com.example.songxanh.data.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.utils.GlobalMethods;

import java.util.List;

public class ExercisePracticingPageAdapter extends PagerAdapter {
    private Context context;
    private List<Exercise> exercises;
    private ViewPager viewPager;


    public ExercisePracticingPageAdapter(Context context,  List<Exercise> exercises) {
        this.context = context;
        this.exercises = exercises;
    }
// == Load ảnh bằng Glide và hiển thị ==
    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
// == Load ảnh bằng Glide và hiển thị ==
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Exercise exercise = exercises.get(position);
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.workout_practicing_page_layout, container, false);

        TextView name = layout.findViewById(R.id.exercise_practicing_name_tv);        ImageView image = layout.findViewById(R.id.exercise_practicing_image);

        TextView startingPosition = layout.findViewById(R.id.exercise_practicing_starting_position_tv);
        TextView execution = layout.findViewById(R.id.exercise_practicing_execution_tv);
        Glide.with(context).load(exercise.getImageUrl()).into(image);
        name.setText(exercise.getName().toUpperCase());





        startingPosition.setText(exercise.getStartingPosition());
        execution.setText(exercise.getExecution());

        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return exercises.size();
    }



    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
