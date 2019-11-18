package com.atharvainfo.nilkamal.Others;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.atharvainfo.nilkamal.R;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class GoogleService extends Service implements LocationListener {
    //private final Context mContext;

    boolean checkGPS = false;
    boolean checkNetwork = false;
    double latitude, longitude;
    boolean canGetLocation = false;
    Location loc;
    LocationManager locationManager;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String spname;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    private FusedLocationProviderClient mLocationProviderClient;
    private LocationCallback locationUpdatesCallback;
    private LocationRequest locationRequest;
    String defaultUploadWebsite;
    private final Context mContext;
    public static final String ACTION_STOP_TRACK = "ACTION_STOP_TRACK";
    public static final String ACTION_START_TRACK = "ACTION_START_TRACK";
    private static final String TAG = "LocationService";
    public static final String PREFS="PREFS";
    public GoogleService(Context mContext){
       this.mContext = mContext;
       defaultUploadWebsite = "http://202.21.38.82:8080/nilkamalpoultry/updateLocation.php";
       getlocation();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        onTaskRemoved(intent);
        getlocation();
        Toast.makeText(getApplicationContext(),"This is a Service running in Background",
                Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        boolean permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        //locationManager = (LocationManager) getApplicationContext()
        //        .getSystemService(LOCATION_SERVICE);
//        locationManager = (LocationManager) getApplicationContext().getSystemService (getApplicationContext().LOCATION_SERVICE);

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        sharedPreferences.edit();
        String spcode = sharedPreferences.getString("supervisorcode",null);
        spname = sharedPreferences.getString("supervisorname",null);
        Log.d("Start GService", "Start");
    }


    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            Log.e("Google Service", "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());
            if (location.getAccuracy() < 500.0f) {
                //stopLocationUpdates();
                sendLocationDataToWebsite(location);
            }
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @SuppressLint("MissingPermission")
    public Location getlocation() {

        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            String PROVIDER_NAME;
            // get GPS status

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                checkGPS = true;
                Log.e("GPS", "Gps Is Enabled");
                PROVIDER_NAME = LocationManager.GPS_PROVIDER;
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                checkGPS = true;
                checkNetwork = true;
                Log.e("NetworkGPS", "NetworkGps Is Enabled");
                PROVIDER_NAME = LocationManager.NETWORK_PROVIDER;
            }
            if (!checkGPS && !checkNetwork) {
                Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
                showSettingsAlert();
            } else {
                this.canGetLocation = true;
                // if GPS Enabled get lat/long using GPS Services
                Log.e("Getting Location", "Getting Location Enabled");
                if (checkGPS) {
                   locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                            Log.e("LAT LAN", latitude + " "+ longitude);
                            sendLocationDataToWebsite(loc);
                        }
                    }
                } else if (checkNetwork){
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null) {
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                            Log.e("LAT LAN", latitude + " "+ longitude);
                            sendLocationDataToWebsite(loc);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loc;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    public double getLatitude(){
        if(loc != null){
            latitude = loc.getLatitude();
        }
        // return latitude
        return latitude;
    }

    public double getLongitude(){
        if(loc != null){
            longitude = loc.getLongitude();
        }
        // return longitude
        return longitude;
    }


    public void sendLocationDataToWebsite(Location location) {
        // formatted for mysql datetime format
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        Long currentdate=System.currentTimeMillis();
        String datestring=dateFormat.format(currentdate);


        sharedPreferences = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        float totalDistanceInMeters = sharedPreferences.getFloat("totalDistanceInMeters", 0f);

        boolean firstTimeGettingPosition = sharedPreferences.getBoolean("firstTimeGettingPosition", true);

        if (firstTimeGettingPosition) {
            editor.putBoolean("firstTimeGettingPosition", false);
        } else {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(sharedPreferences.getFloat("previousLatitude", 0f));
            previousLocation.setLongitude(sharedPreferences.getFloat("previousLongitude", 0f));

            float distance = location.distanceTo(previousLocation);
            totalDistanceInMeters += distance;
            editor.putFloat("totalDistanceInMeters", totalDistanceInMeters);
        }

        editor.putFloat("previousLatitude", (float)location.getLatitude());
        editor.putFloat("previousLongitude", (float)location.getLongitude());
        editor.apply();

        final RequestParams requestParams = new RequestParams();
        requestParams.put("latitude", Double.toString(location.getLatitude()));
        requestParams.put("longitude", Double.toString(location.getLongitude()));

        Double speedInMilesPerHour = location.getSpeed()* 2.2369;
        requestParams.put("speed",  Integer.toString(speedInMilesPerHour.intValue()));

       // try {
       //     requestParams.put("date", URLEncoder.encode(dateFormat.format(datestring), "UTF-8"));
       // } catch (UnsupportedEncodingException e) {}

        requestParams.put("locationmethod", location.getProvider());

        if (totalDistanceInMeters > 0) {
            requestParams.put("distance", String.format("%.1f", totalDistanceInMeters / 1609)); // in miles,
        } else {
            requestParams.put("distance", "0.0"); // in miles
        }

        requestParams.put("username", sharedPreferences.getString(Constants.KEY_USERNAME, ""));
        requestParams.put("phonenumber", sharedPreferences.getString(Constants.KEY_APPID, "")); // uuid
        requestParams.put("sessionid", sharedPreferences.getString(Constants.KEY_SESSIONID, "")); // uuid

        Double accuracyInFeet = location.getAccuracy()* 3.28;
        requestParams.put("accuracy",  Integer.toString(accuracyInFeet.intValue()));

        Double altitudeInFeet = location.getAltitude() * 3.28;
        requestParams.put("extrainfo",  Integer.toString(altitudeInFeet.intValue()));

        requestParams.put("eventtype", "android");

        Float direction = location.getBearing();
        requestParams.put("direction",  Integer.toString(direction.intValue()));

        final String uploadWebsite = sharedPreferences.getString("defaultUploadWebsite", defaultUploadWebsite);

        LoopjHttpClient.get(uploadWebsite, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                LoopjHttpClient.debugLoopJ("Google Service", "sendLocationDataToWebsite - success", uploadWebsite, requestParams, responseBody, headers, statusCode, null);
                stopSelf();
            }
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                LoopjHttpClient.debugLoopJ("Google Service", "sendLocationDataToWebsite - failure", uploadWebsite, requestParams, errorResponse, headers, statusCode, e);
                stopSelf();
            }
        });
    }


    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Setting Dialog Title
        alertDialog.setTitle("GPS Error");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNotification() {

        NotificationChannel channel = new NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Notification.Builder builder = new Notification.Builder(getApplicationContext(), "channel_01").setAutoCancel(true);
        return builder.build();
    }
    public void stopListener() {
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(GoogleService.this);
        }
    }

}
