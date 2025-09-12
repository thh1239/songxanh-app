package com.example.songxanh.ui.screens.menu;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.R;

import java.util.Date;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private List<Date> dates;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private final OnDateClickListener onDateClickListener;

    public DateAdapter(List<Date> dates, OnDateClickListener onDateClickListener) {
        this.dates = dates;
        this.onDateClickListener = onDateClickListener;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.date_item, parent, false);

//        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
//        int itemWidth = (int) (screenWidth * 0.2);
//        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
//        layoutParams.width = itemWidth;
//        itemView.setLayoutParams(layoutParams);
        return new DateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        Date date = dates.get(position);

        String day = (String) android.text.format.DateFormat.format("dd", date);
        String month = (String) android.text.format.DateFormat.format("MMM", date);

        holder.dayTextView.setText(day);
        holder.monthTextView.setText(month);
        holder.itemView.setOnClickListener(v -> {
            if (selectedItems.get(holder.getAdapterPosition(), false)) {
                selectedItems.delete(holder.getAdapterPosition());
            } else {
                selectedItems.clear();
                selectedItems.put(holder.getAdapterPosition(), true);
            }
            notifyDataSetChanged();
            onDateClickListener.onDateClick(date);
        });
        holder.itemView.setSelected(selectedItems.get(position, false));
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public void updateDates(Date newDate) {
        dates.add(newDate);
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        selectedItems.clear();
        selectedItems.put(position, true);
        notifyDataSetChanged();
    }

    public Date getSelectedDate() {
        int selectedPosition = selectedItems.keyAt(0);
        return dates.get(selectedPosition);
    }
    public class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        TextView monthTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.day);
            monthTextView = itemView.findViewById(R.id.month);
        }
    }

    public interface OnDateClickListener {
        void onDateClick(Date date);
    }
}
