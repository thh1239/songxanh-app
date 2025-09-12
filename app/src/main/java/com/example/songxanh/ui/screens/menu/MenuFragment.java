package com.example.songxanh.ui.screens.menu;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.songxanh.R;
import com.example.songxanh.data.models.Dish;
import com.example.songxanh.data.models.Ingredient;
import com.example.songxanh.databinding.FragmentMenuBinding;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class MenuFragment extends Fragment implements DishRecycleViewAdapter.MealOptionsClickListener, DishRecycleViewAdapter.AddIngredientClickListener, DishRecycleViewAdapter.MealOptionDialogListener, DateAdapter.OnDateClickListener {

    MenuVM menuVM;
    private FragmentMenuBinding binding;
    DishRecycleViewAdapter adapter;
    DateAdapter dateAdapter;
    List<Date> dates;


    private Double totalCalories = 0.0;


    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        menuVM = provider.get(MenuVM.class);
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        binding.setViewModel(menuVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        dates = initializeDatesList();

        // Remove the observer before setting up a new one
        menuVM.getFirestoreDishes().removeObservers(this);

        adapter = new DishRecycleViewAdapter(this.getContext(), menuVM.getFirestoreDishes().getValue(), true, this);
        binding.meals.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.meals.setAdapter(adapter);
        dateAdapter = new DateAdapter(dates, this);
        dateAdapter.setSelectedPosition(2);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 5) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        binding.dateSlider.setLayoutManager(gridLayoutManager);
        binding.dateSlider.setAdapter(dateAdapter);

        updateDatesList(Calendar.getInstance().getTime());
        int centerPos = 2;
        binding.dateSlider.scrollToPosition(centerPos);
        binding.addMealButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddMealClick();
            }
        });
        binding.calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                                Date date = new GregorianCalendar(year, month, dayOfMonth).getTime();
                                updateDatesList(date);
                                int centerPosition = 2;
                                dateAdapter.setSelectedPosition(centerPosition);
                                dateAdapter.notifyDataSetChanged();
                                binding.dateSlider.scrollToPosition(centerPosition);
                                if (GlobalMethods.isToday(date)) {
                                    adapter = new DishRecycleViewAdapter(requireContext(), menuVM.getFirestoreDishes().getValue(), true, MenuFragment.this);
                                    binding.meals.setLayoutManager(new LinearLayoutManager(requireContext()));
                                    adapter.notifyDataSetChanged();
                                    binding.meals.setAdapter(adapter);
                                    binding.meals.requestLayout();
                                    binding.displayDate.setText("Today");
                                    binding.menuTodayCalories.setText(GlobalMethods.formatDoubleToString(totalCalories));
                                } else {
                                    fetchDishes(date);
                                }
                            }
                        }, year, month, day
                );

                dialog.show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuVM.getFirestoreDishes().observe(getViewLifecycleOwner(), new Observer<ArrayList<Dish>>() {
            @Override
            public void onChanged(ArrayList<Dish> dishArrayList) {
                adapter.setDishes(dishArrayList);
                totalCalories = 0.0;
                for (Dish dish : dishArrayList
                ) {
                    totalCalories += dish.getCalories();
                    binding.menuTodayCalories.setText(GlobalMethods.formatDoubleToString(totalCalories));
                }
            }
        });
    }

    public void onAddMealClick() {
        NavHostFragment.findNavController(MenuFragment.this).navigate(R.id.action_menuFragment_to_addMealFragment);
    }

    @Override
    public void onEditMealClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        NavHostFragment.findNavController(MenuFragment.this).navigate(R.id.action_menuFragment_to_editMealFragment, bundle);
    }

    @Override
    public void onAddIngredientClick(int position) {

    }

    @Override
    public void onDeleteMealClick(int position) {
        menuVM.getFirestoreDishes().deleteDish(menuVM.getFirestoreDishes().getValue().get(position));
    }

    @Override
    public void onCancelClick(int position) {

    }

    @Override
    public void onMealOptionClick(int position) {

    }

    private void updateDatesList(Date chosenDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(chosenDate);
        calendar.add(Calendar.DATE, -2);
        dates.clear();
        for (int i = 0; i < 5; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
    }

    private List<Date> initializeDatesList() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2); // Go back two days from today (or chosen date)

        for (int i = 0; i < 5; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }

        return dates;
    }

    @Override
    public void onDateClick(Date date) {
        updateDatesList(date);
        int centerPosition = 2;
        dateAdapter.setSelectedPosition(centerPosition);
        dateAdapter.notifyDataSetChanged();
        binding.dateSlider.scrollToPosition(centerPosition);
        if (GlobalMethods.isToday(date)) {
            adapter = new DishRecycleViewAdapter(this.getContext(), menuVM.getFirestoreDishes().getValue(), true, this);
            binding.meals.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapter.notifyDataSetChanged();
            binding.meals.setAdapter(adapter);
            binding.meals.requestLayout();
            binding.displayDate.setText("Today");
            binding.menuTodayCalories.setText(GlobalMethods.formatDoubleToString(totalCalories));
        } else {
            fetchDishes(date);
        }

    }

    private void fetchDishes(Date date) {
        ArrayList<Dish> dishes = new ArrayList<>();
        AtomicDouble totalCalories = new AtomicDouble(0.0);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference userRef = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        CollectionReference dailyActivitiesRef = userRef.collection("daily_activities");
        DocumentReference dailyActivityRef = dailyActivitiesRef.document(GlobalMethods.convertDateToHyphenSplittingFormat(date));
        MenuFragment menuFragment = this;
        dailyActivityRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<Map<String, Object>> dishesMapLists = (List<Map<String, Object>>) document.get("dishes");
                        if (dishesMapLists != null) {
                            for (Map<String, Object> dishMap : dishesMapLists) {
                                Dish dish = new Dish();
                                dish.setName((String) dishMap.get("name"));
                                dish.setSession((String) dishMap.get("session"));
                                List<Map<String, Object>> ingredientsMapList = (List<Map<String, Object>>) dishMap.get("ingredients");
                                if (ingredientsMapList == null || ingredientsMapList.isEmpty()) {
                                    continue;
                                }
                                List<Ingredient> ingredients = new ArrayList<>();
                                double dishTotalCalories = 0;
                                double dishTotalCarb = 0;
                                double dishTotalLipid = 0;
                                double dishTotalProtein = 0;
                                for (Map<String, Object> ingredientMap : ingredientsMapList) {
                                    Ingredient ingredient = new Ingredient();
                                    ingredient.setName((String) ingredientMap.get("name"));
                                    ingredient.setCalories(((Number) ingredientMap.get("calories")).doubleValue());
                                    ingredient.setCarb(((Number) ingredientMap.get("carb")).doubleValue());
                                    ingredient.setLipid(((Number) ingredientMap.get("lipid")).doubleValue());
                                    ingredient.setProtein(((Number) ingredientMap.get("protein")).doubleValue());
                                    ingredient.setWeight(((Number) ingredientMap.get("weight")).doubleValue());
                                    dishTotalCalories += ingredient.getCalories();
                                    dishTotalCarb += ingredient.getCarb();
                                    dishTotalLipid += ingredient.getLipid();
                                    dishTotalProtein += ingredient.getProtein();
                                    ingredients.add(ingredient);
                                }
                                dish.setIngredients(ingredients);
                                dish.setCalories(dishTotalCalories);
                                dish.setCarb(dishTotalCarb);
                                dish.setLipid(dishTotalLipid);
                                dish.setProtein(dishTotalProtein);
                                dishes.add(dish);
                                totalCalories.addAndGet(dishTotalCalories);
                            }
                        }
                    }
                }

                adapter = new DishRecycleViewAdapter(menuFragment.getContext(), dishes, false, menuFragment);
                adapter.notifyDataSetChanged();
                binding.meals.setLayoutManager(new LinearLayoutManager(requireContext()));
                binding.meals.setAdapter(adapter);
                binding.meals.requestLayout();
                if (GlobalMethods.isToday(date)) {
                    binding.displayDate.setText("Today");
                } else {
                    binding.displayDate.setText(GlobalMethods.convertDateToHyphenSplittingFormat(date));
                }
                binding.menuTodayCalories.setText(GlobalMethods.formatDoubleToString(totalCalories.doubleValue()));
            }
        });

    }
}