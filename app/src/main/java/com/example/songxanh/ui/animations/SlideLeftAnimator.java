package com.example.songxanh.ui.animations;

import android.animation.ObjectAnimator;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class SlideLeftAnimator extends DefaultItemAnimator {
    @Override
// == Xóa dữ liệu hoặc item ==
    public boolean animateRemove(RecyclerView.ViewHolder holder) {

        View view = holder.itemView;

        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, -view.getWidth());
        animator.setDuration(getRemoveDuration());

        animator.start();

        return false;
    }
}
