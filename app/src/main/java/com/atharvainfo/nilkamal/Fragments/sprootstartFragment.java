package com.atharvainfo.nilkamal.Fragments;


import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.atharvainfo.nilkamal.Others.GpsTrackerAlarmReceiver;
import com.atharvainfo.nilkamal.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;


public class sprootstartFragment extends Fragment {

    private static final String TAG = "Rootstart";
    private View view;

    private String defaultUploadWebsite;

    private TextView txtUserName;
    private EditText txtWebsite;
    private Button trackingButton, btntakephoto;

    private boolean currentlyTracking;
    private RadioGroup intervalRadioGroup;
    private int intervalInMinutes = 1;
    private AlarmManager alarmManager;
    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;
    private GoogleApiClient mGoogleApiClient;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private Location mLocation;
    private LocationManager mLocationManager;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 2 sec */
    private LocationManager locationManager;
    Double latitude,longitude;
    Geocoder geocoder;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public sprootstartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sprootstart, container, false);

        defaultUploadWebsite =  "http://www.gotulyadairy.in/v1/updatelocation.php";
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        txtUserName = view.findViewById(R.id.txtusername);
        trackingButton = view.findViewById(R.id.trackingButton);


        sharedPreferences = getActivity().getSharedPreferences("com.atharvainfo.gotulyadairy.prefs", Context.MODE_PRIVATE);
        currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);

        boolean firstTimeLoadingApp = sharedPreferences.getBoolean("firstTimeLoadingApp", true);
        if (firstTimeLoadingApp) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTimeLoadingApp", false);
            editor.putString("appID",  UUID.randomUUID().toString());
            editor.apply();
        }


        sharedPreferences=getActivity().getApplicationContext().getSharedPreferences("Mydata",Context.MODE_PRIVATE);
        sharedPreferences.edit();
        String usernamelog= sharedPreferences.getString("user_name",null);

        // String resultlog="2";
        Log.e("login",usernamelog );
        if((!(usernamelog == null) )){
            txtUserName.setText(usernamelog.toString());
        }

        saveInterval();


        trackingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                trackLocation(view);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void saveInterval() {
        if (currentlyTracking) {
            Toast.makeText(getContext(), "Please restart tracking to change the time interval.", Toast.LENGTH_LONG).show();
        }

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.atharvainfo.gotulyadairy.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("intervalInMinutes", 1);
        editor.apply();
    }

    protected void trackLocation(View v) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.atharvainfo.gotulyadairy.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();



        if (!saveUserSettings()) {
            return;
        }

        if (!checkIfGooglePlayEnabled()) {
            return;
        }

        if (currentlyTracking) {
            cancelAlarmManager();

            currentlyTracking = false;
            editor.putBoolean("currentlyTracking", false);
            editor.putString("sessionID", "");
        } else {
            startAlarmManager();

            currentlyTracking = true;
            editor.putBoolean("currentlyTracking", true);
            editor.putFloat("totalDistanceInMeters", 0f);
            editor.putBoolean("firstTimeGettingPosition", true);
            editor.putString("sessionID",  UUID.randomUUID().toString());
        }

        editor.apply();
        setTrackingButtonState();
    }



    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        //Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(getContext(), GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void startAlarmManager() {
        Log.d(TAG, "startAlarmManager");

        //Context context = getBaseContext();
        alarmManager = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(getContext(), GpsTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, gpsTrackerIntent, 0);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.atharvainfo.gotulyadairy.prefs", Context.MODE_PRIVATE);
        intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalInMinutes * 60000, // 60000 = 1 minute
                pendingIntent);
    }

    private boolean saveUserSettings() {
        //if (textFieldsAreEmptyOrHaveSpaces()) {
        //    return false;
        //}

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.atharvainfo.gotulyadairy.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.putInt("intervalInMinutes", 1);

        editor.putString("userName", txtUserName.getText().toString().trim());
        editor.putString("defaultUploadWebsite", txtWebsite.getText().toString().trim());

        editor.apply();

        return true;
    }


    private void displayUserSettings() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.atharvainfo.gotulyadairy.prefs", Context.MODE_PRIVATE);
        intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);
        //intervalRadioGroup.check(R.id.i1);


        txtWebsite.setText(sharedPreferences.getString("defaultUploadWebsite", defaultUploadWebsite));
        txtUserName.setText(sharedPreferences.getString("userName", ""));
    }

    private boolean checkIfGooglePlayEnabled() {

        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity()) == ConnectionResult.SUCCESS) {
            return true;
        } else {

            Log.e(TAG, String.valueOf(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity())) );
            Log.e(TAG, "unable to connect to google play services.");
            Toast.makeText(getContext(), R.string.google_play_services_unavailable, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void setTrackingButtonState() {
        if (currentlyTracking) {
            trackingButton.setBackgroundResource(R.drawable.green_tracking_button);
            trackingButton.setTextColor(Color.BLACK);
            trackingButton.setText(R.string.tracking_is_on);
        } else {
            trackingButton.setBackgroundResource(R.drawable.red_tracking_button);
            trackingButton.setTextColor(Color.WHITE);
            trackingButton.setText(R.string.tracking_is_off);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        displayUserSettings();
        setTrackingButtonState();
    }





}
