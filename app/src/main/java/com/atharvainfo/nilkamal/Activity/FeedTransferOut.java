package com.atharvainfo.nilkamal.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Adapter.feedreqadapter;
import com.atharvainfo.nilkamal.Adapter.purchaseadapter;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.feedreqmodel;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FeedTransferOut extends AppCompatActivity {
    public static final String PREFS="PREFS";
    DatePickerDialog.OnDateSetListener date;
    String dtf="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dtf, Locale.US);
    Calendar mycal=Calendar.getInstance();
    TextView datechoice3,datechoice4,invoiceno,subtotal,discount,gst,total,txtsupname,txtsupadd,txtspcontact,txtgstno,txtcity,txtdistrict,txttaluka,txtgrid;
    EditText purchaseno;
    TextView txtpartybal,txtbilldate,txtbillduedate,txtglcode;
    Button btnaddcustomer,btnadditem, btnsavepurchase, btncancelpur, btnaddtocustomer;
    TextInputEditText narration;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DatabaseHelper myDb;
    private Toolbar toolbar;
    ArrayList<HashMap<String, String>> itmlist;

    String dateformat="dd-MM-yyyy";
    SimpleDateFormat nDateform=new SimpleDateFormat(dateformat, Locale.US);
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri,FyStartDate,FyEndDate,spname,spemail;
    double sumptot= 0;
    double sumdtot=0;
    double sumntot =0;
    double sumgsttot =0;
    double psubtotamt =0;
    String TempVoucherNo="";
    RecyclerView sitemlist;
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    ArrayList<pitemmodel> datamodel;
    com.atharvainfo.nilkamal.Adapter.purchaseadapter purchaseadapter;
    private Button btndeleteproduct;
    TextView txtbatchno,txtfeedstock,txtbirdstock, txttofarmname,txttofarmadd,txttofcontact,txttofpbal,txttofcode,txttofbatch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_transfer_out);

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

        mContext = getApplicationContext();
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
        btnaddcustomer=findViewById(R.id.btnadd);
        btnsavepurchase = findViewById(R.id.btnsavepurchase);
        btncancelpur = findViewById(R.id.btncanclepur);
        btnaddtocustomer = findViewById(R.id.btnaddto);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initToolbar();

        btnadditem=findViewById(R.id.btnadd1);
        invoiceno=findViewById(R.id.invoice);
        purchaseno=findViewById(R.id.purchaseno);
        //itemlist=findViewById(R.id.list);
        sitemlist = findViewById(R.id.list);
        txtglcode = findViewById(R.id.farmcode);
        total=findViewById(R.id.total);
        narration=findViewById(R.id.narration);

        txtsupname = findViewById(R.id.txtsupname);
        txtsupadd = findViewById(R.id.txtsupadd);
        txtspcontact = findViewById(R.id.txtspcontact);
        txtbilldate = findViewById(R.id.billdate);
        txtbillduedate = findViewById(R.id.billduedate);
        txtbatchno = findViewById(R.id.txtbatchno);
        txtfeedstock= findViewById(R.id.txtfeedstk);
        txtbirdstock = findViewById(R.id.txtbirdstk);

        txttofarmname = findViewById(R.id.txttofarmname);
        txttofarmadd = findViewById(R.id.txttofarmadd);
        txttofcontact = findViewById(R.id.txttofcontact);
        txttofpbal = findViewById(R.id.txttofpbal);
        txttofcode = findViewById(R.id.txttofcode);
        txttofbatch = findViewById(R.id.txttofbatch);

        Long currentdate=System.currentTimeMillis();
        String datestring=nDateform.format(currentdate);
        datechoice3.setText(datestring);
        String newdate = dateform.format(currentdate);
        txtbilldate.setText(newdate);
        TempVoucherNo = UUID.randomUUID().toString();

        date= (datePicker, year, monthofyear, dateofmonth) -> {
            mycal.set(Calendar.YEAR,year);
            mycal.set(Calendar.MONTH,monthofyear);
            mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
            updateDate();
        };
        datechoice3.setOnClickListener(view -> new DatePickerDialog(FeedTransferOut.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show());

        btnaddcustomer.setOnClickListener(view -> {
            Intent partylist = new Intent(FeedTransferOut.this, FarmListSp.class);
            creditledger.launch(partylist);
        });
        btnaddtocustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent partylist = new Intent(FeedTransferOut.this, FarmListAll.class);
                tofarmledger.launch(partylist);
            }
        });

        btnadditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent feedprod = new Intent(FeedTransferOut.this, FeedtransferItemselect.class);
                saleitem.launch(feedprod);
            }
        });
    }

    private void updateDate()
    {
        datechoice3.setText(nDateform.format(mycal.getTime()));
        txtbilldate.setText(dateform.format(mycal.getTime()));
    }
    private void initToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Feed Transfer Out");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    ActivityResultLauncher<Intent> creditledger = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                String farmname = data.getStringExtra("FarmName");
                String farmaddress = data.getStringExtra("FarmAddress");
                String batchno = data.getStringExtra("BatchNo");
                String feedstock = data.getStringExtra("FeedStock");
                String farmcode = data.getStringExtra("FarmCode");
                String birdplace = data.getStringExtra("BirdPlace");
                String birdstock = data.getStringExtra("BirdStock");

                txtsupname.setText(farmname);
                txtsupadd.setText(farmaddress);
                txtspcontact.setText(batchno);
                txtglcode.setText(farmcode);
                txtbatchno.setText(batchno);
                txtfeedstock.setText(feedstock);
                txtbirdstock.setText(birdstock);

                sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                editor = sp.edit();
                editor.putString("farmcode", farmcode);
                editor.putString("farmname", farmname);
                editor.putString("farmaddress", farmaddress);
                editor.putString("farmbatch", batchno);
                editor.commit();


            }
        }
    });

    ActivityResultLauncher<Intent> tofarmledger = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                String farmname = data.getStringExtra("FarmName");
                String farmaddress = data.getStringExtra("FarmAddress");
                String batchno = data.getStringExtra("BatchNo");
                String feedstock = data.getStringExtra("FeedStock");
                String farmcode = data.getStringExtra("FarmCode");

                txttofarmname.setText(farmname);
                txttofarmadd.setText(farmaddress);
                txttofcontact.setText(batchno);
                txttofcode.setText(farmcode);
                txttofbatch.setText(batchno);
                txttofpbal.setText(feedstock);


            }
        }
    });
    ActivityResultLauncher<Intent> saleitem = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                String prodno = data.getStringExtra("ProdNo");
                String pname = data.getStringExtra("ProdName");
                String pqty = data.getStringExtra("ProdQty");
                String prate = data.getStringExtra("ProdRate");
                String pamount = data.getStringExtra("ProdAmount");
                double ppqty = Double.parseDouble(pqty);
                double pamt = Double.parseDouble(pamount);
                sumptot += pamt;

                datamodel.add(new pitemmodel(prodno,pname,"0",pqty, pqty,prate,pamount,prate,pamount,"","","","","","","Main",""));
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                sitemlist.setLayoutManager(mLayoutManager);
                sitemlist.setItemAnimator(new DefaultItemAnimator());
                sitemlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                purchaseadapter = new purchaseadapter(datamodel, getApplicationContext());

                sitemlist.setAdapter(purchaseadapter);

                total.setText(Tools.amountpriceformat(sumptot));
                Log.e("TotalQt", String.valueOf(sumptot));

                mDatabase.beginTransaction();
                String mSql = "Insert Into salestran(tempvochno,vdate,ledgername,paddress,prodno,prodname," +
                        "qty,rateinctax,amountinctax,rateexctax,amountexctax,addeddate) values ('"+ TempVoucherNo+ "','"+
                        txtbilldate.getText().toString() +"','"+ txtsupname.getText().toString()+"','"+ txtsupadd.getText().toString()+"','"+
                        prodno+ "','"+ pname+"','"+pqty+"','"+ prate+"','"+ pamount +"','"+ prate +"','"+pamount+"','"+ spname +"')";
                mDatabase.execSQL(mSql);
                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();
                total.setText(Tools.amountpriceformat(sumptot));

                purchaseadapter.setOnItemClickListener((view, obj, position) -> {
                    final String prodn =((TextView)view.findViewById(R.id.lblitemname)).getText().toString();
                    final String prodno1 =((TextView)view.findViewById(R.id.lblitemno)).getText().toString();
                    final double pprate1 = Double.parseDouble(((TextView)view.findViewById(R.id.lblitemrate)).getText().toString());
                    final double ppqty1 = Double.parseDouble(((TextView)view.findViewById(R.id.lblitemqty)).getText().toString());
                    final double ptot1 = pprate1 * ppqty1;
                    double mppamount = (pprate1 * ppqty1);

                    sumptot -= mppamount;

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
                        total.setText(Tools.amountpriceformat(sumptot));
                    });

                });
            }
        }
    });
    private void SaveFeedTransferVoucher(){
        if (!txtsupname.getText().toString().isEmpty()){
            if (!txttofarmname.getText().toString().isEmpty()) {
                if (purchaseadapter.getItemCount() != 0) {
                    double ntot = 0;
                    double disamt = 0;

                    try {
                        ntot = Objects.requireNonNull(DecimalFormat.getNumberInstance().parse(total.getText().toString())).doubleValue();
                        //disamt = Objects.requireNonNull(DecimalFormat.getNumberInstance().parse(discount.getText().toString())).doubleValue();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String nt = String.valueOf(ntot);
                    String dt = String.valueOf(disamt);
                    Tools.showSimpleProgressDialog(this, "Saving Feed Transfer Invoice...", "Please Wait ...", false);

                    JSONArray smarray = new JSONArray();
                    JSONObject jso = new JSONObject();
                    String invmode = "SAL";
                    try {
                        jso.put("tranid", TempVoucherNo);
                        jso.put("vdate", txtbilldate.getText().toString());
                        jso.put("addedby", spname);
                        jso.put("glcode", txtgrid.getText().toString());
                        jso.put("glcode1", txtgrid.getText().toString());
                        jso.put("farmnameone", txtsupname.getText().toString());
                        jso.put("farmnametwo", txtsupname.getText().toString());
                        jso.put("address", txtsupadd.getText().toString());
                        jso.put("address1", txtsupadd.getText().toString());
                        jso.put("narr", Objects.requireNonNull(narration.getText()).toString());
                        jso.put("dcno", purchaseno.getText().toString());
                        jso.put("batch", txtbilldate.getText().toString());
                        jso.put("batch1", txtbatchno.getText().toString());
                        jso.put("spcode", txtspcontact.getText().toString());
                        jso.put("vochtype", txtbillduedate.getText().toString());
                        jso.put("invmode", invmode);
                        jso.put("amount", nt.toString());

                    } catch (Exception e) {
                        Log.d("InputStream", e.getLocalizedMessage());
                    }

                    JSONObject jsot = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    JSONObject finalobject = new JSONObject();

                    String sqlr = "Select tempvochno,vdate,ledgername,paddress,prodno,prodname," +
                            "mfgcomp,prodcategory,qty,rateinctax,amountinctax,rateexctax,amountexctax," +
                            "prodtype,prodpack,addeddate,igstp,igstamount from salestran where tempvochno ='" + TempVoucherNo + "'";
                    mDatabase.beginTransaction();
                    Cursor c = mDatabase.rawQuery(sqlr, null);
                    if (c.moveToFirst()) {
                        while (!c.isAfterLast()) {
                            jsot = new JSONObject();
                            try {
                                double pkg = c.getDouble(14);
                                double qty = c.getDouble(8);
                                double wtqty = (pkg) * (qty);

                                jsot.put("prodname", c.getString(5));
                                jsot.put("prodno", c.getString(4));
                                jsot.put("qty", c.getString(8));
                                jsot.put("nqty", wtqty);
                                jsot.put("rateinctax", c.getString(9));
                                jsot.put("amountinctax", c.getString(10));
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

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_SAVESALEINVOICE,
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

                                       // SendNoticifationMessage();
                                        btnsavepurchase.setVisibility(View.GONE);
                                        //Toast.makeText(SaleActivity.this, "Sales Invoice Save Successfully...", Toast.LENGTH_SHORT).show();

                                        //psDialogMsg.showSuccessDialog("Sales Invoice Save Successfully...", getString(R.string.OkDialogTitle));

                                    } else {
                                        //psDialogMsg.showErrorDialog(getString(R.string.somethingwentwrong), getString(R.string.OkDialogTitle));
                                    }
                                   // psDialogMsg.show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            },
                            error -> {

                            }) {
                        @Override
                        protected Map<String, String> getParams() {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("smdata", smarray.toString());
                            params.put("smtdata", jsonArray.toString());
                            Log.e("All data:", smarray.toString());
                            Log.e("json object data:", jsonArray.toString());

                            return params;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(FeedTransferOut.this);
                    queue.add(stringRequest);

                } else {
                   // psDialogMsg.showErrorDialog(getString(R.string.plzaddproduct), getString(R.string.OkDialogTitle));
                   // psDialogMsg.show();

                }
            }
        } else {
            //sDialogMsg.showErrorDialog(getString(R.string.selectcustomername), getString(R.string.OkDialogTitle));
            //psDialogMsg.show();
        }
    }
}