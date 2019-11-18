package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ActivityPayment extends AppCompatActivity {
    private String ledgername,ledgeraddress,ledgercontact,ledgeremail,ledgergst,vdate,invoiceamount,ledgeracname, branchid,username;

    DatePickerDialog.OnDateSetListener date;
    TextView txtpartyname, txtpcode, txtpartyadd, txtpartycontact, txtpbal,txtreceiptno,txtvdate,txtrvdate, txtbgrid;
    EditText txtrecamount, txtnarration;
    Spinner sppaytype;
    AutoCompleteTextView txtbankacname, txtfarmname;
    Button btnselectac, btncanclerec, btnsaverec, btnaddbankac;
    CardView card_view4;
    private PSDialogMsg psDialogMsg;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ArrayAdapter<String> farmlistadapter;

    Calendar mycal = Calendar.getInstance();
    String dtf = "yyyy-MM-dd";
    SimpleDateFormat dateform = new SimpleDateFormat(dtf, Locale.US);

    String dateformat = "dd-MM-yyyy";
    SimpleDateFormat nDateform = new SimpleDateFormat(dateformat, Locale.US);
    String farmbirdcap = "0";
    String[] data = {"Cash", "Bank","Bearar Cheque"};
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri;

    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        psDialogMsg = new PSDialogMsg(this, false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initToolbar();

        txtreceiptno = findViewById(R.id.txtreceiptno);
        txtvdate = findViewById(R.id.txtvdate);
        txtrvdate = findViewById(R.id.txtrvdate);
        txtpartyname = findViewById(R.id.txtsupname);
        txtpartyadd = findViewById(R.id.txtsupadd);
        txtpartycontact = findViewById(R.id.txtspcontact);
        txtpbal = findViewById(R.id.txtpbal);
        txtpcode = findViewById(R.id.txtgrid);
        txtrecamount = findViewById(R.id.txtreceivedamt);
        sppaytype = findViewById(R.id.sppaytype);
        txtnarration = findViewById(R.id.txtnarration);

        card_view4 = findViewById(R.id.card_view4);
        btnselectac = findViewById(R.id.btnadd);
        btncanclerec = findViewById(R.id.btncanclerec);
        btnsaverec = findViewById(R.id.btnsaverec);

        sharedPreferences=getApplicationContext().getSharedPreferences("Mydata",MODE_PRIVATE);
        CompanyName = sharedPreferences.getString("company_name",null);
        CompanyAddress = sharedPreferences.getString("company_addres", null);
        CompanyCity = sharedPreferences.getString("company_city",null);
        CompanyTal = sharedPreferences.getString("company_tal", null);
        CompanyDist = sharedPreferences.getString("company_dist", null);
        CompanyMobile = sharedPreferences.getString("company_mobile", null);
        CompanyGST = sharedPreferences.getString("company_gst", null);
        CompanyJuri = sharedPreferences.getString("company_jurisdiction", null);
        CompanyEmail = sharedPreferences.getString("company_email", null);
        username = sharedPreferences.getString("UserName", null);

        //getVoucherNoNew();

        Long currentdate = System.currentTimeMillis();
        String datestring = nDateform.format(currentdate);
        txtvdate.setText(datestring);

        String newdate = dateform.format(currentdate);
        txtrvdate.setText(newdate);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR, year);
                mycal.set(Calendar.MONTH, monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH, dateofmonth);
                updateDate();
            }
        };

        txtvdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ActivityPayment.this, date, mycal.get(Calendar.YEAR), mycal.get(Calendar.MONTH), mycal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        btnselectac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent partylist = new Intent(ActivityPayment.this, AllAccountList.class);
                startActivityForResult(partylist,1);
            }
        });



        btnsaverec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveReceiptVoucher();
            }
        });

        btncanclerec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getBranchDataList();


    }


    private void updateDate(){

        txtvdate.setText(nDateform.format(mycal.getTime()));
        txtrvdate.setText(dateform.format(mycal.getTime()));

    }

    private void initToolbar() {

        Objects.requireNonNull(getSupportActionBar()).setTitle("Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String ppartyname = data.getStringExtra("PartyName");
                String partybal = data.getStringExtra("PartyBalance");
                String grid = data.getStringExtra("grid");
                txtpartyname.setText(ppartyname.toString());
                txtpbal.setText("Balance :" + partybal.toString());
                txtpcode.setText(grid.toString());
                getAccountDetails();

            }
        }
    }

    private void SaveReceiptVoucher(){

        if (txtpartyname.getText().toString()!= null && !txtpartyname.getText().toString().isEmpty()){
            if( txtrecamount.getText().toString()!= null && !txtrecamount.getText().toString().isEmpty() && !txtrecamount.getText().toString().equals("0")){

            new AsyncTask<Void, Void, String>(){
                protected String doInBackground(Void[] params) {
                    String response="";
                    HashMap<String, String> map=new HashMap<>();
                    map.put("vdate", txtrvdate.getText().toString());
                    map.put("glcode", txtpcode.getText().toString());
                    map.put("ledgername", txtpartyname.getText().toString());
                    map.put("brcode", branchid);
                    map.put("branchname", sppaytype.getSelectedItem().toString());
                    map.put("paddress", txtpartyadd.getText().toString());
                    map.put("contactno", txtpartycontact.getText().toString());
                    map.put("amount", txtrecamount.getText().toString());
                    map.put("details", txtnarration.getText().toString());
                    map.put("vochtype", "Payment");
                    map.put("addedby", username.toString());
                    map.put("actype", ledgeracname.toString());
                    try {
                        HttpRequest req = new HttpRequest(myConfig.URl_SAVECASHPAYMENTENTRY);
                        response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                    } catch (Exception e) {
                        response=e.getMessage();
                    }
                    return response;
                }
                protected void onPostExecute(String result) {
                    //do something with response
                    Log.d("newwwss",result);
                    try {
                        onSaveTaskCompleted(result,jsoncode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute();

            } else {
                psDialogMsg.showErrorDialog(getString(R.string.enteramount), getString(R.string.OkDialogTitle));
                psDialogMsg.show();
            }
        } else {
            psDialogMsg.showErrorDialog(getString(R.string.selectparty), getString(R.string.OkDialogTitle));
            psDialogMsg.show();
        }

    }

    public void onSaveTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();
                        JSONArray json = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            //loginUserId = cursor.getString(cursor.getColumnIndex("username"));
                            // if (obj.getString("amount") != "0"){
                           String PayVochno = obj.getString("payvochno");
                           txtreceiptno.setText(PayVochno);

                            btnsaverec.setEnabled(false);
                            Toast.makeText(ActivityPayment.this, "Payment Voucher Save Successfully...", Toast.LENGTH_SHORT).show();
                            sharedPreferences = getApplicationContext().getSharedPreferences("Mydata", MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.putString("vochtype", "Payment");
                            editor.putString("invoiceno", txtreceiptno.getText().toString());
                            editor.commit();
                            Intent pintent = new Intent(ActivityPayment.this, Activity_webview.class);
                            startActivity(pintent);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAccountDetails(){

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("glcode", txtpcode.getText().toString());

                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_GETACCOUNTLEDGERDETAIL);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response=e.getMessage();
                }
                return response;
            }
            protected void onPostExecute(String result) {
                //do something with response
                Log.d("newwwss",result);
                try {
                    onTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void onTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();
                        JSONArray json = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            //loginUserId = cursor.getString(cursor.getColumnIndex("username"));
                            // if (obj.getString("amount") != "0"){
                            ledgername = obj.getString("sledgername");
                            ledgeraddress = obj.getString("sladdress");
                            ledgercontact = obj.getString("contact");
                            ledgergst = obj.getString("gsttinno");
                            ledgeremail = obj.getString("emailid1");
                            ledgeracname = obj.getString("acname");
                            txtpartyadd.setText(ledgeraddress);
                            txtpartycontact.setText(ledgercontact);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    private void getBranchDataList(){

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                //map.put("username", loginUserId);

                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_GETBRANCHDATALIST);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response=e.getMessage();
                }
                return response;
            }
            protected void onPostExecute(String result) {
                //do something with response
                Log.d("newwwss",result);
                try {
                    onBranchTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    public void onBranchTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        final List<String> blist = new ArrayList<String>();
        switch (serviceCode) {
            case jsoncode:

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();
                        JSONArray json = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            //loginUserId = cursor.getString(cursor.getColumnIndex("username"));
                            // if (obj.getString("amount") != "0"){
                            blist.add(obj.getString("br_name"));
                        }
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ActivityPayment.this, android.R.layout.select_dialog_item, blist);
                    //txtprodname = (TextView) findViewById(R.id.prodname);
                    sppaytype.setAdapter(arrayAdapter);

                    sppaytype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String item_position = String.valueOf(position);
                            int positonInt = Integer.valueOf(item_position)+1;
                            branchid = String.valueOf(positonInt);
                            Toast.makeText(ActivityPayment.this, "value is "+ positonInt, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
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
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}