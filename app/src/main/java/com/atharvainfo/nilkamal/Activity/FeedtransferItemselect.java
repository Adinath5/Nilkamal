package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FeedtransferItemselect extends AppCompatActivity {

    TextView txtfeedrate, txtfeedbal, txtamount;
    TextInputEditText txtfeedqty;
    Spinner spprodname;
    Button btncancleitem, btnsaveitem;
    String spname, spcode,farmname,farmaddress,farmbatch,farmtype,farmcode,farmage;
    String fprodno,fprodname,fprodbal,fprodsupqt, fprodconqt, fprodrate,fcvochno;
    SharedPreferences sharedPreferences;
    ArrayList<String> FeedProdName;
    String productname,productqty,productrate,productamt;
    public static final String PREFS="PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedtransfer_itemselect);

        txtfeedrate = findViewById(R.id.txtrate);
        txtfeedbal = findViewById(R.id.txtbalanceqty);
        txtamount = findViewById(R.id.txtamount);
        txtfeedqty = findViewById(R.id.txtqty);
        spprodname = findViewById(R.id.spprodname);

        btncancleitem = findViewById(R.id.btncanclefitem);
        btnsaveitem = findViewById(R.id.btnsavefeedcitem);

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        spcode = sharedPreferences.getString(Constants.KEY_USERID,null);
        spname = sharedPreferences.getString(Constants.KEY_USERNAME,null);
        //sharedPreferences=getApplicationContext().getSharedPreferences("Mydata",MODE_PRIVATE);
        farmcode =sharedPreferences.getString("farmcode",null);
        farmname = sharedPreferences.getString("farmname",null);
        farmaddress = sharedPreferences.getString("farmaddress",null);
        farmbatch = sharedPreferences.getString("farmbatch",null);
        fcvochno = sharedPreferences.getString("newvoucherno",null);
        farmtype = sharedPreferences.getString("farmtype",null);
        farmage = sharedPreferences.getString("farmage",null);

        FeedProdName=new ArrayList<>();

        initToolbar();

        getSelectedFarmFeedBalance();

        spprodname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                productname = spprodname.getSelectedItem().toString();
                Log.i("Selected Product", productname);
                getProrudtStockAndRate();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        txtfeedbal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String balfeed = txtfeedbal.getText().toString();
                if(!balfeed.equals("0") && !balfeed.equals("null") && !balfeed.isEmpty())
                {
                    txtfeedqty.setEnabled(true);
                } else if(balfeed.equals("0")){
                    txtfeedqty.setEnabled(false);
                    Toast.makeText(FeedtransferItemselect.this, "Selected Product Has No Stock", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                String balfeed = txtfeedbal.getText().toString();
                if(!balfeed.equals("0") && !balfeed.equals("null") && !balfeed.isEmpty())
                {
                    txtfeedqty.setEnabled(true);
                } else if(balfeed.equals("0")){
                    txtfeedqty.setEnabled(false);
                    Toast.makeText(FeedtransferItemselect.this, "Selected Product Has No Stock", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtfeedqty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                DecimalFormat formatter = new DecimalFormat("#######.##");

                String fdqt = Objects.requireNonNull(txtfeedqty.getText()).toString();
                if (!fdqt.equals("0") && !fdqt.isEmpty() && !fdqt.equals("null")) {
                    double fqt = 0;
                    fqt = Double.parseDouble(txtfeedqty.getText().toString());
                    double frt = 0;
                    frt = Double.parseDouble(txtfeedrate.getText().toString());
                    double amt = (fqt) * (frt);
                    txtamount.setText(String.valueOf(formatter.format(amt)));
                }

                String fdbl = txtfeedbal.getText().toString();
                Log.i("Feed Bal", fdbl);
                Log.i("Feed Qt", fdqt);
                if (!fdqt.equals("0") && !fdqt.isEmpty() && !fdqt.equals("null")) {
                    double fqt = Double.parseDouble(fdqt);
                    double fb = Double.parseDouble(fdbl);

                    if(fqt > fb){
                        Toast.makeText(FeedtransferItemselect.this, "Enter Quantity Is Greater Than Available Stock", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnsaveitem.setOnClickListener(v -> {
            String fdqt = Objects.requireNonNull(txtfeedqty.getText()).toString();
            String fdbl = txtfeedbal.getText().toString();
            Log.i("Feed Bal", fdbl);
            Log.i("Feed Qt", fdqt);
            if (!fdqt.equals("0") && !fdqt.isEmpty() && !fdqt.equals("null")) {
                double fqt = Double.parseDouble(fdqt);
                double fb = Double.parseDouble(fdbl);

                if(fqt > fb){
                    Toast.makeText(FeedtransferItemselect.this, "Enter Quantity Is Greater Than Available Stock", Toast.LENGTH_SHORT).show();
                } else if (fqt <= fb){

                    productname =spprodname.getSelectedItem().toString();
                    productqty = txtfeedqty.getText().toString();
                    productrate = txtfeedrate.getText().toString();
                    productamt = txtamount.getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra("ProdNo", fprodno);
                    intent.putExtra("ProdName", productname);
                    intent.putExtra("ProdQty", productqty);
                    intent.putExtra("ProdRate", productrate);
                    intent.putExtra("ProdAmount", productamt);

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }
    private void getSelectedFarmFeedBalance() {
        Tools.showSimpleProgressDialog(this, "Getting Feed Data...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETFARMBALANCEFEED,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("result").equals("success")) {
                            Tools.removeSimpleProgressDialog();
                            JSONArray j = jsonObject.getJSONArray("spfarmbalancefeed");
                            for (int i = 0; i < j.length(); i++) {
                                JSONObject json = j.getJSONObject(i);

                                fprodname = json.getString("fprodname");
                                fprodsupqt = json.getString("fsup");
                                fprodconqt = json.getString("fcon");
                                FeedProdName.add(fprodname);
                                Log.i("Prodname", FeedProdName.toString());
                            }
                            spprodname.setAdapter(new ArrayAdapter<>(FeedtransferItemselect.this, android.R.layout.simple_spinner_dropdown_item, FeedProdName));
                        } else if (jsonObject.optString("result").equals("failure")) {
                            Tools.removeSimpleProgressDialog();
                            Toast.makeText(this, "Farm Data Not Found...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

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
        RequestQueue queue = Volley.newRequestQueue(FeedtransferItemselect.this);
        queue.add(stringRequest);
    }

    private void getProrudtStockAndRate(){
        Tools.showSimpleProgressDialog(this, "Getting Feed Data...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETFEEDSTOCK,
                response -> {
                    fprodbal="0";
                    fprodrate="0";
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("result").equals("success")) {
                            Tools.removeSimpleProgressDialog();
                            JSONObject json = jsonObject.getJSONObject("spfeedstock");
                            fprodno = json.getString("fprodno");
                            fprodbal = json.getString("fbalqt");
                            fprodrate = json.getString("frate");
                            txtfeedbal.setText(fprodbal);
                            txtfeedrate.setText(fprodrate);
                            Log.i("BalanceStock", fprodbal);
                        } else if (jsonObject.optString("result").equals("failure")) {
                            Tools.removeSimpleProgressDialog();
                            Toast.makeText(this, "Farm Data Not Found...", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("farmcode", farmcode);
                params.put("batchno", farmbatch);
                params.put("productname", productname);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FeedtransferItemselect.this);
        queue.add(stringRequest);
    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Feed Transfer Stock");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    @Override
    public void onBackPressed() {

        finish();
    }

}