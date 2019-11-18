package com.atharvainfo.nilkamal.Activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.atharvainfo.nilkamal.Adapter.GridBaseAdapter;
import com.atharvainfo.nilkamal.BuildConfig;
import com.atharvainfo.nilkamal.Fragments.CashReceipt;
import com.atharvainfo.nilkamal.Fragments.LeaveForm;
import com.atharvainfo.nilkamal.Fragments.PaymentReceipt;
import com.atharvainfo.nilkamal.Fragments.SupplyClearance;
import com.atharvainfo.nilkamal.MainActivity;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.GoogleService;
import com.atharvainfo.nilkamal.Others.GpsTracker;
import com.atharvainfo.nilkamal.Others.ImageModel;
import com.atharvainfo.nilkamal.Others.LoopjHttpClient;
import com.atharvainfo.nilkamal.Others.NotificationUtils;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.PrefManager;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.Utils;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.nilkamalpoultry;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SupervisorMain extends AppCompatActivity implements com.google.android.gms.location.LocationListener {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String[] PERMISSIONS_REQUIRED = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final String TAG = SupervisorMain.class.getSimpleName();
    ArrayList<String> permissionsToRequest= new ArrayList<String>();
    //private final ArrayList permissionsRejected = new ArrayList();
    ArrayList<Object> permissionsRejected = new ArrayList<Object>();
    ArrayList<String> permissions = new ArrayList<String>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    GoogleService googleService;
    private boolean mAlreadyStartedService = false;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private DatabaseHelper helper;
    protected String loginUserId;
    private ActionBarDrawerToggle toggle;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    String spname;
    private boolean currentlyTracking;
    private Firebase.AuthStateListener mAuthStateListener;
    public static final String PREFS="PREFS";

    LinearLayout lyt_rootstart,lyt_dailyentry,lyt_newfarm,lyt_leavereq,lyt_feedreq,lyt_chickplace, lyt_suplclea, lyt_feedtran, lyt_farmcomp,lyt_notification,lyt_rootstop, lyt_f;
    String userDesignation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        nilkamalpoultry application = (nilkamalpoultry) getApplication();
        application.getTracker();
        application.sendEvent("main", SupervisorMain.class.getSimpleName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.nav_view);

        View v = navigationView.getHeaderView(0);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        googleService = new GoogleService(SupervisorMain.this);

        NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(Constants.CHANNEL_DESC);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        lyt_rootstart = findViewById(R.id.lyt_rootstart);
        lyt_dailyentry = findViewById(R.id.lyt_dailyentry);
        lyt_newfarm = findViewById(R.id.lyt_farmenq);
        lyt_leavereq = findViewById(R.id.lyt_leave);
        lyt_feedreq = findViewById(R.id.lyt_feedreq);
        lyt_chickplace = findViewById(R.id.lyt_chicksplace);
        lyt_suplclea = findViewById(R.id.lyt_supplycl);
        lyt_feedtran = findViewById(R.id.lyt_feedret);
        lyt_farmcomp = findViewById(R.id.lyt_farmcompl);
        lyt_notification = findViewById(R.id.lyt_notifi);
        lyt_rootstop = findViewById(R.id.lyt_rootstop);
        lyt_f = findViewById(R.id.lyt_f);

        initToolbar();
        initNavigationMenu();

        sp=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        spname = sp.getString("empname",null);
        String usernamelog= sp.getString(Constants.KEY_USERNAME,null);
        String useremail = sp.getString(Constants.KEY_USEREMAIL, null);
        userDesignation = sp.getString(Constants.KEY_USERROLE, null);
        currentlyTracking = sp.getBoolean("currentlyTracking", false);
        sp.edit().putString(Constants.KEY_DISPLAY_NAME, usernamelog).apply();
        sp.edit().putString(Constants.KEY_EMAIL, useremail).apply();
        sp.edit().putString(Constants.KEY_TRAVEL_REF, usernamelog).apply();

        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        edit = sp.edit();
        edit.putString(Constants.KEY_APPID,  UUID.randomUUID().toString());
        edit.putString(Constants.KEY_SESSIONID,  UUID.randomUUID().toString());
        edit.apply();

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);

        if (permissionsToRequest.size() > 0)
            requestPermissions((String[]) permissionsToRequest.toArray(new String[0]), ALL_PERMISSIONS_RESULT);

        showCustomDialog();

        if (!isLocationEnabled(this)){

            AlertDialog.Builder builder = new AlertDialog.Builder(SupervisorMain.this);
            builder.setTitle(R.string.network_not_enabled)
                    .setMessage(R.string.open_location_settings)
                    .setPositiveButton(R.string.location_yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton(R.string.location_no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if (currentlyTracking){
            if (isLocationEnabled(getApplicationContext())) {
                startStep1();
            }
        }

        lyt_rootstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupervisorMain.this, SpRootStartActivity.class);
                startActivity(i);
            }
        });
        lyt_dailyentry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupervisorMain.this, SpFarmList.class);
                startActivity(i);
            }
        });

        lyt_newfarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupervisorMain.this, FarmEnquiry.class);
                startActivity(i);
            }
        });
        lyt_feedreq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupervisorMain.this, FeedRequirement.class);
                startActivity(i);
            }
        });

        lyt_rootstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupervisorMain.this, SpRootStopActivity.class);
                startActivity(i);
            }
        });

        lyt_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    Intent i = new Intent(SupervisorMain.this, SupervisorList.class);
            //    startActivity(i);
            }
        });

        lyt_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SupervisorMain.this, usernotification.class);
                startActivity(i);
            }
        });
        lyt_farmcomp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SupervisorMain.this, FeedTransferOut.class);
                startActivity(i);
            }
        });
        lyt_feedtran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SupervisorMain.this, FeedTransferOut.class);
                startActivity(i);
            }
        });
        Intent notificationIntent;
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();
        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    private boolean canMakeSmores() {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perms : permissionsToRequest) {
                if (!hasPermission(perms)) {
                    permissionsRejected.add(perms);
                }
            }

            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                    showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions((String[]) permissionsRejected.toArray(new String[0]), ALL_PERMISSIONS_RESULT);
                                }
                            });
                }

            }
        }

    }

    private void showMessageOKCancel(String tmessage, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SupervisorMain.this)
                .setMessage(tmessage)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (currentlyTracking){
            if (isLocationEnabled(getApplicationContext())) {
                startStep1();
            }
        }

    }
    private void startStep1() {
        if (isGooglePlayServicesAvailable()) {
            startStep2(null);
            Intent intentStartTracking = new Intent(SupervisorMain.this, GoogleService.class);
            intentStartTracking.setAction(GoogleService.ACTION_START_TRACK);
            startService(intentStartTracking);

        } else {
            Toast.makeText(getApplicationContext(), R.string.no_google_playservice_available, Toast.LENGTH_LONG).show();
        }
    }

    public static Boolean isLocationEnabled(Context context)
    {
        // This is new method provided in API 28
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isLocationEnabled();
    }
    private Boolean startStep2(DialogInterface dialog) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            promptInternetConnect();
            return false;
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        if (checkPermissions()) { //Yes permissions are granted by the user. Go to the next step.
            startStep3();
        } else {  //No user has not granted the permissions yet. Request now.
            requestPermissions();
        }
        return true;
    }
    public boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                Objects.requireNonNull(googleApiAvailability.getErrorDialog(this, status, 2404)).show();
            }

            return false;
        }
        return true;
    }


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Nilkamal Poultry");
        Tools.setSystemBarColor(this,R.color.global__primary_dark);
        Tools.setSystemBarLight(this);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private void initNavigationMenu() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawer.closeDrawers();
                String titile = String.valueOf(item.getTitle());
                if(titile.equalsIgnoreCase("DashBoard")){
                    Intent i = new Intent(SupervisorMain.this, SupervisorMain.class);
                    startActivity(i);
                    finish();
                } else
                if(titile.equalsIgnoreCase("Purchase")){
                    //Intent i = new Intent(SupervisorMain.this, PurchaseActivity.class);
                    //startActivity(i);
                    //finish();
                }
                return true;
            }
        });
    }
    @Override
    public void onBackPressed() {
        Exit();
    }

    public void Exit() {
        new android.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.logo1)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.backbutton))
                .setPositiveButton(getString(R.string.yes_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no_dialog), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.my_dialog);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.price)).setText(getString(R.string.WelComeUser, spname));

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        ((AppCompatButton) dialog.findViewById(R.id.buttonOk)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (currentlyTracking) {
            googleService.sendLocationDataToWebsite(location);
        }
    }
    private void promptInternetConnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SupervisorMain.this);
        builder.setTitle(R.string.title_alert_no_intenet);
        builder.setMessage(R.string.msg_alert_no_internet);

        String positiveText = getString(R.string.btn_label_refresh);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (startStep2(dialog)) {
                            if (checkPermissions()) {
                                startStep3();
                            } else if (!checkPermissions()) {
                                requestPermissions();
                            }
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void startStep3() {
        if (!mAlreadyStartedService ) {
            stopService(new Intent(this, GoogleService.class));
            //Start location sharing service to app server.........
            Intent intent = new Intent(this, GoogleService.class);
            startService(intent);
            Location mLocation = googleService.getlocation();
            if (mLocation != null){
                googleService.sendLocationDataToWebsite(mLocation);
            }
            mAlreadyStartedService = true;
            //Ends................................................
        }
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
        boolean shouldProvideRationale2 =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
        if (shouldProvideRationale || shouldProvideRationale2) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(SupervisorMain.this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(SupervisorMain.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionState2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState1 == PackageManager.PERMISSION_GRANTED && permissionState2 == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onDestroy() {
        //Stop location sharing service to app server.........
        stopService(new Intent(this, GoogleService.class));
        mAlreadyStartedService = false;
        //Ends................................................
        super.onDestroy();
    }


}
