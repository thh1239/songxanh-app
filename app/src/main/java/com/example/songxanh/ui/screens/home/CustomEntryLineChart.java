package com.example.songxanh.ui.screens.home;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomEntryLineChart implements Comparable<CustomEntryLineChart> {
    private int x;
    private int steps;
    private String date;

    public CustomEntryLineChart(int x, int steps, String date) {
        this.x = x;
        this.steps = steps;
        this.date = date;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(CustomEntryLineChart other) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM");
        try {
            Date date1 = format.parse(this.getDate());
            Date date2 = format.parse(other.getDate());
            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
