package com.example.songxanh.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.songxanh.R;
import com.example.songxanh.data.models.Exercise;
import com.example.songxanh.ui.screens.workout_exercise_practicing.PracticingOnBackDialogInterface;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class GlobalMethods {

    public static void backToPreviousFragment(Fragment fragment) {
        if (fragment != null && fragment.isAdded()) {
            try {
                NavHostFragment.findNavController(fragment).popBackStack();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    public static Map<String, Object> toKeyValuePairs(Object instance) {
        return Arrays.stream(instance.getClass().getDeclaredFields())
                .collect(Collectors.toMap(
                        Field::getName,
                        field -> {
                            try {
                                Object result = null;
                                field.setAccessible(true);
                                result = field.get(instance);
                                return result != null ? result : "";
                            } catch (Exception e) {
                                return "";
                            }
                        }));
    }

    public static String formatDoubleToString(double value) {
        long rounded = Math.round(safeNumber(value));
        return String.valueOf(rounded);
    }
// == Tính toán và hiển thị tổng calo ==

    public static String convertDateToSlashSplittingFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }
// == Tính toán và hiển thị tổng calo ==

    public static String convertDateToHyphenSplittingFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }
// == Tính toán và hiển thị tổng calo ==

    public static String formatTimeOrRep(int count, String unitType) {
        if (unitType.equals("reps")) {
            return "x" + String.valueOf(count);
        } else {
            int minute = count / 60;
            return String.format("%02d", minute) + ":" + String.valueOf(count % 60);
        }
    }
// == Tính toán và hiển thị tổng calo ==

    public static int calculateTotalCalories(List<Exercise> exercises) {
        int totalCalories = 0;
        for (Exercise exercise : exercises) {
            totalCalories += exercise.getCaloriesPerUnit();
        }
        return totalCalories;
    }

    public static boolean isToday(Date date) {
        LocalDate today = LocalDate.now();
        LocalDate givenDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return today.isEqual(givenDate);
    }

    public static String convertTimeInSeconds(int timeInSeconds) {
        int hour = timeInSeconds / 3600;
        int minute = (timeInSeconds - hour * 3600) / 60;
        int second = timeInSeconds - hour * 3600 - minute * 60;
        return (hour == 0 ? "" : String.valueOf(hour) + ":")
                + (minute == 0 ? "00:" : String.valueOf(minute) + ":")
                + (second < 10 ? "0" : "") + String.valueOf(second);
    }

    public static void showWarningDialog(Context context, String message, PracticingOnBackDialogInterface onBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Warning");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(DialogInterface dialogInterface, int i) {
                onBack.onPositiveButton();
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
// == Xử lý sự kiện click từ người dùng ==
            public void onClick(DialogInterface dialogInterface, int i) {
                onBack.onNegativeButton();
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_warning);
        AlertDialog warningDialog = builder.create();
        warningDialog.show();
    }

    public static List<String> generateKeyword(String name) {
        String nameInLowerCase = name.toLowerCase(Locale.ROOT);
        String[] words = nameInLowerCase.split(" ");
        List<String> generatedStrings = new ArrayList<>();

        int startPosition = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            for (int j = startPosition; j < nameInLowerCase.length(); j++) {
                sb.append(nameInLowerCase.charAt(j));
                if (sb.charAt(sb.length() - 1) != ' ') {
                    generatedStrings.add(sb.toString());
                }
            }
            sb.setLength(0);
            startPosition += words[i].length() + 1;
        }

        return generatedStrings;
    }
// == Tính toán và hiển thị tổng calo ==

    public static double bmrMifflin(boolean isMale, int weightKg, double heightCm, int ageYears) {
        double bmr = isMale
                ? (10 * weightKg + 6.25 * heightCm - 5 * ageYears + 5)
                : (10 * weightKg + 6.25 * heightCm - 5 * ageYears - 161);
        return safeNumber(bmr);
    }
// == Tính toán và hiển thị tổng calo ==

    public static double tdee(double bmr, double activityFactor) {
        return safeNumber(bmr * activityFactor);
    }
// == Tính toán và hiển thị tổng calo ==

    public static double clamp(double v, double lo, double hi) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return lo;
        if (hi < lo) return v;
        return Math.max(lo, Math.min(hi, v));
    }

    public static double calculateDailyCalories(String gender,
                                                int weight,
                                                double height,
                                                int age,
                                                int goalWeight,
                                                Date startDate,
                                                Date goalDate) {
        if (weight <= 0 || height <= 0 || age <= 0 || goalWeight <= 0) return 0d;
        if (startDate == null || goalDate == null) return 0d;

        LocalDate localStart = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localGoal  = goalDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        long daysBetween = ChronoUnit.DAYS.between(localStart, localGoal);
        if (daysBetween <= 0) return 0d;

        boolean isMale = normalizeGender(gender);
        double bmr = bmrMifflin(isMale, weight, height, age);

        double activityFactor = 1.375; // mặc định: nhẹ (light)
        double tdee = tdee(bmr, activityFactor);

        double kgDiff = goalWeight - weight;   // âm: giảm cân; dương: tăng cân
        double kcalPerKg = 7700d;
        double deltaPerDay = (kgDiff * kcalPerKg) / (double) daysBetween;

        deltaPerDay = clamp(deltaPerDay, -1000d, 1000d);

        double daily = tdee + deltaPerDay;

        daily = Math.max(daily, bmr);

        daily = safeNumber(daily);
        if (daily < 0d) daily = 0d;

        return (double) Math.round(daily);
    }

    private static boolean normalizeGender(String gender) {
        if (gender == null) return true;
        String g = gender.trim().toLowerCase(Locale.ROOT);
        if (g.startsWith("m") || g.equals("nam")) return true;
        if (g.startsWith("f") || g.equals("nữ") || g.equals("nu")) return false;
        return true;
    }

    public static boolean normalizeGenderPublic(String gender) {
        return normalizeGender(gender);
    }

    private static double safeNumber(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) return 0d;
        return v;
    }
}
