package com.example.songxanh.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class FirebaseConstants {
    public static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static CollectionReference usersRef = firestore.collection("users");
    public static CollectionReference communityRef = firestore.collection("community");
    public static CollectionReference dailyActivitiesRef = firestore.collection("users").document(firebaseAuth.getCurrentUser().getUid())
            .collection("daily_activities");
    public static CollectionReference todaySelectedExercisesRef = dailyActivitiesRef.document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()))
            .collection("today_selected_exercises");
    public static DocumentReference todayActivities = dailyActivitiesRef.document(GlobalMethods.convertDateToHyphenSplittingFormat(new Date()));
    public static CollectionReference achievementsRef = firestore.collection("achievements");
    public static CollectionReference reportsRef = firestore.collection("reports");
    public static CollectionReference exerciseCategoriesRef = firestore.collection("workout_categories");
}

