package com.example.songxanh.ui.animations;

import android.animation.ObjectAnimator;
import android.view.View;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class SlideLeftAnimator extends DefaultItemAnimator {
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        // get view of ViewHolder
        View view = holder.itemView;

        // Set up slide animation
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, -view.getWidth());
        animator.setDuration(getRemoveDuration());

        // start animation
        animator.start();

        // Return false to let the RecyclerView handle the removal of the item
        return false;
    }
}
