package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.GoogleService;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.LocationAddress;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.UserDetails;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.ChatMessage;
import com.atharvainfo.nilkamal.services.MySingleton;
import com.firebase.client.Firebase;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class FarmfeedEntrylast extends AppCompatActivity {
    final String TAG = "NOTIFICATION TAG";
    private final int jsoncode = 1;

    private JSONArray result;

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    Firebase reference1, reference2;

    TextView txtspname, txtspcode, txtfarmcode, txtfarmname,txtfarmbatch,txtspfarmadd,txtfarmaddress,txtplacebird,txtplacedate,txtfdsp, txtfdcon,txtmortqt, txtmortperc,txtage,txtvochno,txtbalancebird;
    String spname, spcode,farmname,farmaddress,farmbatch,farmtype,farmcode, placedate, placebird, feedsuplbag,feedconsbag,mortqty,bsalqty,balbird,farmage,lastentryno,mortpc,device_token;
    Button btncancle, btnnext, btnfeedphoto,btnmortphoto;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ListView feedconlist;
    private SimpleAdapter prodadapter;
    String mortreasone;
    Spinner spmortreason;
    ArrayList<String> MortReasonList;
    Boolean FeedEntryExists = false;
    Boolean MortEntryExists = false;

    ArrayList<HashMap<String, String>> itmlist;
    private static ProgressDialog mProgressDialog;
    public static final String JSON_ARRAY = "result";
    public static final String DATE_FORMAT = "yyyy-M-d";
    ImageView feedphoto,mortphoto;
    private Uri mImageCaptureUri;
    private AlertDialog dialog;
    GoogleService googleService;
    private PSDialogMsg psDialogMsg;
    TextInputEditText txtfarmobservation,txtfarmsuggestion,txtfarmbroodtemp,txtfarmbodyweight,txtfarmfeeder,txtfarmdrinker,txtfarmwater,txtfarmlitter,txtfarmlight;
    public static final String PREFS="PREFS";
    String userEmail;
    LocationAddress locationAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmfeed_entrylast);
        googleService = new GoogleService(FarmfeedEntrylast.this);
        txtspcode = findViewById(R.id.txtspcode);
        txtspname = findViewById(R.id.txtspname);

        txtfarmname = findViewById(R.id.txtfname);
        txtfarmbatch = findViewById(R.id.txtfbatch);
        txtfarmobservation = findViewById(R.id.txtfarmobsrv);
        txtfarmsuggestion = findViewById(R.id.txtfarmsugg);
        txtfarmbroodtemp = findViewById(R.id.txtfarmbemp);
        txtfarmbodyweight = findViewById(R.id.txtfarmbwt);
        txtfarmfeeder = findViewById(R.id.txtfarmfdr);
        txtfarmdrinker = findViewById(R.id.txtfarmdrnk);
        txtfarmwater = findViewById(R.id.txtfarmwaterq);
        txtfarmlitter = findViewById(R.id.txtfarmlitterc);
        txtfarmlight = findViewById(R.id.txtfarmlightp);
        txtspfarmadd = findViewById(R.id.txtspaddress);

        btncancle = findViewById(R.id.btnfdlcancle);
        btnnext = findViewById(R.id.btnfdlsave);

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        spcode = sharedPreferences.getString(Constants.KEY_USERID,null);
        spname = sharedPreferences.getString(Constants.KEY_USERNAME,null);
        userEmail = sharedPreferences.getString(Constants.KEY_USEREMAIL, null);
        farmcode =sharedPreferences.getString("farmcode",null);
        farmname = sharedPreferences.getString("farmname",null);
        farmaddress = sharedPreferences.getString("farmaddress",null);
        farmbatch = sharedPreferences.getString("farmbatch",null);
        lastentryno = sharedPreferences.getString("newvoucherno",null);
        farmtype = sharedPreferences.getString("farmtype",null);
        farmage = sharedPreferences.getString("farmage",null);
        device_token = sharedPreferences.getString("device_tokan", null);

        psDialogMsg = new PSDialogMsg(FarmfeedEntrylast.this, false);
        txtspcode.setText(spcode);
        txtspname.setText(spname);
        txtfarmname.setText(farmname);
        txtfarmbatch.setText(farmbatch);
        //Firebase.setAndroidContext(this);
        //reference1 = new Firebase("https://nilkamal-b0462.firebaseio.com/messages/" +UserDetails.username + "_" + UserDetails.chatWith);
        //reference2 = new Firebase("https://nilkamal-b0462.firebaseio.com/messages/");


        initToolbar();
        Location location = googleService.getlocation();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            locationAddress = new LocationAddress();
            LocationAddress.getAddressFromLocation(latitude, longitude,
                    getApplicationContext(), new GeocoderHandler());
        }

        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bdwt = txtfarmbodyweight.getText().toString();
                if (!bdwt.isEmpty()) {
                    SaveFeedConsumpionEntryLst(farmcode, farmbatch);
                } else {
                    psDialogMsg.showWarningDialog("Please Enter Body Weight First", "Ok");
                    psDialogMsg.show();
                }
            }
        });

    }

    private void SaveFeedConsumpionEntryLst(String farmcode, String farmbatch) {
        showSimpleProgressDialog(this, "Getting Feed Data...","Please Wait ...",false);
        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("farmcode", farmcode);
                map.put("batchno", farmbatch);
                map.put("vochno", lastentryno);
                map.put("farmobs", txtfarmobservation.getText().toString());
                map.put("farmsugg", txtfarmsuggestion.getText().toString());
                map.put("farmbroodt", txtfarmbroodtemp.getText().toString());
                map.put("farmbodywt", txtfarmbodyweight.getText().toString());
                map.put("farmfeeder", txtfarmfeeder.getText().toString());
                map.put("farmdrinker", txtfarmdrinker.getText().toString());
                map.put("farmwaterq", txtfarmwater.getText().toString());
                map.put("farmlitter", txtfarmlitter.getText().toString());
                map.put("farmlight", txtfarmlight.getText().toString());
                map.put("spaddress", txtspfarmadd.getText().toString());

                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_SAVEDAILYENTRYLAST);
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
                    onTaskCompleted(result,jsoncode);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Farm Daily Entry");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    public void onTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        if (serviceCode == jsoncode) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("result").equals("success")) {
                    removeSimpleProgressDialog();
                    // JSONArray j = jsonObject.getJSONArray("spfeedstock");

                    getFarmFeedMortDetail(farmcode, farmbatch);

                } else if (jsonObject.optString("result").equals("failure")) {
                    removeSimpleProgressDialog();
                    Toast.makeText(this, "Farm Data Not Found...", Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean isSuccess(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("result").equals("success")) {
                removeSimpleProgressDialog();
                return true;
            } else {
                removeSimpleProgressDialog();
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
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
            txtspfarmadd.setText(locationAddress);
        }
    }



    private void getFarmFeedMortDetail(String farmcode, String farmbatch) {
        Log.i("spname", farmcode);
        showSimpleProgressDialog(this, "Getting Data...","Please Wait ...",false);
        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("farmcode", farmcode);
                map.put("batchno", farmbatch);

                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_GETFARMDETAILS);
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
                    onTaskCompletedFarmData(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    public void onTaskCompletedFarmData(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        if (serviceCode == jsoncode) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("result").equals("success")) {
                    removeSimpleProgressDialog();
                    JSONObject json = jsonObject.getJSONObject("spfarmerdetailtwo");

                    feedsuplbag = json.getString("feedsuplbag");
                    bsalqty = json.getString("bsalqty");
                    placebird = json.getString("bplaceqty");
                    feedconsbag = json.getString("feedconbag");
                    mortqty = json.getString("bmortqty");
                    //txtfdsp.setText(feedsuplbag);
                    //txtfdcon.setText(feedconsbag);
                    //txtmortqt.setText(mortqty);
                    SendNoticifationMessage();

                    psDialogMsg.showSuccessDialog("Daily Entry Save Successfully", "Ok");
                    psDialogMsg.show();
                    psDialogMsg.okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), SupervisorMain.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    });

                } else if (jsonObject.optString("result").equals("failure")) {
                    removeSimpleProgressDialog();
                    Toast.makeText(this, "Farm Data Not Found...", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void SendNoticifationMessage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_SENDFARMNOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                removeSimpleProgressDialog();

                            } else if (jsonObject.optString("result").equals("failure")) {
                                removeSimpleProgressDialog();
                                Toast.makeText(getApplicationContext(), "No Such Customer Name Found", Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e) {
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
                Map<String, String> params=new HashMap<>();
                double pbq = Double.parseDouble(placebird);
                double bsl = Double.parseDouble(bsalqty);
                double bmq = Double.parseDouble(mortqty);
                double balb = (pbq) - ((bsl) + (bmq));
                double mp = (((bmq) * 100) / (pbq));
                DecimalFormat precision = new DecimalFormat("0.00");
                balbird = String.valueOf(balb);
                mortpc = precision.format(mp);

                String msg = "Farm Name :" + farmname + "\n Farm Address :" + farmaddress + "\n Batch No. : " + farmbatch + " Farm Type : " + farmtype + "\n Place Bird : " + placebird + " Age : " + farmage + "\n Feed Cons Bag :" + feedconsbag + " Total Mort. : " + mortqty + "\n Feed Suply Bag : " + feedsuplbag + " \n Balance Bird :" + balbird + " Mort.% " + mortpc + " \n Visit By : " + spname;
                TOPIC = "/topics/Nilkamal"; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = "Daily Visit Entry " + spcode;
                NOTIFICATION_MESSAGE = msg;
                params.put("mtitle", NOTIFICATION_TITLE);
                params.put("mtext", NOTIFICATION_MESSAGE);
                params.put("useremail", userEmail);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FarmfeedEntrylast.this);
        queue.add(stringRequest);
    }
    public static long getDaysBetweenDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            assert endDate != null;
            numberOfDays = getUnitBetweenDates(startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
