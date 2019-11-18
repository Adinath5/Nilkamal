package com.atharvainfo.nilkamal.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.BackgroundLocationService;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.GoogleService;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.LocationAddress;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.RequestHandler;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import static xdroid.toaster.Toaster.toast;

public class SpRootStartActivity extends AppCompatActivity {

    private static final String TAG = SupervisorMain.class.getSimpleName();
    private String defaultUploadWebsite, spcode,spname;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private TextView txtUserName, mLocationUpdatesResultView,txtlocationaddress;
    private TextInputEditText txtopeningkm;
    private TextView txtWebsite;
    private Button trackingButton, btntakephoto;

    private boolean currentlyTracking;
    private final boolean mAlreadyStartedService = false;
    private Location mLocation;
    public static final int LOCATION_PERMISSION = 100;

    private com.google.android.gms.location.LocationListener listener;
    private static final long UPDATE_INTERVAL = 60 * 1000;  /* 10 secs */
    private final long FASTEST_INTERVAL = 5000; /* 2 sec */
    LocationManager locationManager;
    Geocoder geocoder;
    boolean boolean_permission;
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 3;
    private PSDialogMsg psDialogMsg;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_PERMISSIONS = 102;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Button mRemoveUpdatesButton;
    private Button mRequestUpdatesButton;
    public BackgroundLocationService gpsService;
    public boolean mTracking = false;
    ImageView imagekm;
    Bitmap opkmimage;
    String mTravelTitle,mTravelDescription;
    GoogleService googleService;
    public static final String PREFS="PREFS";
    public static final String UPLOAD_URL = "http://202.21.38.82:8080/nilkamalpoultry/newsaveopkmimage.php";
    public static final String UPLOAD_KEY = "startkmimage";

    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_root_start);

        googleService = new GoogleService(SpRootStartActivity.this);
        psDialogMsg = new PSDialogMsg(this, false);

        btntakephoto = findViewById(R.id.btntakephoto);
        imagekm = findViewById(R.id.imageopkm);
        txtopeningkm = findViewById(R.id.txtopeningkm);
        txtlocationaddress = findViewById(R.id.txtlocationaddress);

        defaultUploadWebsite =  "http://202.21.38.82:8080/nilkamalpoultry/updateLocation.php";
        geocoder = new Geocoder(this, Locale.getDefault());

        txtUserName = findViewById(R.id.txtusername);
        mRequestUpdatesButton = findViewById(R.id.trackingButton);
        mRemoveUpdatesButton = findViewById(R.id.remove_updates_button);
        mLocationUpdatesResultView = findViewById(R.id.location_updates_result);
        txtWebsite = findViewById(R.id.txtwebsite);
        //currentlyTracking=true;

        if (isOnline()) {
            // Do you Stuff
        } else {
            try {
                new AlertDialog.Builder(SpRootStartActivity.this)
                        .setTitle("Error")
                        .setMessage("Internet not available, Cross check your internet connectivity and try again later...")
                        .setCancelable(false)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            } catch (Exception e) {
                Log.d(TAG, "Show Dialog: " + e.getMessage());
            }
        }

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        String usernamelog= sharedPreferences.getString(Constants.KEY_USERNAME,null);
        spcode = sharedPreferences.getString(Constants.KEY_USERID,null);
        spname = sharedPreferences.getString(Constants.KEY_USERNAME,null);
        currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);

        Log.i("CurrentTracking", String.valueOf(currentlyTracking));
        if (!currentlyTracking){
            mRequestUpdatesButton.setVisibility(View.VISIBLE);
        } else {
            checkPermission();
            mLocation = googleService.getlocation();
            if (mLocation != null) {
                double latitude = mLocation.getLatitude();
                double longitude = mLocation.getLongitude();
                LocationAddress locationAddress = new LocationAddress();
                LocationAddress.getAddressFromLocation(latitude, longitude,
                        getApplicationContext(), new GeocoderHandler());
                //txtlocationaddress.setText(locationAddress.toString());
            }
            mRequestUpdatesButton.setVisibility(View.GONE);
            //mRemoveUpdatesButton.setVisibility(View.VISIBLE);
        }

        // String resultlog="2";
        Log.e("login",usernamelog );
        if((!(usernamelog == null) )){
            txtUserName.setText(usernamelog.toString());
        }

        //checkEnrtiExistsOpkm();
        getPhotoOpKm();

        btntakephoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String opkmt = txtopeningkm.getText().toString();
                if (currentlyTracking) {
                    if (!opkmt.isEmpty()) {
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                        } else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }

                    } else {
                        psDialogMsg.showInfoDialog("Please Enter Opening KM Reading First", "OK");
                        psDialogMsg.okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                psDialogMsg.cancel();
                            }
                        });
                    }
                } else {
                    psDialogMsg.showInfoDialog("Please Start Root First", "OK");
                    psDialogMsg.okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            psDialogMsg.cancel();
                        }
                    });
                }
            }
        });

        mRequestUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    sharedPreferences = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("currentlyTracking", true);
                    editor.commit();
                    mLocation = googleService.getlocation();
                    //googleService.sendLocationDataToWebsite(mLocation);
                    mRequestUpdatesButton.setVisibility(View.GONE);
                    //mRemoveUpdatesButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check for the integer request code originally supplied to startResolutionForResult().
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                opkmimage = (Bitmap) data.getExtras().get("data");
                imagekm.setImageBitmap(opkmimage);
                getCurrentAddress();
                uploadKmImage();
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;
    }

    public boolean checkPermission() {
        int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int ACCESS_CAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED && ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
            return false;
        }
        if (ACCESS_CAMERA != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
            return false;
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            googleService.getlocation();

        }
        if (requestCode==MY_CAMERA_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //  googleService.getlocation();
            Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }


    private void uploadKmImage() {
        class UploadFeedImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SpRootStartActivity.this, "Uploading Feed Image", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadOpkmImage = getStringImage(bitmap);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String opkmimagename = "OPKMIMG" + spcode + timeStamp + ".jpg";

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadOpkmImage);
                data.put("spcode", spcode);
                data.put("spname", spname);
                data.put("openingkm", txtopeningkm.getText().toString());
                data.put("startaddress", txtlocationaddress.getText().toString());
                data.put("opkmimagename", opkmimagename);

                return rh.sendPostRequest(UPLOAD_URL,data);
            }
        }

        UploadFeedImage ui = new UploadFeedImage();
        ui.execute(opkmimage);

    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            if (message.what == 1) {
                Bundle bundle = message.getData();
                locationAddress = bundle.getString("address");
            } else {
                locationAddress = null;
            }
            txtlocationaddress.setText(locationAddress);
        }
    }

    public void getCurrentAddress() {
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            if (checkPermission()){
                mLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(mLocation.getLatitude(),
                        mLocation.getLongitude(), 1);
                if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String locality = addresses.get(0).getLocality();
                    String subLocality = addresses.get(0).getSubLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    txtlocationaddress.setText(locality + " " +address);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(HomeActivity.this, Constants.ToastConstatnts.ERROR_FETCHING_LOCATION, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getPhotoOpKm(){
        /// showSimpleProgressDialog(this, "Getting Data...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETSPOPKMPHOTOFARM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("Result").equals("success")) {
                                removeSimpleProgressDialog();

                                JSONArray json = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject obj = json.getJSONObject(i);
                                    String userprofilepic = obj.getString("imageopkm");
                                    String opkm = obj.getString("openingkm");
                                    Picasso.with(getApplicationContext()).load(userprofilepic).placeholder(R.drawable.logo1).into(imagekm);
                                    txtopeningkm.setText(opkm);
                                    btntakephoto.setEnabled(false);
                                    txtopeningkm.setEnabled(false);
                                    mLocation = googleService.getlocation();
                                }
                            } else {
                                removeSimpleProgressDialog();
                                // Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
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
            protected Map<String, String> getParams()
            {
                HashMap<String, String> params = new HashMap<>();
                params.put("spname", spname);
                return params;
            }
        };
        RequestQueue rqueue = Volley.newRequestQueue(SpRootStartActivity.this);
        rqueue.add(stringRequest);
    }

    public String getErrorCode(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            removeSimpleProgressDialog();
            return jsonObject.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "No data";
    }
    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }
    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }
}
