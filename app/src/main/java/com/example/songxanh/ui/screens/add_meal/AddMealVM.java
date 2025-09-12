package com.example.songxanh.ui.screens.add_meal;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Dish;
import com.example.songxanh.data.models.Ingredient;

import java.util.ArrayList;

public class AddMealVM extends ViewModel {
    MutableLiveData<ArrayList<Ingredient>> ingredients = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Ingredient>> getIngredients() {
        return ingredients;
    }

    public void setIngredients(MutableLiveData<ArrayList<Ingredient>> ingredients) {
        this.ingredients = ingredients;
    }

}
