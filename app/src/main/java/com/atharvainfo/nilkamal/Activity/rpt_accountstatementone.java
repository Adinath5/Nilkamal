package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class rpt_accountstatementone extends AppCompatActivity {

    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener date1;
    TextView txtgrid, txtsdate, txtedate, txtstdate, txtetdate,txtlbldrtot,txtlblcrtot,txtlblnettot, txtbgrid;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri,FyStartDate,FyEndDate,loginUserId;
    AutoCompleteTextView txtbankacname;
    private PSDialogMsg psDialogMsg;
    private Toolbar toolbar;
    private ArrayAdapter<String> farmlistadapter;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Calendar mycal = Calendar.getInstance();
    String dtf = "yyyy-MM-dd";
    SimpleDateFormat dateform = new SimpleDateFormat(dtf, Locale.US);

    String dateformat = "dd-MM-yyyy";
    SimpleDateFormat nDateform = new SimpleDateFormat(dateformat, Locale.US);
    String dtformat = "dd-MM-yy";
    SimpleDateFormat nDtform = new SimpleDateFormat(dtformat, Locale.US);

    Date fsDt, feDt;
    String FySDt, FyEDt;
    double netDrBal =0;
    double netCrBal =0;
    double rtotal =0;
    String postId, vochid;
    ArrayList<HashMap<String, String>> voucherlist;
    private ArrayList<StatementModel> VoucherList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StatementAdapter mAdapter;
    private final int jsoncode = 1;
    public static final String JSON_ARRAY = "result";
    HashMap<String, String> Aclist;
    public static final String PREFS="PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_accountstatementone);

        psDialogMsg = new PSDialogMsg(this, false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initToolbar();
        voucherlist = new ArrayList<HashMap<String, String>>();
        txtgrid = (TextView) findViewById(R.id.txtgrid);
        txtsdate = (TextView) findViewById(R.id.txtsdate);
        txtedate = (TextView) findViewById(R.id.txtedate);
        txtstdate = findViewById(R.id.txtstdate);
        txtetdate = findViewById(R.id.txtetdate);
        txtbankacname = findViewById(R.id.txtbankname);
        txtlbldrtot = findViewById(R.id.lbldrtotal);
        txtlblcrtot = findViewById(R.id.lblcrtotal);
        txtlblnettot = findViewById(R.id.lblnettotal);

        Long currentdate = System.currentTimeMillis();
        String datestring = nDateform.format(currentdate);
        txtsdate.setText(datestring);
        txtedate.setText(datestring);

        String newdate = dateform.format(currentdate);
        txtstdate.setText(newdate);
        txtetdate.setText(newdate);

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
        System.out.print("Start Date"+ FyStartDate);


        txtstdate.setText(FyStartDate);
        txtetdate.setText(FyEndDate);
        Log.i("NewFDate ", txtstdate.getText().toString());
        Log.i("NewEDate", txtetdate.getText().toString());

        try {
            fsDt = dateform.parse(FyStartDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            feDt =dateform.parse(FyEndDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FySDt = nDateform.format(fsDt);
        FyEDt = nDateform.format(feDt);

        Log.i("FDate ", FySDt.toString());
        Log.i("EDate", FyEDt.toString());

        txtsdate.setText(FySDt.toString());
        txtedate.setText(FyEDt.toString());

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR, year);
                mycal.set(Calendar.MONTH, monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH, dateofmonth);
                updateDate();
            }
        };
        date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mycal.set(Calendar.YEAR, year);
                mycal.set(Calendar.MONTH, month);
                mycal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate1();
            }
        };

        txtsdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(rpt_accountstatementone.this, date, mycal.get(Calendar.YEAR), mycal.get(Calendar.MONTH), mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txtedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(rpt_accountstatementone.this, date1, mycal.get(Calendar.YEAR), mycal.get(Calendar.MONTH), mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        getAccountListAll();

        txtbankacname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //  mdatabase = dbHelper.getWritableDatabase();

             // getAccountLedgerDetail();
                String val = txtbankacname.getText()+ "";
                String code = Aclist.get(val);
                Log.v("rpt_acstate", "Selected Gl Code: " + code);
                if (code != null){
                    txtgrid.setText(code);
                }

            }
        });


        txtgrid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
              NumberFormat nf = NumberFormat.getInstance();
                getAccountStatementDetail();
            }
        });
    }
    private void initToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Account Statement");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    private void updateDate(){
        txtsdate.setText(nDateform.format(mycal.getTime()));
        txtstdate.setText(dateform.format(mycal.getTime()));
    }
    private void updateDate1(){
        txtedate.setText(nDateform.format(mycal.getTime()));
        txtetdate.setText(dateform.format(mycal.getTime()));
    }

    private void getAccountListAll(){
        final List<String> flist = new ArrayList<String>();

        Aclist = new HashMap<String, String>();
        Tools.showSimpleProgressDialog(rpt_accountstatementone.this, "Getting Ledger...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETALLACCOUNTLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String cData[]= null;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONArray json = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject obj = json.getJSONObject(i);

                                    Aclist.put(obj.getString("sledgername"), obj.getString("glcode"));

                                    cData = Aclist.keySet().toArray(new String[0]);
                                }

                                farmlistadapter = new ArrayAdapter<String>(rpt_accountstatementone.this, android.R.layout.simple_dropdown_item_1line, cData);
                                //txtprodname = (TextView) findViewById(R.id.prodname);
                                //txtbankacname.setThreshold(3);
                                txtbankacname.setAdapter(farmlistadapter);
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
        RequestQueue queue = Volley.newRequestQueue(rpt_accountstatementone.this);
        queue.add(stringRequest);
    }

    private void getAccountStatementDetail(){
        NumberFormat nf = NumberFormat.getInstance();

        VoucherList.clear();
        netDrBal =0;
        netCrBal =0;
        Tools.showSimpleProgressDialog(rpt_accountstatementone.this, "Generating Statement...","Please Wait ...",false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETACCOUNTSTATEMENT,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String dc="Dr";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONArray json1 = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json1.length(); i++) {
                                    JSONObject obj = json1.getJSONObject(i);

                                    String damt = obj.getString("debitamt");
                                    String camt = obj.getString("creditamt");
                                    String clamt = obj.getString("clbal");
                                    String vdt= obj.getString("vdate");
                                    Date trdt = dateform.parse(vdt);
                                    double dam = Double.parseDouble(damt);
                                    double cam =Double.parseDouble(camt);
                                    rtotal= Double.parseDouble(clamt);
                                    netDrBal = (netDrBal)+ (dam);
                                    netCrBal = (netCrBal)+ (cam);
                                    StatementModel statementModel = new StatementModel(nDtform.format(trdt),obj.getString("vochno"),
                                            obj.getString("perticulars"),nf.format(dam),nf.format(Math.abs(cam)), nf.format(Math.abs(rtotal)),
                                            obj.getString("invmode"), obj.getString("challanno"));
                                    VoucherList.add(statementModel);
                                    dam =0;
                                    cam =0;
                                }
                                if (rtotal >= 0) {
                                    dc = "Dr";
                                } else {
                                    dc = "Cr";
                                }
                                txtlbldrtot.setText(nf.format(netDrBal));
                                txtlblcrtot.setText(nf.format(Math.abs(netCrBal)));
                                txtlblnettot.setText(nf.format(Math.abs(rtotal)) + " "+ dc);
                                recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                                mAdapter = new StatementAdapter(rpt_accountstatementone.this, VoucherList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rpt_accountstatementone.this);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();

                                mAdapter.setOnItemClickListener(new StatementAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        StatementModel statementModel = VoucherList.get(position);
                                        postId = statementModel.getInvoicetype();
                                        vochid = statementModel.getInvoiceno();
                                        String vt = vochid.substring(0,2);
                                        Log.i("Vouchertype", vt.toString());
                                        if (vt.equals("PR")){
                                            sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                                            editor = sp.edit();
                                            editor.putString("vochtype", "Purchase");
                                            editor.putString("invoiceno", vochid);
                                            editor.commit();
                                            //Intent i = new Intent(rpt_accountstatementone.this, PurchaseActivityModify.class);
                                            //startActivity(i);
                                        }
                                        Toast.makeText(getApplicationContext(), statementModel.getInvoiceno() + " is selected!", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                        } catch (JSONException | ParseException e) {
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
                params.put("glcode", txtgrid.getText().toString());
                params.put("sdate",txtstdate.getText().toString());
                params.put("edate", txtetdate.getText().toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(rpt_accountstatementone.this);
        queue.add(stringRequest);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbarmenu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_favorite:
               // ExportSaleRegisterToExcel();
                return true;
            case R.id.action_print:
                String sdate = txtstdate.getText().toString();
                String edate = txtetdate.getText().toString();

                sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                editor = sp.edit();
                editor.putString("grid", txtgrid.getText().toString());
                editor.commit();
                Intent i = new Intent(rpt_accountstatementone.this, rpt_accountstatement.class);
                startActivity(i);
                //Intent printsale = new Intent(rpt_salesregister.this, PrintSaleRegister.class);
                //startActivity(printsale);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}