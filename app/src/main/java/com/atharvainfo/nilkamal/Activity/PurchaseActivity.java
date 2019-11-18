package com.atharvainfo.nilkamal.Activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Adapter.purchaseadapter;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.pitemmodel;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PurchaseActivity extends AppCompatActivity {

    public static final String PREFS="PREFS";
    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener date1;
    String dtf="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dtf, Locale.US);
    Calendar mycal=Calendar.getInstance();
    TextView datechoice3,datechoice4,invoiceno,subtotal,discount,gst,total,txtsupname,txtsupadd,txtspcontact,txtgstno,txtcity,txtdistrict,txttaluka,txtgrid;
    EditText purchaseno;
    TextView txtpartybal,txtbilldate,txtbillduedate;
    Button btnaddcustomer,btnadditem, btnsavepurchase, btncancelpur;
    TextInputEditText narration;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DatabaseHelper myDb;
    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> itmlist;
    String vochtype = "Cash";
    private SwitchCompat vtype;
    private Toolbar toolbar;
    String dateformat="dd-MM-yyyy";
    SimpleDateFormat nDateform=new SimpleDateFormat(dateformat, Locale.US);
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri,FyStartDate,FyEndDate,spname,spemail;

    double psubtotamt =0;
    double sumgsttot=0;
    String TempVoucherNo="";
    RecyclerView sitemlist;
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    ArrayList<pitemmodel> datamodel;
    purchaseadapter purchaseadapter;
    private Button btndeleteproduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        sp=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
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
        spname = sp.getString(Constants.KEY_USERNAME,null);
        spemail = sp.getString(Constants.KEY_USEREMAIL,null);
        String usernamelog= sp.getString(Constants.KEY_USERNAME,null);
        String useremail = sp.getString(Constants.KEY_USEREMAIL, null);
        String userDesignation = sp.getString(Constants.KEY_USERROLE, null);

        mContext = getApplicationContext();
        psDialogMsg = new PSDialogMsg(this, false);
        myDb = new DatabaseHelper(this);
        mDatabase = myDb.getWritableDatabase();
        if (!myDb.isTableExists("salestran", true)){
            myDb.CreateSaleTranTbl();
        }
        mDatabase = myDb.getWritableDatabase();
        mDatabase.beginTransaction();
        String sql = "Delete from salestran";
        mDatabase.execSQL(sql);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

        itmlist = new ArrayList<>();
        datamodel = new ArrayList<>();

        datechoice3=findViewById(R.id.datechoice3);
        datechoice4=findViewById(R.id.datechoice4);
        btnaddcustomer=findViewById(R.id.btnadd);
        btnsavepurchase = findViewById(R.id.btnsavepurchase);
        btncancelpur = findViewById(R.id.btncanclepur);

        vtype = findViewById(R.id.simpleSwitch);

        toolbar = findViewById(R.id.toolbarpr);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initToolbar();


        btnadditem=findViewById(R.id.btnadd1);
        invoiceno=findViewById(R.id.invoice);
        purchaseno=findViewById(R.id.purchaseno);
        sitemlist = findViewById(R.id.list);
        subtotal=findViewById(R.id.subtotal);
        discount=findViewById(R.id.discount);
        gst=findViewById(R.id.gst);
        txtgrid = findViewById(R.id.txtgrid);
        total=findViewById(R.id.total);
        narration=findViewById(R.id.narration);

        txtsupname = findViewById(R.id.txtsupname);
        txtsupadd = findViewById(R.id.txtsupadd);
        txtspcontact = findViewById(R.id.txtspcontact);
        txtpartybal = findViewById(R.id.txtpbal);
        txtbilldate = findViewById(R.id.billdate);
        txtbillduedate = findViewById(R.id.billduedate);
        txtgstno=findViewById(R.id.txtgstno);
        txtcity=findViewById(R.id.txtcity);
        txtdistrict=findViewById(R.id.txtdistrict);
        txttaluka=findViewById(R.id.txttaluka);

        Long currentdate=System.currentTimeMillis();
        String datestring=nDateform.format(currentdate);
        datechoice3.setText(datestring);
        datechoice4.setText(datestring);
        String newdate = dateform.format(currentdate);
        txtbilldate.setText(newdate);
        txtbillduedate.setText(newdate);
        TempVoucherNo = UUID.randomUUID().toString();

        vtype.setChecked(true);
        vochtype = "Cash";

        vtype.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                vochtype = "Cash";
                vtype.setText(getString(R.string.VoucherTypeCash));
            } else {
                vochtype = "Credit";
                vtype.setText(getString(R.string.VoucherTypeCredit));
            }
        });

        date= (datePicker, year, monthofyear, dateofmonth) -> {
            mycal.set(Calendar.YEAR,year);
            mycal.set(Calendar.MONTH,monthofyear);
            mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
            updateDate();
        };
        date1= (datePicker, year, monthofyear, dateofmonth) -> {
            mycal.set(Calendar.YEAR,year);
            mycal.set(Calendar.MONTH,monthofyear);
            mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
            updateDate1();
        };

        datechoice3.setOnClickListener(view -> new DatePickerDialog(PurchaseActivity.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show());
        datechoice4.setOnClickListener(view -> new DatePickerDialog(PurchaseActivity.this,date1,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show());

        btnaddcustomer.setOnClickListener(v -> {

            Intent partylist = new Intent(PurchaseActivity.this, PurchaseAcccountList.class);
            creditledger.launch(partylist);
        });

        btncancelpur.setOnClickListener(v -> finish());
        btnadditem.setOnClickListener(v -> {
            Intent itemselectlist = new Intent(PurchaseActivity.this, ItemSelectActivity.class);
            purchitem.launch(itemselectlist);
        });

        btnsavepurchase.setOnClickListener(v -> {
            //getVoucherNoNew();
            SavePurchaseInvoice();
        });
    }


    private void updateDate()
    {
        datechoice3.setText(nDateform.format(mycal.getTime()));
        txtbilldate.setText(dateform.format(mycal.getTime()));

    }
    private void updateDate1() {

        datechoice4.setText(nDateform.format(mycal.getTime()));
        txtbillduedate.setText(dateform.format(mycal.getTime()));

    }
    private void initToolbar() {

        Objects.requireNonNull(getSupportActionBar()).setTitle("Purchase Invoice");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }
    ActivityResultLauncher<Intent> creditledger = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                String ppartyname = data.getStringExtra("PartyName");
                String partybal = data.getStringExtra("PartyBalance");
                String grid = data.getStringExtra("grid");
                String taluka = data.getStringExtra("taluka");
                String address = data.getStringExtra("address");
                String phoneno = data.getStringExtra("phoneno");
                String city = data.getStringExtra("city");
                txtsupadd.setText(address);
                txtspcontact.setText(phoneno);
                txttaluka.setText(taluka);
                txtcity.setText(city);
                txtsupname.setText(ppartyname);
                txtpartybal.setText(getString(R.string.LedgerBalance, partybal));
                txtgrid.setText(grid);

            }
        }
    });

    private void SavePurchaseInvoice(){

        txtsupname.getText().toString();
        if (!txtsupname.getText().toString().isEmpty()){
            if(purchaseadapter.getItemCount()!=0){
                double ntot=0;
                double disamt=0;
                double gstamt=0;
                double cgst=0;
                double sgst=0;
                if (!gst.getText().toString().equals("") && !gst.getText().toString().equals("null") && !gst.getText().toString().isEmpty()){
                    gstamt = Double.parseDouble(gst.getText().toString());
                    cgst = (gstamt)/2;
                    sgst = (gstamt)/2;
                }
                try {
                    ntot = Objects.requireNonNull(DecimalFormat.getNumberInstance().parse(total.getText().toString())).doubleValue();
                    disamt = Objects.requireNonNull(DecimalFormat.getNumberInstance().parse(discount.getText().toString())).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String nt = String.valueOf(ntot);
                String dt = String.valueOf(disamt);
                String cgstamt = String.valueOf(cgst);
                String sgstamt = String.valueOf(sgst);

                JSONArray smarray=new JSONArray();
                JSONObject jso = new JSONObject();
                String invmode = "PUR";
                try {
                    jso.put("tranid", TempVoucherNo);
                    jso.put("vdate", txtbilldate.getText().toString());
                    jso.put("addedby", spname);
                    jso.put("glcode", txtgrid.getText().toString());
                    jso.put("ledgername", txtsupname.getText().toString());
                    jso.put("paddress", txtsupadd.getText().toString());
                    jso.put("narration", Objects.requireNonNull(narration.getText()).toString());
                    jso.put("challanno", purchaseno.getText().toString());
                    jso.put("challandate", txtbilldate.getText().toString());
                    jso.put("taluka", txttaluka.getText().toString());
                    jso.put("phoneno", txtspcontact.getText().toString());
                    jso.put("duedate", txtbillduedate.getText().toString());
                    jso.put("invmode", invmode);
                    jso.put("netamount", nt.toString());
                    jso.put("prodamount", nt.toString());
                    jso.put("vochtype", vochtype);
                    jso.put("igsttaxamt", "0");
                    jso.put("cgsttaxamt", cgstamt);
                    jso.put("sgsttaxamt", sgstamt);
                    jso.put("hamali", "0");
                    jso.put("freight", "0");

                } catch (Exception e) {
                    Log.d("InputStream", e.getLocalizedMessage());
                }

                JSONObject jsot = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject finalobject = new JSONObject();

                String sqlr = "Select tempvochno,vdate,ledgername,paddress,prodno,prodname," +
                        "mfgcomp,prodcategory,qty,rateinctax,amountinctax,rateexctax,amountexctax," +
                        "prodtype,prodpack,addeddate,igstp,igstamount from salestran where tempvochno ='" + TempVoucherNo+"'";
                mDatabase.beginTransaction();
                Cursor c = mDatabase.rawQuery(sqlr, null);
                if (c.moveToFirst()){
                    while (!c.isAfterLast()){
                        jsot = new JSONObject();
                        try {
                            double pkg = c.getDouble(14);
                            double qty = c.getDouble(8);
                            double wtqty = (pkg)*(qty);

                            jsot.put("prodname", c.getString(5));
                            jsot.put("prodno", c.getString(4));
                            jsot.put("qty",c.getString(8));
                            jsot.put("nqty",wtqty);
                            jsot.put("mfgcomp",c.getString(6));
                            jsot.put("prodtype", c.getString(13));
                            jsot.put("packing", c.getString(14));
                            jsot.put("rateinctax",c.getString(9));
                            jsot.put("amountinctax",c.getString(10));
                            jsot.put("rateexctax",c.getString(11));
                            jsot.put("amountexctax",c.getString(12));
                            jsot.put("prodamt",c.getString(10));
                            jsot.put("gid","1");
                            jsot.put("gname","Main");
                            jsot.put("gstperc",c.getString(16));
                            jsot.put("gstamount",c.getString(17));
                            jsot.put("dcno", purchaseno.getText().toString());
                            jsot.put("prate", c.getString(9));
                            jsot.put("prodcategory", c.getString(7));
                            jsot.put("glcode", txtgrid.getText().toString());
                            Log.e("Add Item", c.getString(1));

                        } catch (Exception e) {
                            Log.d("InputStream", e.getLocalizedMessage());
                        }
                        jsonArray.put(jsot);

                        c.moveToNext();
                    }
                }
                mDatabase.endTransaction();

                try {
                    finalobject.put("salestran", jsonArray);
                } catch (Exception e) {
                    Log.d("InputStream", e.getLocalizedMessage());
                }
                smarray.put(jso);


                Tools.showSimpleProgressDialog(this, "Saving Purchase Invoice...","Please Wait ...",false);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_SAVEPURCHASEINVOICE,
                        response -> {
                            Log.e("Result", response.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.optString("Result").equals("Success")) {
                                    Tools.removeSimpleProgressDialog();
                                    JSONArray json = jsonObject.getJSONArray("Data");
                                    for (int i = 0; i < json.length(); i++) {
                                        JSONObject obj = json.getJSONObject(i);
                                        //Ventryno = obj.getString("vochno");
                                        invoiceno.setText(obj.getString("voucherno"));
                                    }

                                    SendNoticifationMessage();
                                    btnsavepurchase.setVisibility(View.GONE);
                                    Toast.makeText(PurchaseActivity.this, "Purchase Invoice Save Successfully...", Toast.LENGTH_SHORT).show();

                                    psDialogMsg.showSuccessDialog("Purchase Invoice Save Successfully...", getString(R.string.OkDialogTitle));

                                } else {
                                    psDialogMsg.showErrorDialog(getString(R.string.somethingwentwrong), getString(R.string.OkDialogTitle));
                                }
                                psDialogMsg.show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {

                        })
                {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        HashMap<String, String> params = new HashMap<>();
                        params.put("smdata", smarray.toString());
                        params.put("smtdata", jsonArray.toString());
                        Log.e("All data:",smarray.toString());
                        Log.e("json object data:",jsonArray.toString());

                        return params;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(PurchaseActivity.this);
                queue.add(stringRequest);


            } else {
                psDialogMsg.showErrorDialog(getString(R.string.plzaddproduct), getString(R.string.OkDialogTitle));
                psDialogMsg.show();
            }
        } else {
            psDialogMsg.showErrorDialog(getString(R.string.selectcustomername), getString(R.string.OkDialogTitle));
            psDialogMsg.show();
        }
    }


    ActivityResultLauncher<Intent> purchitem = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                String pname = data.getStringExtra("ProductName");
                String pqty = data.getStringExtra("PQty");
                String prate = data.getStringExtra("PRate");
                String pamount = data.getStringExtra("PAmount");
                String prodno = data.getStringExtra("Itemno");
                String pcat = data.getStringExtra("ItemCat");
                String pmfg = data.getStringExtra("ItemMfg");
                String pmrp = data.getStringExtra("ItemMrp");
                String ppack = data.getStringExtra("Itempack");
                String pgstprc = data.getStringExtra("Itemgstprc");
                String pgstamt = data.getStringExtra("ItemGstAmt");
                double pprate = Double.parseDouble(prate);
                double ppqty = Double.parseDouble(pqty);
                double ptot = pprate * ppqty;
                double pgst = Double.parseDouble(pgstamt);

                psubtotamt += ptot;
                sumgsttot +=pgst;

                datamodel.add(new pitemmodel(prodno,pname,"0",pqty, pqty,prate,pamount,prate,pamount,pmfg,pcat,"",pgstprc,pgstamt,pmrp,"Main",ppack));
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                sitemlist.setLayoutManager(mLayoutManager);
                sitemlist.setItemAnimator(new DefaultItemAnimator());
                sitemlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                purchaseadapter = new purchaseadapter(datamodel, getApplicationContext());

                sitemlist.setAdapter(purchaseadapter);
                mDatabase.beginTransaction();
                String mSql = "Insert Into salestran(tempvochno,vdate,ledgername,paddress,prodno,prodname," +
                        "mfgcomp,prodcategory,qty,rateinctax,amountinctax,rateexctax,amountexctax," +
                        "prodtype,prodpack,addeddate,igstp,igstamount) values ('"+ TempVoucherNo+ "','"+ txtbilldate.getText().toString() +"','"+
                        txtsupname.getText().toString()+"','"+ txtsupadd.getText().toString()+"','"+ prodno+ "','"+ pname+"','"+pmfg+"','"+pcat+"','"+pqty+"','"+ pprate+"','"+
                        ptot +"','"+ pprate +"','"+ptot+"','"+pcat+"','"+ ppack +"','"+ spname +"','"+ pgstprc +"','"+ pgst +"')";
                mDatabase.execSQL(mSql);
                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();
                subtotal.setText(Tools.amountpriceformat(psubtotamt));

                total.setText(Tools.amountpriceformat(psubtotamt));
                gst.setText(Tools.amountpriceformat(sumgsttot));
                purchaseadapter.setOnItemClickListener((view, obj, position) -> {
                    final String prodn =((TextView)view.findViewById(R.id.lblitemname)).getText().toString();
                    final String prodno1 =((TextView)view.findViewById(R.id.lblitemno)).getText().toString();
                    final double pprate1 = Double.parseDouble(((TextView)view.findViewById(R.id.lblitemrate)).getText().toString());
                    final double ppqty1 = Double.parseDouble(((TextView)view.findViewById(R.id.lblitemqty)).getText().toString());
                    final double ptot1 = pprate1 * ppqty1;
                    double mppamount = (pprate1 * ppqty1);

                    psubtotamt -= mppamount;

                    btndeleteproduct = view.findViewById(R.id.btndeleteitem);
                    btndeleteproduct.setOnClickListener(view1 -> {
                        Log.e("ProductName", prodn);
                        datamodel.remove(position);
                        mDatabase.beginTransaction();
                        String sql = "Delete from salestran where prodno ='" + prodno1 +"' and tempvochno='" + TempVoucherNo + "'";
                        mDatabase.execSQL(sql);
                        mDatabase.setTransactionSuccessful();
                        mDatabase.endTransaction();
                        purchaseadapter.notifyDataSetChanged();
                        subtotal.setText(Tools.amountpriceformat(psubtotamt));
                        total.setText(Tools.amountpriceformat(psubtotamt));
                    });

                });

            }
        }
    });

    private void SendNoticifationMessage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_SENDFARMNOTIFICATION,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("result").equals("success")) {
                            Tools.removeSimpleProgressDialog();

                        } else if (jsonObject.optString("result").equals("failure")) {
                            Tools.removeSimpleProgressDialog();
                            Toast.makeText(getApplicationContext(), "No Such Customer Name Found", Toast.LENGTH_LONG).show();
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
                Map<String, String> params=new HashMap<>();
                String msg = "Purchase Invoice Supplier Name :" + txtsupname.getText().toString() + "\n Address :" + txtsupadd.getText().toString() + "\n Amount : " + total.getText().toString() + " \n Added By : " + spname;
                TOPIC = "/topics/Agrosoft"; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = "Purchase Invoice";
                NOTIFICATION_MESSAGE = msg;
                params.put("mtitle", NOTIFICATION_TITLE);
                params.put("mtext", NOTIFICATION_MESSAGE);
                params.put("useremail", spemail);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(PurchaseActivity.this);
        queue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
