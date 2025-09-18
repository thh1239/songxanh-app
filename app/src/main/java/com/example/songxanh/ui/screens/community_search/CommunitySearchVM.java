package com.example.songxanh.ui.screens.community_search;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.NormalUser;
import com.example.songxanh.data.models.User;
import com.example.songxanh.utils.FirebaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CommunitySearchVM extends ViewModel {
    private MutableLiveData<List<NormalUser>> result = new MutableLiveData<>();
// == Tương tác với dịch vụ Firebase ==

    public void search(String keyword) {
        FirebaseConstants.usersRef.whereEqualTo("type", "NORMAL_USER").whereArrayContains("keyword", keyword).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<NormalUser> newResult = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                newResult.add(doc.toObject(NormalUser.class));
                            }
                            result.setValue(newResult);
                        }
                    }
                });
    }

    public MutableLiveData<List<NormalUser>> getResult() {
        return result;
    }

    public void setResult(List<NormalUser> result) {
        this.result.setValue(result);
    }
}
