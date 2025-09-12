package com.example.songxanh.ui.screens.admin_community_screen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.Report;
import com.example.songxanh.utils.GlobalMethods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportRecyclerViewAdapter extends RecyclerView.Adapter {

    Context context;

    ArrayList<Report> reportArrayList;

    OnItemApproveListener onItemApproveListener;
    OnItemDeleteListener onItemDeleteListener;

    OnItemDetailsListener onItemDetailsListener;

    public ReportRecyclerViewAdapter(Context context, ArrayList<Report> reportArrayList, OnItemApproveListener onItemApproveListener, OnItemDeleteListener onItemDeleteListener, OnItemDetailsListener onItemDetailsListener) {
        this.context = context;
        this.reportArrayList = reportArrayList;
        this.onItemApproveListener = onItemApproveListener;
        this.onItemDeleteListener = onItemDeleteListener;
        this.onItemDetailsListener = onItemDetailsListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.report_layout, parent,false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReportViewHolder reportViewHolder = (ReportViewHolder) holder;
        Report report = reportArrayList.get(position);
        Log.d("reporter avatar", "onBindViewHolder: " + report.getReportUserImageUrl());
        Log.d(" avatar", "onBindViewHolder: " + report.getAchievementUserImageUrl());

        reportViewHolder.tvReportTitle.setText(report.getTitle());
        reportViewHolder.tvReportedUsername.setText(report.getAchievementUserName());
//        reportViewHolder.tvNumberOfViolations
        reportViewHolder.tvDate.setText(new SimpleDateFormat("dd MMM yyyy").format(report.getAchievementCreatedTime()));
        reportViewHolder.tvCalories.setText(GlobalMethods.formatDoubleToString(report.getAchievementCalories()));
        reportViewHolder.tvSteps.setText(String.valueOf(report.getAchievementSteps()));
        reportViewHolder.tvFoodCalories.setText(GlobalMethods.formatDoubleToString(report.getAchievementFoodCalories()));
        reportViewHolder.tvExercisesCalories.setText(GlobalMethods.formatDoubleToString(report.getAchievementExerciseCalories()));
        reportViewHolder.tvReportDescription.setText(report.getDescription());
        reportViewHolder.tvReporterUsername.setText(report.getReportUserName());
        if(report.getAchievementUserImageUrl() == null|| report.getAchievementUserImageUrl().equals("")) {
            reportViewHolder.reportedUserImg.setImageResource(R.drawable.default_profile_image);
        } else {
            Glide.with(context).load(report.getAchievementUserImageUrl()).into(reportViewHolder.reportedUserImg);
        }
        if(report.getReportUserImageUrl() == null || report.getReportUserImageUrl().equals("")) {
            reportViewHolder.reporterImg.setImageResource(R.drawable.default_profile_image);
        } else {
            Glide.with(context).load(report.getReportUserImageUrl()).into(reportViewHolder.reporterImg);
        }
        reportViewHolder.approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveItem(position);
            }
        });
        reportViewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
            }
        });
        reportViewHolder.tvReportDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemDetailsListener!=null) {
                    onItemDetailsListener.onItemDetailsClick(position);
                }
            }
        });
    }

    public void approveItem(int position) {
        if(onItemApproveListener != null) {
            onItemApproveListener.onItemApprove(position);
        }
    }

    public void deleteItem(int position) {
        if(onItemDeleteListener != null) {
            onItemDeleteListener.onItemDelete(position);
        }
    }

    @Override
    public int getItemCount() {
        return reportArrayList == null ? 0 : reportArrayList.size();
    }

    public interface OnItemApproveListener {
        void onItemApprove(int position);
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public interface OnItemDetailsListener {
        void onItemDetailsClick(int position);
    }

    public void setReportArrayList(ArrayList<Report> reportArrayList) {
        this.reportArrayList = reportArrayList;
        notifyDataSetChanged();
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {

        TextView tvReportTitle, tvReportedUsername, tvNumberOfViolations, tvDate, tvCalories, tvSteps, tvFoodCalories, tvExercisesCalories, tvReportDescription, tvReporterUsername, tvReportDetails;
        CircleImageView reportedUserImg, reporterImg;

        AppCompatButton approveBtn, deleteBtn;
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportTitle = itemView.findViewById(R.id.pending_report_title);
            tvReportedUsername = itemView.findViewById(R.id.reported_user_name);
            tvDate = itemView.findViewById(R.id.violation_date);
            tvCalories = itemView.findViewById(R.id.reported_calories);
            tvSteps = itemView.findViewById(R.id.steps_number);
            tvFoodCalories = itemView.findViewById(R.id.food_calories_number);
            tvExercisesCalories = itemView.findViewById(R.id.exercise_calories_number);
            tvReportDescription = itemView.findViewById(R.id.report_description);
            tvReporterUsername = itemView.findViewById(R.id.reporter_user_name);
            tvReportDetails = itemView.findViewById(R.id.report_details);
            reportedUserImg = itemView.findViewById(R.id.reported_account_img);
            reporterImg = itemView.findViewById(R.id.reporter_avatar);
            approveBtn = itemView.findViewById(R.id.approve_post_button);
            deleteBtn = itemView.findViewById(R.id.delete_post_button);
        }
    }
}
