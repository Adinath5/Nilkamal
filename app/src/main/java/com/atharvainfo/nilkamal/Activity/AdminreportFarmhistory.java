package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AdminreportFarmhistory extends AppCompatActivity {

    private JSONArray result;
    public static final String JSON_ARRAY = "result";

    ProgressDialog progressDialog;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    AutoCompleteTextView edit_search;
    LinearLayout empty_view;

    String spname,empname,spcode;
    TextView txtspcode, txtspname;
    ListView farmerlist;
    EditText txtfarmname;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> ldgrlist;
    private SimpleAdapter adapter;
    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;
    public static final String PREFS="PREFS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminreport_farmhistory);

        ldgrlist = new ArrayList<HashMap<String, String>>();

        initToolbar();

        //txtspcode = findViewById(R.id.txtspcode);
        farmerlist = findViewById(R.id.spfarmlist);
        txtfarmname = findViewById(R.id.txtspname);


        sp=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        sp.edit();
        spcode = sp.getString("user_name",null);
        spname = sp.getString("empname",null);


        getFarmList();

        farmerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String farmname =((TextView)view.findViewById(R.id.txtpname)).getText().toString();
                String farmaddress = ((TextView)view.findViewById(R.id.txtfadd)).getText().toString();
                String fcode = ((TextView)view.findViewById(R.id.txtglcoe)).getText().toString();
                String birdplace = ((TextView)view.findViewById(R.id.txtqty)).getText().toString();
                String farmbatch = ((TextView)view.findViewById(R.id.txtbatch)).getText().toString();
                String fpqty = ((TextView)view.findViewById(R.id.txtfpqty)).getText().toString();
                String fcqty = ((TextView)view.findViewById(R.id.txtfcqty)).getText().toString();
                String bmqty = ((TextView)view.findViewById(R.id.txtbmqty)).getText().toString();
                String bsqty = ((TextView)view.findViewById(R.id.txtbsqty)).getText().toString();
                String farmtp =((TextView)view.findViewById(R.id.txtfarmtype)).getText().toString();
                double fq = Double.parseDouble(fpqty)- Double.parseDouble(fcqty);
                double bq = Double.parseDouble(birdplace)-(Double.parseDouble(bmqty)+Double.parseDouble(bsqty));

                sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                editor = sp.edit();
                editor.putString("farmcode", fcode);
                editor.putString("farmname", farmname);
                editor.putString("farmaddress", farmaddress);
                editor.putString("farmbatch", farmbatch);
                editor.putString("farmtype", farmtp);
                editor.commit();

                Intent i = new Intent(AdminreportFarmhistory.this, rpt_farmhistory.class);
                startActivity(i);
            }
        });


        txtfarmname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                adapter.getFilter().filter(txtfarmname.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Select Farmer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }



    private void getFarmList(){

        showSimpleProgressDialog(this, "Getting Farm List...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_FARMLISTADMIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                removeSimpleProgressDialog();
                                JSONArray json = jsonObject.getJSONArray("spfarmerlist");
                                result = jsonObject.getJSONArray("spfarmerlist");
                                getRecord(result);
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
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AdminreportFarmhistory.this);
        queue.add(stringRequest);

    }
    private void getRecord(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);

                HashMap<String, String> parties = new HashMap<>();
                parties.put("A", json.getString("farmname"));
                parties.put("B", json.getString("glcode"));
                parties.put("C", json.getString("faddress"));
                parties.put("D", json.getString("batch"));
                parties.put("E", json.getString("bpqty"));
                parties.put("F", json.getString("fpqty"));
                parties.put("G", json.getString("fcqty"));
                parties.put("H", json.getString("bmqty"));
                parties.put("I", json.getString("bsqty"));
                parties.put("J", json.getString("farmtype"));
                ldgrlist.add(parties);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter = new SimpleAdapter(getApplicationContext(), ldgrlist, R.layout.farmlistsp, new String[]{"A", "B", "C", "D", "E","F","G","H","I","J"}, new int[]{R.id.txtpname, R.id.txtglcoe, R.id.txtfadd,R.id.txtbatch, R.id.txtqty, R.id.txtfpqty,R.id.txtfcqty,R.id.txtbmqty,R.id.txtbsqty,R.id.txtfarmtype});
        farmerlist.setAdapter(null);
        farmerlist.setAdapter(adapter);


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
}
