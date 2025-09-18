package com.example.songxanh.ui.screens.community_share_achievement;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.songxanh.data.models.Achievement;
import com.example.songxanh.data.models.User;
import com.example.songxanh.utils.FirebaseConstants;
import com.example.songxanh.utils.GlobalMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

public class WorkoutShareAchievementVM extends ViewModel {
    private final MutableLiveData<Achievement> todayAchievement = new MutableLiveData<>(null);
    private final MutableLiveData<String> warningDialogMessage = new MutableLiveData<>("");
    private final MutableLiveData<Boolean> isAddedSuccessfully = new MutableLiveData<>(false);

    public WorkoutShareAchievementVM() {
        loadTodayAchievement();
    }
// == Xác thực người dùng với FirebaseAuth ==

    public void loadTodayAchievement() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String dateKey = GlobalMethods.convertDateToHyphenSplittingFormat(new Date());

        CollectionReference dailyActivitiesRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("daily_activities");

        dailyActivitiesRef.document(dateKey).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        Achievement newAchievement = task.getResult().toObject(Achievement.class);
                        if (newAchievement != null) {
                            newAchievement.setId(task.getResult().getId());
                            todayAchievement.postValue(newAchievement);
                        } else {
                            Log.w("WorkoutShareAchievementVM", "Dữ liệu Achievement null sau khi parse.");
                        }
                    } else {
                        Log.w("WorkoutShareAchievementVM", "Không tìm thấy dữ liệu daily_activities hôm nay.");
                    }
                });
    }
// == Xác thực người dùng với FirebaseAuth ==

    public void addAchievementToDb(User user) {
        Achievement achievementData = todayAchievement.getValue();

        if (achievementData == null) {
            warningDialogMessage.setValue("Không có dữ liệu thành tích hôm nay để chia sẻ.");
            return;
        }

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date startOfNextDate = calendar.getTime();

        FirebaseConstants.achievementsRef
                .whereEqualTo("userId", FirebaseConstants.firebaseAuth.getCurrentUser().getUid())
                .whereGreaterThanOrEqualTo("createdTime", startOfDate)
                .whereLessThan("createdTime", startOfNextDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Achievement achievement = new Achievement(
                                    achievementData.getCalories(),
                                    achievementData.getSteps(),
                                    achievementData.getExerciseCalories(),
                                    achievementData.getFoodCalories(),
                                    user.getUid(),
                                    user.getName(),
                                    user.getImageUrl(),
                                    GlobalMethods.convertDateToHyphenSplittingFormat(new Date()),
                                    new Date()
                            );

                            FirebaseConstants.achievementsRef.add(achievement)
                                    .addOnCompleteListener(addTask -> {
                                        if (addTask.isSuccessful()) {
                                            isAddedSuccessfully.setValue(true);
                                        } else {
                                            warningDialogMessage.setValue("Không thể chia sẻ thành tích. Vui lòng thử lại.");
                                        }
                                    });
                        } else {
                            warningDialogMessage.setValue("Bạn đã chia sẻ thành tích hôm nay rồi!");
                        }
                    } else {
                        warningDialogMessage.setValue("Lỗi khi kiểm tra thành tích đã chia sẻ.");
                    }
                });
    }

    public MutableLiveData<Achievement> getTodayAchievement() {
        return todayAchievement;
    }

    public void resetWarningMessage() {
        warningDialogMessage.setValue("");
    }

    public MutableLiveData<String> getWarningDialogMessage() {
        return warningDialogMessage;
    }
// == Thêm mới dữ liệu hoặc item ==

    public MutableLiveData<Boolean> getIsAddedSuccessfully() {
        return isAddedSuccessfully;
    }
}