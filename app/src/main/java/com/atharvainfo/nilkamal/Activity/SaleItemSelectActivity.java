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
import com.atharvainfo.nilkamal.Adapter.productadapter;
import com.atharvainfo.nilkamal.model.productlist;
import com.google.android.material.textfield.TextInputEditText;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SaleItemSelectActivity extends AppCompatActivity {

    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;
    public static final String PREFS="PREFS";
    DatabaseHelper dbHelper = null;
    SQLiteDatabase mdatabase;
    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> itmlist;
    private SimpleAdapter prodadapter;
    private ArrayAdapter<String> itemadapter;
    AutoCompleteTextView txtsuppliername, txtprodname;
    TextInputEditText txtqty, txtrate, txtdis, txtamount,txtgstprc,txtgstamt;
    TextView txtitemno, txtcategory, txtmfgcompany,txtmrp,txthsnsac,txtpacking;
    private Toolbar toolbar;
    private ActionBar actionBar;

    Button btncancelitem, btnsaveitem, btnaddpart, btnadditem;
    String vochtype = "";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    productadapter productadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_item_select);

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        vochtype = sharedPreferences.getString("vtype",null);

        dbHelper = new DatabaseHelper(this);
        psDialogMsg = new PSDialogMsg(this, false);

        itmlist = new ArrayList<HashMap<String, String>>();
        btncancelitem = findViewById(R.id.btncancleitem);
        btnsaveitem = findViewById(R.id.btnsavepurchaseitem);


        txtitemno = findViewById(R.id.lblitemno);
        txtprodname = findViewById(R.id.prodname);
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

        txtprodname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem=txtprodname.getAdapter().getItem(position).toString();
                Toast.makeText(getApplicationContext(),selectedItem ,  Toast.LENGTH_SHORT).show();
                getProductItemDetails();
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

                String pq = "0";
                String pr = "0";
                String pd= "0";
                String gp ="0";

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
                if (!Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(txtgstprc.getText())))).toString().equals("") && !txtgstprc.getText().toString().isEmpty() && !txtgstprc.getText().toString().equals("null")){
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

                String pq = "0";
                String pr = "0";
                String pd= "0";
                String gp ="0";

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

                String pq = "0";
                String pr = "0";
                String pd= "0";
                String gp ="0";

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

                String pq = "0";
                String pr = "0";
                String pd= "0";
                String gp ="0";

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

            String productname =txtprodname.getText().toString();
            String productqty = txtqty.getText().toString();
            String productrate = txtrate.getText().toString();
            String productdis = txtdis.getText().toString();
            String productamt = txtamount.getText().toString();
            String prodno = txtitemno.getText().toString();
            String pcat = txtcategory.getText().toString();
            String pmfg = txtmfgcompany.getText().toString();
            String pmrp = txtmrp.getText().toString();
            String phsn = txthsnsac.getText().toString();
            String ppack = txtpacking.getText().toString();
            String pgstprc = txtgstprc.getText().toString();
            String pgstamt = Objects.requireNonNull(txtgstamt.getText()).toString();

            Intent intent = new Intent();
            intent.putExtra("ProductName", productname);
            intent.putExtra("PQty", productqty);
            intent.putExtra("PRate", productrate);
            intent.putExtra("PDis", productdis);
            intent.putExtra("PAmount", productamt);
            intent.putExtra("Itemno", prodno);
            intent.putExtra("ItemCat", pcat);
            intent.putExtra("ItemMfg", pmfg);
            intent.putExtra("ItemMrp", pmrp);
            intent.putExtra("Itemhsn", phsn);
            intent.putExtra("Itempack", ppack);
            intent.putExtra("Itemgstprc", pgstprc);
            intent.putExtra("ItemGstAmt", pgstamt);

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
                    final List<String> flist = new ArrayList<String>();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("Result").equals("success")) {
                            Tools.removeSimpleProgressDialog();

                            JSONArray json = jsonObject.getJSONArray("Data");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject obj = json.getJSONObject(i);
                                flist.add(obj.getString("prodname"));
                            }

                            itemadapter = new ArrayAdapter<String>(SaleItemSelectActivity.this, android.R.layout.select_dialog_item, flist);
                            txtprodname.setAdapter(itemadapter);

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
        RequestQueue queue = Volley.newRequestQueue(SaleItemSelectActivity.this);
        queue.add(stringRequest);
    }

    private void getProductItemDetails(){
        Tools.showSimpleProgressDialog(this, "Getting Stock Detail...","Please Wait ...",false);
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
                        txtrate.setText(obj.getString("crate"));
                        txtmrp.setText(obj.getString("maxrate"));
                        txthsnsac.setText(obj.getString("hsnsac"));
                        txtpacking.setText(obj.getString("packing"));
                    }

                } else {
                    Tools.removeSimpleProgressDialog();
                    Toast.makeText(this, getErrorCode(response.toString()), Toast.LENGTH_SHORT).show();
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
                params.put("prodname", txtprodname.getText().toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(SaleItemSelectActivity.this);
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
