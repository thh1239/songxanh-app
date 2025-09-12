package com.example.songxanh.ui.screens.menu;

import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.custom_livedata.FirestoreDishes;

public class MenuVM extends ViewModel {
    private FirestoreDishes firestoreDishes;
    public FirestoreDishes getFirestoreDishes() {
        return firestoreDishes;
    }
    public MenuVM() {
        firestoreDishes = new FirestoreDishes();
    }
}
