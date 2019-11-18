package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Adapter.StatementAdapter;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.StatementModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class rpt_receivablepayable extends AppCompatActivity {

    TextView txtgrid, txtlblreceivabletot;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri,loginUserId,FyStartDate,FyEndDate;
    AutoCompleteTextView txttransactions;
    private PSDialogMsg psDialogMsg;
    private Toolbar toolbar;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Calendar mycal = Calendar.getInstance();
    String dtf = "yyyy-MM-dd";
    SimpleDateFormat dateform = new SimpleDateFormat(dtf, Locale.US);
    double netDrBal =0;
    double netCrBal =0;

    ArrayList<HashMap<String, String>> voucherlist;
    private ArrayList<StatementModel> VoucherList = new ArrayList<>();
    private ListView list;
    private StatementAdapter mAdapter;
    private String[] transactions ={"All Transactions","Receivable","Payable"};
    private ArrayAdapter<String> adapter;
    private SimpleAdapter rcadapter;
    private final int jsoncode = 1;
    public static final String JSON_ARRAY = "result";
    ArrayList<HashMap<String, String>> acstatedatalist;
    public static final String PREFS="PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_receivablepayable);

        psDialogMsg = new PSDialogMsg(this, false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initToolbar();
        voucherlist = new ArrayList<HashMap<String, String>>();
        txtgrid = findViewById(R.id.txtgrid);

        txttransactions = findViewById(R.id.txttransactiontype);
        txtlblreceivabletot = findViewById(R.id.lblreceivabletotal);

        list=findViewById(R.id.recycler_view);

        sp=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        sp.edit();
        loginUserId = sp.getString(Constants.KEY_USERPHONE, null);
        CompanyName = sp.getString(Constants.COMPANY_NAME, null);
        CompanyAddress = sp.getString(Constants.COMPANY_ADDRESS, null);
        CompanyCity = sp.getString(Constants.COMPANY_CITY, null);
        CompanyTal = sp.getString(Constants.COMPANY_TALUKA, null);
        CompanyDist = sp.getString(Constants.COMPANY_DISTRICT, null);
        CompanyMobile = sp.getString(Constants.COMPANY_MOBILE, null);
        CompanyGST = sp.getString(Constants.COMPANY_GST, null);
        CompanyJuri = sp.getString(Constants.COMPANY_JURISDICTION, null);
        CompanyEmail = sp.getString(Constants.COMPANY_EMAIL, null);
        FyStartDate = sp.getString(Constants.COMPANY_FYSTARTDATE, null);
        FyEndDate = sp.getString(Constants.COMPANY_FYENDDATE, null);

        adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, transactions);
        //lv.setAdapter(adapter);
        txttransactions.setThreshold(2);
        txttransactions.setAdapter(adapter);

        getReceipvablePayable();

        txttransactions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String txttran = txttransactions.getText().toString();
                if (txttran.equals("Receivable")){
                    getReceipvablelist();
                } else if (txttran.equals("Payable")){
                    getPayablelist();
                } else {
                    getReceipvablePayable();
                }
            }
        });
    }
    private void initToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Receivable/Payable Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getReceipvablePayable(){
        voucherlist.clear();

        Tools.showSimpleProgressDialog(rpt_receivablepayable.this, "Getting Ledger...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETRCPAYLEDGERLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            netDrBal =0;
                            netCrBal=0;
                            NumberFormat nf = NumberFormat.getInstance();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();

                                JSONArray json1 = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json1.length(); i++) {
                                    JSONObject obj = json1.getJSONObject(i);
                                    double bamt = Double.parseDouble(obj.getString("netbalance"));
                                    HashMap<String, String> parties = new HashMap<>();
                                    parties.put("A", obj.getString("sledgername"));
                                    parties.put("B", obj.getString("sladdress"));
                                    if (bamt > 0) {
                                        parties.put("C", nf.format(bamt) + " " + "Dr");
                                        netDrBal = (netDrBal) + (bamt);
                                    } else if (bamt < 0) {
                                        parties.put("C", nf.format(Math.abs(bamt)) + " " + "Cr");
                                        netCrBal = (netCrBal) + (bamt);
                                    }
                                    parties.put("D", obj.getString("glcode"));
                                    //parties.put("E", cr.getString(4));
                                    voucherlist.add(parties);

                                }
                                //salestranadapter = new SimpleAdapter(Activity_webview.this, salesprodlist, R.layout.sale_item_list, new String[]{"A", "B","C","D","E","F","G","H","I"}, new int[]{R.id.lblitemname, R.id.lblitemamount, R.id.lblitemqty,R.id.lblitemrate,R.id.lblitemdiscountamt,R.id.lblitemsubtotalamt,R.id.lblitemno,R.id.lblitemcat,R.id.lblitemmfg});
                                //itemlist.setAdapter(prodadapter);
                                rcadapter = new SimpleAdapter(rpt_receivablepayable.this, voucherlist, R.layout.receivable_payable_itemlist, new String[]{"A", "B", "C", "D"}, new int[]{R.id.txtladgername, R.id.txtaddress, R.id.txtrcvamount, R.id.txtgrid});
                                list.setAdapter(null);
                                list.setAdapter(rcadapter);
                                rcadapter.notifyDataSetChanged();
                                txtlblreceivabletot.setText(nf.format(netDrBal-netCrBal));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(rpt_receivablepayable.this,"No Data Found",Toast.LENGTH_LONG).show();
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
        RequestQueue queue = Volley.newRequestQueue(rpt_receivablepayable.this);
        queue.add(stringRequest);

    }

    private void getReceipvablelist(){
        voucherlist.clear();

        Tools.showSimpleProgressDialog(rpt_receivablepayable.this, "Getting Ledger...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETRCPAYLEDGERLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            netDrBal =0;
                            netCrBal=0;
                            NumberFormat nf = NumberFormat.getInstance();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();

                                JSONArray json1 = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json1.length(); i++) {
                                    JSONObject obj = json1.getJSONObject(i);
                                    double bamt = Double.parseDouble(obj.getString("netbalance"));
                                    if (bamt > 0) {
                                        HashMap<String, String> parties = new HashMap<>();
                                        parties.put("A", obj.getString("sledgername"));
                                        parties.put("B", obj.getString("sladdress"));
                                        parties.put("C", nf.format(bamt) + " " + "Dr");
                                        parties.put("D", obj.getString("glcode"));
                                        //parties.put("E", cr.getString(4));
                                        netDrBal = (netDrBal)+(bamt);
                                        voucherlist.add(parties);
                                    }

                                }
                                //salestranadapter = new SimpleAdapter(Activity_webview.this, salesprodlist, R.layout.sale_item_list, new String[]{"A", "B","C","D","E","F","G","H","I"}, new int[]{R.id.lblitemname, R.id.lblitemamount, R.id.lblitemqty,R.id.lblitemrate,R.id.lblitemdiscountamt,R.id.lblitemsubtotalamt,R.id.lblitemno,R.id.lblitemcat,R.id.lblitemmfg});
                                //itemlist.setAdapter(prodadapter);
                                rcadapter = new SimpleAdapter(rpt_receivablepayable.this, voucherlist, R.layout.receivable_payable_itemlist, new String[]{"A", "B", "C", "D"}, new int[]{R.id.txtladgername, R.id.txtaddress, R.id.txtrcvamount, R.id.txtgrid});
                                list.setAdapter(null);
                                list.setAdapter(rcadapter);
                                rcadapter.notifyDataSetChanged();
                                txtlblreceivabletot.setText(nf.format(netDrBal));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(rpt_receivablepayable.this,"No Data Found",Toast.LENGTH_LONG).show();
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
        RequestQueue queue = Volley.newRequestQueue(rpt_receivablepayable.this);
        queue.add(stringRequest);

    }


    private void getPayablelist(){
        voucherlist.clear();

        Tools.showSimpleProgressDialog(rpt_receivablepayable.this, "Getting Ledgers...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETRCPAYLEDGERLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            netDrBal =0;
                            netCrBal=0;
                            NumberFormat nf = NumberFormat.getInstance();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();

                                JSONArray json1 = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json1.length(); i++) {
                                    JSONObject obj = json1.getJSONObject(i);
                                    double bamt = Double.parseDouble(obj.getString("netbalance"));
                                    if (bamt < 0) {
                                        HashMap<String, String> parties = new HashMap<>();
                                        parties.put("A", obj.getString("sledgername"));
                                        parties.put("B", obj.getString("sladdress"));
                                        parties.put("C", nf.format(Math.abs(bamt)) + " " + "Cr");
                                        parties.put("D", obj.getString("glcode"));
                                        //parties.put("E", cr.getString(4));
                                        netCrBal = (netCrBal)+(bamt);
                                        voucherlist.add(parties);
                                    }

                                }
                                //salestranadapter = new SimpleAdapter(Activity_webview.this, salesprodlist, R.layout.sale_item_list, new String[]{"A", "B","C","D","E","F","G","H","I"}, new int[]{R.id.lblitemname, R.id.lblitemamount, R.id.lblitemqty,R.id.lblitemrate,R.id.lblitemdiscountamt,R.id.lblitemsubtotalamt,R.id.lblitemno,R.id.lblitemcat,R.id.lblitemmfg});
                                //itemlist.setAdapter(prodadapter);
                                rcadapter = new SimpleAdapter(rpt_receivablepayable.this, voucherlist, R.layout.receivable_payable_itemlist, new String[]{"A", "B", "C", "D"}, new int[]{R.id.txtladgername, R.id.txtaddress, R.id.txtrcvamount, R.id.txtgrid});
                                list.setAdapter(null);
                                list.setAdapter(rcadapter);
                                rcadapter.notifyDataSetChanged();
                                txtlblreceivabletot.setText(nf.format(Math.abs(netCrBal)));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(rpt_receivablepayable.this,"No Data Found",Toast.LENGTH_LONG).show();
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
        RequestQueue queue = Volley.newRequestQueue(rpt_receivablepayable.this);
        queue.add(stringRequest);

    }

}