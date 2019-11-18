package com.atharvainfo.nilkamal.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Adapter.farmlistadapter;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.farmlistmodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpFarmList extends AppCompatActivity {
    ArrayList<farmlistmodel> datamodel;
    farmlistadapter farmlistadapter;
    private JSONArray result;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String spname,spcode, farmcode, farmname,farmbatch,farmaddrress;
    TextView txtspcode, txtspname;
    RecyclerView farmerlist;

    ArrayList<HashMap<String, String>> ldgrlist;
    Boolean EntryDone = false;
    public static final String PREFS="PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_farm_list);
        ldgrlist = new ArrayList<>();

        initToolbar();

        txtspcode = findViewById(R.id.txtspcode);
        txtspname = findViewById(R.id.txtspname);
        farmerlist = findViewById(R.id.farmlist);
        datamodel = new ArrayList<>();

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        spcode = sharedPreferences.getString(Constants.KEY_USER_UID,null);
        spname = sharedPreferences.getString(Constants.KEY_USERNAME,null);

        txtspcode.setText(spcode);
        txtspname.setText(spname);
        Log.i("Spcode", txtspname.getText().toString());

        getSpFarmListAll();

    }

    private void getFarmVisitData(){
        Tools.showSimpleProgressDialog(SpFarmList.this, "Checking Data...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETFARMVISITDATA,
            response -> {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("visited")) {
                        Tools.removeSimpleProgressDialog();
                        EntryDone = true;
                        Toast.makeText(this, "Farm Already Visited", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.optString("result").equals("notvisited")){
                        Tools.removeSimpleProgressDialog();
                        sharedPreferences = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putString("farmcode", farmcode);
                        editor.putString("farmname", farmname);
                        editor.putString("farmaddress", farmaddrress);
                        editor.putString("farmbatch", farmbatch);
                        editor.commit();

                        EntryDone =false;
                        Intent i = new Intent(SpFarmList.this, FarmHistoy.class);
                        startActivity(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
                error -> {
                    Tools.removeSimpleProgressDialog();
                    error.printStackTrace();
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
        RequestQueue queue = Volley.newRequestQueue(SpFarmList.this);
        queue.add(stringRequest);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Select Farmer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SpFarmList.this, SupervisorMain.class);
        startActivity(i);
        finish();
    }

    private void getSpFarmListAll(){
        Tools.showSimpleProgressDialog(this, "Getting Farm List...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_FARMLISTSP,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("result").equals("success")) {
                            Tools.removeSimpleProgressDialog();
                            result = jsonObject.getJSONArray("spfarmerlist");
                            getRecord(result);
                        } else {
                            Tools.removeSimpleProgressDialog();
                            // Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Tools.removeSimpleProgressDialog();
                    error.printStackTrace();
                })
        {
            @Override
            protected Map<String, String> getParams(){
                String openclose ="O";
                Map<String, String> params = new HashMap<>();
                params.put("spname", spname);
                params.put("openclose", openclose);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(SpFarmList.this);
        queue.add(stringRequest);

    }

    private void getRecord(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                datamodel.add(new farmlistmodel(json.getString("glcode"),json.getString("farmname"),
                        json.getString("faddress"),json.getString("batch"),json.getString("bpqty"),
                        json.getString("fpqty"),json.getString("fcqty"),json.getString("bmqty"),
                        json.getString("bsqty")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e("FarmList", datamodel.toString());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        farmerlist.setLayoutManager(mLayoutManager);
        farmerlist.setItemAnimator(new DefaultItemAnimator());
        //ledgerlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        farmlistadapter = new farmlistadapter(datamodel, getApplicationContext());

        farmerlist.setAdapter(farmlistadapter);
        farmlistadapter.setOnItemClickListener((view, obj, position) -> {
            farmcode = ((TextView) view.findViewById(R.id.txtglcoe)).getText().toString();
            farmbatch = ((TextView) view.findViewById(R.id.txtbatch)).getText().toString();
            farmname = ((TextView) view.findViewById(R.id.txtpname)).getText().toString();
            farmaddrress  =((TextView) view.findViewById(R.id.txtfadd)).getText().toString();

            getFarmVisitData();
        });
    }
    public boolean isSuccess(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("result").equals("success")) {
                Tools.removeSimpleProgressDialog();
                return true;
            } else {
                Tools.removeSimpleProgressDialog();
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
            Tools.removeSimpleProgressDialog();
            return jsonObject.getString("message");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "No data";
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
