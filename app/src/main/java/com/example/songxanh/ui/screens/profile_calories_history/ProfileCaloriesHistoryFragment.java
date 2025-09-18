package com.example.songxanh.ui.screens.profile_calories_history;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.songxanh.R;
import com.example.songxanh.databinding.FragmentProfileCaloriesHistoryBinding;
import com.example.songxanh.ui.screens.home.CustomEntry;
import com.example.songxanh.ui.screens.home.CustomEntryLineChart;
import com.example.songxanh.ui.screens.home.HomeVM;
import com.example.songxanh.ui.screens.profile_personal_info.ProfilePersonalInfoFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ProfileCaloriesHistoryFragment extends Fragment {
    private FragmentProfileCaloriesHistoryBinding binding;
    private ProfileCaloriesHistoryVM profileCaloriesHistoryVM;
    private LineChart lineChart;

    public ProfileCaloriesHistoryFragment() {

    }


    @Override
// == Tính toán và hiển thị tổng calo ==
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileCaloriesHistoryVM = new ViewModelProvider(requireActivity()).get(ProfileCaloriesHistoryVM.class);
        profileCaloriesHistoryVM.getUserLiveData();
    }

    @Override
// == Tính toán và hiển thị tổng calo ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileCaloriesHistoryBinding.inflate(inflater,container,false);
        binding.setViewModel(profileCaloriesHistoryVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        lineChart = binding.lineChart;
        profileCaloriesHistoryVM.getIsLoadingLine().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onChanged(Boolean isLoadingLine) {
                if (isLoadingLine != null && !isLoadingLine) {
                    drawLine();
                }
            }
        });
        binding.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
// == Tính toán và hiển thị tổng calo ==
            public void onClick(View view) {
                NavController navController = NavHostFragment.findNavController(ProfileCaloriesHistoryFragment.this);
                navController.popBackStack();
            }
        });
        return binding.getRoot();
    }

    private void drawLine() {
        ArrayList<CustomEntryLineChart> entries = new ArrayList<>();
        entries = profileCaloriesHistoryVM.getLineEntries1();

        List<Entry> entryList = new ArrayList<>();
        for (CustomEntryLineChart customEntry : entries) {
            entryList.add(new Entry(customEntry.getX(), customEntry.getSteps()));
        }

        LineDataSet dataSet = new LineDataSet(entryList, "Calories");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        LineData lineData = new LineData(dataSet);

        String[] labels = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            labels[i] = entries.get(i).getDate();
        }

        IndexAxisValueFormatter xAxisFormatter = new IndexAxisValueFormatter(labels);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setGranularity(1);

        xAxis.setAxisMaximum(entries.size() - 1);


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        lineChart.setData(lineData);
        lineChart.animateXY(1000, 1000, Easing.EaseInOutBounce);

        Description description = new Description();
        description.setText("Calories per day");
        description.setTextColor(getResources().getColor(R.color.primaryColor, null));
        lineChart.setDescription(description);

        Legend legend = lineChart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.primaryColor, null));

        dataSet.setValueTextColor(getResources().getColor(R.color.primaryColor, null));
        dataSet.setLineWidth(2f);
        lineChart.getAxisLeft().setTextColor(getResources().getColor(R.color.primaryTextColor, null));
        lineChart.getAxisRight().setTextColor(Color.TRANSPARENT);
        lineChart.getXAxis().setTextColor(getResources().getColor(R.color.primaryTextColor, null));

        lineChart.invalidate();
    }
}