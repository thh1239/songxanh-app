package com.example.songxanh.ui.screens.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.songxanh.R;

public class mealNotificationReceiver extends BroadcastReceiver {

    private static final String SECOND_CHANNEL_ID = "meal_notification_channel";
    private static final String SECOND_CHANNEL_NAME = "Meal Channel";
    private static final String SECOND_CHANNEL_DESC = "This is the meal notification channel";
    private static final int SECOND_NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        createSecondNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SECOND_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_calendar)
                .setContentTitle("Don't forget to eat!")
                .setContentText("Nourish your body, fuel your life")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SECOND_NOTIFICATION_ID, builder.build());
    }

    private void createSecondNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(SECOND_CHANNEL_ID, SECOND_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(SECOND_CHANNEL_DESC);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
