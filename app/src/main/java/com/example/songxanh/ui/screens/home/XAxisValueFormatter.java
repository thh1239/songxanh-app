package com.example.songxanh.ui.screens.home;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class XAxisValueFormatter extends ValueFormatter {
    private final String[] labels;

    public XAxisValueFormatter(String[] labels) {
        this.labels = labels;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        int index = (int) value;
        if (index >= 0 && index < labels.length) {
            return labels[index];
        }
        return "";
    }
}

