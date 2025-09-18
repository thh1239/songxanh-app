package com.example.songxanh.ui.screens.admin_community_screen;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.data.models.Report;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminCommunityVM extends ViewModel {
    MutableLiveData<Integer> reportCount;
    MutableLiveData<ArrayList<Report>> pendingReportList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Query query;

    private int pageSize = 20;
    private DocumentSnapshot lastVisibleDocument;

    public AdminCommunityVM() {
        reportCount = new MutableLiveData<>(0);
        pendingReportList = new MutableLiveData<>();
        fetchPendingReportList();
        fetchReportCount();
        query = db.collection("reports").limit(pageSize);


    }


    public MutableLiveData<Integer> getReportCount() {
        return reportCount;
    }

    public void setReportCount(MutableLiveData<Integer> reportCount) {
        this.reportCount = reportCount;
    }

    public MutableLiveData<ArrayList<Report>> getPendingReportList() {
        return pendingReportList;
    }

    public void setPendingReportList(MutableLiveData<ArrayList<Report>> pendingReportList) {
        this.pendingReportList = pendingReportList;
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public void fetchPendingReportList() {
        db.collection("reports").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Report> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Report report = doc.toObject(Report.class).withId(doc.getId());
                        tempList.add(report);
                    }
                    if (!tempList.isEmpty()) {
                        pendingReportList.postValue(tempList);

                        lastVisibleDocument = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    }
                }
            }
        });
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public void loadMore() {
        Query newQuery;
        if (lastVisibleDocument != null) {
            newQuery = query.startAfter(lastVisibleDocument);
        } else {
            newQuery = query;
        }

        newQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<Report> tempList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Report report = doc.toObject(Report.class).withId(doc.getId());
                        tempList.add(report);
                    }
                    if (!tempList.isEmpty()) {
                        ArrayList<Report> currentList = pendingReportList.getValue();
                        currentList.addAll(tempList);
                        pendingReportList.postValue(currentList);

                        lastVisibleDocument = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    }
                }
            }
        });
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public void fetchReportCount() {
        db.collection("count").document("reports_count").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long count = documentSnapshot.getLong("count");
                    if (count != null) {
                        reportCount.postValue(count.intValue());
                    }
                }
            }
        });
    }

    public void approvePost(int position) {
        Report report = pendingReportList.getValue().get(position);
        deleteFromReportPendingList(position);
    }

    private void deleteFromReportPendingList(int position) {
        db.collection("reports").document(pendingReportList.getValue().get(position).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                ArrayList<Report> temp = pendingReportList.getValue();
                temp.remove(position);
                if(temp.isEmpty()) {
                    pendingReportList.postValue(new ArrayList<>());
                } else {
                    pendingReportList.postValue(temp);
                }
                decreasePendingCountByOne();
            }
        });

    }

    private void decreasePendingCountByOne() {
        int temp = reportCount.getValue();
        reportCount.postValue(temp - 1);
        db.collection("count").document("reports_count").update("count", temp - 1);
    }
// == Xóa dữ liệu hoặc item ==

    public void deletePost(int position) {
        db.collection("achievements").document(pendingReportList.getValue().get(position).getAchievementId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deleteFromReportPendingList(position);
            }
        });
    }

    public interface FetchDataCallback {
        void onCallback(Achievement achievement);
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public void fetchAchievementDetails(int position, FetchDataCallback callback) {
        String achievementId = pendingReportList.getValue().get(position).getAchievementId();
        DocumentReference achievementDocumentReference = db.collection("achievements").document(achievementId);
        achievementDocumentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Achievement achievement = document.toObject(Achievement.class);
                    callback.onCallback(achievement);
                }
            }
        });
    }

}
