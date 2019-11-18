package com.atharvainfo.nilkamal.services;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.R;

public class NotificationHelper {
    public static void displayNotification(Context context, String title, String text){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.logo1);
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(context);
        notificationCompat.notify(1,mBuilder.build());
    }
}
