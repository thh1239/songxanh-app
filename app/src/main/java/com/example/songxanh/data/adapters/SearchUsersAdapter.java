package com.example.songxanh.data.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.songxanh.R;
import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.data.models.User;
import com.example.songxanh.ui.screens.community_search.CommunitySearchFragmentDirections;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUsersAdapter.ViewHolder> {
    private Context context;
    private List<NormalUser> result;
    private NavController navController;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public SearchUsersAdapter(Context context, List<NormalUser> result, NavController navController) {
        this.context = context;
        this.result = result;
        this.navController = navController;
    }

    @NonNull
    @Override
// == Load ảnh bằng Glide và hiển thị ==
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
// == Load ảnh bằng Glide và hiển thị ==
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (result.get(position).getImageUrl() == null || result.get(position).getImageUrl().isEmpty()) {
            holder.avatarImg.setImageResource(R.drawable.default_profile_image);
        } else {
            Glide.with(context).load(result.get(position).getImageUrl()).into(holder.avatarImg);
        }
        holder.nameTv.setText(result.get(position).getName());
        if (!result.get(position).getFollowers().contains(auth.getCurrentUser().getUid())) {
            holder.isFollowingIc.setVisibility(View.GONE);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
// == Điều hướng sang màn hình khác ==
            public void onClick(View view) {
                CommunitySearchFragmentDirections.ActionCommunitySearchFragmentToCommunityUserProfileFragment action =
                        CommunitySearchFragmentDirections.actionCommunitySearchFragmentToCommunityUserProfileFragment(result.get(position).getUid());
                navController.navigate(action);
            }
        });
    }

    @Override
// == Hiển thị danh sách bằng RecyclerView/Adapter ==
    public int getItemCount() {
        return result.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarImg;
        private TextView nameTv;
        private ImageView isFollowingIc;
        private ConstraintLayout container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarImg = itemView.findViewById(R.id.search_user_avatar_img);
            nameTv = itemView.findViewById(R.id.search_user_name_tv);
            isFollowingIc = itemView.findViewById(R.id.is_following_ic);
            container = itemView.findViewById(R.id.search_user_item_layout);
        }
    }

    public void setData(List<NormalUser> newResults) {
        result.clear();
        result.addAll(newResults);
        notifyDataSetChanged();
    }
}
