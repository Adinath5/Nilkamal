package com.atharvainfo.nilkamal.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.MainActivity;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor edit;

    private static final int REQUEST= 112;
    private ImageView spimage;
    Thread splashTread;

    protected String loginUserId;
    protected String EmpName, UserDesignation,UserEmail,UserContact, UserPass;
    private String username, userMobile,SPID;
    private final int jsoncode = 1;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    String currentVersion="";
    private int MY_REQUEST_CODE =1234;
    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;
    private static final int REQ_CODE_VERSION_UPDATE = 530;
    public static final String PREFS="PREFS";
    private FirebaseAuth mAuth;
    private static final String TAG = "Loginactivity";
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        sp=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        loginUserId = sp.getString(Constants.KEY_USERID,null);
        userMobile = sp.getString(Constants.KEY_USERPHONE, null);
        UserEmail = sp.getString(Constants.KEY_USEREMAIL, null);
        UserPass = sp.getString(Constants.KEY_USERPASS, null);
       // String spname = sharedPreferences.getString("empname",null);


        String[] PERMISSIONS = {android.Manifest.permission.CAMERA,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.NFC,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            Log.d("TAG","@@@ IN IF hasPermissions");
            ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST );
        } else {
            Log.d("TAG","@@@ IN ELSE hasPermissions");
            //callNextActivity();
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        edit = sp.edit();
                        edit.putString("device_tokan", token);
                        edit.commit();
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END log_reg_token]

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI  || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE ) {

                PackageInfo pInfo = null;
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                currentVersion = pInfo.versionName;
                //versionCode = pInfo.versionCode;
                Log.e("currentVersion", currentVersion);
                //Log.e("Usermobile", userMobile);
                //checkForAppUpdate();

              if (userMobile != null && !userMobile.equals("null") && !userMobile.isEmpty()) {
                    getCompanyDetails();
                    //getUserData();
                } else {
                    // String is empty or null
                    userMobile = "";

                    Intent intent = new Intent(SplashActivity.this, RegisterUser.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        } else {
            Intent i = new Intent(SplashActivity.this, NoItemInternetImage.class);
            startActivity(i);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "@@@ PERMISSIONS grant");
                //callNextActivity();
            } else {
                Log.d("TAG", "@@@ PERMISSIONS Denied");
                Toast.makeText(this, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void callNextActivity(){
        // if(isConnectingToInternet(MainActivity.this)) {
        splashTread = new Thread(){

            @Override
            public void run() {
                try {
                    synchronized(this){
                        // Wait given period of time or exit on touch
                        wait(3000);
                    }
                }
                catch(InterruptedException ignored){
                }
            }
        };
        splashTread.start();
    }

    private void getUserData(){
                //userpass = txtpassword.getText().toString();
        Log.i("usernamae", loginUserId);
        Log.i("usermobile", userMobile);

        Tools.showSimpleProgressDialog(this, "Signing Up...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETUSERDATA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONArray json = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject obj = json.getJSONObject(i);

                                    sp = getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                                    edit = sp.edit();
                                    edit.putString(Constants.KEY_USERID, obj.getString("username"));
                                    edit.putString(Constants.KEY_USERROLE, obj.getString("usertp"));
                                    edit.putString(Constants.KEY_USERPHONE, obj.getString("phone"));
                                    edit.putString(Constants.KEY_USEREMAIL, obj.getString("email"));
                                    edit.putString(Constants.KEY_USERNAME, obj.getString("empname"));
                                    edit.putString(Constants.KEY_USERADDRESS, obj.getString("address"));
                                    edit.putString(Constants.KEY_USERSPID,obj.getString("spcode"));
                                    edit.apply();

                                    //loginUserId = cursor.getString(cursor.getColumnIndex("username"));
                                    SPID= obj.getString("spcode");
                                    username = obj.getString("username");
                                    EmpName = obj.getString("empname");
                                    UserDesignation = obj.getString("usertp");
                                    UserContact = obj.getString("phone");
                                    UserEmail = obj.getString("email");

                                    Log.e("fetch data", EmpName + "=" + UserDesignation + "=" + UserEmail);
                                    Log.e("UserName data", username + "=" + UserDesignation + "=" + UserEmail);
                                    Log.e("Sp data", SPID + "=" + UserDesignation + "=" + UserEmail);

                                    if (!username.isEmpty() || !username.equals("")) {
                                        Log.d("UserName ", loginUserId.toString());
                                        Log.d("UserPower ", UserDesignation.toString());

                                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        SplashActivity.this.finish();

                                        //callNextActivity();
                                        /*if (UserDesignation.equals("Supervisor")) {
                                            Intent intent = new Intent(SplashActivity.this, SupervisorMain.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                            SplashActivity.this.finish();
                                        } else if (UserDesignation.equals("Manager")) {

                                            Intent intent = new Intent(SplashActivity.this, ManagerMain.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                            SplashActivity.this.finish();
                                        } else if (UserDesignation.equals("Admin")) {
                                            Intent intent = new Intent(SplashActivity.this, AdminMain.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                            SplashActivity.this.finish();
                                        } else if (UserDesignation.equals("Operator")) {

                                            Intent intent = new Intent(SplashActivity.this, AdminMain.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                            SplashActivity.this.finish();
                                        } else if (UserDesignation.equals("Layer Supervisor")) {
                                            Intent intent = new Intent(SplashActivity.this, LayerFarmSpMain.class);
                                            startActivity(intent);
                                            SplashActivity.this.finish();
                                        } else if (UserDesignation.equals("Feed Mill Supervisor")) {
                                            Intent intent = new Intent(SplashActivity.this, FeedMillSpMain.class);
                                            startActivity(intent);
                                            SplashActivity.this.finish();
                                        }*/

                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("usermobile", userMobile);
                return params;
            }

        };
        RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);
        queue.add(stringRequest);
    }

    private void getCompanyDetails(){
        Tools.showSimpleProgressDialog(this, "Getting Data...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.Base_url+"claintnew.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONArray json = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject obj = json.getJSONObject(i);
                                    //loginUserId = cursor.getString(cursor.getColumnIndex("username"));
                                    // if (obj.getString("amount") != "0"){
                                    String CompanyName = obj.getString("company");
                                    String CompanyAdd = obj.getString("address");
                                    String CompanyCity = obj.getString("city");
                                    String CompanyTal = obj.getString("tal");
                                    String CompanyDist = obj.getString("dist");
                                    String CompanyTele = obj.getString("telephone");
                                    String CompanyMob = obj.getString("mobileno");
                                    String CompanyEmail = obj.getString("email");
                                    String CompanyGst = obj.getString("gstin");
                                    String CompanyJuri = obj.getString("jurisdiction");
                                    String CompanyProp = obj.getString("propname");
                                    String FyStartDate = obj.getString("fysdate");
                                    String FyEndDate = obj.getString("fyedate");

                                    sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                                    edit = sp.edit();
                                    edit.putString(Constants.COMPANY_NAME, CompanyName);
                                    edit.putString(Constants.COMPANY_ADDRESS, CompanyAdd);
                                    edit.putString(Constants.COMPANY_CITY, CompanyCity);
                                    edit.putString(Constants.COMPANY_TALUKA, CompanyTal);
                                    edit.putString(Constants.COMPANY_DISTRICT, CompanyDist);
                                    edit.putString(Constants.COMPANY_TELEPHONE, CompanyTele);
                                    edit.putString(Constants.COMPANY_MOBILE, CompanyMob);
                                    edit.putString(Constants.COMPANY_EMAIL, CompanyEmail);
                                    edit.putString(Constants.COMPANY_GST, CompanyGst);
                                    edit.putString(Constants.COMPANY_JURISDICTION, CompanyJuri);
                                    edit.putString(Constants.COMPANY_PROPRIETOR, CompanyProp);
                                    edit.putString(Constants.COMPANY_FYSTARTDATE, FyStartDate);
                                    edit.putString(Constants.COMPANY_FYENDDATE, FyEndDate);
                                    edit.commit();
                                    Log.e("Start Date", FyStartDate);
                                    Log.e("End Date", FyEndDate);
                                    //}
                                    //System.out.println(ldgrlist.toString());
                                    getUserData();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {

        };
        RequestQueue queue = Volley.newRequestQueue(SplashActivity.this);
        queue.add(stringRequest);
    }

    private void checkForAppUpdate() {
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);

        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Create a listener to track request state updates.
        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState installState) {
                // Show module progress, log state, or install the update.
                if (installState.installStatus() == InstallStatus.DOWNLOADED)
                    // After the update is downloaded, show a notification
                    // and request user confirmation to restart the app.
                    popupSnackbarForCompleteUpdateAndUnregister();
            }
        };

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                // Request the update.
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                    // Before starting an update, register a listener for updates.
                    appUpdateManager.registerListener(installStateUpdatedListener);
                    // Start an update.
                    startAppUpdateFlexible(appUpdateInfo);
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) ) {
                    // Start an update.
                    startAppUpdateImmediate(appUpdateInfo);
                }
            }
        });
    }

    private void startAppUpdateImmediate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    SplashActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void startAppUpdateFlexible(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    SplashActivity.REQ_CODE_VERSION_UPDATE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            unregisterInstallStateUpdListener();
        }
    }

    /**
     * Displays the snackbar notification and call to action.
     * Needed only for Flexible app update
     */
    private void popupSnackbarForCompleteUpdateAndUnregister() {
        Snackbar snackbar =
                Snackbar.make(findViewById(R.id.main_activity), getString(R.string.youAreNotUpdatedMessage), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.restart, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.md_teal_400));
        snackbar.show();

        unregisterInstallStateUpdListener();
    }

    /**
     * Checks that the update is not stalled during 'onResume()'.
     * However, you should execute this check at all app entry points.
     */
    private void checkNewAppVersionState() {
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            //FLEXIBLE:
                            // If the update is downloaded but not installed,
                            // notify the user to complete the update.
                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                popupSnackbarForCompleteUpdateAndUnregister();
                            }
                            //IMMEDIATE:
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                startAppUpdateImmediate(appUpdateInfo);
                            }
                        });

    }

    /**
     * Needed only for FLEXIBLE update
     */
    private void unregisterInstallStateUpdListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null)
            appUpdateManager.unregisterListener(installStateUpdatedListener);
    }
}
