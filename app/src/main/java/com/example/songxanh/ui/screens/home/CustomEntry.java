package com.example.songxanh.ui.screens.home;

import com.github.mikephil.charting.data.Entry;

public class CustomEntry extends Entry {
    private String xLabel;

    public CustomEntry(float x, float y, String xLabel) {
        super(x, y);
        this.xLabel = xLabel;
    }

    public String getXLabel() {
        return xLabel;
    }
}
