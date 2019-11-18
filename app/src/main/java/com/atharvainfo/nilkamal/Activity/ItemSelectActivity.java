package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.textfield.TextInputEditText;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemSelectActivity extends AppCompatActivity {

    DatabaseHelper dbHelper = null;
    SQLiteDatabase mdatabase;
    ArrayList<HashMap<String, String>> itmlist;
    TextInputEditText txtqty, txtrate, txtdis, txtamount,txtgstprc,txtgstamt;
    TextView txtitemno, txtcategory, txtmfgcompany,txtmrp,txthsnsac,txtpacking;
    private Toolbar toolbar;
    private ActionBar actionBar;
    ArrayList<String> ProdNameList;
    Button btncancelitem, btnsaveitem, btnaddpart, btnadditem;
    String vochtype = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Spinner spprodname;
    String ProductName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_select);
        dbHelper = new DatabaseHelper(this);

        itmlist = new ArrayList<HashMap<String, String>>();
        btncancelitem = findViewById(R.id.btncancleitem);
        btnsaveitem = findViewById(R.id.btnsavepurchaseitem);
        btnadditem = findViewById(R.id.btnadditem);
        ProdNameList = new ArrayList<>();
        spprodname = findViewById(R.id.spprodname);

        txtitemno = findViewById(R.id.lblitemno);
        txtqty = findViewById(R.id.txtqty);
        txtrate = findViewById(R.id.txtrate);
        txtdis = findViewById(R.id.txtdiscamt);
        txtamount = findViewById(R.id.txtamount);
        txtcategory = findViewById(R.id.lblcat);
        txtmfgcompany = findViewById(R.id.lblmfg);
        txtgstprc = findViewById(R.id.txtgstperc);
        txtgstamt = findViewById(R.id.txtgstamt);
        txtmrp = findViewById(R.id.txtmrp);
        txthsnsac = findViewById(R.id.txthsnsac);
        txtpacking = findViewById(R.id.txtpacking);

        txtdis.setText("0");

        toolbar = findViewById(R.id.toolbarit);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);

        getProductList();

        spprodname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ProductName = spprodname.getSelectedItem().toString();
                Log.i("Selected Product", ProductName);
                getProductItemDetails();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnadditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent intent = new Intent(ItemSelectActivity.this, NewProductActivity.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
              //  startActivity(intent);
            }
        });

        txtrate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String pq ;
                String pr ;
                String pd;
                String gp ;

                if (!Objects.requireNonNull(txtqty.getText()).toString().equals("") && !txtqty.getText().toString().isEmpty() && !txtqty.getText().toString().equals("null")){
                    pq = txtqty.getText().toString();
                } else {
                    pq ="0";
                }
                if (!Objects.requireNonNull(txtrate.getText()).toString().equals("") && !txtrate.getText().toString().isEmpty() && !txtrate.getText().toString().equals("null")){
                    pr = txtrate.getText().toString();
                } else {
                    pr ="0";
                }
                if (!Objects.requireNonNull(txtdis.getText()).toString().equals("") && !txtdis.getText().toString().isEmpty() && !txtdis.getText().toString().equals("null")){
                    pd = txtdis.getText().toString();
                } else {
                    pd ="0";
                }
                if (!Objects.requireNonNull(txtgstprc.getText()).toString().equals("") && !txtgstprc.getText().toString().isEmpty() && !txtgstprc.getText().toString().equals("null")){
                    gp = txtgstprc.getText().toString();
                } else {
                    gp ="0";
                }

                if(!pq.isEmpty()) {
                    if (!pd.isEmpty()) {
                        if (!pr.isEmpty()) {
                            double prodamt = Double.parseDouble(pq) * Double.parseDouble(pr);
                            double bgamount = prodamt - Double.parseDouble(pd);
                            double gprc = Double.parseDouble(gp);
                            double bgstamount = bgamount /(1+(gprc)/100);
                            double gstrate = (bgstamount)/(Double.valueOf(pq));
                            double gstamount = (bgamount)-(bgstamount);

                            txtamount.setText(NumberFormat.getInstance().format(bgamount));
                            txtgstamt.setText(NumberFormat.getInstance().format(gstamount));

                        }
                    }
                }

            }
        });


        txtqty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String pq;
                String pr;
                String pd;
                String gp;

                if (!Objects.requireNonNull(txtqty.getText()).toString().equals("") && !txtqty.getText().toString().isEmpty() && !txtqty.getText().toString().equals("null")){
                    pq = txtqty.getText().toString();
                } else {
                    pq ="0";
                }
                if (!Objects.requireNonNull(txtrate.getText()).toString().equals("") && !txtrate.getText().toString().isEmpty() && !txtrate.getText().toString().equals("null")){
                    pr = txtrate.getText().toString();
                } else {
                    pr ="0";
                }
                if (!Objects.requireNonNull(txtdis.getText()).toString().equals("") && !txtdis.getText().toString().isEmpty() && !txtdis.getText().toString().equals("null")){
                    pd = txtdis.getText().toString();
                } else {
                    pd ="0";
                }
                if (!Objects.requireNonNull(txtgstprc.getText()).toString().equals("") && !txtgstprc.getText().toString().isEmpty() && !txtgstprc.getText().toString().equals("null")){
                    gp = txtgstprc.getText().toString();
                } else {
                    gp ="0";
                }

                if(!pq.isEmpty()) {
                    if (!pd.isEmpty()) {
                        if (!pr.isEmpty()) {
                            double prodamt = Double.parseDouble(pq) * Double.parseDouble(pr);
                            double bgamount = prodamt - Double.parseDouble(pd);
                            double gprc = Double.parseDouble(gp);
                            double bgstamount = bgamount /(1+(gprc)/100);
                            double gstrate = (bgstamount)/(Double.valueOf(pq));
                            double gstamount = (bgamount)-(bgstamount);

                            txtamount.setText(NumberFormat.getInstance().format(bgamount));
                            txtgstamt.setText(NumberFormat.getInstance().format(gstamount));

                        }
                    }
                }

            }
        });

        txtdis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String pq;
                String pr;
                String pd;
                String gp;

                if (!Objects.requireNonNull(txtqty.getText()).toString().equals("") && !txtqty.getText().toString().isEmpty() && !txtqty.getText().toString().equals("null")){
                    pq = txtqty.getText().toString();
                } else {
                    pq ="0";
                }
                if (!Objects.requireNonNull(txtrate.getText()).toString().equals("") && !txtrate.getText().toString().isEmpty() && !txtrate.getText().toString().equals("null")){
                    pr = txtrate.getText().toString();
                } else {
                    pr ="0";
                }
                if (!Objects.requireNonNull(txtdis.getText()).toString().equals("") && !txtdis.getText().toString().isEmpty() && !txtdis.getText().toString().equals("null")){
                    pd = txtdis.getText().toString();
                } else {
                    pd ="0";
                }
                if (!Objects.requireNonNull(txtgstprc.getText()).toString().equals("") && !txtgstprc.getText().toString().isEmpty() && !txtgstprc.getText().toString().equals("null")){
                    gp = txtgstprc.getText().toString();
                } else {
                    gp ="0";
                }

                if(!pq.isEmpty()) {
                    if (!pd.isEmpty()) {
                        if (!pr.isEmpty()) {
                            double prodamt = Double.parseDouble(pq) * Double.parseDouble(pr);
                            double bgamount = prodamt - Double.parseDouble(pd);
                            double gprc = Double.parseDouble(gp);
                            double bgstamount = bgamount /(1+(gprc)/100);
                            double gstrate = (bgstamount)/(Double.valueOf(pq));
                            double gstamount = (bgamount)-(bgstamount);

                            txtamount.setText(NumberFormat.getInstance().format(bgamount));
                            txtgstamt.setText(NumberFormat.getInstance().format(gstamount));

                        }
                    }
                }


            }
        });

        txtgstprc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String pq ;
                String pr ;
                String pd;
                String gp;

                if (!Objects.requireNonNull(txtqty.getText()).toString().equals("") && !txtqty.getText().toString().isEmpty() && !txtqty.getText().toString().equals("null")){
                    pq = txtqty.getText().toString();
                } else {
                    pq ="0";
                }
                if (!Objects.requireNonNull(txtrate.getText()).toString().equals("") && !txtrate.getText().toString().isEmpty() && !txtrate.getText().toString().equals("null")){
                    pr = txtrate.getText().toString();
                } else {
                    pr ="0";
                }
                if (!Objects.requireNonNull(txtdis.getText()).toString().equals("") && !txtdis.getText().toString().isEmpty() && !txtdis.getText().toString().equals("null")){
                    pd = txtdis.getText().toString();
                } else {
                    pd ="0";
                }
                if (!Objects.requireNonNull(txtgstprc.getText()).toString().equals("") && !txtgstprc.getText().toString().isEmpty() && !txtgstprc.getText().toString().equals("null")){
                    gp = txtgstprc.getText().toString();
                } else {
                    gp ="0";
                }

                if(!pq.isEmpty()) {
                    if (!pd.isEmpty()) {
                        if (!pr.isEmpty()) {
                            double prodamt = Double.parseDouble(pq) * Double.parseDouble(pr);
                            double bgamount = prodamt - Double.parseDouble(pd);
                            double gprc = Double.parseDouble(gp);
                            double bgstamount = bgamount /(1+(gprc)/100);
                            double gstrate = (bgstamount)/(Double.valueOf(pq));
                            double gstamount = (bgamount)-(bgstamount);

                            txtamount.setText(NumberFormat.getInstance().format(bgamount));
                            txtgstamt.setText(NumberFormat.getInstance().format(gstamount));

                        }
                    }
                }
            }
        });

        btnsaveitem.setOnClickListener(v -> {

            String productname =spprodname.getSelectedItem().toString();
            String productqty = Objects.requireNonNull(txtqty.getText()).toString();
            String productrate = Objects.requireNonNull(txtrate.getText()).toString();
            String productdis = Objects.requireNonNull(txtdis.getText()).toString();
            String productamt = Objects.requireNonNull(txtamount.getText()).toString();
            String prodno = txtitemno.getText().toString();
            String pcat = txtcategory.getText().toString();
            String pmfg = txtmfgcompany.getText().toString();
            String pmrp = txtmrp.getText().toString();
            String phsn = txthsnsac.getText().toString();
            String ppack = txtpacking.getText().toString();
            String pgstprc = Objects.requireNonNull(txtgstprc.getText()).toString();
            String pgstamt = Objects.requireNonNull(txtgstamt.getText()).toString();
            double gstamt=0;
            double ptotamt =0;
            try {
                gstamt = DecimalFormat.getNumberInstance().parse(txtgstamt.getText().toString()).doubleValue();
                ptotamt=DecimalFormat.getNumberInstance().parse(txtamount.getText().toString()).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.e("GST Amount", String.valueOf(ptotamt));
            Log.e("GST Amount", String.valueOf(gstamt));

            Intent intent = new Intent();
            intent.putExtra("ProductName", productname);
            intent.putExtra("PQty", productqty);
            intent.putExtra("PRate", productrate);
            intent.putExtra("PDis", productdis);
            intent.putExtra("PAmount", String.valueOf(ptotamt));
            intent.putExtra("Itemno", prodno);
            intent.putExtra("ItemCat", pcat);
            intent.putExtra("ItemMfg", pmfg);
            intent.putExtra("ItemMrp", pmrp);
            intent.putExtra("Itemhsn", phsn);
            intent.putExtra("Itempack", ppack);
            intent.putExtra("Itemgstprc", pgstprc);
            intent.putExtra("ItemGstAmt", String.valueOf(gstamt));

            setResult(RESULT_OK, intent);
            finish();
        });

        btncancelitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getProductList(){
        Tools.showSimpleProgressDialog(this, "Getting Product List...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_PRODUCTNAMELIST,
        response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("Result").equals("success")) {
                    Tools.removeSimpleProgressDialog();

                    JSONArray json = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        String productnm = obj.getString("prodname");
                        ProdNameList.add(productnm);
                    }
                    spprodname.setAdapter(new ArrayAdapter<String>(ItemSelectActivity.this, android.R.layout.simple_spinner_dropdown_item, ProdNameList));

                } else {
                    Tools.removeSimpleProgressDialog();
                    Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
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
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ItemSelectActivity.this);
        queue.add(stringRequest);
    }

    private void getProductItemDetails(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETPRODUCTITEMDETAILS,
        response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("Result").equals("success")) {
                    Tools.removeSimpleProgressDialog();
                    JSONArray json = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        txtitemno.setText(obj.getString("prodno"));
                        txtcategory.setText(obj.getString("prodtype"));
                        txtmfgcompany.setText(obj.getString("mfgcomp"));
                        txtgstprc.setText(obj.getString("igst"));
                        txtrate.setText(obj.getString("prate"));
                        String nmrp = "0";
                        if (!obj.getString("maxrate").equals("") && !obj.getString("maxrate").equals("null") && !obj.getString("maxrate").isEmpty()){
                            txtmrp.setText(obj.getString("maxrate"));
                        } else {
                            txtmrp.setText(nmrp);
                        }
                        txthsnsac.setText(obj.getString("hsnsac"));
                        txtpacking.setText(obj.getString("packing"));
                    }

                } else {
                    Tools.removeSimpleProgressDialog();
                    Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
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
                params.put("prodname", ProductName);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ItemSelectActivity.this);
        queue.add(stringRequest);
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
