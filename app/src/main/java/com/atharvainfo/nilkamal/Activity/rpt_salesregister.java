package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class rpt_salesregister extends AppCompatActivity {

    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener date1;
    TextView txtsdate, txtedate, txtstdate, txtetdate,txtlblnettot;
    Calendar mycal = Calendar.getInstance();
    String dtf = "yyyy-MM-dd";
    SimpleDateFormat dateform = new SimpleDateFormat(dtf, Locale.US);

    String dateformat = "dd-MM-yyyy";
    SimpleDateFormat nDateform = new SimpleDateFormat(dateformat, Locale.US);
    Date fsDt, feDt;
    String FySDt, FyEDt;
    private PSDialogMsg psDialogMsg;
    private Toolbar toolbar;
    private ActionBar actionBar;
    ArrayList<HashMap<String, String>> itemlist;
    private ListView salereglist;
    private SimpleAdapter adapter;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri,FyStartDate,FyEndDate,loginUserId;
    Date vt;
    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, String>> salesregisterlist;
    private SimpleAdapter salestranadapter;
    public static final String PREFS="PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_salesregister);

        psDialogMsg = new PSDialogMsg(this, false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        sp=getBaseContext().getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
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
        //  itemno = sharedPreferences.getString("grid", null);
        // itemname = sharedPreferences.getString("prodname", null);

        salesregisterlist = new ArrayList<HashMap<String, String>>();

        initToolbar();
        itemlist = new ArrayList<HashMap<String, String>>();
        salereglist = findViewById(R.id.recycler_view);

        txtsdate = (TextView) findViewById(R.id.txtsdate);
        txtedate = (TextView) findViewById(R.id.txtedate);
        txtstdate = findViewById(R.id.txtstdate);
        txtetdate = findViewById(R.id.txtetdate);

        txtlblnettot = findViewById(R.id.lblnettotal);

        Long currentdate = System.currentTimeMillis();
        String datestring = nDateform.format(currentdate);
        txtsdate.setText(datestring);
        txtedate.setText(datestring);

        String newdate = dateform.format(currentdate);
        txtstdate.setText(newdate);
        txtetdate.setText(newdate);

        txtstdate.setText(FyStartDate);
        txtetdate.setText(FyEndDate);

        Log.i("NewFDate ", txtstdate.getText().toString());
        Log.i("NewEDate", txtetdate.getText().toString());

        try {
            fsDt = dateform.parse(FyStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            feDt =dateform.parse(FyEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FySDt = nDateform.format(fsDt);
        FyEDt = nDateform.format(feDt);

        Log.i("FDate ", FySDt);
        Log.i("EDate", FyEDt);

        txtsdate.setText(FySDt);
        txtedate.setText(FyEDt);

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
                new DatePickerDialog(rpt_salesregister.this, date, mycal.get(Calendar.YEAR), mycal.get(Calendar.MONTH), mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txtedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(rpt_salesregister.this, date1, mycal.get(Calendar.YEAR), mycal.get(Calendar.MONTH), mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        getSalesRegister();

    }
    private void initToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sale Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    private void updateDate(){
        txtsdate.setText(nDateform.format(mycal.getTime()));
        txtstdate.setText(dateform.format(mycal.getTime()));
        getSalesRegister();
    }
    private void updateDate1(){
        txtedate.setText(nDateform.format(mycal.getTime()));
        txtetdate.setText(dateform.format(mycal.getTime()));
        getSalesRegister();
    }

    private void getSalesRegister(){
        itemlist.clear();
        Tools.showSimpleProgressDialog(getApplicationContext(), "Getting Sales Register...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETSALESREGISTERLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try
                        {
                            double vchamt =0;
                            double nettot= 0;
                            NumberFormat nf = NumberFormat.getInstance();
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();

                                JSONArray json = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject obj = json.getJSONObject(i);

                                    String sdt = obj.getString("vdate");
                                    String pamt = obj.getString("netamount");
                                    if (!pamt.isEmpty()){
                                        vchamt = Double.parseDouble(pamt);
                                    }

                                    try {
                                        vt = dateform.parse(sdt);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String opdate = nDateform.format(vt);

                                    HashMap<String, String> parties = new HashMap<>();
                                    parties.put("A", obj.getString("vochno"));
                                    parties.put("B", opdate.toString());
                                    parties.put("C", obj.getString("ledgername"));
                                    parties.put("D", nf.format(vchamt));
                                    //parties.put("E", cr.getString(4));
                                    nettot = (nettot)+ (vchamt);
                                    salesregisterlist.add(parties);
                                }
                                adapter = new SimpleAdapter(rpt_salesregister.this, salesregisterlist, R.layout.saleregisteritemlist, new String[]{"A", "B", "C","D"}, new int[]{R.id.txtvochno, R.id.txtvdate, R.id.txtladgername, R.id.txtamount});
                                salereglist.setAdapter(null);
                                salereglist.setAdapter(adapter);

                                txtlblnettot.setText(String.valueOf(nf.format(nettot)));
                                //salestranadapter = new SimpleAdapter(Activity_webview.this, salesprodlist, R.layout.sale_item_list, new String[]{"A", "B","C","D","E","F","G","H","I"}, new int[]{R.id.lblitemname, R.id.lblitemamount, R.id.lblitemqty,R.id.lblitemrate,R.id.lblitemdiscountamt,R.id.lblitemsubtotalamt,R.id.lblitemno,R.id.lblitemcat,R.id.lblitemmfg});
                                //itemlist.setAdapter(prodadapter);
                                salereglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        // String vochtype = ((TextView)view.findViewById(R.id.txtinvmode)).getText().toString();
                                        String vochno = ((TextView)view.findViewById(R.id.txtvochno)).getText().toString();

                                        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                                        editor = sp.edit();
                                        //editor.putString("vochtype", vochtype);
                                        editor.putString("invoiceno", vochno);
                                        editor.commit();
                                        //Intent i = new Intent(rpt_salesregister.this, SaleActivityModity.class);
                                        //startActivity(i);

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(rpt_salesregister.this,"No Data Found",Toast.LENGTH_LONG).show();
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
                params.put("sdate", txtstdate.getText().toString());
                params.put("edate", txtetdate.getText().toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(rpt_salesregister.this);
        queue.add(stringRequest);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_favorite:
                ExportSaleRegisterToExcel();
                return true;
            case R.id.action_print:
                String sdate = txtstdate.getText().toString();
                String edate = txtetdate.getText().toString();

                sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                editor = sp.edit();
                editor.putString("rptfirstdate", sdate);
                editor.putString("rptseconddate", edate);
                editor.commit();
                //Intent printsale = new Intent(rpt_salesregister.this, PrintSaleRegister.class);
                //startActivity(printsale);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ExportSaleRegisterToExcel(){
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "salesregister.xls";

        File directory = new File(sd.getAbsolutePath());
//create directory if not exist
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        try {

//file path
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
//Excel sheet name. 0 represents first sheet
            WritableSheet sheet = workbook.createSheet("salesregister", 0);
// column and row
            sheet.addCell(new Label(0, 0, CompanyName));
            sheet.addCell(new Label(0, 1, CompanyAddress));
            sheet.addCell(new Label(0, 2, "Sales Register"));
            sheet.addCell(new Label(0, 3, "From Date :" + txtstdate.getText().toString() + " To Date : " + txtedate.getText().toString()));

            sheet.addCell(new Label(0, 5, "Date"));
            sheet.addCell(new Label(1, 5, "Voucher No."));
            sheet.addCell(new Label(2, 5, "Ledger Name"));
            sheet.addCell(new Label(3, 5, "Net Amount"));

            for (int i = 0; i < salesregisterlist.size(); i++){
                int p = i + 6;
                HashMap<String, String> hashmap= salesregisterlist.get(i);
                String vdt= hashmap.get("B");
                String vchno = hashmap.get("A");
                String lname = hashmap.get("C");
                String namt = hashmap.get("D");


                assert vdt != null;
                sheet.addCell(new Label(0, p, vdt));
                assert vchno != null;
                sheet.addCell(new Label(1, p, vchno));
                assert lname != null;
                sheet.addCell(new Label(2, p, lname));
                assert namt != null;
                sheet.addCell(new Label(3, p, namt));

            }

           workbook.write();
            workbook.close();
            Toast.makeText(getApplication(),
                    "Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

}