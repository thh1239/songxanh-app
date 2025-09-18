package com.example.songxanh.ui.screens.home_update_weight;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentHomeBinding;
import com.example.songxanh.databinding.FragmentHomeWeightReportBinding;
import com.example.songxanh.ui.screens.home.CustomEntry;
import com.example.songxanh.ui.screens.home.HomeVM;
import com.example.songxanh.ui.screens.workout_history.WorkoutHistoryFragment;
import com.example.songxanh.utils.GlobalMethods;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class HomeUpdateWeightFragment extends Fragment {
    private HomeUpdateWeightVM homeUpdateWeightVM;
    private FragmentHomeWeightReportBinding binding;
    private BarChart barChart;
    private HomeVM homeVM = new HomeVM();

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeUpdateWeightVM = new ViewModelProvider(requireActivity()).get(HomeUpdateWeightVM.class);
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeWeightReportBinding.inflate(inflater, container, false);
        binding.setViewModel(homeUpdateWeightVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        binding.weightReportBack.setOnClickListener(new View.OnClickListener() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(HomeUpdateWeightFragment.this);
            }
        });

        binding.updateWeightDailyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onClick(View view) {
                String addWeightValue = binding.addWeightDaily.getText().toString();
                if (!addWeightValue.isEmpty()) {
                    int weight = Integer.valueOf(addWeightValue);
                    homeUpdateWeightVM.saveDailyWeight(weight,
                            homeVM.getSteps(),
                            homeVM.getExerciseCalories(),
                            homeVM.getCalories(),
                            homeVM.getFoodCalories());
                    Toast.makeText(getContext(), "Update today's weight successfully", Toast.LENGTH_LONG).show();
                    binding.addWeightDaily.setText("");

                    homeUpdateWeightVM.loadBarData();
                } else {
                    Toast.makeText(getContext(), "Please enter a weight value", Toast.LENGTH_LONG).show();
                }
            }
        });


        barChart = binding.barChart;
        homeUpdateWeightVM.getIsLoadingData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoadingData) {
                if (isLoadingData != null && !isLoadingData) {
                    drawWeightChart(homeUpdateWeightVM.getBarEntries());
                } else {

                }
            }
        });

        return binding.getRoot();
    }

    private void drawWeightChart(List<CustomEntry> entries) {
        if (!entries.isEmpty()) {
            List<BarEntry> barEntries = new ArrayList<>();

            for (int i = 0; i < entries.size(); i++) {
                CustomEntry entry = entries.get(i);
                barEntries.add(new BarEntry(entry.getX(), entry.getY()));
            }

            BarDataSet dataSet = new BarDataSet(barEntries, "Weight");
            dataSet.setValueTextColor(getResources().getColor(R.color.primaryTextColor, null));
            BarData barData = new BarData(dataSet);

            barChart.setData(barData);
            barChart.animateXY(1000, 1000, Easing.EaseInOutBounce);
            barChart.getDescription().setText("Weight by Date");
            barChart.getDescription().setTextColor(getResources().getColor(R.color.primaryTextColor, null));

            XAxis xAxis = barChart.getXAxis();
            xAxis.setGranularity(1f); // hiển thị 1 giá trị mỗi lần
            xAxis.setDrawGridLines(false); // cái này để xóa grid

            Legend legend = barChart.getLegend();
            legend.setTextColor(getResources().getColor(R.color.primaryTextColor, null));

            xAxis.setValueFormatter(new IndexAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    int index = (int) value;
                    if (index >= 0 && index < entries.size()) {
                        CustomEntry entry = entries.get(index);
                        return entry.getXLabel();
                    }
                    return "";
                }
            });

            xAxis.setTextColor(getResources().getColor(R.color.primaryTextColor, null));

            YAxis leftAxis = barChart.getAxisLeft();
            YAxis rightAxis = barChart.getAxisRight();

            leftAxis.setTextColor(getResources().getColor(R.color.primaryTextColor, null));
            rightAxis.setTextColor(getResources().getColor(R.color.primaryTextColor, null));

            ValueFormatter weightValueFormatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return value + "kg";
                }
            };

            leftAxis.setValueFormatter(weightValueFormatter);
            rightAxis.setValueFormatter(weightValueFormatter);

            barChart.invalidate();
        }
    }
}
