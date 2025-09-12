package com.example.songxanh.ui.screens.menu;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.R;
import com.example.songxanh.data.models.Dish;
import com.example.songxanh.utils.GlobalMethods;

import java.util.ArrayList;

public class DishRecycleViewAdapter extends RecyclerView.Adapter<DishRecycleViewAdapter.MealViewHolder> {
    Context context;

    private MealOptionDialogListener mealOptionDialogListener;
    private boolean allowOperation;

    public void setMealOptionDialogListener(MealOptionDialogListener listener) {
        this.mealOptionDialogListener = listener;
    }

    public void setDishes(ArrayList<Dish> dishes) {
        this.dishes = dishes;
        notifyDataSetChanged();
    }

    ArrayList<Dish> dishes = new ArrayList<Dish>();

    private MealOptionsClickListener mealOptionsClickListener;
    private AddIngredientClickListener addIngredientClickListener;

    public DishRecycleViewAdapter(Context context, ArrayList<Dish> dishArrayList,boolean allowOperation, MealOptionDialogListener mealOptionDialogListener ) {
        this.context = context;
        this.dishes = dishArrayList;
        this.allowOperation = allowOperation;
        this.mealOptionDialogListener = mealOptionDialogListener;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.meal_layout, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        holder.tvDishName.setText(dishes.get(position).getName());
        holder.tvMealCalories.setText(GlobalMethods.formatDoubleToString(dishes.get(position).getCalories()));

        IngredientRowRecyclerViewAdapter ingredientRowRecyclerViewAdapter = new IngredientRowRecyclerViewAdapter(context, dishes.get(position).getIngredients());
        holder.rvIngredients.setLayoutManager(new LinearLayoutManager(context));
        holder.rvIngredients.setAdapter(ingredientRowRecyclerViewAdapter);
        if(!allowOperation) {
            holder.btnMealOption.setVisibility(View.GONE);
        }
        holder.btnMealOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(context, position);
            }
        });

        Drawable startDrawable;
        switch (dishes.get(position).getSession()) {

            case "Breakfast":
                startDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_breakfast);
                break;
            case "Lunch":
                startDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_lunch);
                break;
            case "Dinner":
                startDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_dinner);
                break;
            default:
                startDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_breakfast);

        }
        holder.tvDishName.setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable, null, null, null);

    }

    private void showDialog(Context context, int position) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.meal_options_popup_layout);
        dialog.show();

        Button editMeal = (Button) dialog.findViewById(R.id.dialog_edit_meal_button);
        Button deleteMeal = (Button) dialog.findViewById(R.id.dialog_delete_meal_button);
        Button btnCancel = (Button) dialog.findViewById(R.id.dialog_cancel_button);

        editMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mealOptionDialogListener != null) {
                    mealOptionDialogListener.onEditMealClick(position);
                }
                dialog.dismiss();
            }
        });

        deleteMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mealOptionDialogListener != null) {
                    mealOptionDialogListener.onDeleteMealClick(position);
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mealOptionDialogListener != null) {
                    dialog.dismiss();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return dishes == null ? 0 : dishes.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvDishName, tvMealCalories;
        RecyclerView rvIngredients;
        AppCompatButton btnMealOption, btnAddIngredient;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDishName = itemView.findViewById(R.id.menu_dish_name);
            tvMealCalories = itemView.findViewById(R.id.meal_calories);
            rvIngredients = itemView.findViewById(R.id.meal_ingredients_list);
            btnMealOption = itemView.findViewById(R.id.meal_options);
        }
    }

    public interface MealOptionsClickListener {
        void onMealOptionClick(int position);
    }

    public interface AddIngredientClickListener {
        void onAddIngredientClick(int position);
    }

    public interface MealOptionDialogListener {
        void onEditMealClick(int position);

        void onAddIngredientClick(int position);

        void onDeleteMealClick(int position);
        void onCancelClick(int position);
    }
}
