package com.example.songxanh.ui.screens.exercise_detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.DailyActivity;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.data.models.ExerciseCategory;
import com.example.songxanh.utils.FirebaseConstants;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExerciseDetailVM extends ViewModel {
    private MutableLiveData<DailyActivity> todayActivity = new MutableLiveData<>();
    private MutableLiveData<List<Exercise>> workouts = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoaded = new MutableLiveData<>(false);
    private MutableLiveData<List<ExerciseCategory>> categories = new MutableLiveData<>();

    public ExerciseDetailVM() {
        loadDataFromFb();
    }
// == Tương tác với dịch vụ Firebase ==

    public void loadDataFromFb() {
        FirebaseConstants.dailyActivitiesRef.document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date())).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            todayActivity.setValue(task.getResult().toObject(DailyActivity.class));
                            task.getResult().getReference().collection("workouts").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
// == Tương tác với dịch vụ Firebase ==
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                List<Exercise> temp = new ArrayList<>();
                                                for (DocumentSnapshot doc : task.getResult()) {
                                                    temp.add(doc.toObject(Exercise.class));
                                                }
                                                workouts.setValue(temp);
                                            }
                                        }
                                    });
                        }
                    }
                });

        FirebaseConstants.exerciseCategoriesRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ExerciseCategory> temp = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                temp.add(doc.toObject(ExerciseCategory.class));
                            }
                            categories.setValue(temp);
                        }
                    }
                });
    }

    public MutableLiveData<DailyActivity> getTodayActivity() {
        return todayActivity;
    }

    public MutableLiveData<List<Exercise>> getWorkouts() {
        return workouts;
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public MutableLiveData<Boolean> getIsLoaded() {
        return isLoaded;
    }

    public MutableLiveData<List<ExerciseCategory>> getCategories() {
        return categories;
    }

    public void setCategories(MutableLiveData<List<ExerciseCategory>> categories) {
        this.categories = categories;
    }
}
