package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
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
import android.view.ViewGroup;
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

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


public class rpt_cashbook extends AppCompatActivity {

    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener date1;
    TextView txtsdate, txtedate, txtstdate, txtetdate,txtlblnettot;
    Calendar mycal = Calendar.getInstance();
    String dtf = "yyyy-MM-dd";
    SimpleDateFormat dateform = new SimpleDateFormat(dtf, Locale.US);
    String dateformat = "dd-MM-yyyy";
    SimpleDateFormat nDateform = new SimpleDateFormat(dateformat, Locale.US);
    private Toolbar toolbar;
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
        setContentView(R.layout.activity_rpt_cashbook);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        //  itemno = sharedPreferences.getString("grid", null);
        // itemname = sharedPreferences.getString("prodname", null);

        salesregisterlist = new ArrayList<HashMap<String, String>>();

        initToolbar();

        itemlist = new ArrayList<HashMap<String, String>>();
        salereglist = findViewById(R.id.recycler_view);

        txtsdate = (TextView) findViewById(R.id.txtsdate);
        txtstdate = findViewById(R.id.txtstdate);

        txtlblnettot = findViewById(R.id.lblnettotal);

        Long currentdate = System.currentTimeMillis();
        String datestring = nDateform.format(currentdate);
        txtsdate.setText(datestring);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR, year);
                mycal.set(Calendar.MONTH, monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH, dateofmonth);
                updateDate();
            }
        };


        txtsdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(rpt_cashbook.this, date, mycal.get(Calendar.YEAR), mycal.get(Calendar.MONTH), mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbarmenu, menu);
        return true;
    }

    private void initToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cash Book");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    private void updateDate(){
        txtsdate.setText(nDateform.format(mycal.getTime()));
        txtstdate.setText(dateform.format(mycal.getTime()));
        getCashBookRegister();
    }

    private void getCashBookRegister(){
        itemlist.clear();
        salesregisterlist.clear();
        Tools.showSimpleProgressDialog(getApplicationContext(), "Generating Cash Book...","Please Wait ...",false);
        NumberFormat nf = NumberFormat.getInstance();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_RPTCASHBOOKVOUCHERADMIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            double netdrbal =0;
                            double netcrbal =0;
                            double vchamt =0;
                            double clbal = 0;
                            String drcr ="";
                            String vtype = "";
                            int textColorId;
                            double nettot=0;
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();

                                JSONArray json = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject obj = json.getJSONObject(i);

                                    String sdt = obj.getString("vdate");
                                    String pamt = obj.getString("amount");
                                    drcr = obj.getString("drcr");
                                    vtype = obj.getString("invmode");
                                    if (pamt != null && !pamt.isEmpty()){
                                        vchamt = Double.parseDouble(pamt);
                                    }
                                    if (drcr.equals("DR")){
                                        netdrbal = (netdrbal)+(vchamt);
                                    }
                                    if (drcr.equals("CR")){
                                        netcrbal = netcrbal+(vchamt);
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
                                    parties.put("C", obj.getString("details"));
                                    parties.put("D", nf.format(vchamt)+ " "+ drcr);
                                    parties.put("E", vtype);
                                    parties.put("F", obj.getString("ledgername"));
                                    nettot = (nettot)+ (vchamt);
                                    salesregisterlist.add(parties);

                                    System.out.println(itemlist.toString());
                                }
                                adapter = new SimpleAdapter(rpt_cashbook.this, salesregisterlist, R.layout.cashbooklist,
                                        new String[]{"A", "B", "C","D","E","F"},
                                        new int[]{R.id.txtvochno, R.id.txtvdate, R.id.txtladgername, R.id.txtamount,R.id.txtvochtype,R.id.txtlname})
                                {
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent){
                                        View view = super.getView(position, convertView, parent);
                                        String dcr = salesregisterlist.get(position).get("E");
                                        TextView text;
                                        text = (TextView) view.findViewById(R.id.txtamount);
                                        int color = R.color.md_teal_600;
                                        int color1 = R.color.md_red_700;
                                        assert dcr != null;
                                        if (dcr.equals("SAL")) {
                                            text.setTextColor(getResources().getColor(color));
                                        } else if (dcr.equals("REC")){
                                            text.setTextColor(getResources().getColor(color));
                                        } else if (dcr.equals("PAY")){
                                            text.setTextColor(getResources().getColor(color1));
                                        }
                                        return view;
                                    }

                                };
                                salereglist.setAdapter(null);
                                salereglist.setAdapter(adapter);

                                txtlblnettot.setText(String.valueOf(nf.format(netdrbal-netcrbal)));
                                salereglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String vochno = ((TextView)view.findViewById(R.id.txtvochno)).getText().toString();

                                        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                                        editor = sp.edit();
                                        editor.putString("invoiceno", vochno);
                                        editor.commit();
                                        //Intent i = new Intent(rpt_salesregister.this, SaleActivityModity.class);
                                        //startActivity(i);

                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(rpt_cashbook.this,"No Data Found",Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("tdate", txtstdate.getText().toString());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(rpt_cashbook.this);
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

}