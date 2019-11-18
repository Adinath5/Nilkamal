package com.atharvainfo.nilkamal.Others;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.atharvainfo.nilkamal.Activity.SupervisorMain;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atharvainfo.nilkamal.R;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.NOTIFICATION_SERVICE;

public class Utils {
    private static Locale myLocale;
    private static Typeface fromAsset;
    private static SpannableString spannableString;
    private static Fonts currentTypeface;

    public static String getFirebaseUserUrl(String userUID) {
        return Constants.FIREBASE_URL + "/" + Constants.FIREBASE_USERS + "/" + userUID;
    }

    public static String getFirebaseUserActiveTravelUrl(String userUID) {
        return Constants.FIREBASE_URL + "/" + Constants.FIREBASE_USERS + "/" + userUID + "/" + Constants.FIREBASE_ACTIVE_TRAVEL;
    }

    public static String getFirebaseUserTravelsUrl(String userUID) {
        return Constants.FIREBASE_URL + "/" + Constants.FIREBASE_USERS + "/" + userUID + "/" + Constants.FIREBASE_TRAVELS;
    }

    public static String getFirebaseUserDiaryUrl(String userUID) {
        return Constants.FIREBASE_URL + "/" + Constants.FIREBASE_USERS + "/" + userUID + "/" + Constants.FIREBASE_DIARY;
    }

    public static String getFirebaseUserTracksUrl(String userUID) {
        return Constants.FIREBASE_URL + "/" + Constants.FIREBASE_USERS + "/" + userUID + "/" + Constants.FIREBASE_TRACKS;
    }

    public static String getFirebaseUserReminderUrl(String userUID) {
        return Constants.FIREBASE_URL + "/" + Constants.FIREBASE_USERS + "/" + userUID + "/" + Constants.FIREBASE_REMINDER;
    }

    public static String getFirebaseUserWaypointsUrl(String userUID) {
        return Constants.FIREBASE_URL + "/" + Constants.FIREBASE_USERS + "/" + userUID + "/" + Constants.FIREBASE_WAYPOINTS;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }

    public static void clearImageCache(final Context context) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Glide.get(context).clearDiskCache();
                return true;
            }
        };
        task.execute();
    }

    public static void tintWidget(Context context, View view, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(view.getBackground());
        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, color));
        view.setBackground(wrappedDrawable);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        Cursor cursor = null;
        try {
            String filePath = imageFile.getAbsolutePath();
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=? ",
                    new String[]{filePath}, null);
            if (cursor != null && cursor.moveToFirst()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                if (imageFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, filePath);
                    return context.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    return null;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean checkFileExists(Context context, String uri) {
        if (uri == null) {
            return false;
        }

        Uri uriPath = Uri.parse(uri);
        String path = Utils.getRealPathFromURI(context, uriPath);

        if (path != null) {
            File file = new File(path);
            return file.exists();
        }

        return false;
    }

    public static ArrayList<String> photoArrayToStringArray(Context context, ArrayList<ContactsContract.Contacts.Photo> images) {

        ArrayList<String> albumImages = new ArrayList<>();

        /*for (int i = 0; i < images.size(); i++) {
            if (checkFileExists(context, images.get(i).getLocalUri())) {
                albumImages.add(images.get(i).getLocalUri());
            } else {
                albumImages.add(images.get(i).getPicasaUri());
            }
        }*/

        return albumImages;
    }

    public static boolean isInternetAvailable(Context context) {
        NetworkInfo networkInfo = (NetworkInfo) ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        } else if (!networkInfo.isConnected() || networkInfo.isRoaming()) {
            return false;
        }
        return true;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String getMediumDate(long timestamp) {
        String date = SimpleDateFormat.getDateInstance().format(timestamp);
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp);
        return date + " " + time;

    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public static void startTravel(Context context, String travelKey, String travelTitle) {
        /*
          For switch active travel logic see listener mActiveTravelListener in BaseActivity class
        */

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userUID = sharedPreferences.getString(Constants.KEY_USER_UID, null);
        String activeTravelKey = sharedPreferences.getString(Constants.KEY_ACTIVE_TRAVEL_KEY, null);
        if (activeTravelKey != null && !Constants.FIREBASE_TRAVELS_DEFAULT_TRAVEL_KEY.equals(activeTravelKey)) {
            setStopTime(context, activeTravelKey);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.FIREBASE_ACTIVE_TRAVEL_TITLE, travelTitle);
        map.put(Constants.FIREBASE_ACTIVE_TRAVEL_KEY, travelKey);
        Firebase activeTravelRef = new Firebase(Utils.getFirebaseUserActiveTravelUrl(userUID));
        activeTravelRef.setValue(map);

       // ((BaseActivity) context).enableStartTrackingButton(true);
    }

    public static void stopTravel(Context context, String travelKey) {
        if (!Constants.FIREBASE_TRAVELS_DEFAULT_TRAVEL_KEY.equals(travelKey)) {
            setStopTime(context, travelKey);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String activeTravelKey = sharedPreferences.getString(Constants.KEY_ACTIVE_TRAVEL_KEY, null);
            if (activeTravelKey != null && activeTravelKey.equals(travelKey)) {
                startTravel(context, Constants.FIREBASE_TRAVELS_DEFAULT_TRAVEL_KEY, context.getString(R.string.default_travel_title));
            }
         //   ((BaseActivity) context).stopTracking();
         //   ((BaseActivity) context).enableStartTrackingButton(false);
        }
    }

    public static void setStopTime(Context context, String travelKey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userUID = sharedPreferences.getString(Constants.KEY_USER_UID, null);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(Constants.FIREBASE_TRAVEL_STOP_TIME, System.currentTimeMillis());
        new Firebase(Utils.getFirebaseUserTravelsUrl(userUID))
                .child(travelKey)
                .updateChildren(map);
    }

    public static void deleteTravel(final Context context, final String travelKey) {
        new AlertDialog.Builder(context)
                .setInverseBackgroundForced(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(context.getString(R.string.travels_delete_question_text))
                .setMessage(context.getString(R.string.travels_delete_warning_text))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        String activeTravel = sharedPreferences.getString(Constants.KEY_ACTIVE_TRAVEL_KEY, null);
                        String userUID = sharedPreferences.getString(Constants.KEY_USER_UID, null);

                        // delete reminder items
                        new Firebase(Utils.getFirebaseUserReminderUrl(userUID))
                                .orderByChild(Constants.FIREBASE_REMINDER_TRAVELID)
                                .equalTo(travelKey)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            child.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });

                        if (activeTravel != null && activeTravel.equals(travelKey)) {
                            // stop tracking
                            Intent intentStopTracking = new Intent(context, GoogleService.class);
                            intentStopTracking.setAction(GoogleService.ACTION_STOP_TRACK);
                            context.startService(intentStopTracking);
                            sharedPreferences.edit()
                                    .putString(Constants.KEY_ACTIVE_TRAVEL_KEY, null)
                                    .apply();
                            sharedPreferences.edit()
                                    .putString(Constants.KEY_ACTIVE_TRAVEL_TITLE, null)
                                    .apply();
                            Utils.startTravel(context, Constants.FIREBASE_TRAVELS_DEFAULT_TRAVEL_KEY,
                                    context.getString(R.string.default_travel_title));
                        }
                        // delete tracks
                        new Firebase(Utils.getFirebaseUserTracksUrl(userUID))
                                .child(travelKey)
                                .removeValue();
                        // delete travel
                        new Firebase(Utils.getFirebaseUserTravelsUrl(userUID))
                                .child(travelKey)
                                .removeValue();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    /*public static void disableAlarmGeofence(Context context, ReminderItem reminderItem) {
        AlarmSetterService.cancelAlarm(context, reminderItem);
        GeofenceSetterService.cancelGeofence(context, reminderItem);
    }

    public static void enableAlarmGeofence(Context context, ReminderItem reminderItem, String itemKey) {
        if (Constants.FIREBASE_REMINDER_TASK_ITEM_TYPE_TIME.equals(reminderItem.getType())) {
            AlarmSetterService.setAlarm(context, reminderItem, itemKey);
        } else if (Constants.FIREBASE_REMINDER_TASK_ITEM_TYPE_LOCATION.equals(reminderItem.getType())) {
            GeofenceSetterService.setGeofence(context, reminderItem, itemKey);
        }
    }*/

    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String formatTimeWithMarker(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static int getHourOfDay(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("H", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    public static int getMinute(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("m", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     * @param timeInMillis  The time to convert, in milliseconds.
     * @return  The time or date.
     */
    public static String formatDateTime(long timeInMillis) {
        if(isToday(timeInMillis)) {
            return formatTime(timeInMillis);
        } else {
            return formatDate(timeInMillis);
        }
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    public static String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(System.currentTimeMillis()));
    }

    /**
     * Checks if two dates are of the same day.
     * @param millisFirst   The time in milliseconds of the first date.
     * @param millisSecond  The time in milliseconds of the second date.
     * @return  Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }


    public static Hashtable<String, Object> getFileInfo(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        try {
            String mime = context.getContentResolver().getType(uri);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "sendbird");

            ParcelFileDescriptor inputPFD = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fd = null;
            if (inputPFD != null) {
                fd = inputPFD.getFileDescriptor();
            }
            FileInputStream inputStream = new FileInputStream(fd);
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                Hashtable<String, Object> value = new Hashtable<>();

                if (cursor.moveToFirst()) {
                    String name = cursor.getString(nameIndex);
                    int size = (int) cursor.getLong(sizeIndex);

                    value.put("path", file.getPath());
                    value.put("size", size);
                    value.put("mime", mime);
                    value.put("name", name);
                }
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e.getLocalizedMessage(), "File not found.");
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Downloads a file using DownloadManager.
     */
    public static void downloadFile(Context context, String url, String fileName) {
        DownloadManager.Request downloadRequest = new DownloadManager.Request(Uri.parse(url));
        downloadRequest.setTitle(fileName);

        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            downloadRequest.allowScanningByMediaScanner();
            downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        downloadRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(downloadRequest);
    }


    /**
     * Converts byte value to String.
     */
    public static String toReadableFileSize(long size) {
        if (size <= 0) return "0KB";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static void saveToFile(File file, String data) throws IOException {
        File tempFile = File.createTempFile("sendbird", "temp");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(data.getBytes());
        fos.close();

        if(!tempFile.renameTo(file)) {
            throw new IOException("Error to rename file to " + file.getAbsolutePath());
        }
    }

    public static String loadFromFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        Reader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[8192];
        int read;
        while ((read = reader.read(buffer, 0, buffer.length)) > 0) {
            builder.append(buffer, 0, read);
        }
        return builder.toString();
    }

    public static void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }


    public static void displayRoundImageFromUrl(final Context context, final String url, final ImageView imageView) {
        RequestOptions myOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate();

        Glide.with(context)
                .asBitmap()
                .apply(myOptions)
                .load(url)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public static void displayImageFromUrl(final Context context, final String url,
                                           final ImageView imageView) {
        displayImageFromUrl(context, url, imageView, null);
    }

    /**
     * Displays an image from a URL in an ImageView.
     */
    public static void displayImageFromUrl(final Context context, final String url,
                                           final ImageView imageView, RequestListener listener) {
        RequestOptions myOptions = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        if (listener != null) {
            Glide.with(context)
                    .load(url)
                    .apply(myOptions)
                    .listener(listener)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(url)
                    .apply(myOptions)
                    .listener(listener)
                    .into(imageView);
        }
    }

    public static void displayRoundImageFromUrlWithoutCache(final Context context, final String url,
                                                            final ImageView imageView) {
        displayRoundImageFromUrlWithoutCache(context, url, imageView, null);
    }

    public static void displayRoundImageFromUrlWithoutCache(final Context context, final String url,
                                                            final ImageView imageView, RequestListener listener) {
        RequestOptions myOptions = new RequestOptions()
                .centerCrop()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        if (listener != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .apply(myOptions)
                    .listener(listener)
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } else {
            Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .apply(myOptions)
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }

    /**
     * Displays an image from a URL in an ImageView.
     * If the image is loading or nonexistent, displays the specified placeholder image instead.
     */
    public static void displayImageFromUrlWithPlaceHolder(final Context context, final String url,
                                                          final ImageView imageView,
                                                          int placeholderResId) {
        RequestOptions myOptions = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(placeholderResId);

        Glide.with(context)
                .load(url)
                .apply(myOptions)
                .into(imageView);
    }

    /**
     * Displays an image from a URL in an ImageView.
     */
    public static void displayGifImageFromUrl(Context context, String url, ImageView imageView, RequestListener listener) {
        RequestOptions myOptions = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        if (listener != null) {
            Glide.with(context)
                    .asGif()
                    .load(url)
                    .apply(myOptions)
                    .listener(listener)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .asGif()
                    .load(url)
                    .apply(myOptions)
                    .into(imageView);
        }
    }

    /**
     * Displays an GIF image from a URL in an ImageView.
     */
    public static void displayGifImageFromUrl(Context context, String url, ImageView imageView, String thumbnailUrl) {
        RequestOptions myOptions = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        if (thumbnailUrl != null) {
            Glide.with(context)
                    .asGif()
                    .load(url)
                    .apply(myOptions)
                    .thumbnail(Glide.with(context).asGif().load(thumbnailUrl))
                    .into(imageView);
        } else {
            Glide.with(context)
                    .asGif()
                    .load(url)
                    .apply(myOptions)
                    .into(imageView);
        }
    }
    public static void psLog(String log) {
        if (myConfig.IS_DEVELOPMENT) {
            Log.d("TEAMPS", log);
        }
    }

    public static boolean isGooglePlayServicesOK(Activity activity) {

        final int GPS_ERRORDIALOG_REQUEST = 9001;

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, activity, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(activity, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static boolean isEmailFormatValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void saveBitmapImage(Context context, Bitmap b, String picName) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(picName, Context.MODE_APPEND);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("TEAMPS", "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("TEAMPS", "io exception");
            e.printStackTrace();
        }

    }

    public static Bitmap loadBitmapImage(Context context, String picName) {
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();

        } catch (FileNotFoundException e) {
            Log.d("TEAMPS", "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("TEAMPS", "io exception");
            e.printStackTrace();
        }
        return b;
    }

    public static Typeface getTypeFace(Context context, Fonts fonts) {

        if (currentTypeface == fonts) {
            if (fromAsset == null) {
                if (fonts == Fonts.NOTO_SANS) {
                    fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSans-Regular.ttf");
                } else if (fonts == Fonts.ROBOTO) {
//                    fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
                    fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Product-Sans-Regular.ttf");
                } else if (fonts == Fonts.ROBOTO_MEDIUM) {
                    fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Product-Sans-Bold.ttf");
                } else if (fonts == Fonts.ROBOTO_LIGHT) {
                    fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Product-Sans-Regular.ttf");
                } else if (fonts == Fonts.MM_FONT) {
                    fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/mymm.ttf");
                } else if (fonts == Fonts.GOTHIC) {
                    fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/gothic.ttf");
                }

            }
        } else {
            if (fonts == Fonts.NOTO_SANS) {
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSans-Regular.ttf");
            } else if (fonts == Fonts.ROBOTO) {
//                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Product-Sans-Regular.ttf");
            } else if (fonts == Fonts.ROBOTO_MEDIUM) {
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Product-Sans-Bold.ttf");
            } else if (fonts == Fonts.ROBOTO_LIGHT) {
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Product-Sans-Regular.ttf");
            } else if (fonts == Fonts.MM_FONT) {
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/mymm.ttf");
            } else if (fonts == Fonts.GOTHIC) {
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/gothic.ttf");
            } else {
                fromAsset = Typeface.createFromAsset(context.getAssets(), "fonts/Product-Sans-Regular.ttf");
            }

            //fromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/Roboto-Italic.ttf");
            currentTypeface = fonts;
        }
        return fromAsset;
    }

    public static SpannableString getSpannableString(Context context, String str, Fonts font) {
        spannableString = new SpannableString(str);
        spannableString.setSpan(new PSTypefaceSpan("", Utils.getTypeFace(context, font)), 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    public enum Fonts {
        ROBOTO,
        NOTO_SANS,
        ROBOTO_LIGHT,
        ROBOTO_MEDIUM,
        MM_FONT,
        GOTHIC
    }

    public enum LoadingDirection {
        top,
        bottom,
        none
    }

    public static Bitmap getUnRotatedImage(String imagePath, Bitmap rotatedBitmap) {
        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        return Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix,
                true);
    }

    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[4].getLineNumber();
    }

    public static String getClassName(Object obj) {
        return "" + ((Object) obj).getClass();
    }

    public static void psErrorLog(String log, Object obj) {
        try {
            Log.d("TEAMPS", log);
            Log.d("TEAMPS", "Line : " + getLineNumber());
            Log.d("TEAMPS", "Class : " + getClassName(obj));
        } catch (Exception ee) {
            Log.d("TEAMPS", "Error in psErrorLog");
        }
    }

    public static void psErrorLog(String log, Exception e) {
        try {
            StackTraceElement l = e.getStackTrace()[0];
            Log.d("TEAMPS", log);
            Log.d("TEAMPS", "Line : " + l.getLineNumber());
            Log.d("TEAMPS", "Method : " + l.getMethodName());
            Log.d("TEAMPS", "Class : " + l.getClassName());
        } catch (Exception ee) {
            Log.d("TEAMPS", "Error in psErrorLogE");
        }

    }


    public static void unbindDrawables(View view) {
        try {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }

                if (!(view instanceof AdapterView)) {
                    ((ViewGroup) view).removeAllViews();
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("Error in Unbind", e);
        }
    }

    public static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Utils.psLog("Permission is granted");
                return true;
            } else {
                Utils.psLog("Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Utils.psLog("Permission is granted");
            return true;
        }
    }

    // Sleep Me
    public static void sleepMe(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Utils.psErrorLog("InterruptedException", e);
        } catch (Exception e) {
            Utils.psErrorLog("Exception", e);
        }
    }


    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);

            if (imm != null) {
                if (activity.getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            Utils.psErrorLog("Error in hide keyboard.", e);
        }
    }
    public static int getDeviceHeight(Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.heightPixels;
    }

    //Ad
//    public static void initInterstitialAd(Context context, String adKey) {
//        //load ad
//        AdRequest adRequest = new AdRequest.Builder().build();
//
//        InterstitialAd interstitial;
//        // Prepare the Interstitial Ad
//        interstitial = new InterstitialAd(context);
//
//        // Insert the Ad Unit ID
//        interstitial.setAdUnitId(adKey);
//
//        interstitial.loadAd(adRequest);
//
//        // Prepare an Interstitial Ad Listener
//        interstitial.setAdListener(new AdListener() {
//            public void onAdLoaded() {
//                // Call displayInterstitial() function
//                displayInterstitial(interstitial);
//            }
//        });
//    }

//    public static void displayInterstitial(InterstitialAd interstitial) {
//        // If Ads are loaded, show Interstitial else show nothing.
//        if (interstitial.isLoaded()) {
//            interstitial.show();
//        }
//    }

    public static boolean toggleUporDown(View v) {
        if (v.getRotation() == 0) {
            v.animate().setDuration(150).rotation(180);
            return true;
        } else {
            v.animate().setDuration(150).rotation(0);
            return false;
        }
    }

//    public static void setConfigCountToShared(int value, SharedPreferences pref, String name) {
//
//        if (name.equals(CONFIG_COLLECTION_COUNT)) {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putInt(CONFIG_COLLECTION_COUNT, value);
//            editor.apply();
//        } else if (name.equals(CONFIG_HOME_PRODUCT_COUNT)) {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putInt(CONFIG_HOME_PRODUCT_COUNT, value);
//            editor.apply();
//        } else if (name.equals(CONFIG_PRODUCT_COUNT)) {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putInt(CONFIG_PRODUCT_COUNT, value);
//            editor.apply();
//        } else if (name.equals(CONFIG_HOME_CATEGORY_COUNT)) {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putInt(CONFIG_HOME_CATEGORY_COUNT, value);
//            editor.apply();
//        } else if (name.equals(CONFIG_LIST_CATEGORY_COUNT)) {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putInt(CONFIG_LIST_CATEGORY_COUNT, value);
//            editor.apply();
//        } else if (name.equals(CONFIG_NOTI_LIST_COUNT)) {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putInt(CONFIG_NOTI_LIST_COUNT, value);
//            editor.apply();
//        } else if (name.equals(CONFIG_COMMENT_COUNT)) {
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putInt(CONFIG_COMMENT_COUNT, value);
//            editor.apply();
//        }
//
//    }


    public static void setDatesToShared(String start_date, String end_date, SharedPreferences pref) {

        SharedPreferences.Editor editor = pref.edit();
       // editor.putString(Constants.SHOP_START_DATE, start_date);
       // editor.putString(Constants.SHOP_END_DATE, end_date);
       // editor.apply();

    }

}
