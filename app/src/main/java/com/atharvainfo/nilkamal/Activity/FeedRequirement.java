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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
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
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.feedreqmodel;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class FeedRequirement extends AppCompatActivity {

    Button btnaddcustomer,btnadditem, btnsavepurchase, btncancelpur, btnpaddok, btncancelitem, btnsaveitem;
    public static final String PREFS="PREFS";
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    DatePickerDialog.OnDateSetListener date;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    TextView datechoice3,txtbilldate,invoiceno,txtbatchno,txtfeedstock,txtbirdstock,total,txtsupname,txtsupadd,txtspcontact;
    EditText purchaseno;
    TextView txtpartybal,txtglcode;
    RecyclerView itemlist;
    TextInputEditText narration;
    ArrayList<feedreqmodel> datamodel;
    feedreqadapter feedreqadapter;
    ImageView btndeleteitem;

    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> itmlist;
    private SimpleAdapter prodadapter;
    double sumptot= 0;
    double sumdtot=0;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private DatabaseHelper myDb;
    SimpleDateFormat nDateform=new SimpleDateFormat(dateformat, Locale.US);
    String TempVoucherNo="";
    RecyclerView sitemlist;
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    String spname,spemail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_requirement);

        psDialogMsg = new PSDialogMsg(this, false);
        sp=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        spname = sp.getString(Constants.KEY_USERNAME,null);
        spemail = sp.getString(Constants.KEY_USEREMAIL,null);

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
        btnaddcustomer=findViewById(R.id.btnadd);
        btnsavepurchase = findViewById(R.id.btnsavefeedr);
        btncancelpur = findViewById(R.id.btncanclefr);
        btnadditem=findViewById(R.id.btnadd1);
        itemlist=findViewById(R.id.list);
        total=findViewById(R.id.total);
        narration=findViewById(R.id.narration);
        txtsupname = findViewById(R.id.txtsupname);
        txtsupadd = findViewById(R.id.txtsupadd);
        txtspcontact = findViewById(R.id.txtspcontact);
        txtpartybal = findViewById(R.id.txtpbal);
        txtglcode = findViewById(R.id.txtgrid);
        datechoice3=findViewById(R.id.datechoice3);
        txtbilldate = findViewById(R.id.billdate);
        txtbatchno = findViewById(R.id.txtbatchno);
        txtfeedstock= findViewById(R.id.txtfeedstk);
        txtbirdstock = findViewById(R.id.txtbirdstk);
        purchaseno = findViewById(R.id.purchaseno);
        invoiceno = findViewById(R.id.invoice);

        mContext = getApplicationContext();
        myDb = new DatabaseHelper(this);
        mDatabase = myDb.getWritableDatabase();
        if (!myDb.isTableExists("salestran", true)){
            myDb.CreateSaleTranTbl();
        }
        mDatabase = myDb.getWritableDatabase();
        mDatabase.beginTransaction();
        String sql1 = "Delete from salestran";
        mDatabase.execSQL(sql1);
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

        initToolbar();

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
        datechoice3.setOnClickListener(view -> new DatePickerDialog(FeedRequirement.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show());


        btnaddcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent partylist = new Intent(FeedRequirement.this, FarmListSp.class);
                creditledger.launch(partylist);

            }
        });

        btncancelpur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        btnadditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent feedprod = new Intent(FeedRequirement.this, FeedProducts.class);
                saleitem.launch(feedprod);

            }
        });
        btnsavepurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveFeedRequirement();
            }
        });

    }
    private void updateDate()
    {
        datechoice3.setText(nDateform.format(mycal.getTime()));
        txtbilldate.setText(dateform.format(mycal.getTime()));
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Feed Requirement");
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
                txtpartybal.setText("Feed Balance :" + feedstock.toString());

            }
        }
    });

    ActivityResultLauncher<Intent> saleitem = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                String pname = data.getStringExtra("ProductName");
                String pqty = data.getStringExtra("PQty");
                double ppqty = Double.parseDouble(pqty);
                sumptot += ppqty;

                datamodel.add(new feedreqmodel("", pname, pqty));

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                itemlist.setLayoutManager(mLayoutManager);
                itemlist.setItemAnimator(new DefaultItemAnimator());
                itemlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                feedreqadapter = new feedreqadapter(datamodel, getApplicationContext());

                itemlist.setAdapter(feedreqadapter);
                total.setText(Tools.amountpriceformat(sumptot));
                Log.e("TotalQt", String.valueOf(sumptot));

                mDatabase.beginTransaction();
                String mSql = "Insert Into salestran(tempvochno,vdate,ledgername,paddress,prodname,qty) values ('"+ TempVoucherNo+ "','"+
                        txtbilldate.getText().toString() +"','"+ txtsupname.getText().toString()+"','"+ txtsupadd.getText().toString()+"','"+
                        pname+"','"+pqty+"')";
                mDatabase.execSQL(mSql);
                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();

                feedreqadapter.setOnItemClickListener(new feedreqadapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, feedreqmodel obj, int position) {
                        final String prodnm =((TextView)view.findViewById(R.id.txtfprodname)).getText().toString();
                        final double ppqty1 = Double.parseDouble(((TextView)view.findViewById(R.id.txtfqty)).getText().toString());
                        sumptot -= ppqty1;

                        btndeleteitem = view.findViewById(R.id.imagedelete);
                        btndeleteitem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("ProductName", prodnm);
                                datamodel.remove(position);
                                mDatabase.beginTransaction();
                                String sql = "Delete from salestran where prodname ='" + prodnm +"' and tempvochno='" + TempVoucherNo + "'";
                                mDatabase.execSQL(sql);
                                mDatabase.setTransactionSuccessful();
                                mDatabase.endTransaction();
                                feedreqadapter.notifyDataSetChanged();
                                total.setText(Tools.amountpriceformat(sumptot));

                            }
                        });
                    }
                });
            }
        }
    });


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    private void SaveFeedRequirement(){
        if (!txtsupname.getText().toString().isEmpty()){
            if(feedreqadapter.getItemCount()!=0){
                double ntot = 0;
                double disamt=0;

                try {
                    ntot = Objects.requireNonNull(DecimalFormat.getNumberInstance().parse(total.getText().toString())).doubleValue();
                    //disamt = Objects.requireNonNull(DecimalFormat.getNumberInstance().parse(discount.getText().toString())).doubleValue();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String nt = String.valueOf(ntot);
                String dt = String.valueOf(disamt);

                Tools.showSimpleProgressDialog(this, "Saving Feed Requirement...","Please Wait ...",false);

                JSONObject jsot = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                JSONObject finalobject = new JSONObject();

                String sqlr = "Select tempvochno,vdate,ledgername,paddress,prodno,prodname," +
                        "mfgcomp,prodcategory,qty from salestran where tempvochno ='" + TempVoucherNo+"'";
                mDatabase.beginTransaction();
                Cursor c = mDatabase.rawQuery(sqlr, null);
                if (c.moveToFirst()){
                    while (!c.isAfterLast()){
                        jsot = new JSONObject();
                        try {

                            jsot.put("prodname", c.getString(5));
                            jsot.put("prodno", c.getString(4));
                            jsot.put("qty",c.getString(8));
                            jsot.put("tranid", TempVoucherNo);
                            jsot.put("vdate", txtbilldate.getText().toString());
                            jsot.put("addedby", spname);
                            jsot.put("farmcode", txtglcode.getText().toString());
                            jsot.put("farmname", txtsupname.getText().toString());
                            jsot.put("farmaddress", txtsupadd.getText().toString());
                            jsot.put("batchno", txtbatchno.getText().toString());
                            jsot.put("feedstock", txtfeedstock.getText().toString());
                            jsot.put("birdstock", txtbirdstock.getText().toString());
                            //jsot.put("narration", Objects.requireNonNull(narration.getText()).toString());
                            jsot.put("challanno", purchaseno.getText().toString());
                            jsot.put("challandate", txtbilldate.getText().toString());
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

                StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETSAVEFEEDREQVOUCHER,
                        response -> {
                            Log.e("Result", response.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.optString("Result").equals("Success")) {
                                    Tools.removeSimpleProgressDialog();
                                    JSONArray json = jsonObject.getJSONArray("Data");
                                    for (int i = 0; i < json.length(); i++) {
                                        JSONObject obj = json.getJSONObject(i);
                                        invoiceno.setText(obj.getString("voucherno"));
                                    }

                                    SendNoticifationMessage();
                                    btnsavepurchase.setVisibility(View.GONE);
                                    Toast.makeText(FeedRequirement.this, "Feed Requirement Invoice Save Successfully...", Toast.LENGTH_SHORT).show();
                                    psDialogMsg.showSuccessDialog("Feed Requirement Invoice Save Successfully...", getString(R.string.OkDialogTitle));

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
                        params.put("smtdata", jsonArray.toString());
                        Log.e("json object data:",jsonArray.toString());

                        return params;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(FeedRequirement.this);
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
                String msg = "Feed Requirement Farm Name :" + txtsupname.getText().toString() + "\n Address :" + txtsupadd.getText().toString() + "\n Total Qty : " + total.getText().toString() + " \n Added By : " + spname;
                TOPIC = "/topics/Agrosoft"; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = "Feed Requirement";
                NOTIFICATION_MESSAGE = msg;
                params.put("mtitle", NOTIFICATION_TITLE);
                params.put("mtext", NOTIFICATION_MESSAGE);
                params.put("useremail", spemail);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FeedRequirement.this);
        queue.add(stringRequest);
    }
}
