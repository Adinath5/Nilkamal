package com.atharvainfo.nilkamal.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Others.BackgroundLocationService;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.GoogleService;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.LocationAddress;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.RequestHandler;
import com.atharvainfo.nilkamal.Others.Utils;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class SpRootStopActivity extends AppCompatActivity {

    private static final String TAG = SupervisorMain.class.getSimpleName();
    private String defaultUploadWebsite, spcode,spname;
    private TextView txtUserName, mLocationUpdatesResultView,txtlocationaddress;
    private TextInputEditText txtclosingkm;
    private TextView txtWebsite;
    private Button btntakephoto;

    private boolean currentlyTracking;
    private Location mLocation;
    private com.google.android.gms.location.LocationListener listener;
    private static final long UPDATE_INTERVAL = 60 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 5000; /* 2 sec */
    private LocationManager locationManager;
    Geocoder geocoder;
    boolean boolean_permission;
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 3;
    private PSDialogMsg psDialogMsg;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_PERMISSIONS = 102;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Button mRemoveUpdatesButton;
    private Button mRequestUpdatesButton;

    ImageView imagekm;
    Bitmap opkmimage;
    private boolean mAlreadyStartedService;
    GoogleService googleService;
    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;

    public static final String UPLOAD_URL = "http://202.21.38.82:8080/nilkamalpoultry/saveclosingkmimage.php";
    public static final String UPLOAD_KEY = "stopkmimage";
    public static final String PREFS="PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_root_stop);

        googleService = new GoogleService(SpRootStopActivity.this);
        psDialogMsg = new PSDialogMsg(this, false);

        btntakephoto = findViewById(R.id.btntakephoto);
        imagekm = findViewById(R.id.imageopkm);
        txtclosingkm = findViewById(R.id.txtclosinggkm);
        txtlocationaddress = findViewById(R.id.txtlocationaddress);

        defaultUploadWebsite =  "http://202.21.38.82:8080/nilkamalpoultry/updateLocation.php";
        geocoder = new Geocoder(this, Locale.getDefault());

        txtUserName = findViewById(R.id.txtusername);
        mRequestUpdatesButton = findViewById(R.id.trackingButton);
        mRemoveUpdatesButton = findViewById(R.id.remove_updates_button);
        mLocationUpdatesResultView = findViewById(R.id.location_updates_result);
        txtWebsite = findViewById(R.id.txtwebsite);
        //currentlyTracking=true;

        //mRemoveUpdatesButton.setVisibility(View.GONE);

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        sharedPreferences.edit();
        String usernamelog= sharedPreferences.getString(Constants.KEY_USERNAME,null);
        spcode = sharedPreferences.getString(Constants.KEY_USERID,null);
        spname = sharedPreferences.getString(Constants.KEY_USERNAME,null);
        currentlyTracking = sharedPreferences.getBoolean("currentlyTracking", false);

        Log.i("CurrentTracking", String.valueOf(currentlyTracking));
        if (!currentlyTracking){
         //   mRequestUpdatesButton.setVisibility(View.VISIBLE);
            mRemoveUpdatesButton.setVisibility(View.GONE);
        } else {
         //   mRequestUpdatesButton.setVisibility(View.GONE);
            mLocation = googleService.getlocation();
            mAlreadyStartedService = true;
            mRemoveUpdatesButton.setVisibility(View.VISIBLE);
        }
        //Location location = locationService.getLocation(LocationManager.GPS_PROVIDER);
        if (mLocation != null) {
            double latitude = mLocation.getLatitude();
            double longitude = mLocation.getLongitude();
            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new SpRootStopActivity.GeocoderHandler());
            txtlocationaddress.setText(locationAddress.toString());
        }
        // String resultlog="2";
        Log.e("login",usernamelog );
        if((!(usernamelog == null) )){
            txtUserName.setText(usernamelog.toString());
        }


        //checkEnrtiExistsOpkm();
        getPhotoClKm();

        btntakephoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String opkmt = txtclosingkm.getText().toString();
                if(currentlyTracking) {
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
                } else{

                }
            }
        });

        mRemoveUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mGoogleApiClient.(this);
                //mGoogleApiClient.disconnect();
                sharedPreferences = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putBoolean("currentlyTracking", false);
                editor.commit();

                stopService(new Intent(SpRootStopActivity.this, GoogleService.class));
                googleService.stopListener();
                mAlreadyStartedService = false;

               mRemoveUpdatesButton.setVisibility(View.GONE);
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().

            case CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");

                    opkmimage = (Bitmap) data.getExtras().get("data");
                    imagekm.setImageBitmap(opkmimage);
                    getCurrentAddress();
                    uploadKmImage();
                }
                break;
        }
    }

    private void checkEnrtiExistsOpkm(){

        class GetOpKmImage extends AsyncTask<String,Void,Bitmap> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SpRootStopActivity.this, "Uploading...", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();
                imagekm.setImageBitmap(b);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String id = params[0];
                String add = "http://202.21.38.238:8080/nilkamalpoultry/getClosingKmImage.php?spname="+ id;
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(add);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
        }
        GetOpKmImage gi = new GetOpKmImage();
        gi.execute(spname);
    }
    private void uploadKmImage() {
        class UploadFeedImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SpRootStopActivity.this, "Uploading Feed Image", "Please wait...",true,true);
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
                String clkmimagename = "CLKMIMG" + spcode + timeStamp + ".jpg";

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadOpkmImage);
                data.put("spname", spname);
                data.put("closingkm", txtclosingkm.getText().toString());
                data.put("stopaddress", txtlocationaddress.getText().toString());
                data.put("clkmimagename", clkmimagename);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionResult");
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;
                    //    gps.canGetLocation=true;
                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

                }
            }
            break;
            case MY_CAMERA_PERMISSION_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
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

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void getCurrentAddress() {
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }

            } catch (Exception ex) {
                Log.i("msg", "fail to request location update, ignore", ex);
            }
            if (locationManager != null) {
                mLocation = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
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

    private void getPhotoClKm(){
        /// showSimpleProgressDialog(this, "Getting Data...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("spname", spname);
                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_GETSPCLKMPHOTOFARM);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response=e.getMessage();
                }
                return response;
            }
            protected void onPostExecute(String result) {
                //do something with response
                Log.d("newwwss",result);
                try {
                    onGetTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void onGetTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());

        int imageid =0;
        if (serviceCode == jsoncode) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("Result").equals("success")) {
                    removeSimpleProgressDialog();

                    JSONArray json = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        String userprofilepic = obj.getString("imageclkm");
                        String opkm = obj.getString("closingkm");
                        if ((!userprofilepic.equals("")) && (!userprofilepic.equals("null")) && !userprofilepic.isEmpty() && !opkm.equals("") && !opkm.equals("null") && !opkm.isEmpty()) {
                            Picasso.with(getApplicationContext()).load(userprofilepic).placeholder(R.drawable.logo1).into(imagekm);
                            txtclosingkm.setText(opkm);
                            btntakephoto.setEnabled(false);
                            txtclosingkm.setEnabled(false);
                        } else {
                            btntakephoto.setEnabled(true);
                            txtclosingkm.setEnabled(true);
                            removeSimpleProgressDialog();
                        }
                    }
                } else {
                    //    btntakephoto.setEnabled(true);
                    //    txtclosingkm.setEnabled(true);
                    removeSimpleProgressDialog();
                    // Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
