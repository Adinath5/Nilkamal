package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FarmHistoy extends AppCompatActivity {

    TextView txtspname, txtspcode, txtfarmcode, txtfarmname,txtfarmbatch,txtfarmtype,txtfarmaddress,txtplacebird,txtplacedate,txtfdsp, txtfdcon,txtmortqt, txtmortperc,txtage,txtvochno,txtbalancebird;
    String spname, spcode,farmname,farmaddress,farmbatch,farmtype,farmcode, placedate, placebird, feedsuplbag,feedconsbag,mortqty,bsalqty,balbird,farmage,lastentryno,newvoucherno;
    Button btncancle, btnnext;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;

    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    public static final String DATE_FORMAT = "yyyy-M-d";
    public static final String PREFS="PREFS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_histoy);

        initToolbar();

        txtspcode = findViewById(R.id.txtspcode);
        txtspname = findViewById(R.id.txtspname);
        txtfarmcode = findViewById(R.id.txtglcode);
        txtfarmname = findViewById(R.id.txtfname);
        txtfarmaddress = findViewById(R.id.txtfadd);
        txtfarmtype = findViewById(R.id.txtfarmtype);
        txtfarmbatch = findViewById(R.id.txtfbatch);
        txtvochno = findViewById(R.id.txtvochno);

        txtplacebird = findViewById(R.id.txtplacebird);
        txtplacedate = findViewById(R.id.txtplacedate);
        txtfdsp = findViewById(R.id.txtfeedsuplb);
        txtfdcon = findViewById(R.id.txtfeedcons);
        txtmortqt = findViewById(R.id.txtmortqty);
        txtmortperc = findViewById(R.id.txtmortperc);
        txtage = findViewById(R.id.txtage);
        txtbalancebird = findViewById(R.id.txtbalancebird);

        btncancle = findViewById(R.id.btncancle);
        btnnext = findViewById(R.id.btnnext);

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        spcode = sharedPreferences.getString(Constants.KEY_USERID,null);
        spname = sharedPreferences.getString(Constants.KEY_USERNAME,null);
        farmcode =sharedPreferences.getString("farmcode",null);
        farmname = sharedPreferences.getString("farmname",null);
        farmaddress = sharedPreferences.getString("farmaddress",null);
        farmbatch = sharedPreferences.getString("farmbatch",null);

        txtspcode.setText(spcode);
        txtspname.setText(spname);
        txtfarmname.setText(farmname);
        txtfarmaddress.setText(farmaddress);
        txtfarmbatch.setText(farmbatch);
        txtfarmcode.setText(farmcode);

        newvoucherno= "";

        checkEntryExists();
        // formattedDate have current date/time

        //getFarmPurchaseBirdDetail();
        //getFarmFeedMortDetail();


        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newvoucherno.isEmpty()){

                    sharedPreferences = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putString("newvoucherno", newvoucherno.toString());
                    editor.putString("farmtype", farmtype);
                    editor.putString("farmage", farmage);
                    editor.commit();

                    Intent feedconsentry = new Intent(FarmHistoy.this, FeedConsumptionEntry.class);
                    startActivity(feedconsentry);

                } else {
                    getNewVisitEntryNo();
                }
            }
        });
        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void checkEntryExists() {
        Tools.showSimpleProgressDialog(FarmHistoy.this, "Checking Voucher...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_CHECKFEEDENTRYESISTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("CheckEntryExists", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONObject json = jsonObject.getJSONObject("Data");
                                lastentryno = json.getString("vochno");
                                txtvochno.setText("Voucher No : " + lastentryno.toString());
                                newvoucherno = lastentryno;
                                Log.i("Voucher No", lastentryno);

                                getFarmPurchaseBirdDetail();

                            } else if (jsonObject.optString("result").equals("failure")) {
                                Tools.removeSimpleProgressDialog();
                                //getNewVisitEntryNo();
                                getFarmPurchaseBirdDetail();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Tools.removeSimpleProgressDialog();
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("farmcode", farmcode);
                params.put("batchno", farmbatch);
                Log.e("Farmcode", farmcode);
                Log.e("batchno", farmbatch);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FarmHistoy.this);
        queue.add(stringRequest);
    }


    private void getFarmFeedMortDetail(){
        Tools.showSimpleProgressDialog(FarmHistoy.this, "Getting Farm History...","Please Wait ...",false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETFARMDETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("FeedMortDetail", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONObject json = jsonObject.getJSONObject("spfarmerdetailtwo");
                                feedsuplbag = json.getString("feedsuplbag");
                                bsalqty = json.getString("bsalqty");
                                placebird = json.getString("bplaceqty");
                                feedconsbag = json.getString("feedconbag");
                                mortqty = json.getString("bmortqty");
                                farmage = json.getString("birdage");
                                txtfdsp.setText(feedsuplbag);
                                txtfdcon.setText(feedconsbag);
                                txtmortqt.setText(mortqty);
                                double pbq = Double.parseDouble(placebird);
                                double bsl = Double.parseDouble(bsalqty);
                                double bmq = Double.parseDouble(mortqty);
                                double balb = (pbq) - ((bsl) + (bmq));
                                double mp = (((bmq) * 100) / (pbq));
                                DecimalFormat precision = new DecimalFormat("0.00");
                                txtbalancebird.setText(String.valueOf(balb));
                                txtmortperc.setText(precision.format(mp));
                                txtage.setText(String.valueOf(farmage));

                                getNewVisitEntryNo();
                            } else if (jsonObject.optString("result").equals("failure")) {
                                Tools.removeSimpleProgressDialog();
                                Toast.makeText(FarmHistoy.this, "Farm Data Not Found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Tools.removeSimpleProgressDialog();
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("farmcode", farmcode);
                params.put("batchno", farmbatch);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FarmHistoy.this);
        queue.add(stringRequest);
    }
    private void getFarmPurchaseBirdDetail() {

        Tools.showSimpleProgressDialog(FarmHistoy.this, "Getting Data...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETFARMBIRDSUPPLY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("BirdPurchaseDetail", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONObject json = jsonObject.getJSONObject("spfarmerdetailone");
                                farmtype = json.getString("farmtype");
                                placedate = json.getString("vdate");
                                placebird = json.getString("pqty");
                                txtfarmtype.setText(farmtype);
                                txtplacedate.setText(placedate);
                                txtplacebird.setText(placebird);

                                getFarmFeedMortDetail();
                            } else if (jsonObject.optString("result").equals("failure")) {
                                Tools.removeSimpleProgressDialog();
                                Toast.makeText(FarmHistoy.this, "Farm Data Not Found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Tools.removeSimpleProgressDialog();
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("farmcode", farmcode);
                params.put("batchno", farmbatch);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FarmHistoy.this);
        queue.add(stringRequest);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Farm Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static long getDaysBetweenDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    private void getNewVisitEntryNo(){
        Tools. showSimpleProgressDialog(FarmHistoy.this, "Getting Voucher No...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETVISITENTRYNO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("NewVoucherEntry", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONObject json = jsonObject.getJSONObject("spfarmvisitentryno");
                                lastentryno = json.getString("newentryno");
                                txtvochno.setText("Voucher No : " + lastentryno.toString());
                                Log.i("Voucher No", lastentryno);
                                newvoucherno = lastentryno;
                                sharedPreferences = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("newvoucherno", newvoucherno.toString());
                                editor.putString("farmtype", farmtype);
                                editor.putString("farmage", farmage);
                                editor.commit();


                            } else if (jsonObject.optString("result").equals("failure")) {
                                Tools.removeSimpleProgressDialog();
                                Toast.makeText(FarmHistoy.this , "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Tools.removeSimpleProgressDialog();
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("farmcode", farmcode);
                params.put("batchno", farmbatch);
                params.put("batchsize", placebird);
                params.put("batchage", farmage);
                params.put("spname", spcode);
                params.put("farmname", farmname);
                params.put("openbird", txtbalancebird.getText().toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FarmHistoy.this);
        queue.add(stringRequest);
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
