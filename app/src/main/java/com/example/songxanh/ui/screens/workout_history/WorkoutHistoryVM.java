package com.example.songxanh.ui.screens.workout_history;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutHistoryVM extends ViewModel {
    private MutableLiveData<Date> selectedDate = new MutableLiveData<>(new Date());
    private MutableLiveData<String> selectedDateInString = new MutableLiveData<>(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()));
    private MutableLiveData<List<Exercise>> exercisesToday = new MutableLiveData<>(new ArrayList<>());
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public WorkoutHistoryVM() {
        loadExercisesFromDb();
    }
// == Tải dữ liệu và hiển thị lên UI ==

    public void loadExercisesFromDb() {
        firestore.collection("users").document(auth.getCurrentUser().getUid())
                .collection("daily_activities").document(GlobalMethods.convertDateToHyphenSplittingFormat(selectedDate.getValue()))
                .collection("workouts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            exercisesToday.setValue(new ArrayList<>());
                            List<Exercise> temp = new ArrayList<>();
                            for (DocumentSnapshot doc : task.getResult()) {
                                temp.add(doc.toObject(Exercise.class));
                            }
                            exercisesToday.setValue(temp);
                        }
                    }
                });
    }

    public MutableLiveData<List<Exercise>> getExercisesToday() {
        return exercisesToday;
    }

    public MutableLiveData<Date> getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        Date currentDate = this.selectedDate.getValue();
        this.selectedDate.setValue(selectedDate);
        this.selectedDateInString.setValue(GlobalMethods.convertDateToHyphenSplittingFormat(selectedDate));
        if (!selectedDate.equals(currentDate)) {
            loadExercisesFromDb();
        }
    }

    public MutableLiveData<String> getSelectedDateInString() {
        return selectedDateInString;
    }

    public void setSelectedDateInString(String selectedDateInString) {
        this.selectedDateInString.setValue(selectedDateInString);
    }
}
