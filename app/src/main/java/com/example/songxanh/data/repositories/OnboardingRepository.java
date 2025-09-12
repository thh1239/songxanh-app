package com.example.songxanh.data.repositories;

import com.example.songxanh.R;

public class OnboardingRepository {
    private int[] images = {
            R.raw.onboarding1,
            R.raw.onboarding2,
            R.raw.onboarding3
    };

    private int[] titles = {
            R.string.onboarding_title_1,
            R.string.onboarding_title_2,
            R.string.onboarding_title_3
    };

    private int[] subtitles = {
            R.string.onboarding_subtitle_1,
            R.string.onboarding_subtitle_2,
            R.string.onboarding_subtitle_3
    };

    public int getSize() {
        return titles.length;
    }

    public int getImage(int position) {
        return images[position];
    }

    public int getTitle(int position) {
        return titles[position];
    }

    public int getSubTitle(int position) {
        return subtitles[position];
    }
}
