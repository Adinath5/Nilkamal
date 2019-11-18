package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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


public class FarmListAll extends AppCompatActivity {

    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    SharedPreferences sharedPreferences;
    public static final String PREFS="PREFS";
    String spname,spcode;
    RecyclerView farmerlist;
    ArrayList<HashMap<String, String>> ldgrlist;
    ArrayList<farmlistmodel> datamodel;
    farmlistadapter farmlistadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_list_all);

        ldgrlist = new ArrayList<>();

        initToolbar();

        farmerlist = findViewById(R.id.farmlist);
        datamodel = new ArrayList<>();

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        spcode = sharedPreferences.getString(Constants.KEY_USERSPID, null);
        spname = sharedPreferences.getString(Constants.KEY_USERNAME, null);
        Log.i("Spcode", spname);
        //txtspcode.setText(spcode);
        getFarmList();



    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Select Farmer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }
    private void getFarmList(){

        String openclose ="O";
        Log.i("spname", spname);
        Tools.showSimpleProgressDialog(this, "Getting Farmers...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_FARMLISTALL,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("result").equals("success")) {
                            Tools.removeSimpleProgressDialog();
                            result = jsonObject.getJSONArray("spfarmerlist");
                            getRecord(result);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Tools.removeSimpleProgressDialog();
                    Toast.makeText(FarmListAll.this, getErrorCode(error.toString()), Toast.LENGTH_SHORT).show();
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("openclose", openclose);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FarmListAll.this);
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
            String farmname =((TextView)view.findViewById(R.id.txtpname)).getText().toString();
            String farmaddress = ((TextView)view.findViewById(R.id.txtfadd)).getText().toString();
            String fcode = ((TextView)view.findViewById(R.id.txtglcoe)).getText().toString();
            String birdplace = ((TextView)view.findViewById(R.id.txtqty)).getText().toString();
            String farmbatch = ((TextView)view.findViewById(R.id.txtbatch)).getText().toString();
            String fpqty = ((TextView)view.findViewById(R.id.txtfpqty)).getText().toString();
            String fcqty = ((TextView)view.findViewById(R.id.txtfcqty)).getText().toString();
            String bmqty = ((TextView)view.findViewById(R.id.txtbmqty)).getText().toString();
            String bsqty = ((TextView)view.findViewById(R.id.txtbsqty)).getText().toString();
            double fq = Double.parseDouble(fpqty)- Double.parseDouble(fcqty);
            double bq = Double.parseDouble(birdplace)-(Double.parseDouble(bmqty)+Double.parseDouble(bsqty));

            Intent intent = new Intent();
            intent.putExtra("FarmName", farmname);
            intent.putExtra("FarmAddress", farmaddress);
            intent.putExtra("FarmCode", fcode);
            intent.putExtra("BatchNo", farmbatch);
            intent.putExtra("BirdPlace", birdplace);
            intent.putExtra("FeedStock", String.valueOf(fq));
            intent.putExtra("BirdStock", String.valueOf(bq));

            setResult(RESULT_OK, intent);
            finish();

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

}