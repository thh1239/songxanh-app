package com.example.songxanh.ui.screens.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.data.models.User;
import com.example.songxanh.databinding.FragmentHomeBinding;
import com.example.songxanh.ui.screens.MainVM;
import com.example.songxanh.ui.screens.workout.WorkoutVM;
import com.example.songxanh.utils.GlobalMethods;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {
    public static final String PREF_FILE_NAME = "theme_pref";
    public static final String THEME_KEY = "theme_mode";
    private HomeVM homeVM;
    private MainVM mainVM;
    private MutableLiveData<NormalUser> user = new MutableLiveData<>();
    private FragmentHomeBinding binding;
    private WorkoutVM workoutVM;

    private PieChart pieChart;
    private LinearLayout legendLayout;
    private LineChart lineChart;
    private List<String> legendEntries;
    private List<Integer> legendValues;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private int stepCount = 0;
    private Date currentDate = new Date();
    private static final float STEP_THRESHOLD = 8.0f;
    private float previousX = 0.0f, previousY = 0.0f, previousZ = 0.0f;
    private long previousTimestamp = 0;
    Date previousDate;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String todayMsg = "TỔNG KCAL HÔM NAY: ";

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ON CREATE HOME FRAGMENT", "CREATING");
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        homeVM = new ViewModelProvider(requireActivity()).get(HomeVM.class);
        binding.setViewModel(homeVM);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        mainVM = new ViewModelProvider(requireActivity()).get(MainVM.class);
        mainVM.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
// == Quản lý dữ liệu bằng ViewModel ==
            public void onChanged(User user) {
                if (user != null) {
                    homeVM.setUser(mainVM.getUser());
                    homeVM.loadDocument();
                    homeVM.loadLineData();
                }
            }
        });

        workoutVM = new ViewModelProvider(requireActivity()).get(WorkoutVM.class);
        workoutVM.initDailyActivity();

        lineChart = binding.lineChart;
        pieChart = binding.pieChart;
        legendLayout = binding.legendLayout;

        homeVM.getIsLoadingDocument().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoadingDocument) {
                if (isLoadingDocument != null && !isLoadingDocument) {
                    setLoading();
                    drawPie();
                }
            }
        });
        homeVM.getIsLoadingLine().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoadingLine) {
                if (isLoadingLine != null && !isLoadingLine) {
                    drawLine();
                }
            }
        });

        binding.updateWeightBtn.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment_to_homeUpdateWeightFragment)
        );
        binding.exerciseDetailBtn.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment_to_excerciseDetail)
        );

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (stepSensor != null) {
            sensorManager.registerListener(accelerometerSensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(requireContext(), "Step counter is not available on your device", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        long previousDateMillis = sharedPreferences.getLong("previousDate", 0);
        previousDate = new Date(previousDateMillis);
        stepCount = sharedPreferences.getInt("stepCount", 0);
        binding.stepCountTextView.setText(String.valueOf(stepCount));

        binding.setThemeButton.setOnClickListener(v -> {
            SharedPreferences sp = getActivity().getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
            boolean isDarkTheme = !sp.getBoolean(THEME_KEY, true);
            sp.edit().putBoolean(THEME_KEY, isDarkTheme).apply();
            getActivity().recreate();
        });

        return binding.getRoot();
    }

    private void drawPie() {
        legendEntries = new ArrayList<>();
        legendEntries.add("Kcal cần nạp");
        legendEntries.add("Kcal từ đồ ăn");
        legendEntries.add("Kcal đã đốt");

        legendValues = new ArrayList<>();
        legendValues.add(homeVM.getRemaining().intValue());
        legendValues.add(homeVM.getFoodCalories().intValue());
        legendValues.add(homeVM.getExerciseCalories().intValue());

        List<Integer> iconResources = new ArrayList<>();
        iconResources.add(R.drawable.home_target);
        iconResources.add(R.drawable.home_food);
        iconResources.add(R.drawable.home_calories);

        List<PieEntry> entries = homeVM.getPieEntries();

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#0DBBFC"));
        colors.add(Color.parseColor("#69E6A6"));
        colors.add(Color.parseColor("#FFAA7E"));

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.TRANSPARENT);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setHoleRadius(70f);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.transparent);
        pieChart.setTransparentCircleRadius(58f);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(android.R.color.black);

        int food = Math.max(0, Math.round(homeVM.getFoodCalories()));
        int exercise = Math.max(0, Math.round(homeVM.getExerciseCalories()));
        int totalToday = Math.max(0, food - exercise);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(todayMsg + totalToday + " kcal");
        pieChart.setCenterTextSize(13f);
        pieChart.setCenterTextColor(getResources().getColor(R.color.primaryTextColor, null));
        pieChart.animateXY(1000, 1000, Easing.EaseInOutBounce);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        for (int i = 0; i < entries.size(); i++) {
            String legendEntry = legendEntries.get(i < legendEntries.size() ? i : 0);
            int legendValue = legendValues.get(i < legendValues.size() ? i : 0);

            View legendItemView = LayoutInflater.from(getContext()).inflate(R.layout.legend_item, null);
            ImageView legendIconView = legendItemView.findViewById(R.id.legendIcon);
            TextView legendLabelTextView = legendItemView.findViewById(R.id.legendLabel);
            TextView legendValueTextView = legendItemView.findViewById(R.id.legendValue);

            legendIconView.setImageResource(iconResources.get(i));
            legendLabelTextView.setText(legendEntry);
            legendLabelTextView.setTextColor(getResources().getColor(R.color.primaryTextColor, null));
            legendValueTextView.setText(legendValue + " kcal");
            legendValueTextView.setTextColor(getResources().getColor(R.color.primaryTextColor, null));

            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            itemParams.setMargins(10, 10, 0, 6);
            legendItemView.setLayoutParams(itemParams);

            legendLayout.addView(legendItemView);
        }

        pieChart.invalidate();
    }

    private void setLoading() {
        binding.userNameTv.setText(homeVM.getUser().getValue().getName());

        binding.exerciseTv.setText(Math.round(homeVM.getExerciseCalories()) + " kcal");
        binding.dailyCalories.setText(GlobalMethods.formatDoubleToString(homeVM.getDailyCalories()) + " kcal");

        binding.startWeight.setText(GlobalMethods.formatDoubleToString(homeVM.getStartWeight()) + " kg");
        binding.goalWeight.setText(GlobalMethods.formatDoubleToString(homeVM.getGoalWeight()) + " kg");
        binding.currentWeight.setText(GlobalMethods.formatDoubleToString(homeVM.getWeight()) + " kg");

        if (binding.lightCalories != null) {
            binding.lightCalories.setText(GlobalMethods.formatDoubleToString(homeVM.getActivityLight()) + " " + getString(R.string.kcal_suffix));
        }
        if (binding.moderateCalories != null) {
            binding.moderateCalories.setText(GlobalMethods.formatDoubleToString(homeVM.getActivityModerate()) + " " + getString(R.string.kcal_suffix));
        }
        if (binding.heavyCalories != null) {
            binding.heavyCalories.setText(GlobalMethods.formatDoubleToString(homeVM.getActivityHeavy()) + " " + getString(R.string.kcal_suffix));
        }

        if (mainVM.getUserImageUrl() == null) {
            binding.userAvatar.setImageResource(R.drawable.default_profile_image);
        } else {
            Glide.with(requireContext())
                    .load(homeVM.getUser().getValue().getImageUrl())
                    .into(binding.userAvatar);
        }
    }

    private final SensorEventListener accelerometerSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0], y = event.values[1], z = event.values[2];
            float acceleration = Math.abs(x + y + z - previousX - previousY - previousZ);

            if (acceleration > STEP_THRESHOLD) {
                long currentTimestamp = System.currentTimeMillis();
                if (currentTimestamp - previousTimestamp > 300) {
                    stepCount++;
                    previousTimestamp = currentTimestamp;
                }
            }
            previousX = x; previousY = y; previousZ = z;

            binding.stepCountTextView.setText(String.valueOf(stepCount));
        }
        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
// == Reset dữ liệu hằng ngày khi quay lại màn hình ==
    public void onResume() {
        super.onResume();

        homeVM.loadDocument();

        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        long previousDateMillis = sharedPreferences.getLong("previousDate", 0);
        previousDate = new Date(previousDateMillis);
        currentDate = new Date();

        if (!isSameDay(currentDate, previousDate)) {
            stepCount = 0;
            binding.stepCountTextView.setText(String.valueOf(stepCount));
            sharedPreferences.edit()
                    .putLong("previousDate", currentDate.getTime())
                    .putInt("stepCount", stepCount)
                    .apply();
        }
    }

    @Override
// == Reset dữ liệu hằng ngày khi quay lại màn hình ==
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt("stepCount", stepCount)
                .putLong("previousDate", currentDate.getTime())
                .apply();

        SensorManager sm = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sm != null) sm.unregisterListener(accelerometerSensorEventListener);

        homeVM.saveDailySteps(stepCount, previousDate);
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance();
        cal1.setTime(date1); cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
                && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private void drawLine() {
        ArrayList<CustomEntryLineChart> entries = homeVM.getLineEntries1();

        List<Entry> entryList = new ArrayList<>();
        for (CustomEntryLineChart customEntry : entries) {
            entryList.add(new Entry(customEntry.getX(), customEntry.getSteps()));
        }

        LineDataSet dataSet = new LineDataSet(entryList, "Steps");
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setColors(getResources().getColor(R.color.primaryColor, null));
        dataSet.setValueTextSize(10);
        dataSet.setValueTextColor(getResources().getColor(R.color.primaryTextColor, null));
        LineData lineData = new LineData(dataSet);

        String[] labels = new String[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            labels[i] = entries.get(i).getDate();
        }

        IndexAxisValueFormatter xAxisFormatter = new IndexAxisValueFormatter(labels);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setGranularity(1);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(entries.size() - 1);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        lineChart.setData(lineData);
        lineChart.animateXY(1000, 1000, Easing.EaseInOutBounce);

        Description description = new Description();
        description.setText("Steps per day");
        description.setTextColor(getResources().getColor(R.color.primaryTextColor, null));
        lineChart.setDescription(description);

        Legend legend = lineChart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.primaryTextColor, null));

        dataSet.setColor(getResources().getColor(R.color.primaryColor, null));
        dataSet.setLineWidth(2f);
        lineChart.getAxisLeft().setTextColor(getResources().getColor(R.color.primaryTextColor, null));
        lineChart.getAxisRight().setTextColor(Color.TRANSPARENT);
        lineChart.getXAxis().setTextColor(getResources().getColor(R.color.primaryTextColor, null));

        lineChart.invalidate();
    }
}
