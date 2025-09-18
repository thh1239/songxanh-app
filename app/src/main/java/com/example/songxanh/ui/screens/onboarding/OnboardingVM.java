package com.example.songxanh.ui.screens.onboarding;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.repositories.OnboardingRepository;

public class OnboardingVM extends ViewModel {
    private OnboardingRepository repository = new OnboardingRepository();
    private MutableLiveData<Integer> titleLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> subtitleLiveData = new MutableLiveData<>();

    public OnboardingVM() {
        updateTitleAndSubtitle(0);
    }
// == Cập nhật dữ liệu và UI liên quan ==

    public void updateTitleAndSubtitle(int position) {
        titleLiveData.setValue(getTitle(position));
        subtitleLiveData.setValue(getSubTitle(position));
    }

    public int getImage(int position) {
        return repository.getImage(position);
    }

    public int getTitle(int position) {
        return repository.getTitle(position);
    }

    public int getSubTitle(int position) {
        return repository.getSubTitle(position);
    }

    public int getSize() {
        return repository.getSize();
    }

    public MutableLiveData<Integer> getTitleLiveData() {
        return titleLiveData;
    }

    public MutableLiveData<Integer> getSubtitleLiveData() {
        return subtitleLiveData;
    }
}
