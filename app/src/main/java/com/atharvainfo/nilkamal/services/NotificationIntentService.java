package com.atharvainfo.nilkamal.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.TaskStackBuilder;

import com.atharvainfo.nilkamal.MainActivity;
import com.atharvainfo.nilkamal.Others.Constants;

import java.util.Calendar;
import java.util.Locale;
import com.atharvainfo.nilkamal.R;

public class NotificationIntentService extends IntentService {


    public static final String KEY_UID = "KEY_UID";
    public static final String KEY_TYPE = "KEY_TYPE";
    public static final String KEY_TITLE = "KEY_TITLE";
    public static final String KEY_TIME = "KEY_TIME";
    public static final String KEY_LOCATION_TITLE = "KEY_LOCATION_TITLE";
    public static final String KEY_ITEM_KEY = "KEY_ITEM_KEY";

    public NotificationIntentService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int uid = intent.getIntExtra(KEY_UID, 0);
        String title = intent.getStringExtra(KEY_TITLE);
        String type = intent.getStringExtra(KEY_TYPE);
        String itemKey = intent.getStringExtra(KEY_ITEM_KEY);
        String text = null;

        // open Main Activity with Reminder list
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        //mainIntent.putExtra(MainActivity.KEY_FRAGMENT, MainActivity.REMINDER_LIST_FRAGMENT_TAG);
        //
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(mainIntent);
        // open reminder item activity with itemKey
       // Intent reminderItemIntent = new Intent(getApplicationContext(), ReminderItemActivity.class);
      //  reminderItemIntent.putExtra(Constants.KEY_REMINDER_ITEM_KEY, itemKey);
      //  stackBuilder.addNextIntent(reminderItemIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(uid, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Constants.FIREBASE_REMINDER_TASK_ITEM_TYPE_LOCATION.equals(type)) {
            String locationTitle = intent.getStringExtra(KEY_LOCATION_TITLE);
            text = String.format(Locale.getDefault(), "Enter geofence, %s", locationTitle);
        } else if (Constants.FIREBASE_REMINDER_TASK_ITEM_TYPE_TIME.equals(type)) {
            long time = intent.getLongExtra(KEY_TIME, System.currentTimeMillis());
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            int hours = c.get(Calendar.HOUR);
            int minutes = c.get(Calendar.MINUTE);
            text = String.format(Locale.getDefault(), "Today, %02d:%02d", hours, minutes);
        }
        sendNotification(type, uid, title, text, pendingIntent);
    }

    private void sendNotification(String type, int uid, String title, String text, PendingIntent pendingIntent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder notificationBuilder = (Notification.Builder) new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(text)
                .setVibrate(new long[]{300, 300, 300, 300, 300})
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Constants.FIREBASE_REMINDER_TASK_ITEM_TYPE_LOCATION.equals(type)) {
            notificationBuilder.setSmallIcon(R.drawable.ic_location_on_black_24dp);
        } else if (Constants.FIREBASE_REMINDER_TASK_ITEM_TYPE_TIME.equals(type)) {
            notificationBuilder.setSmallIcon(R.drawable.ic_notifications_none_black_24dp);
        }

        notificationManager.notify(uid, notificationBuilder.build());
    }
}
