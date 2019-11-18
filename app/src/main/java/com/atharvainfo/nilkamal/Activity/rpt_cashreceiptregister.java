package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class rpt_cashreceiptregister extends AppCompatActivity {

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
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri,FyStartDate,FyEndDate;
    Date vt;
    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, String>> salesregisterlist;
    private SimpleAdapter salestranadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_cashreceiptregister);

        psDialogMsg = new PSDialogMsg(this, false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences=getBaseContext().getApplicationContext().getSharedPreferences("Mydata",MODE_PRIVATE);
        sharedPreferences.edit();
        CompanyName = sharedPreferences.getString("company_name",null);
        CompanyAddress = sharedPreferences.getString("company_addres", null);
        CompanyCity = sharedPreferences.getString("company_city",null);
        CompanyTal = sharedPreferences.getString("company_tal", null);
        CompanyDist = sharedPreferences.getString("company_dist", null);
        CompanyMobile = sharedPreferences.getString("company_mobile", null);
        CompanyGST = sharedPreferences.getString("company_gst", null);
        CompanyJuri = sharedPreferences.getString("company_jurisdiction", null);
        CompanyEmail = sharedPreferences.getString("company_email", null);
        FyStartDate = sharedPreferences.getString("startdatefy", null);
        FyEndDate = sharedPreferences.getString("enddatefy", null);
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
                new DatePickerDialog(rpt_cashreceiptregister.this, date, mycal.get(Calendar.YEAR), mycal.get(Calendar.MONTH), mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txtedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(rpt_cashreceiptregister.this, date1, mycal.get(Calendar.YEAR), mycal.get(Calendar.MONTH), mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        getPurchaseRegister();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbarmenu, menu);
        return true;
    }

    private void initToolbar() {

        getSupportActionBar().setTitle("Purchase Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    private void updateDate(){

        txtsdate.setText(nDateform.format(mycal.getTime()));
        txtstdate.setText(dateform.format(mycal.getTime()));
        getPurchaseRegister();
    }
    private void updateDate1(){

        txtedate.setText(nDateform.format(mycal.getTime()));
        txtetdate.setText(dateform.format(mycal.getTime()));
        getPurchaseRegister();
    }

    private void getPurchaseRegister(){
        itemlist.clear();
        showSimpleProgressDialog(getApplicationContext(), "Nilkamal...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("sdate", txtstdate.getText().toString());
                map.put("edate", txtetdate.getText().toString());

                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_GETPURCHASEREGISTERLIST);
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
                    onSalesInvoiceTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

        // } catch (Exception e) {
        //     e.printStackTrace();
        //  }
    }

    public void onSalesInvoiceTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        double nettot=0;
        NumberFormat nf = NumberFormat.getInstance();
        switch (serviceCode) {
            case jsoncode:
                if (isSuccess(response)) {
                    double netdrbal =0;
                    double netcrbal =0;
                    double vchamt =0;
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();

                        JSONArray json = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);

                            String sdt = obj.getString("vdate");
                            String pamt = obj.getString("netamount");
                            if (pamt != null && !pamt.isEmpty()){
                                vchamt = Double.valueOf(pamt);
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

                            System.out.println(itemlist.toString());
                        }
                        adapter = new SimpleAdapter(rpt_cashreceiptregister.this, salesregisterlist, R.layout.saleregisteritemlist, new String[]{"A", "B", "C","D"}, new int[]{R.id.txtvochno, R.id.txtvdate, R.id.txtladgername, R.id.txtamount});
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

                                sharedPreferences = getApplicationContext().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                //editor.putString("vochtype", vochtype);
                                editor.putString("invoiceno", vochno);
                                editor.commit();
                                //Intent i = new Intent(rpt_salesregister.this, SaleActivityModity.class);
                                //startActivity(i);

                            }
                        });
                    }
                }else {
                    Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                }
        }
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

                sharedPreferences = getApplicationContext().getSharedPreferences("Mydata", MODE_PRIVATE);
                editor = sharedPreferences.edit();
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
        String csvFile = "purchaseregister.xls";

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
            WritableSheet sheet = workbook.createSheet("purchaseregister", 0);
// column and row
            sheet.addCell(new Label(0, 0, CompanyName));
            sheet.addCell(new Label(0, 1, CompanyAddress));
            sheet.addCell(new Label(0, 2, "Purchase Register"));
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
                sheet.addCell(new Label(0, p, vdt.toString()));
                sheet.addCell(new Label(1, p, vchno.toString()));
                sheet.addCell(new Label(2, p, lname.toString()));
                sheet.addCell(new Label(3, p, namt.toString()));

            }

            workbook.write();
            workbook.close();
            Toast.makeText(getApplication(),
                    "Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();
        } catch(Exception e){
            e.printStackTrace();
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