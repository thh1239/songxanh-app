package com.example.songxanh.data.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.example.songxanh.R;
import com.example.songxanh.ui.screens.onboarding.OnboardingVM;

public class OnboardingAdapter extends PagerAdapter {
    private Context context;
    private OnboardingVM viewModel;

    public OnboardingAdapter(Context context, OnboardingVM viewModel) {
        this.context = context;
        this.viewModel = viewModel;
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public int getCount() {
        return viewModel.getSize();
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
// == Quản lý dữ liệu bằng ViewModel ==
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.onboarding_slider_layout, container, false);

        LottieAnimationView onboardingImg = view.findViewById(R.id.onboardingImg);
        onboardingImg.setAnimation(viewModel.getImage(position));

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
