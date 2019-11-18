package com.atharvainfo.nilkamal.Others;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.atharvainfo.nilkamal.bus.BusProvider;
import com.atharvainfo.nilkamal.model.LocationPoint;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.TimerTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationService";
    public static String str_receiver = "com.atharvainfo.nilkamal.receiver";

    private String defaultUploadWebsite = "http://202.21.32.179:8080/nilkamalpoultry/updateLocation.php";

    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    private Handler mHandler = new Handler();
    private Location location;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String usernamelog;

    protected LocationManager locationManager;

    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

    public static final String ACTION_START_TRACK = "ACTION_START_TRACK";
    public static final String ACTION_STOP_TRACK = "ACTION_STOP_TRACK";
    public static final String ACTION_GET_CURRENT_LOCATION = "ACTION_GET_CURRENT_LOCATION";
    public static final String ACTION_CHECK_TRACKING = "ACTION_CHECK_TRACKING";
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    private boolean isRequestingLocationUpdates = false;
    private boolean isTrackingEnabled = false;
    private boolean isSingleRequestLocation = false;
    private String mUserUID;
    private String mTravelId;
    private Firebase mTrackRef = null;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 5;
    public static final long SMALLEST_DISPLACEMENT_IN_METERS = 15;
    private long mLastUpdateTimestamp;
    private Location mCurrentLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 15000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    public class CheckTrackingEvent {
        public boolean isTrackingEnabled;

        public CheckTrackingEvent(boolean isTrackingEnabled) {
            this.isTrackingEnabled = isTrackingEnabled;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserUID = sharedPreferences.getString(Constants.KEY_USER_UID, null);
        defaultUploadWebsite = "http://202.21.32.179:8080/nilkamalpoultry/updateLocation.php";

        sharedPreferences = getApplicationContext().getSharedPreferences("Mydata", MODE_PRIVATE);
        sharedPreferences.edit();
        usernamelog = sharedPreferences.getString("user_name", null);


        buildGoogleApiClient();
        if (isGooglePlayServicesAvailable()) {
            googleApiClient.connect();
        }

        // createLocationRequest();
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT_IN_METERS);
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("Running.", "Start");
                    startLocationUpdates();
                }
            });

        }
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            if (!googleApiClient.isConnected() && !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
            return Service.START_STICKY;
        }

       /* String action = intent.getAction();
        if (action == null) {
            action = "";
        }

        switch (action) {
            case ACTION_GET_CURRENT_LOCATION:
                isSingleRequestLocation = true;
                if (googleApiClient.isConnected()) {

                    mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    mLastUpdateTimestamp = System.currentTimeMillis();

                    if (mCurrentLocation != null) {
                        isSingleRequestLocation = false;
                        sendCurrentLocation();
                    } else {
                        if (!isRequestingLocationUpdates) {
                            startLocationUpdates();
                        }
                    }
                } else {
                    if (!googleApiClient.isConnecting()) {
                        googleApiClient.connect();
                    }
                }
                break;

            case ACTION_STOP_TRACK:
                isTrackingEnabled = false;
                mTrackRef = null;
                if (googleApiClient.isConnected() && !isSingleRequestLocation && isRequestingLocationUpdates) {
                    stopLocationUpdates();
                }
                break;
            case ACTION_CHECK_TRACKING:
                BusProvider.bus().post(new CheckTrackingEvent(isTrackingEnabled));
                break;
            default:
        }*/

        return Service.START_STICKY;

        /*if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();


            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
                locationRequest = LocationRequest.create();
                locationRequest.setInterval(UPDATE_INTERVAL);
                locationRequest.setFastestInterval(FASTEST_INTERVAL);


                int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
                //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


                locationRequest.setPriority(priority);
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }*/


        //googleApiClient.connect();
//        startLocationUpdates();
        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        // return START_STICKY;

    }


    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Log.e(TAG, "Google Play Services not available");
            }
            return false;
        }
        return true;
    }


    protected void sendLocationDataToWebsite(Location location) {
        // formatted for mysql datetime format
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(location.getTime());

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.atharvainfo.nilkamal.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

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

        editor.putFloat("previousLatitude", (float) location.getLatitude());
        editor.putFloat("previousLongitude", (float) location.getLongitude());
        editor.apply();

        final RequestParams requestParams = new RequestParams();
        requestParams.put("latitude", Double.toString(location.getLatitude()));
        requestParams.put("longitude", Double.toString(location.getLongitude()));

        Double speedInMilesPerHour = location.getSpeed() * 3.6;
        requestParams.put("speed", Integer.toString(speedInMilesPerHour.intValue()));

        try {
            requestParams.put("date", URLEncoder.encode(dateFormat.format(date), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
        }

        requestParams.put("locationmethod", location.getProvider());

        if (totalDistanceInMeters > 0) {
            requestParams.put("distance", String.format("%.1f", totalDistanceInMeters / 1000)); // in miles,
        } else {
            requestParams.put("distance", "0.0"); // in miles
        }

        requestParams.put("username", sharedPreferences.getString("userName", ""));
        requestParams.put("phonenumber", sharedPreferences.getString("appID", "")); // uuid
        requestParams.put("sessionid", sharedPreferences.getString("sessionID", "")); // uuid

        Double accuracyInFeet = location.getAccuracy() * 3.28;
        requestParams.put("accuracy", Integer.toString(accuracyInFeet.intValue()));

        Double altitudeInFeet = location.getAltitude() * 3.28;
        requestParams.put("extrainfo", Integer.toString(altitudeInFeet.intValue()));

        requestParams.put("eventtype", "android");

        Float direction = location.getBearing();
        requestParams.put("direction", Integer.toString(direction.intValue()));

        final String uploadWebsite = sharedPreferences.getString("defaultUploadWebsite", defaultUploadWebsite);

        LoopjHttpClient.get(uploadWebsite, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                LoopjHttpClient.debugLoopJ(TAG, "sendLocationDataToWebsite - success", uploadWebsite, requestParams, responseBody, headers, statusCode, null);
                stopSelf();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] errorResponse, Throwable e) {
                LoopjHttpClient.debugLoopJ(TAG, "sendLocationDataToWebsite - failure", uploadWebsite, requestParams, errorResponse, headers, statusCode, e);
                stopSelf();
            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        //Log.d(TAG, "Connected to Google API");
        //location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        mCurrentLocation = location;
        mLastUpdateTimestamp = System.currentTimeMillis();

        if (mCurrentLocation != null) {
            //if (isSingleRequestLocation) {
            sendCurrentLocation();
            //    isSingleRequestLocation = false;
            //}

            if (isTrackingEnabled) {
                // saveCurrentTrackPoint();
            } else {
                stopLocationUpdates();
            }
        }


    }

    public void stopLocationUpdates() {
       /* if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }*/

        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        isRequestingLocationUpdates = false;

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        mLastUpdateTimestamp = System.currentTimeMillis();

        if (mCurrentLocation != null) {
            //if (isSingleRequestLocation) {
            sendCurrentLocation();
            //  /  isSingleRequestLocation = false;
            //}

            if (isTrackingEnabled) {
                //saveCurrentTrackPoint();
                startLocationUpdates();
            } else {
                stopLocationUpdates();
            }
        } else {
            startLocationUpdates();
        }


    }

    private void startLocationUpdates() {
        isRequestingLocationUpdates = true;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspended.");
        if (isTrackingEnabled || isSingleRequestLocation) {
            googleApiClient.connect();
        }
    }

    private void sendCurrentLocation() {
        final String path = Constants.FIREBASE_USERS;
        if (mCurrentLocation != null) {
            LocationPoint location = new LocationPoint(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    mCurrentLocation.getAltitude());


            //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path).child(usernamelog);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //DatabaseReference ref = FirebaseDatabase.getInstance().getReference("locaion");
            DatabaseReference ref = database.getReference().child("supervisors").child(usernamelog);
            ref.setValue(mCurrentLocation);
            //ref.push().setValue("latitude : "+mCurrentLocation.getLatitude());
            //ref.push().setValue("longitude : "+mCurrentLocation.getLongitude());
            //ref.push().setValue("time", mCurrentLocation.getTime());
            //Toast.makeText(MainActivity.this ,"Location saved to the Firebasedatabase",Toast.LENGTH_LONG).show();
            Log.i("tag", "Location update saved");

            System.out.println("New Location Lat Lang : "+ mCurrentLocation.getLatitude() + " "+ mCurrentLocation.getLongitude());
            sendLocationDataToWebsite(mCurrentLocation);

            //if (mCurrentLocation != null) {

////Save the location data to the database//

  //              ref.setValue(mCurrentLocation);
    //        }
            BusProvider.bus().post(location);
        }
    }

    private void saveCurrentTrackPoint() {
        if (mCurrentLocation != null) {
            LocationPoint point = new LocationPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), mCurrentLocation.getAltitude());
            if (mTrackRef != null) {
                mTrackRef.child(String.valueOf(mLastUpdateTimestamp)).setValue(point);
            }
        }
    }

    public Location getLocation(String provider) {

        if (mCurrentLocation != null) {

            return mCurrentLocation;
        }
         return mCurrentLocation;

    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog((Activity) getApplicationContext(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            }

            return false;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        if (isRequestingLocationUpdates) {
            stopLocationUpdates();
        }
        googleApiClient.disconnect();
        super.onDestroy();
    }
}
