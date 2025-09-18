package com.example.songxanh.ui.screens.menu;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.R;
import com.example.songxanh.data.models.Ingredient;
import com.example.songxanh.utils.GlobalMethods;

import java.util.ArrayList;

public class IngredientRowRecyclerViewAdapterForAddAndDelete extends RecyclerView.Adapter<IngredientRowRecyclerViewAdapterForAddAndDelete.IngredientRowForAddAndDeleteViewHolder> {

    Context context;
    ArrayList<Ingredient> ingredients = new ArrayList<>();

    private RemoveIngredientClickListener removeIngredientClickListener;

    private OnWeightChangedListener onWeightChangedListener;
// == Xử lý dữ liệu nguyên liệu trong món ăn ==

    public void setOnWeightChangedListener(OnWeightChangedListener listener) {
        this.onWeightChangedListener = listener;
    }
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    public IngredientRowRecyclerViewAdapterForAddAndDelete(Context context, ArrayList<Ingredient> ingredients, RemoveIngredientClickListener removeIngredientClickListener, OnWeightChangedListener onWeightChangedListener) {
        this.context = context;
        this.ingredients = ingredients;
        this.removeIngredientClickListener = removeIngredientClickListener;
        this.onWeightChangedListener = onWeightChangedListener;
    }

    @NonNull
    @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
    public IngredientRowForAddAndDeleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ingredient_row_layout_for_add_and_edit, parent, false);
        return new IngredientRowForAddAndDeleteViewHolder(view);
    }

    @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
    public void onBindViewHolder(@NonNull IngredientRowForAddAndDeleteViewHolder holder, int position) {
        holder.tvIngredientName.setText(ingredients.get(position).getName());
        holder.tvIngredientCalories.setText(GlobalMethods.formatDoubleToString(ingredients.get(position).getCalories()));
        holder.etIngredientWeight.setText(GlobalMethods.formatDoubleToString(ingredients.get(position).getWeight()));
        holder.btnRemoveIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public void onClick(View v) {
                if(removeIngredientClickListener != null) {
                    removeIngredientClickListener.onRemoveIngredientClick(position);

                }
            }
        });
        holder.etIngredientWeight.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
// == Xử lý dữ liệu nguyên liệu trong món ăn ==
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    double newWeight = Double.parseDouble(v.getText().toString());
                    ingredients.get(position).updateWeight(newWeight);
                    notifyItemChanged(position);

                    if (onWeightChangedListener != null) {
                        onWeightChangedListener.onWeightChanged(position, newWeight);
                    }
                } catch (NumberFormatException e) {
                    holder.etIngredientWeight.setText(GlobalMethods.formatDoubleToString(0));
                }

                return true;
            }
        });
    }

    @Override
// == Cập nhật nguyên liệu và tính lại tổng calo ==
    public int getItemCount() {
        return ingredients == null ? 0 : ingredients.size();
    }

    public static class IngredientRowForAddAndDeleteViewHolder extends RecyclerView.ViewHolder {
        TextView tvIngredientName, tvIngredientCalories;
        EditText etIngredientWeight;
        AppCompatButton btnRemoveIngredient;

        public IngredientRowForAddAndDeleteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.add_meal_ingredient_name);
            etIngredientWeight = itemView.findViewById(R.id.add_meal_ingredient_weight);
            tvIngredientCalories = itemView.findViewById(R.id.add_meal_ingredient_calories);
            btnRemoveIngredient = itemView.findViewById(R.id.remove_ingredient_button);
        }
    }

    public interface RemoveIngredientClickListener {
        void onRemoveIngredientClick(int position);
    }

    public interface OnWeightChangedListener {
        void onWeightChanged(int position, double newValue);
    }
}
