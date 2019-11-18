package com.atharvainfo.nilkamal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class FeedProducts extends AppCompatActivity {
    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    ArrayList<HashMap<String, String>> prdlist;
    TextInputEditText txtqty;
    Button btncancelitem, btnsaveitem;
    Spinner spproduct;
    final List<String> plist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_products);

        prdlist = new ArrayList<>();

        btncancelitem = findViewById(R.id.btncancleitem);
        btnsaveitem = findViewById(R.id.btnsaveitem);
        spproduct = findViewById(R.id.spprodname);
        txtqty = findViewById(R.id.txtqty);


        initToolbar();

        getProductList();

        btnsaveitem.setOnClickListener(v -> {

            String productname = spproduct.getSelectedItem().toString();
            String productqty = Objects.requireNonNull(txtqty.getText()).toString();

            Intent intent = new Intent();
            intent.putExtra("ProductName", productname);
            intent.putExtra("PQty", productqty);
            Log.i("Qty", productname + "//"+ productqty);
            setResult(RESULT_OK, intent);
            finish();
        });

       btncancelitem.setOnClickListener(v -> finish());
    }


    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Select Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    private void getProductList(){
        Tools.showSimpleProgressDialog(this, "Getting Product...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_FEEDPRODLIST,
        response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("result").equals("success")) {
                    Tools.removeSimpleProgressDialog();
                    result = jsonObject.getJSONArray("feedplist");
                    getRecord(result);
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
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FeedProducts.this);
        queue.add(stringRequest);
    }



    private void getRecord(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                plist.add(json.getString("prodname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spproduct.setAdapter(new ArrayAdapter<>(FeedProducts.this, android.R.layout.simple_spinner_dropdown_item, plist));

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
