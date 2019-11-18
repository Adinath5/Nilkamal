package com.atharvainfo.nilkamal.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.NumberWordConverter;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.text.html.simpleparser.HTMLWorker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;


public class Activity_webview extends AppCompatActivity {

    private PSDialogMsg psDialogMsg;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private String vochtype, invoiceno;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    WebView mywebview;
    String dateformat="dd-MM-yyyy";
    SimpleDateFormat nDateform=new SimpleDateFormat(dateformat, Locale.US);
    private String ledgername,ledgeraddress,ledgercontact,ledgeremail,ledgergst,vdate,invoiceamount,narration,supinvno,farmname,prodamount,addedby,challanno,challandate;
    Date vdt;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    ArrayList<HashMap<String, String>> salesprodlist;
    private SimpleAdapter salestranadapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        mywebview = (WebView) findViewById(R.id.webView);

        psDialogMsg = new PSDialogMsg(this, false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getApplicationContext().getSharedPreferences("Mydata", MODE_PRIVATE);
        sharedPreferences.edit();
        vochtype = sharedPreferences.getString("vochtype",null);
        invoiceno = sharedPreferences.getString("invoiceno",null);
        CompanyName = sharedPreferences.getString("company_name",null);
        CompanyAddress = sharedPreferences.getString("company_addres", null);
        CompanyCity = sharedPreferences.getString("company_city",null);
        CompanyTal = sharedPreferences.getString("company_tal", null);
        CompanyDist = sharedPreferences.getString("company_dist", null);
        CompanyMobile = sharedPreferences.getString("company_mobile", null);
        CompanyGST = sharedPreferences.getString("company_gst", null);
        CompanyJuri = sharedPreferences.getString("company_jurisdiction", null);
        CompanyEmail = sharedPreferences.getString("company_email", null);
        initToolbar();
        salesprodlist = new ArrayList<HashMap<String, String>>();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_recents:
                        // createPdf(mywebview);
                        //File file = new File ("/poultrysoft/invoice.pdf");
                        File file =  new File(getFilesDir(), "poulrysoft");

                        String  destPath = "invoice.pdf";// file.getPath() + File.separator + "invoice.pdf";
                        File mfile = new File(destPath);
                        //Uri path;
                        //path = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", mfile );
                        //Uri uriFile = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", file);

                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft");
                        // String pdffile =  uriFile.;//path.getAbsolutePath() + "/invoice.pdf";
//                        Uri uriFile = FileProvider.getUriForFile(getApplicationContext(), path.getPath(), mfile);
                        Uri selectedUri = Uri.parse(path + "/" + "invoice.pdf");
                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                        //Uri uri = Uri.parse(pdffile.getAbsolutePath());
                        File pdf = new File(path, destPath);
                        if (pdf.exists()) {
                            Intent sharingIntent = new Intent();
                            sharingIntent.setAction(Intent.ACTION_SEND);
                            sharingIntent.setType(mimeType);
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, selectedUri);
                            startActivity(Intent.createChooser(sharingIntent, "Share using"));
                            // startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/pdf"));
                            Toast.makeText(Activity_webview.this, "Share", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.action_favorites:
                        createWebPrintJob(mywebview);

                        Toast.makeText(Activity_webview.this, "Print", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_nearby:
                        Toast.makeText(Activity_webview.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;                }
                return true;
            }
        });
        if (vochtype.equals("Sale")) {
            getSalesInvoiceData();

        }
        if (vochtype.equals("Receipt")){

            getCashReceiptData();
        }

       /* if (vochtype.equals("Purchase")) {

            try {
                CreatePurchaseInvoice(invoiceno);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (vochtype.equals("PurchaseReturn")) {

            CreatePurchaseReturninvoice(invoiceno);
        }
        if (vochtype.equals("BirdSale")) {

            CreateBirdSaleinvoice(invoiceno);
        }
        if (vochtype.equals("BirdPurchase")) {

            CreateBirdSupplyinvoice(invoiceno);
        }
        if (vochtype.equals("FeedSupply")) {

            CreateFeedSupplyinvoice(invoiceno);
        }
        if (vochtype.equals("SaleReturn")) {

            try {
                CreateSaleReturnInvoice(invoiceno);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (vochtype.equals("Receipt")){

            try {
                CreateCashReceiptInvoice(invoiceno);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (vochtype.equals("Payment")) {

            try {
                CreateCashPaymentInvoice(invoiceno);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/


    }

    private void getSalesInvoiceData(){

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("vochno", invoiceno);
                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_SALESINVOICEPRINT);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response=e.getMessage();
                }
                return response;
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
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

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onSalesInvoiceTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:
                if (isSuccess(response)) {
                    double netdrbal =0;
                    double netcrbal =0;
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();

                        JSONArray json = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            ledgername = obj.getString("ledgername");
                            ledgeraddress = obj.getString("paddress");
                            ledgercontact = obj.getString("contactno");
                            ledgergst = obj.getString("gsttin");
                            ledgeremail = obj.getString("emailaddress");
                            vdate = obj.getString("vdate");
                            invoiceamount = obj.getString("netamount");
                            narration = obj.getString("amountinword");
                            vochtype = obj.getString("vochtype");
                            prodamount = obj.getString("prodamount");
                            challanno = obj.getString("challanno");
                            challandate = obj.getString("challandate");
                        }
                        JSONArray json1 = jsonObject.getJSONArray("TranData");
                        for (int i = 0; i < json1.length(); i++) {
                            JSONObject obj = json1.getJSONObject(i);
                            HashMap<String, String> transa = new HashMap<>();
                            transa.put("prodno", obj.getString("prodno"));
                            transa.put("prodname", obj.getString("prodname"));
                            transa.put("wt", obj.getString("wt"));
                            transa.put("qty", obj.getString("qty"));
                            transa.put("kt", obj.getString("kt"));
                            transa.put("fqty", obj.getString("fqty"));
                            transa.put("nqty", obj.getString("nqty"));
                            transa.put("batchno", obj.getString("batchno"));
                            transa.put("expdate", obj.getString("expdate"));
                            transa.put("rateinctax", obj.getString("rateinctax"));
                            transa.put("rateexctax", obj.getString("rateexctax"));
                            transa.put("amountinctax", obj.getString("amountinctax"));
                            transa.put("amountexctax", obj.getString("amountexctax"));
                            transa.put("mfgcompany", obj.getString("mfgcompany"));
                            transa.put("prodtype", obj.getString("prodtype"));
                            transa.put("gname", obj.getString("gname"));
                            transa.put("prodpkg", obj.getString("prodpkg"));
                            transa.put("salunit", obj.getString("salunit"));
                            transa.put("igstamount", obj.getString("igstamount"));
                            transa.put("cgstamount", obj.getString("cgstamount"));
                            transa.put("sgstamount", obj.getString("sgstamount"));
                            transa.put("igstperc", obj.getString("igstperc"));
                            transa.put("cgstperc", obj.getString("cgstperc"));
                            transa.put("sgstperc", obj.getString("sgstperc"));


                            salesprodlist.add(transa);

                        }
                        //salestranadapter = new SimpleAdapter(Activity_webview.this, salesprodlist, R.layout.sale_item_list, new String[]{"A", "B","C","D","E","F","G","H","I"}, new int[]{R.id.lblitemname, R.id.lblitemamount, R.id.lblitemqty,R.id.lblitemrate,R.id.lblitemdiscountamt,R.id.lblitemsubtotalamt,R.id.lblitemno,R.id.lblitemcat,R.id.lblitemmfg});
                        //itemlist.setAdapter(prodadapter);
                        CreateSaleinvoice(invoiceno);
                    }
                }else {
                    Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getCashReceiptData(){

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("vochno", invoiceno);
                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_GETCASHRECEIPTVOUCHERDATA);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response=e.getMessage();
                }
                return response;
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            protected void onPostExecute(String result) {
                //do something with response
                Log.d("newwwss",result);
                try {
                    onReceiptTaskCompleted(result,jsoncode);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onReceiptTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:
                if (isSuccess(response)) {
                    double netdrbal =0;
                    double netcrbal =0;
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();

                        JSONArray json1 = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json1.length(); i++) {
                            JSONObject obj = json1.getJSONObject(i);

                            ledgername = obj.getString("ledgername");
                            ledgeraddress = obj.getString("paddress");
                            ledgercontact = obj.getString("contactno");
                            //ledgergst = obj.getString("bankname");
                            ledgeremail = obj.getString("details");
                            vdate = obj.getString("vdate");
                            invoiceamount = obj.getString("amount");

                        }
                        try {
                            CreateCashReceiptInvoice(invoiceno);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //salestranadapter = new SimpleAdapter(Activity_webview.this, salesprodlist, R.layout.sale_item_list, new String[]{"A", "B","C","D","E","F","G","H","I"}, new int[]{R.id.lblitemname, R.id.lblitemamount, R.id.lblitemqty,R.id.lblitemrate,R.id.lblitemdiscountamt,R.id.lblitemsubtotalamt,R.id.lblitemno,R.id.lblitemcat,R.id.lblitemmfg});
                        //itemlist.setAdapter(prodadapter);
                    }
                }else {
                    Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getCashPaymentData(){

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("vochno", invoiceno);
                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_GETCASHPAYMENTVOUCHERDATA);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response=e.getMessage();
                }
                return response;
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            protected void onPostExecute(String result) {
                //do something with response
                Log.d("newwwss",result);
                try {
                    onPaymentTaskCompleted(result,jsoncode);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onPaymentTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:
                if (isSuccess(response)) {
                    double netdrbal =0;
                    double netcrbal =0;
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();

                        JSONArray json1 = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json1.length(); i++) {
                            JSONObject obj = json1.getJSONObject(i);

                            ledgername = obj.getString("ledgername");
                            ledgeraddress = obj.getString("paddress");
                            ledgercontact = obj.getString("contactno");
                            //ledgergst = obj.getString("bankname");
                            ledgeremail = obj.getString("details");
                            vdate = obj.getString("vdate");
                            invoiceamount = obj.getString("amount");

                        }
                        try {
                            CreateCashPaymentInvoice(invoiceno);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //salestranadapter = new SimpleAdapter(Activity_webview.this, salesprodlist, R.layout.sale_item_list, new String[]{"A", "B","C","D","E","F","G","H","I"}, new int[]{R.id.lblitemname, R.id.lblitemamount, R.id.lblitemqty,R.id.lblitemrate,R.id.lblitemdiscountamt,R.id.lblitemsubtotalamt,R.id.lblitemno,R.id.lblitemcat,R.id.lblitemmfg});
                        //itemlist.setAdapter(prodadapter);
                    }
                }else {
                    Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createWebPrintJob(WebView webView) {

        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter =  null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            printAdapter = webView.createPrintDocumentAdapter("MyDocument");
        }

        String jobName = getString(R.string.app_name) + " Print Test";

        printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }
    private void initToolbar() {

        getSupportActionBar().setTitle(vochtype + " "+ invoiceno);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CreateSaleinvoice(String vochno){

        mywebview.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                // Log.i(TAG, "page finished loading " + url);

            }
        });
        NumberFormat nf = NumberFormat.getInstance();

        try {
            vdt = dt.parse(vdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String opdate = nDateform.format(vdt);
        // Generate an HTML document on the fly:
        String htmlDocument ="";


//        htmlDocument= htmlDocument + "<html>";
        //'htmlDocument= htmlDocument+"<tr><td></td>";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(htmlDocument);
        stringBuilder.append("<!DOCTYPE html>");
        stringBuilder.append("<html>");
        stringBuilder.append("<head><title>Title</title></head>");
        //stringBuilder.append("</head");
        stringBuilder.append("<body>");
        stringBuilder.append("<div class='page';style='width:100%; line-height:normal;'>");
        stringBuilder.append("<table width='100%' cellspacing='0' cellpadding='2' rules='All' style='border: 1px solid;font-size: 9pt;font-family:Arial;border-collapse:collapse;'>");
        stringBuilder.append("<tr><td>");
        stringBuilder.append("<table style='width:100%; border-bottom-style: solid; border-bottom-width: 1px; border-bottom-color: #000000;'>");  //tabale1

        stringBuilder.append("<tr><td style='width:150px'><a><img src='file:///android_res/drawable/logo1.png' alt='logo' height='70px' width='90px'/></a></td>");
        //file:///android_res/drawable/test_image.png
        stringBuilder.append("<td style='vertical-align:Top'>");
        stringBuilder.append("<table style='vertical-align:top; width:100%'>");
        stringBuilder.append("<tr><td style='text-align:center; font-family:Arial; font-size:large; font-weight:bold; background-color:#18B5F0;'>" + CompanyName + "</td></tr>");
        stringBuilder.append("<tr><td style='text-align:center; font-family:Arial; font-size:small; font-weight:bold'>" + CompanyAddress + "</td></tr></table>");

        stringBuilder.append("<table style='width:100%'>");
        stringBuilder.append("<tr><td style='width:50%; font-family:Arial; font-size:small; font-weight:normal'>" + CompanyEmail + "</td>");
        stringBuilder.append("<td style='text-align:right; width:50%; font-family:Arial; font-size:small; font-weight:normal'>Contact No. :" + CompanyMobile + "</td></tr></table>");

        stringBuilder.append("</td></tr></table>");

        stringBuilder.append("<table style='border-bottom-style: solid; border-bottom-width: 1px; border-bottom-color: #000000; width:100%;text-align:center;font-weight:bold'>");
        stringBuilder.append("<tr><td style='width:100%'>Sales Invoice</td></tr></table>");
        //stringBuilder.append("<table style='width:100%'></table>");

        stringBuilder.append("<table style='width:100%;'>");
        stringBuilder.append("<tr><td style='width: 50%; border-right-style: solid; border-right-width: 1px; border-right-color: #000000;'>");
        stringBuilder.append("<table style='text-align:left; vertical-align:top; border-left-style: none;'>");
        stringBuilder.append("<tr><td style='text-align:left; font-family:'Times New Roman', Times, serif; font-size:medium; font-weight:bold;'>Suppliyer Detail :-</td></tr>");
        stringBuilder.append("<tr><td style='font-size:medium; font-weight:bold; font-family:'Times New Roman', Times, serif;text-align: left;'>" + ledgername + "</td></tr>");
        stringBuilder.append("<tr><td style='font-size:medium; font-family:'Times New Roman', Times, serif; font-weight:bold'>" + ledgeraddress + "</td></tr>");
        stringBuilder.append("<tr><td style='font-size:medium;font-family:'Times New Roman', Times, serif; font-weight:bold'>Contact No.: " + ledgercontact + "</td><td>GST No.:" + ledgergst + "</td></tr></table></td>");
        stringBuilder.append("<td style='vertical-align:top'>");
        stringBuilder.append("<table style='width:100%; vertical-align:top'>");
        stringBuilder.append("<tr><td style='text-align:right; font-family:'Times New Roman', Times, serif; font-size:medium; font-weight:bold; width:50%'>Voucher No :</td>");
        stringBuilder.append("<td>" + vochno + "</td></tr>");
        stringBuilder.append("<tr><td style='text-align:right; font-family:'Times New Roman', Times, serif; font-size:medium; font-weight:bold; width:50%'> Voucher Date : </td>");
        stringBuilder.append("<td>" + opdate + "</td></tr>");
        stringBuilder.append("<tr><td style='text-align:right; font-family:'Times New Roman', Times, serif; font-size:medium; font-weight:bold; width:50%'> Challan No : </td>");
        stringBuilder.append("<td> "+ challanno +"</td></tr>");
        stringBuilder.append("<tr><td style='text-align:right; font-family:'Times New Roman', Times, serif; font-size:medium; font-weight:bold; width:50%'> Voucher Type : </td>");
        stringBuilder.append("<td>" + vochtype + "</td></tr></table></td></tr></table>");

        stringBuilder.append("<table width='100%' cellspacing='0' rules='All' style='border: 1px solid;font-size: 9pt;font-family:Arial;border-collapse:collapse;'>");
        stringBuilder.append("<thead><tr><th width='3%'>Sr.No </th><th width='20%'> Product Name </th><th width='10%'> MFg Company </th><th width='7%'> Batch No. </th><th width='7%'> Exp.Date </th><th style='text-align:right;width=7%'> Quantity </th><th style='text-align:right;width=7%'> Rate </th><th style='text-align:right;width=10%'> Amount </th></tr></thead>");
        stringBuilder.append("<tbody>");

        int ptrows =0;
        int pagerows =7;
        int dumyr =0;
        int srn = 1;
        double prodtot =0;

        ptrows = salesprodlist.size();
        if (ptrows < 6){
            dumyr = 5 - ptrows;
        }
        for (int i=0; i <salesprodlist.size();i++)
        {
            double qty =0;
            double prate =0;
            double pamt = 0;
            HashMap<String, String> hashmap= salesprodlist.get(i);
            String pqty= hashmap.get("qty");
            String pprate = hashmap.get("rateinctax");
            String ppamount = hashmap.get("amountinctax");
            String pprodname = hashmap.get("prodname");
            String pmfgcomp = hashmap.get("mfgcompany");

            if (pqty != null && !pqty.isEmpty()){
                qty = Double.valueOf(pqty);
            }

            if (pprate != null && !pprate.isEmpty()){
                prate = Double.valueOf(pprate);
            }

            if (ppamount != null && !ppamount.isEmpty()){
                pamt = Double.valueOf(ppamount);
            }

            stringBuilder.append("<tr><td>" + srn  + "</td><td>" +
                    pprodname +
                    "</td><td>" + pmfgcomp +
                    "</td><td style='text-align:left;'></td><td></td><td style='text-align:right;'>" +
                    nf.format(qty)+
                    "</td><td style='text-align:right;'>"  + nf.format(prate) +
                    "</td><td style='text-align:right;'>" + nf.format(pamt) + "</td></tr>");
            prodtot = (prodtot) + Double.valueOf(pamt);
            srn = srn+1;

        }
        for (int i =0; i <= dumyr; i++){
            stringBuilder.append("<tr><td>" + srn  + "</td><td></td><td></td><td style='text-align:left;'></td><td></td><td style='text-align:right;'>" +
                    "</td><td style='text-align:right;'></td><td style='text-align:right;'></td></tr>");
            srn = srn+1;
        }



        stringBuilder.append("</tbody></table>");
        stringBuilder.append("<table style='width:100%; border-bottom-style: solid; border-bottom-width: 1px; border-bottom-color: #000000;'>");
        stringBuilder.append("<tr>");
        stringBuilder.append("<td style='width: 70%; border-right-style: solid; border-right-width: 1px; border-right-color: #000000;'>");
        stringBuilder.append("<table style='width: 100%; vertical-align:Top'>");
        stringBuilder.append("<tr><td style='font-size:small; font-weight:normal; font-family:Arial'>Amount In Word :" + narration + "</td></tr>");
        stringBuilder.append("<tr><td style='font-size:small; font-weight:normal; font-family:Arial'>Due Date :" + opdate + "</td></tr>");
        stringBuilder.append("<tr><td style='font-size:small; font-weight:normal; font-family:Arial'>Narration : "+ narration +"</td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("</td>");
        stringBuilder.append("<td style='width:30%'>");
        stringBuilder.append("<table style='width:100%; vertical-align:top'>");
        stringBuilder.append("<tr><td style='text-align:right; font-size:small; font-weight:normal; font-family:Arial'> Amount : </td><td style='text-align:right; font-size:small; font-weight:normal; font-family:Arial'>" + nf.format(prodtot) + "</td></tr>");
        stringBuilder.append("<tr><td style='text-align:right; font-size:small; font-weight:normal; font-family:Arial'> GST Amount : </td><td style='text-align:right; font-size:small; font-weight:normal; font-family:Arial'>0.00</td></tr>");

        stringBuilder.append("<tr><td style='text-align:right; font-size:small; font-weight:bold; font-family:Arial'> Total Amount : </td><td style='text-align:right; font-size:small; font-weight:bold; font-family:Arial'>" + nf.format(Double.valueOf(invoiceamount)) + "</td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("</td>");
        stringBuilder.append("</tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<table style='width:100%'>");
        stringBuilder.append("<tr><td></td></tr>");
        stringBuilder.append("<tr><td></td></tr>");
        stringBuilder.append("<tr><td></td></tr>");
        stringBuilder.append("<tr><td style='width: 25%;'>Driver's Sign</td>");
        stringBuilder.append("<td style='width: 25%;'>Store Keeper's Sign</td>");
        stringBuilder.append("<td style='width: 25%;'>Manager's Sign</td>");
        stringBuilder.append("<td style='width: 25%;'>Accountant Sign</td>");
        stringBuilder.append("</tr>");
        stringBuilder.append("</table>");
        //'htmlDocument= htmlDocument+"<div class='page-footer'></div>";
        //'
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("</td></tr></table>");
        stringBuilder.append("</div>");

        //'stringBuilder.append("</div>");
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");
        htmlDocument= stringBuilder.toString();
        mywebview.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);


        //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft");
        File path =  new File(getFilesDir(), "poultrysoft");
        File imgFile = new File(path.getAbsolutePath() + "/invoice.html");

        String fileName = "invoice.html";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft"), fileName);

        //Uri npath;
        //npath = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", file );

        // String html = "<html><head><title>Title</title></head><body>This is random text.</body></html>";

        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] data = htmlDocument.getBytes();
            out.write(data);
            out.close();
            // Log.e(TAG, "File Save : " + file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            String pdffileName = "invoice.pdf";
            File nfile =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft"), pdffileName);

            String pdfFile =nfile.getPath();
            OutputStream fos = new FileOutputStream(new File(pdfFile));
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
            document.open();
            InputStream is =
                    new ByteArrayInputStream(htmlDocument.toString().getBytes());
            XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, is);
            document.close();

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CreateCashReceiptInvoice(String vochno) throws IOException {

// Create a WebView object specifically for printing


        mywebview.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                // Log.i(TAG, "page finished loading " + url);

            }
        });
        NumberFormat nf = NumberFormat.getInstance();


        try {
            vdt = dt.parse(vdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String opdate = nDateform.format(vdt);
        // Generate an HTML document on the fly:
        String htmlDocument ="";


//        htmlDocument= htmlDocument + "<html>";
        //'htmlDocument= htmlDocument+"<tr><td></td>";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(htmlDocument);
        stringBuilder.append("<!DOCTYPE html>");
        stringBuilder.append("<html>");
        stringBuilder.append("<head><title>Title</title></head>");
        //stringBuilder.append("</head");
        stringBuilder.append("<body>");
        stringBuilder.append("<div class='page';style='width:100%; line-height:normal;'>");
        stringBuilder.append("<table width='100%' cellspacing='0' cellpadding='2' rules='All' style='border: 1px solid;font-size: 9pt;font-family:Arial;border-collapse:collapse;'>");
        stringBuilder.append("<tr><td>");
        stringBuilder.append("<table style='width:100%; border-bottom-style: solid; border-bottom-width: 1px; border-bottom-color: #000000;'>");  //tabale1

        stringBuilder.append("<tr><td style='width:150px'><a><img src='file:///android_res/drawable/logo1.png' alt='logo' height='70px' width='90px'/></a></td>");
        //file:///android_res/drawable/test_image.png
        stringBuilder.append("<td style='vertical-align:Top'>");
        stringBuilder.append("<table style='vertical-align:top; width:100%'>");
        stringBuilder.append("<tr><td style='text-align:center; font-family:Arial; font-size:large; font-weight:bold; background-color:#18B5F0;'>" + CompanyName + "</td></tr>");
        stringBuilder.append("<tr><td style='text-align:center; font-family:Arial; font-size:small; font-weight:bold'>" + CompanyAddress + "</td></tr></table>");

        stringBuilder.append("<table style='width:100%'>");
        stringBuilder.append("<tr><td style='width:50%; font-family:Arial; font-size:small; font-weight:normal'>" + CompanyEmail + "</td>");
        stringBuilder.append("<td style='text-align:right; width:50%; font-family:Arial; font-size:small; font-weight:bold'>Contact No. :" + CompanyMobile + "</td></tr></table>");

        stringBuilder.append("</td></tr></table>");

        stringBuilder.append("<table style='border-bottom-style: solid; border-bottom-width: 1px; border-bottom-color: #000000; width:100%;text-align:center;font-weight:bold'>");
        stringBuilder.append("<tr><td style='width:100%'>Cash Receipt Voucher</td></tr></table>");
        //stringBuilder.append("<table style='width:100%'></table>");

        stringBuilder.append("<table style='width:100%;'>");
        stringBuilder.append("<tr><td style='width: 50%; border-right-style: solid; border-right-width: 1px; border-right-color: #000000;'>");
        stringBuilder.append("<table style='text-align:left; vertical-align:top; border-left-style: none;'>");
        stringBuilder.append("<tr><td style='text-align:left; font-family:'Times New Roman', Times, serif; font-size:medium; font-weight:bold;'>Cash Receipt No :-</td><td> "+ vochno +"</td></tr></table></td>");
        stringBuilder.append("<td style='vertical-align:top'>");
        stringBuilder.append("<table style='width:100%; vertical-align:top'>");
        stringBuilder.append("<tr><td style='text-align:right; font-family:'Times New Roman', Times, serif; font-size:medium; font-weight:bold; width:50%'>Date :</td><td>" + vdate + "</td></tr></table></td></tr></table>");

        stringBuilder.append("<table style='width:100%; border-bottom-style: solid; border-bottom-width: 1px; border-bottom-color: #000000;'>");
        stringBuilder.append("<tr>");
        stringBuilder.append("<td style='width: 100%; border-right-style: solid; border-right-width: 1px; border-right-color: white;'>");
        stringBuilder.append("<table style='width: 100%; vertical-align:Top'>");
        stringBuilder.append("<tr><td style='font-size:small; font-weight:normal; font-family:Arial'>Cash Received From <b>" + ledgername + "</b>, "+ ledgeraddress + " Of " + NumberWordConverter.convertToIndianCurrency(invoiceamount) +  " For " + ledgeremail + "</td></tr>");
        stringBuilder.append("<tr><td style='font-size:large; font-weight:bold; font-family:Arial'>Rs. :" + invoiceamount + "</td></tr>");
        //stringBuilder.append("<tr><td style='font-size:small; font-weight:normal; font-family:Arial'>Payment Received In : <b>"+ ledgergst +"</b></td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("</td>");

        stringBuilder.append("</tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<table style='width:100%'>");
        stringBuilder.append("<tr><td></td></tr>");
        stringBuilder.append("<tr><td></td></tr>");
        stringBuilder.append("<tr><td></td></tr>");
        stringBuilder.append("<tr><td style='width: 50%;text-align:left'>Receiver's Sign</td>");
        stringBuilder.append("<td style='width: 50%;text-align:right;'>Accountant Sign</td>");
        stringBuilder.append("</tr>");
        stringBuilder.append("</table>");
        //'htmlDocument= htmlDocument+"<div class='page-footer'></div>";
        //'
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("</td></tr></table>");
        stringBuilder.append("</div>");

        //'stringBuilder.append("</div>");
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");
        htmlDocument= stringBuilder.toString();
        mywebview.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);


        //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft");
        File path =  new File(getFilesDir(), "poultrysoft");
        File imgFile = new File(path.getAbsolutePath() + "/invoice.html");

        String fileName = "invoice.html";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft"), fileName);

        //Uri npath;
        //npath = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", file );

        // String html = "<html><head><title>Title</title></head><body>This is random text.</body></html>";

        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] data = htmlDocument.getBytes();
            out.write(data);
            out.close();
            // Log.e(TAG, "File Save : " + file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            String pdffileName = "invoice.pdf";
            File nfile =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft"), pdffileName);

            String pdfFile =nfile.getPath();
            OutputStream fos = new FileOutputStream(new File(pdfFile));
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
            document.open();
            InputStream is =
                    new ByteArrayInputStream(htmlDocument.toString().getBytes());
            XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, is);
            document.close();

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager

        //createWebPrintJob(mWebView);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CreateCashPaymentInvoice(String vochno) throws IOException {

// Create a WebView object specifically for printing


        mywebview.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onPageFinished(WebView view, String url) {
                // Log.i(TAG, "page finished loading " + url);

            }
        });
        NumberFormat nf = NumberFormat.getInstance();
        try {
            vdt = dt.parse(vdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String opdate = nDateform.format(vdt);
        // Generate an HTML document on the fly:
        String htmlDocument ="";

//        htmlDocument= htmlDocument + "<html>";
        //'htmlDocument= htmlDocument+"<tr><td></td>";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(htmlDocument);
        stringBuilder.append("<!DOCTYPE html>");
        stringBuilder.append("<html>");
        stringBuilder.append("<head><title>Title</title></head>");
        //stringBuilder.append("</head");
        stringBuilder.append("<body>");
        stringBuilder.append("<div class='page';style='width:100%; line-height:normal;'>");
        stringBuilder.append("<table width='100%' cellspacing='0' cellpadding='2' rules='All' style='border: 1px solid;font-size: 9pt;font-family:Arial;border-collapse:collapse;'>");
        stringBuilder.append("<tr><td>");
        stringBuilder.append("<table style='width:100%; border-bottom-style: solid; border-bottom-width: 1px; border-bottom-color: #000000;'>");  //tabale1

        stringBuilder.append("<tr><td style='width:150px'><a><img src='file:///android_res/drawable/h1.png' alt='logo' height='70px' width='90px'/></a></td>");
        //file:///android_res/drawable/test_image.png
        stringBuilder.append("<td style='vertical-align:Top'>");
        stringBuilder.append("<table style='vertical-align:top; width:100%'>");
        stringBuilder.append("<tr><td style='text-align:center; font-family:Arial; font-size:large; font-weight:bold; background-color:#18B5F0;'>" + CompanyName + "</td></tr>");
        stringBuilder.append("<tr><td style='text-align:center; font-family:Arial; font-size:small; font-weight:bold'>" + CompanyAddress + "</td></tr></table>");

        stringBuilder.append("<table style='width:100%'>");
        stringBuilder.append("<tr><td style='width:50%; font-family:Arial; font-size:small; font-weight:normal'>" + CompanyEmail + "</td>");
        stringBuilder.append("<td style='text-align:right; width:50%; font-family:Arial; font-size:small; font-weight:bold'>Contact No. :" + CompanyMobile + "</td></tr></table>");

        stringBuilder.append("</td></tr></table>");

        stringBuilder.append("<table style='border-bottom-style: solid; border-bottom-width: 1px; border-bottom-color: #000000; width:100%;text-align:center;font-weight:bold'>");
        stringBuilder.append("<tr><td style='width:100%'>Cash Payment Voucher</td></tr></table>");
        //stringBuilder.append("<table style='width:100%'></table>");

        stringBuilder.append("<table style='width:100%;'>");
        stringBuilder.append("<tr><td style='width: 50%; border-right-style: solid; border-right-width: 1px; border-right-color: #000000;'>");
        stringBuilder.append("<table style='text-align:left; vertical-align:top; border-left-style: none;'>");
        stringBuilder.append("<tr><td style='text-align:left; font-family:'Times New Roman', Times, serif; font-size:medium; font-weight:bold;'>Cash Receipt No :-</td><td> "+ vochno +"</td></tr></table></td>");
        stringBuilder.append("<td style='vertical-align:top'>");
        stringBuilder.append("<table style='width:100%; vertical-align:top'>");
        stringBuilder.append("<tr><td style='text-align:right; font-family:'Times New Roman', Times, serif; font-size:medium; font-weight:bold; width:50%'>Date :</td><td>" + vdate + "</td></tr></table></td></tr></table>");

        stringBuilder.append("<table style='width:100%; border-bottom-style: solid; border-bottom-width: 1px; border-bottom-color: #000000;'>");
        stringBuilder.append("<tr>");
        stringBuilder.append("<td style='width: 100%; border-right-style: solid; border-right-width: 1px; border-right-color: white;'>");
        stringBuilder.append("<table style='width: 100%; vertical-align:Top'>");
        stringBuilder.append("<tr><td style='font-size:small; font-weight:normal; font-family:Arial'>Cash Received From <b>" + ledgername + "</b>, "+ ledgeraddress + " Of " + NumberWordConverter.convertToIndianCurrency(invoiceamount) +  " For " + ledgeremail + "</td></tr>");
        stringBuilder.append("<tr><td style='font-size:large; font-weight:bold; font-family:Arial'>Rs. :" + invoiceamount + "</td></tr>");
        //stringBuilder.append("<tr><td style='font-size:small; font-weight:normal; font-family:Arial'>Payment Received In : <b>"+ ledgergst +"</b></td></tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("</td>");

        stringBuilder.append("</tr>");
        stringBuilder.append("</table>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<table style='width:100%'>");
        stringBuilder.append("<tr><td></td></tr>");
        stringBuilder.append("<tr><td></td></tr>");
        stringBuilder.append("<tr><td></td></tr>");
        stringBuilder.append("<tr><td style='width: 50%;text-align:left'>Receiver's Sign</td>");
        stringBuilder.append("<td style='width: 50%;text-align:right;'>Accountant Sign</td>");
        stringBuilder.append("</tr>");
        stringBuilder.append("</table>");
        //'htmlDocument= htmlDocument+"<div class='page-footer'></div>";
        //'
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("</td></tr></table>");
        stringBuilder.append("</div>");

        //'stringBuilder.append("</div>");
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");
        htmlDocument= stringBuilder.toString();
        mywebview.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);


        //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft");
        File path =  new File(getFilesDir(), "poultrysoft");
        File imgFile = new File(path.getAbsolutePath() + "/invoice.html");

        String fileName = "invoice.html";
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft"), fileName);

        //Uri npath;
        //npath = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".fileprovider", file );

        // String html = "<html><head><title>Title</title></head><body>This is random text.</body></html>";

        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] data = htmlDocument.getBytes();
            out.write(data);
            out.close();
            // Log.e(TAG, "File Save : " + file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            String pdffileName = "invoice.pdf";
            File nfile =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS +"/poultrysoft"), pdffileName);

            String pdfFile =nfile.getPath();
            OutputStream fos = new FileOutputStream(new File(pdfFile));
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fos);
            document.open();
            InputStream is =
                    new ByteArrayInputStream(htmlDocument.toString().getBytes());
            XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, is);
            document.close();

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager

        //createWebPrintJob(mWebView);
    }

}