package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SupplyClearance extends AppCompatActivity {
    DatePickerDialog.OnDateSetListener date;
    TextView txtspdate, txtspvdate, txtfarmno, txtlastbatchno, txtnewbatchno, txtspname;
    AutoCompleteTextView txtsuppliername, txtfarmname;
    Button btnaddnewfarm, btncanclespcl, btnsavespcl;

    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7;
    DatabaseHelper dbHelper = null;
    SQLiteDatabase mdatabase;
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
    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supply_clearance);

        txtspvdate = findViewById(R.id.txtnvdate);
        txtspdate = findViewById(R.id.txtvdate);
        txtfarmname = findViewById(R.id.txtfarmname);
        txtspname = findViewById(R.id.lblspname);

        txtfarmno = findViewById(R.id.lblfarmno);
        txtlastbatchno = findViewById(R.id.txtlastbatch);
        txtnewbatchno = findViewById(R.id.txtnewbatch);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);
        checkBox6 = findViewById(R.id.checkBox6);

        btncanclespcl = findViewById(R.id.btncanclespcl);
        btnsavespcl = findViewById(R.id.btnsavespcl);

        dbHelper = new DatabaseHelper(this);
        psDialogMsg = new PSDialogMsg(this, false);

        toolbar = findViewById(R.id.toolbarpd);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initToolbar();
        mdatabase = dbHelper.getWritableDatabase();

        Long currentdate = System.currentTimeMillis();
        String datestring = nDateform.format(currentdate);
        txtspdate.setText(datestring);

        String newdate = dateform.format(currentdate);
        txtspvdate.setText(newdate);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR, year);
                mycal.set(Calendar.MONTH, monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH, dateofmonth);
                updateDate();
            }
        };

        txtspdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SupplyClearance.this, date, mycal.get(Calendar.YEAR), mycal.get(Calendar.MONTH), mycal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        getFarmList();

        btncanclespcl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnsavespcl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSupplyClearance();
            }
        });

    }


    private void updateDate() {
        txtspdate.setText(nDateform.format(mycal.getTime()));
        txtspvdate.setText(dateform.format(mycal.getTime()));

    }


    private void initToolbar() {

        getSupportActionBar().setTitle("Supply Clearance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }


    private void getFarmList() {

        showSimpleProgressDialog(getApplicationContext(), "Nilkamal...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_GETALLFARMLIST);
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
                    onFarmListTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();


    }

    public void onFarmListTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        double nettot=0;
        NumberFormat nf = NumberFormat.getInstance();
        final List<String> flist = new ArrayList<String>();
        switch (serviceCode) {
            case jsoncode:
                if (isSuccess(response)) {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();

                        JSONArray json = jsonObject.getJSONArray("farmerlist");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            flist.add(obj.getString("farmname"));
                        }
                    }
                    else {
                        Toast.makeText(SupplyClearance.this, "There Is No Farm For Selection, Please Add New Farm First...", Toast.LENGTH_SHORT).show();
                    }
                    //Log.i("TotalCount", String.valueOf(i));
                    Log.i("Print PList", flist.toString());
                    // FrameLayout footerLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.footerview,null);
                    // btnaddpart = (Button) footerLayout.findViewById(R.id.btnaddpart);

                    ArrayAdapter<String> inewitemadapter;
                    farmlistadapter = new ArrayAdapter<String>(SupplyClearance.this, android.R.layout.select_dialog_item, flist);
                    //txtprodname = (TextView) findViewById(R.id.prodname);
                    txtfarmname.setAdapter(farmlistadapter);

                    txtfarmname.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String item_position = String.valueOf(i);
                            int positonInt = Integer.valueOf(item_position)+1;
                            getFarmDetails();

                        }
                    });

                }else {
                    Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getFarmDetails(){

        showSimpleProgressDialog(getApplicationContext(), "Nilkamal...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("sledgername", txtfarmname.getText().toString());
                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_GETFARMDETAILSUPLCLR);
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
                    onFarmDetailCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }

    public void onFarmDetailCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());

        switch (serviceCode) {
            case jsoncode:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("Result").equals("success")) {
                        removeSimpleProgressDialog();
                        JSONArray json = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            //Ventryno = obj.getString("vochno");
                            int lastbatch = obj.getInt("oldbatch");
                            txtfarmno.setText(obj.getString("glcode"));
                            txtlastbatchno.setText(obj.getString("oldbatch"));
                            txtspname.setText(obj.getString("spname"));
                            String isopenfarm = obj.getString("isopen");
                            if (isopenfarm.equals("Yes")){
                                psDialogMsg.showInfoDialog("This Farm Is Already Open, Please Close First And Then Give Supply Clearance", "Ok");
                                btnsavespcl.setEnabled(false);

                            } else if (isopenfarm.equals("No")){
                               btnsavespcl.setEnabled(true);
                               int newbatch = lastbatch+1;
                               txtnewbatchno.setText(newbatch);
                            }
                        }

                    } else {
                        removeSimpleProgressDialog();
                        Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void SaveSupplyClearance() {

        Boolean CheckAll = false;
        String OpenNewBatch = "No";
        String check1 ="No";
        if (checkBox1.isChecked()){
            check1 = "Yes";
            CheckAll = true;
        }
        String check2 = "No";
        if (checkBox2.isChecked()){
            check2 = "Yes";
        }
        String check3 = "No";
        if (checkBox3.isChecked()){
            check3 = "Yes";
        }
        String check4 = "No";
        if (checkBox4.isChecked()){
            check4 = "Yes";
        }
        String check5 = "No";
        if (checkBox5.isChecked()){
            check5 = "Yes";
        }
        String check6 = "No";
        if (checkBox6.isChecked()){
            check6 = "Yes";
        }

        if (check1.toString() != "Yes" && check2.toString() != "Yes" && check3.toString() != "Yes" && check4.toString() != "Yes" && check5.toString() != "Yes" && check6.toString() != "Yes" ) {
            OpenNewBatch = "No";
            Toast.makeText(SupplyClearance.this, "Please Check All Options First...", Toast.LENGTH_SHORT).show();

        } else if (check1.toString()== "Yes" && check2.toString() == "Yes" && check3.toString()== "Yes" && check4.toString()== "Yes" && check5.toString() == "Yes" && check6.toString() == "Yes" && !txtfarmname.getText().toString().isEmpty()){

            OpenNewBatch = "Yes";
            String Ventryno="";
            String vochno= "";

            mdatabase = dbHelper.getWritableDatabase();


            String sqlv = "Select vochno from supplyclr_tbl group by vochno order by vochno DESC Limit 1";
            Cursor crv = mdatabase.rawQuery(sqlv, null);
            int i = 0;
            if (crv.getCount() > 0) {
                while (crv.moveToNext()) {
                    Ventryno = crv.getString(0);
                }
            } else {
                Ventryno = "1";
            }
            crv.close();
            Integer nVochno= 0;
            if (!Ventryno.isEmpty() && Ventryno!= null) {
                System.out.println(Ventryno);
                if (Ventryno.length() > 1) {
                    String sbstr = Ventryno.substring(Ventryno.length() - 5);
                    String txtvn = sbstr;
                    Integer newVno = Integer.parseInt(txtvn.toString());
                    nVochno = newVno + 1;
                } else {
                    nVochno = 1;
                }
                if ((nVochno > 0) && (nVochno < 10)) {

                    vochno = "SP0000" + String.valueOf(nVochno);
                } else if ((nVochno > 10) && (nVochno < 100)) {

                    vochno = "SP000" + String.valueOf(nVochno);
                } else if ((nVochno >= 100) && (nVochno < 1000)) {

                    vochno = "SP00" + String.valueOf(nVochno);
                } else if ((nVochno >= 1000) && (nVochno < 10000)) {

                    vochno = "SP0" + String.valueOf(nVochno);
                } else if ((nVochno >= 10000) && (nVochno < 100000)) {

                    vochno = "SP" + String.valueOf(nVochno);
                }
            }
            //invoiceno.setText(vochno.toString());
            System.out.println(vochno);

            mdatabase.execSQL("CREATE TABLE IF NOT EXISTS supplyclr_tbl " +
                    "(spsrno INTEGER PRIMARY KEY AUTOINCREMENT, vochno text,vdate Date, farmno text, farmname text,farmaddress text,lastbatch text, newbatch text,farmcap text,optone text, opttwo text, optthree text, optfour text, optfive text, optsix text,openfornewbatch text,openclose text, addedby text)");


            ContentValues contentValues = new ContentValues();
            contentValues.put("vochno", vochno.toString());
            contentValues.put("vdate", txtspvdate.getText().toString());
            contentValues.put("farmno", txtfarmno.getText().toString());
            contentValues.put("farmname", txtfarmname.getText().toString());
            contentValues.put("lastbatch", txtlastbatchno.getText().toString());
            contentValues.put("newbatch", txtnewbatchno.getText().toString());
            contentValues.put("farmcap", farmbirdcap.toString());
            contentValues.put("optone", check1.toString());
            contentValues.put("opttwo", check2.toString());
            contentValues.put("optthree", check3.toString());
            contentValues.put("optfour", check4.toString());
            contentValues.put("optfive", check5.toString());
            contentValues.put("optsix", check6.toString());
            contentValues.put("openfornewbatch", OpenNewBatch.toString());
            contentValues.put("openclose", "O");


            Long t = mdatabase.insert("supplyclr_tbl", null, contentValues);
            if (t > 0) {
                psDialogMsg.showSuccessDialog(getString(R.string.supplyclearancecreated), getString(R.string.OkDialogTitle));
                psDialogMsg.show();
                txtfarmname.setText("");
                checkBox1.setChecked(false);
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);
                checkBox4.setChecked(false);
                checkBox5.setChecked(false);
                checkBox6.setChecked(false);

            } else {
                psDialogMsg.showErrorDialog(getString(R.string.somethingwentwrong), getString(R.string.OkDialogTitle));
                psDialogMsg.show();
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