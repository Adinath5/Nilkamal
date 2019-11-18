package com.atharvainfo.nilkamal.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
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
import com.atharvainfo.nilkamal.Others.StorageUtils;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class rpt_accountstatement extends AppCompatActivity {
    private PSDialogMsg psDialogMsg;
    private Toolbar toolbar;
    private String grid;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    WebView mywebview;
    String dateformat="dd-MM-yyyy";
    SimpleDateFormat nDateform=new SimpleDateFormat(dateformat, Locale.US);
    private String ledgername,ledgeraddress,ledgercontact,ledgeremail,ledgergst,vdate,invoiceamount;
    Date vdt;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri,loginUserId;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    String FianancialYear;
    String FyStartDate, FyEndDate;
    double OpNetBalAmt = 0;
    Date fsDt, feDt;
    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    ArrayList<HashMap<String, String>> acstatedatalist;
    public static final String PREFS="PREFS";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_accountstatement);

        mywebview = (WebView) findViewById(R.id.webView);
        acstatedatalist = new ArrayList<HashMap<String, String>>();

        psDialogMsg = new PSDialogMsg(this, false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        sp.edit();
        grid = sp.getString("grid",null);
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

        Log.e("GlAc", grid);

        try {
            fsDt = dt.parse(FyStartDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            feDt =dt.parse(FyEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        getAccountDetails();


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_recents:
                        // createPdf(mywebview);
                        //File file = new File ("/poultrysoft/invoice.pdf");
                        File file =  new File(getFilesDir(), "nilkamal");

                        String  destPath = "acstatement.html";// file.getPath() + File.separator + "invoice.pdf";
                        File mfile = new File(destPath);
                        String pdffileName = "acstatement.pdf";
                        File nfile =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/nilkamal");

                        Uri selectedUri = Uri.parse(nfile + "/" + pdffileName);
                        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedUri.toString());
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                        //Uri uri = Uri.parse(pdffile.getAbsolutePath());
                        File pdf = new File(nfile, pdffileName);
                        if (pdf.exists()) {
                            Intent sharingIntent = new Intent();
                            sharingIntent.setAction(Intent.ACTION_SEND);
                            sharingIntent.setType(mimeType);
                            sharingIntent.putExtra(Intent.EXTRA_STREAM, selectedUri);
                            startActivity(Intent.createChooser(sharingIntent, "Share using"));
                            // startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "application/pdf"));
                            Toast.makeText(rpt_accountstatement.this, "Share", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.action_favorites:
                        createWebPrintJob(mywebview);

                        Toast.makeText(rpt_accountstatement.this, "Print", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_nearby:
                        Toast.makeText(rpt_accountstatement.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });


    }

    private void initToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Statement :" + " "+ ledgername);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    private void getAccountStatementData(){
//        Tools.showSimpleProgressDialog(getApplicationContext(), "Generating Statement...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETACCOUNTSTATEMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONArray json1 = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json1.length(); i++) {
                                    JSONObject obj = json1.getJSONObject(i);
                                    HashMap<String, String> transa = new HashMap<>();
                                    transa.put("vdate", obj.getString("vdate"));
                                    transa.put("vochno", obj.getString("vochno"));
                                    transa.put("perticulars", obj.getString("perticulars"));
                                    transa.put("debitamt", obj.getString("debitamt"));
                                    transa.put("creditamt", obj.getString("creditamt"));
                                    transa.put("clbal", obj.getString("clbal"));
                                    transa.put("drcr", obj.getString("drcr"));
                                    transa.put("invmode", obj.getString("invmode"));
                                    transa.put("vochtype", obj.getString("vochtype"));
                                    transa.put("challanno", obj.getString("challanno"));
                                    transa.put("invoiceno", obj.getString("invoiceno"));

                                    acstatedatalist.add(transa);
                                }
                                CreateAccountStatement(grid);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(rpt_accountstatement.this,"No Data Found",Toast.LENGTH_LONG).show();
                        } catch (ParseException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Tools.removeSimpleProgressDialog();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("glcode", grid);
                params.put("sdate", FyStartDate);
                params.put("edate", FyEndDate);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(rpt_accountstatement.this);
        queue.add(stringRequest);
    }

    private void getAccountDetails(){

//         Tools.showSimpleProgressDialog(getApplicationContext(), "Getting Ledger Details...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETACCOUNTLEDGERDETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
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
                                    initToolbar();
                                    getAccountStatementData();
                                }
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
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("glcode", grid);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(rpt_accountstatement.this);
        queue.add(stringRequest);

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createWebPrintJob(WebView webView) {

        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        PrintDocumentAdapter printAdapter =  null;
        printAdapter = webView.createPrintDocumentAdapter("MyDocument");

        String jobName = getString(R.string.app_name) + " Print Test";

        printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void CreateAccountStatement(String glcode) throws ParseException, IOException {
        double netDrBal =0;
        double netCrBal =0;
        double rtotal =0;
        double drOpbal= 0;
        double crOpbal =0;
        double dam =0;
        double cam =0;
        String dc="Dr";
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


        String htmlDocument ="";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(htmlDocument);
        stringBuilder.append("<!DOCTYPE html>");
        stringBuilder.append("<html>");
        stringBuilder.append("<head>");
        stringBuilder.append("<meta http-equiv='Content-Type' content='text/html;charset=UTF-8'/>");
        stringBuilder.append("<style type = 'text/css'> body {height: 29.7cm;width: 21cm;}.page-header, .page-header-space {height: 50px;} .page-footer, .page-footer-space {height: 20px;} .page-footer {position: fixed;bottom: 0; width: 100%; background: yellow;} .page-header {position: fixed; top: 0; width: 100%; background: white;} .page {page-break-after: always;} page[size='A4'] {width: 21cm; height: 29.7cm;} @media print {page[size='A4'] {width: 21cm; height: 29.7cm;} thead {display: table-header-group;} tfoot {display: table-footer-group;} button {display: none;} body {margin: 0;}} table { page-break-inside:auto } tr{ page-break-inside:avoid; page-break-after:auto } thead { display:table-header-group } tfoot { display:table-footer-group }</style>");
        stringBuilder.append("<title>CustomerBill</title>");
        stringBuilder.append("</head>");
        stringBuilder.append("<body>");
        stringBuilder.append("<table width='100%' cellspacing='0' cellpadding='2'>");
        stringBuilder.append("<tr><td align='center' style='font-size: 14pt;font-family:Arial' colspan = '2'><b>" + CompanyName  +"</b></td></tr>");
        stringBuilder.append("<tr><td align='center' style='font-size: 10pt;font-family:Arial' colspan = '2'><b>" + CompanyAddress + "</b></td></tr>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<tr><td align='center' style='font-size: 10pt;font-family:Arial' colspan = '2'><b> Account Statement </b></td></tr>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<tr><td align='Left' style='font-size: 9pt;font-family:Arial' colspan = '2'><b>Ledger Name : " + ledgername + "</b></td></tr>");
        stringBuilder.append("<tr><td align='Left' style='font-size: 9pt;font-family:Arial' colspan = '2'>Ledger Address : " + ledgeraddress + "</td></tr>");
        stringBuilder.append("<tr><td align='Left' style='font-size: 9pt;font-family:Arial' colspan = '2'>Contact No : " + ledgercontact + ", GST No : " + ledgergst +"</td></tr>");
        stringBuilder.append("<tr><td align='Left' style='font-size: 9pt;font-family:Arial' colspan = '2'><b>Ledger Date From : " + nDateform.format(fsDt) + " To Date : " + nDateform.format(feDt) +" </b></td></tr>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("</table>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<table width='100%' cellspacing='0' cellpadding='1' rules='All' style='border: 1px solid;font-size: 9pt;font-family:Arial;border-collapse:collapse'>");
        stringBuilder.append("<tr><td style='text-align:left'>Date</td><td style='text-align:left'>Voucher No</td><td style='text-align:Left'>Details</td><td style='text-align:right'>Debit Amount</td><td style='text-align:right'>Credit Amount</td><td style='text-align:right'>Balance Amount</td><td style='text-align:left'>Dr/Cr</td><td style='text-align:left'>Doc.No</td></tr>");

      //  stringBuilder.append("<tr><td>" + nDateform.format(fsDt) + "</td><td></td><td style='text-align:left'>Opening Balance</td><td style='text-align:right'>" + nf.format(drOpbal) + "</td><td style='text-align:right'>" + nf.format(crOpbal) + "</td><td style='text-align:right'>" + nf.format(Math.abs(OpNetBalAmt)) + "</td><td>" +  + "</td><td></td></tr>");

        for (int i=0;i<acstatedatalist.size();i++)
        {
            HashMap<String, String> hashmap= acstatedatalist.get(i);
            String vdt= hashmap.get("vdate");
            String vchno = hashmap.get("vochno");
            String part = hashmap.get("perticulars");
            String damt = hashmap.get("debitamt");
            String camt = hashmap.get("creditamt");
            String clamt = hashmap.get("clbal");
            String drcr = hashmap.get("drcr");
            String invmode = hashmap.get("invmode");
            String vochtype = hashmap.get("vochtype");
            String challanno = hashmap.get("challanno");
            String invoiceno = hashmap.get("invoiceno");

            assert damt != null;
            netDrBal = (netDrBal)+ (Double.parseDouble(damt));
            assert camt != null;
            netCrBal = (netCrBal)+ (Double.parseDouble(camt));

                dam = Double.parseDouble(damt);
                cam = Double.parseDouble(camt);

            assert clamt != null;
            rtotal= Double.parseDouble(clamt);
            assert vdt != null;
            Date trdt = dt.parse(vdt);
            assert trdt != null;
            stringBuilder.append("<tr><td>" + nDateform.format(trdt) + "</td><td>" + vchno + "</td><td style='text-align:left'>" + part + "</td><td style='text-align:right'>" + nf.format(dam) + "</td><td style='text-align:right'>" + nf.format(cam) + "</td><td style='text-align:right'>" + nf.format(Math.abs(Double.valueOf(clamt))) + "</td><td>" + drcr + "</td><td>" + challanno + "</td></tr>");

        }
        dam=0;
        cam=0;
        if (rtotal >= 0) {
            dc = "Dr";
        } else {
            dc = "Cr";
        }

        if (rtotal >= 0) {
            dc = "Dr";
        } else {
            dc = "Cr";
        }
        stringBuilder.append("<tr><td>Total : </td><td></td><td style='text-align:left'></td><td style='text-align:right'>" + nf.format(netDrBal) + "</td> " +
                "<td style='text-align:right'>" + nf.format(netCrBal) + "</td><td style='text-align:right'>" + nf.format(Math.abs(rtotal)) + "</td><td>" + dc + "</td><td></td></tr>");

        stringBuilder.append("</table>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("<p></p>");
        stringBuilder.append("</body>");

        stringBuilder.append("</html>");

        htmlDocument= stringBuilder.toString();
        mywebview.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

 //       File internalFile = StorageUtils.getFileFromStorage(getFilesDir(),this, "acstatement.html", "nilkamal");
//        Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), AUTHORITY, internalFile);


       // File path1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/nilkamal");
       // File path =  new File(getFilesDir(), "nilkamal");
       // path.mkdir();
        String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/nilkamal/";    // it will return root directory of internal storage
        File root = new File(path1);
        if (!root.exists()) {
            root.mkdirs();       // create folder if not exist
        }
        File imgFile = new File(root + "acstatement.html");
        if (!imgFile.exists()) {
            imgFile.createNewFile();   // create file if not exist
        }
       // String fileName = "acstatement.html";
       // File file = new File(Environment.getExternalStorageDirectory() +"/nilkamal"), fileName);


        try {
            FileOutputStream out = new FileOutputStream(imgFile);
            byte[] data = htmlDocument.getBytes();
            out.write(data);
            out.close();
            // Log.e(TAG, "File Save : " + file.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            String pdffileName = "acstatement.pdf";
            File nfile =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/nilkamal", pdffileName);

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


}