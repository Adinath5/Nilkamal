package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class FeedConsumptionEntry extends AppCompatActivity {

    TextView txtspname, txtspcode, txtfarmcode, txtfarmname,txtfarmbatch,txtfarmtype,txtfarmaddress,txtplacebird,txtplacedate,txtfdsp, txtfdcon,txtmortqt, txtmortperc,txtage,txtvochno,txtbalancebird;
    String spname, spcode,farmname,farmaddress,farmbatch,farmtype,farmcode, placedate, placebird, feedsuplbag,feedconsbag,mortqty,bsalqty,balbird,farmage,lastentryno;
    Button btncancle, btnnext, btnadditem;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ListView feedconlist;
    private SimpleAdapter prodadapter;
    String mortreasone;
    Spinner spmortreason;
    ArrayList<String> MortReasonList;
    Boolean FeedEntryExists = false;
    Boolean MortEntryExists = false;
    ArrayList<HashMap<String, String>> itmlist;
    public static final String JSON_ARRAY = "result";
    public static final String DATE_FORMAT = "yyyy-M-d";
    public static final String PREFS="PREFS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_consumption_entry);
        initToolbar();

        txtspcode = findViewById(R.id.txtspcode);
        txtspname = findViewById(R.id.txtspname);
        txtfarmcode = findViewById(R.id.txtglcode);
        txtfarmname = findViewById(R.id.txtfname);
        txtfarmaddress = findViewById(R.id.txtfadd);
        txtfarmtype = findViewById(R.id.txtfarmtype);
        txtfarmbatch = findViewById(R.id.txtfbatch);
        txtvochno = findViewById(R.id.txtvochno);
        txtmortqt = findViewById(R.id.txtmortqty);

        spmortreason = findViewById(R.id.spmortreason);

        btncancle = findViewById(R.id.btnfdcancle);
        btnnext = findViewById(R.id.btnfdnext);
        btnadditem = findViewById(R.id.btnadditemfd);
        feedconlist = findViewById(R.id.feedclist);
        MortReasonList=new ArrayList<>();

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        spcode = sharedPreferences.getString(Constants.KEY_USERID,null);
        spname = sharedPreferences.getString(Constants.KEY_USERNAME,null);
        farmcode =sharedPreferences.getString("farmcode",null);
        farmname = sharedPreferences.getString("farmname",null);
        farmaddress = sharedPreferences.getString("farmaddress",null);
        farmbatch = sharedPreferences.getString("farmbatch",null);
        lastentryno = sharedPreferences.getString("newvoucherno",null);
        farmtype = sharedPreferences.getString("farmtype",null);
        farmage = sharedPreferences.getString("farmage",null);

        txtspcode.setText(spcode);
        txtspname.setText(spname);
        txtfarmname.setText(farmname);
        txtfarmaddress.setText(farmaddress);
        txtfarmbatch.setText(farmbatch);
        txtfarmcode.setText(farmcode);
        txtvochno.setText("Voucher No : "+ lastentryno.toString());
        itmlist = new ArrayList<HashMap<String, String>>();
        getMortalityReason();
        CheckFeedEntryExists();
        checkMortalityEntry();

        btnadditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedselect = new Intent(FeedConsumptionEntry.this, feedconsitemselect.class);
                startActivityForResult(feedselect, 1);
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MortEntryExists){
                    Intent i = new Intent(FeedConsumptionEntry.this, FarmfeedEntryphoto.class);
                    startActivity(i);
                } else {
                    SaveMortlityEntry();
                    Intent i = new Intent(FeedConsumptionEntry.this, FarmfeedEntryphoto.class);
                    startActivity(i);
                }
            }
        });
        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Feed Consumption");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }
    @Override
    public void onBackPressed() {
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1) {
            if(resultCode == RESULT_OK){
                String prodno = data.getStringExtra("ProdNo");
                String pname = data.getStringExtra("ProdName");
                String pqty = data.getStringExtra("ProdQty");
                String prate = data.getStringExtra("ProdRate");
                String pamount = data.getStringExtra("ProdAmount");
                HashMap<String, String> productlist = new HashMap<>();
                productlist.put("A", pname);
                productlist.put("B", pamount);
                productlist.put("C", pqty);
                productlist.put("D", prate);
                productlist.put("E", prodno);
                //productlist.put("F", pqty.toString() + "x"+ prate.toString()+ "="+ pamount.toString());
                itmlist.add(productlist);

                prodadapter = new SimpleAdapter(FeedConsumptionEntry.this, itmlist, R.layout.feedcon_item_list, new String[]{"A", "B","C","D","E"}, new int[]{R.id.lblitemname, R.id.lblitemamount, R.id.lblitemqty,R.id.lblitemrate,R.id.lblprodno});
                feedconlist.setAdapter(prodadapter);
                setListViewHeightBasedOnChildren(feedconlist);
//                prodPopupWindow.dismiss();

            }
        }
    }

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


    private void getMortalityReason(){

        Tools.showSimpleProgressDialog(FeedConsumptionEntry.this, "Getting Mortality Reason Data...", "Please Wait ...", false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETMORTREASON,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("MortReason", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONArray j = jsonObject.getJSONArray("mortlist");
                                //JSONObject json = jsonObject.getJSONObject("spfarmbalancefeed");
                                for (int i = 0; i < j.length(); i++) {
                                    JSONObject json = j.getJSONObject(i);

                                    mortreasone = json.getString("mreason");
                                    Log.i("Mreasone", mortreasone.toString());
                                    MortReasonList.add(mortreasone);
                                }

                            } else if (jsonObject.optString("result").equals("failure")) {
                                Tools.removeSimpleProgressDialog();
                                Toast.makeText(FeedConsumptionEntry.this, "Mort Reason Data Not Found...", Toast.LENGTH_SHORT).show();
                            }
                            spmortreason.setAdapter(new ArrayAdapter<String>(FeedConsumptionEntry.this, android.R.layout.simple_spinner_dropdown_item, MortReasonList));
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FeedConsumptionEntry.this);
        queue.add(stringRequest);
    }

    private void CheckFeedEntryExists() {

        Tools.showSimpleProgressDialog(FeedConsumptionEntry.this, "Checking Feed Entry...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETFEEDCONENTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CheckFeedEntry", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONArray j = jsonObject.getJSONArray("feedconlist");
                                //JSONObject json = jsonObject.getJSONObject("spfarmbalancefeed");
                                for (int i = 0; i < j.length(); i++) {
                                    JSONObject json = j.getJSONObject(i);

                                    String prodno = json.getString("fprodno");
                                    String pname = json.getString("fprodname");
                                    String pqty = json.getString("fconqty");
                                    String prate = json.getString("fconrate");
                                    String pamount = json.getString("fconamount");
                                    HashMap<String, String> productlist = new HashMap<>();
                                    productlist.put("A", pname.toString());
                                    productlist.put("B", pamount.toString());
                                    productlist.put("C", pqty.toString());
                                    productlist.put("D", prate.toString());
                                    productlist.put("E", prodno.toString());
                                    //productlist.put("F", pqty.toString() + "x"+ prate.toString()+ "="+ pamount.toString());
                                    itmlist.add(productlist);
                                }
                                prodadapter = new SimpleAdapter(FeedConsumptionEntry.this, itmlist, R.layout.feedcon_item_list, new String[]{"A", "B", "C", "D", "E"}, new int[]{R.id.lblitemname, R.id.lblitemamount, R.id.lblitemqty, R.id.lblitemrate, R.id.lblprodno});
                                feedconlist.setAdapter(prodadapter);
                                setListViewHeightBasedOnChildren(feedconlist);
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
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("farmcode", farmcode);
                params.put("farmbatch", farmbatch);
                params.put("vochno", lastentryno);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FeedConsumptionEntry.this);
        queue.add(stringRequest);

    }

    private void checkMortalityEntry(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETCHICKSMORTENTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("CheckMortEntry", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONArray j = jsonObject.getJSONArray("mortmlist");
                                for (int i = 0; i < j.length(); i++) {
                                    JSONObject json = j.getJSONObject(i);
                                    String mortreason = json.getString("mortreason");
                                    String mortqty = json.getString("bmortqty");
                                    txtmortqt.setText(mortqty);
                                    MortEntryExists = true;
                                }
                            } else if (jsonObject.optString("result").equals("failure")) {
                                Tools.removeSimpleProgressDialog();
                                Toast.makeText(FeedConsumptionEntry.this, "Mort Reason Data Not Found...", Toast.LENGTH_SHORT).show();
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
                        error.printStackTrace();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("farmcode", farmcode);
                params.put("farmbatch", farmbatch);
                params.put("vochno", lastentryno);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FeedConsumptionEntry.this);
        queue.add(stringRequest);
    }

    private void SaveMortlityEntry(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_SAVEMORTENTRY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("SaveMortEntry", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                Toast.makeText(FeedConsumptionEntry.this, "Mortality Entry Save Successfully...", Toast.LENGTH_SHORT).show();

                            } else if (jsonObject.optString("result").equals("failure")) {
                                Tools.removeSimpleProgressDialog();
                                Toast.makeText(FeedConsumptionEntry.this, "Mortality Entry Not Save...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                error -> {
                    Tools.removeSimpleProgressDialog();
                    error.printStackTrace();
                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("farmcode", farmcode);
                params.put("farmbatch", farmbatch);
                params.put("vochno", lastentryno);
                params.put("farmname", farmname);
                params.put("farmaddress", farmaddress);
                params.put("spname", spname);
                params.put("farmtype", farmtype);
                params.put("farmage", farmage);
                params.put("mortreason", spmortreason.getSelectedItem().toString());
                params.put("mortqty", txtmortqt.getText().toString());
                params.put("spcode", spcode);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FeedConsumptionEntry.this);
        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
