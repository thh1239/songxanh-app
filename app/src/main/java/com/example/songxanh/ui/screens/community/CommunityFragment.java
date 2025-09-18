package com.example.songxanh.ui.screens.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.songxanh.R;
import com.example.songxanh.data.adapters.AchievementsListAdapter;
import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.databinding.FragmentCommunityBinding;
import com.example.songxanh.ui.interfaces.ActionOnAchievementMenu;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment implements ActionOnAchievementMenu {
    private FragmentCommunityBinding binding;
    private CommunityVM viewModel;
    private AchievementsListAdapter adapter;

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        binding.setCommunityVM(viewModel);
        viewModel = new ViewModelProvider(requireActivity()).get(CommunityVM.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        adapter = new AchievementsListAdapter(requireContext(), new ArrayList<>(), NavHostFragment.findNavController(this), this);
        binding.achievementLst.setAdapter(adapter);
        binding.achievementLst.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel.getAchievements().observe(getViewLifecycleOwner(), new Observer<List<Achievement>>() {
            @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
            public void onChanged(List<Achievement> achievements) {
                adapter.setData(achievements);
            }
        });

        setOnClick();

        return binding.getRoot();
    }

    private void setOnClick() {
        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavHostFragment.findNavController(CommunityFragment.this).navigate(R.id.action_communityFragment_to_workoutShareAchievementFragment);
            }
        });

        binding.followingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavHostFragment.findNavController(CommunityFragment.this).navigate(R.id.action_communityFragment_to_communityFollowingFragment2);
            }
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                NavHostFragment.findNavController(CommunityFragment.this).navigate(R.id.action_communityFragment_to_communitySearchFragment);
            }
        });
    }

    @Override
    public void showPopupMenu(Achievement achievement, View button) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), button);
        popupMenu.inflate(R.menu.achievement_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.achievement_menu_1:
                        CommunityFragmentDirections.ActionCommunityFragmentToCommunityReportFragment ToCommunityReportFragmentAction =
                                CommunityFragmentDirections.actionCommunityFragmentToCommunityReportFragment(achievement);
                        NavHostFragment.findNavController(CommunityFragment.this).navigate(ToCommunityReportFragmentAction);
                        return true;
                    case R.id.achievement_menu_2:
                        CommunityFragmentDirections.ActionCommunityFragmentToCommunityUserProfileFragment ToCommunityUserProfileFragmentAction =
                                CommunityFragmentDirections.actionCommunityFragmentToCommunityUserProfileFragment(achievement.getUserId());
                        NavHostFragment.findNavController(CommunityFragment.this).navigate(ToCommunityUserProfileFragmentAction);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
}