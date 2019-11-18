package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Adapter.spkmadapter;
import com.atharvainfo.nilkamal.Adapter.spvladapter;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.PhotoFullPopupWindow;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.spkmlist;
import com.atharvainfo.nilkamal.model.spvmodellist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.atharvainfo.nilkamal.Adapter.spvladapter;
import com.squareup.picasso.Picasso;

public class rpt_spkmreport extends AppCompatActivity {

    TextView txtvdate, txtnvdate;
    RecyclerView spfvisitlist;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<spvmodellist> rowListItem;

    private SimpleAdapter vladapter;
    private static ProgressDialog mProgressDialog;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    String vdt;

    DatePickerDialog.OnDateSetListener date;

    String dateformat="dd-MM-yyyy";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> ldgrlist;

    private spvladapter spvladapter;
    spkmadapter spkmadapter;
    ArrayList<spkmlist> spkmData;
    private String fullScreenInd;
    ImageView opimage;
    ImageView climage;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_spkmreport);

        rowListItem = new ArrayList<>();
        spfvisitlist = findViewById(R.id.spfvisitlist);
        txtvdate = findViewById(R.id.txtvdate);
        txtnvdate = findViewById(R.id.txtnvdate);
        ldgrlist = new ArrayList<HashMap<String, String>>();

        Long currentdate=System.currentTimeMillis();
        String datestring=dateform.format(currentdate);
        txtvdate.setText(datestring);

        String sdate = dt.format(currentdate);
        txtnvdate.setText(sdate);
        spkmData = new ArrayList<>();


        initToolbar();

        date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate();
            }
        };

        txtvdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(rpt_spkmreport.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        txtnvdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                 getSpVisitList();

            }
        });



    }
    private void updateDate()
    {
        txtvdate.setText(dateform.format(mycal.getTime()));
        txtnvdate.setText(dt.format(mycal.getTime()));
    }
    private void getSpVisitList() {

        vdt = txtnvdate.getText().toString();
        try {
            String td = formatDate(vdt, "dd-MM-yyyy", "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        try {
            Date dt1 = (Date) formatter.parse(vdt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("Date", vdt.toString());

        //showSimpleProgressDialog(getApplicationContext(), "Getting Farm Visit Data...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_SPVISITLISTADMIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("responsejson", response.toString());
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                removeSimpleProgressDialog();
                                JSONArray json = jsonObject.getJSONArray("spdvlist");
                                result = jsonObject.getJSONArray("spdvlist");
                                getCategory(result);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(rpt_spkmreport.this,"No Data Found",Toast.LENGTH_LONG).show();
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
                params.put("vdate", vdt.toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(rpt_spkmreport.this);
        queue.add(stringRequest);

    }
    private void getCategory(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                HashMap<String, String> parties = new HashMap<>();
                parties.put("A", json.getString("spname"));
                parties.put("B", json.getString("spvdate"));
                parties.put("C", json.getString("spopkm"));
                parties.put("D", json.getString("spclkm"));
                parties.put("E", json.getString("op_image"));
                parties.put("F", json.getString("cl_image"));
                ldgrlist.add(parties);

                spkmData.add(new spkmlist(json.getString("spname"), json.getString("spvdate"),
                        json.getString("spopkm"),json.getString("spclkm"),
                        json.getString("op_image"),json.getString("cl_image")));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            spfvisitlist.setLayoutManager(mLayoutManager);
            spfvisitlist.setItemAnimator(new DefaultItemAnimator());
            spfvisitlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
            spkmadapter = new spkmadapter(getApplicationContext(),spkmData);

            spfvisitlist.setAdapter(spkmadapter);

            spkmadapter.setOnItemClickListener(new spkmadapter.ItemClickListener() {
                @Override
                public void onOpImageCick(int position) {
                    spkmlist spkmlist = spkmData.get(position);
                    String spname = spkmlist.getSpname();
                    final String opim = spkmlist.getOp_image();
                    final String clim = spkmlist.getCl_image();
                    if (opim != null) {
                        new PhotoFullPopupWindow(rpt_spkmreport.this, R.layout.popup_photo_full, opim, null);
                    }
                }

                @Override
                public void onClImageCick(int position) {
                    spkmlist spkmlist = spkmData.get(position);
                    String spname = spkmlist.getSpname();
                    final String opim = spkmlist.getOp_image();
                    final String clim = spkmlist.getCl_image();
                    if (clim != null){
                        new PhotoFullPopupWindow(rpt_spkmreport.this, R.layout.popup_photo_full, clim, null);

                    }
                }

                @Override
                public void onItemClick(int position) {
                    spkmlist spkmlist = spkmData.get(position);
                    String spname = spkmlist.getSpname();
                    final String opim = spkmlist.getOp_image();
                    final String clim = spkmlist.getCl_image();
                }

           });
        }
    }

    public static String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException {
        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
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
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Supervisor Km Reading");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }
}
