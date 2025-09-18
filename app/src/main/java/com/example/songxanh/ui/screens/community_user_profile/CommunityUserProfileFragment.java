package com.example.songxanh.ui.screens.community_user_profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.adapters.AchievementsListAdapter;
import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.databinding.FragmentCommunityUserProfileBinding;
import com.example.songxanh.ui.interfaces.ActionOnAchievementMenu;
import com.example.songxanh.utils.GlobalMethods;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class CommunityUserProfileFragment extends Fragment implements ActionOnAchievementMenu {
    private FragmentCommunityUserProfileBinding binding;
    private CommunityUserProfileVM viewModel;
    private AchievementsListAdapter adapter;

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCommunityUserProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(CommunityUserProfileVM.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        String uidFromPreviousFragment = CommunityUserProfileFragmentArgs.fromBundle(getArguments()).getUid();
        viewModel.loadUser(uidFromPreviousFragment);
        viewModel.loadAchievements(uidFromPreviousFragment);

        viewModel.getUser().observe(getViewLifecycleOwner(), new Observer<NormalUser>() {
            @Override
// == Load ảnh bằng Glide và hiển thị ==
            public void onChanged(NormalUser user) {
                if (user != null) {
                    if (user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
                        binding.userProfileAvatarImg.setImageResource(R.drawable.default_profile_image);
                    } else {
                        Glide.with(requireContext()).load(user.getImageUrl()).into(binding.userProfileAvatarImg);
                    }

                    if (user.getFollowers().contains(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        binding.followBtn.setText("Followed");
                    } else {
                        binding.followBtn.setText("Follow");
                    }
                }
            }
        });

        adapter = new AchievementsListAdapter(requireContext(), new ArrayList<>(), NavHostFragment.findNavController(this), this);
        binding.profileUserAchievementLst.setAdapter(adapter);
        binding.profileUserAchievementLst.setLayoutManager(new LinearLayoutManager(requireContext()));

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
        binding.appBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(View view) {
                GlobalMethods.backToPreviousFragment(CommunityUserProfileFragment.this);
            }
        });
    }

    @Override
    public void showPopupMenu(Achievement achievement, View button) {

    }
}