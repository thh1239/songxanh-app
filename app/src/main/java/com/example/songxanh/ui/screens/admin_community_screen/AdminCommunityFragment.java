package com.example.songxanh.ui.screens.admin_community_screen;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.data.models.Report;
import com.example.songxanh.databinding.FragmentAdminCommunityBinding;

import java.util.ArrayList;

public class AdminCommunityFragment extends Fragment implements
        ReportRecyclerViewAdapter.OnItemDeleteListener,
        ReportRecyclerViewAdapter.OnItemApproveListener,
        ReportRecyclerViewAdapter.OnItemDetailsListener {

    FragmentAdminCommunityBinding binding;
    AdminCommunityVM adminCommunityVM;
    ReportRecyclerViewAdapter adapter;

    private boolean isLoading = false;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;

    public AdminCommunityFragment() {

    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewModelProvider provider = new ViewModelProvider(requireActivity());
        adminCommunityVM = provider.get(AdminCommunityVM.class);
        binding = FragmentAdminCommunityBinding.inflate(inflater, container, false);

        ArrayList<Report> initial = adminCommunityVM.pendingReportList.getValue();
        if (initial == null) initial = new ArrayList<>();
        adapter = new ReportRecyclerViewAdapter(this.getContext(), initial, this, this, this);

        binding.setViewModel(adminCommunityVM);
        binding.reportRecyclerView.setAdapter(adapter);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        binding.reportRecyclerView.setLayoutManager(linearLayoutManager);

        binding.reportRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                Log.d("total item count", "totalItemCount: " + totalItemCount);
                Log.d("last visible item", "lastVisibleItem: " + lastVisibleItem);

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    Log.d("Scrolling", "onScrolled: loadMore is called");
                    loadMore();
                    isLoading = true;
                }
            }
        });

        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    private void loadMore() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
            public void run() {
                adminCommunityVM.loadMore();
                isLoading = false;
            }
        }, 2000);
    }

    @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminCommunityVM.pendingReportList.observe(getViewLifecycleOwner(), new Observer<ArrayList<Report>>() {
            @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
            public void onChanged(ArrayList<Report> reportArrayList) {

                if (reportArrayList == null) reportArrayList = new ArrayList<>();
                adapter.setReportArrayList(reportArrayList);
            }
        });

        adminCommunityVM.reportCount.observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                int value = (integer == null) ? 0 : Math.max(integer, 0);
                String temp = value + " báo cáo còn lại";
                binding.remainingReportsCount.setText(temp);
            }
        });
    }

    @Override
    public void onItemApprove(int position) {

        adminCommunityVM.approvePost(position);
    }

    @Override
// == Xóa dữ liệu hoặc item ==
    public void onItemDelete(int position) {

        adminCommunityVM.deletePost(position);
    }

    @Override
// == Xử lý sự kiện click từ người dùng ==
    public void onItemDetailsClick(int position) {
        adminCommunityVM.fetchAchievementDetails(position, new AdminCommunityVM.FetchDataCallback() {
            @Override
            public void onCallback(Achievement achievement) {
                AdminCommunityFragmentDirections.ActionAdminCommunityFragmentToAchievementDetailsFragment action =
                        AdminCommunityFragmentDirections.actionAdminCommunityFragmentToAchievementDetailsFragment(achievement);
                NavHostFragment.findNavController(AdminCommunityFragment.this).navigate(action);
            }
        });
    }
}
