package com.atharvainfo.nilkamal.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Adapter.aclistadapter;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.accmodel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SaleAccountList extends AppCompatActivity {
    ArrayList<accmodel> datamodel;
    private Toolbar actool;
    RecyclerView ledgerlist;
    aclistadapter aclistadapter;
    ArrayList<HashMap<String, String>> ldgrlist;
    public static final String PREFS="PREFS";
    SharedPreferences sp;
    String FyStartDate,FyEndDate;
    Button btnaddac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_account_list);

        ledgerlist = findViewById(R.id.paclist);
        actool = findViewById(R.id.toolbarp);
        btnaddac = findViewById(R.id.btnaddac);

        sp=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        FyStartDate = sp.getString(Constants.COMPANY_FYSTARTDATE, null);
        FyEndDate = sp.getString(Constants.COMPANY_FYENDDATE, null);

        Log.e("sdate", FyStartDate);

        datamodel = new ArrayList<>();

        setSupportActionBar(actool);

        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initToolbar();

        ldgrlist = new ArrayList<>();
        getAlllegerList();

        btnaddac.setOnClickListener(v -> {

        });

    }

    private void getAlllegerList(){
        Tools.showSimpleProgressDialog(this, "Getting Ledger List...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETLEDGERNAMELIST,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("Result").equals("success")) {
                            Tools.removeSimpleProgressDialog();
                            JSONArray json = jsonObject.getJSONArray("Data");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject obj = json.getJSONObject(i);
                                String opamt = obj.getString("clbal");
                                datamodel.add(new accmodel(obj.getString("glcode"),obj.getString("sledgername"),obj.getString("sladdress"),
                                        "",obj.getString("sltype"),obj.getString("amount"),obj.getString("taluka"),obj.getString("city"),
                                        obj.getString("contactperson"),obj.getString("phoneno"),obj.getString("emailid1"),obj.getString("contact"),
                                        obj.getString("statename"),obj.getString("panno"),obj.getString("aclimit"),obj.getString("routname"),
                                        obj.getString("dueperiod"),obj.getString("acstatus"),obj.getString("gsttinno"),obj.getString("clbal"),
                                        obj.getString("drcr1")));
                            }
                            Log.e("LedgerList", datamodel.toString());
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            ledgerlist.setLayoutManager(mLayoutManager);
                            ledgerlist.setItemAnimator(new DefaultItemAnimator());
                            //ledgerlist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                            aclistadapter = new aclistadapter(datamodel, getApplicationContext());

                            ledgerlist.setAdapter(aclistadapter);

                            aclistadapter.setOnItemClickListener((view, obj, position) -> {
                                String ledgername = ((TextView)view.findViewById(R.id.txtpname)).getText().toString();
                                String address =((TextView)view.findViewById(R.id.txtsladdress)).getText().toString();
                                String glcode=((TextView)view.findViewById(R.id.txtglcode)).getText().toString();
                                String contactno =((TextView)view.findViewById(R.id.txtcontactno)).getText().toString();
                                String taluka =((TextView)view.findViewById(R.id.txttaluke)).getText().toString();
                                String city =((TextView)view.findViewById(R.id.txtcity)).getText().toString();
                                String clbal =((TextView)view.findViewById(R.id.txtamount)).getText().toString();
                                String baltype =((TextView)view.findViewById(R.id.txtbaltype)).getText().toString();

                                Intent intent = new Intent();
                                intent.putExtra("PartyName", ledgername);
                                intent.putExtra("PartyBalance", clbal + " "+ baltype);
                                intent.putExtra("grid", glcode);
                                intent.putExtra("address", address);
                                intent.putExtra("phoneno", contactno);
                                intent.putExtra("taluka", taluka);
                                intent.putExtra("city", city);
                                setResult(RESULT_OK, intent);
                                finish();


                            });
                        } else {
                            Tools.removeSimpleProgressDialog();
                            Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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
                params.put("sdate", FyStartDate);
                params.put("edate", FyEndDate);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(SaleAccountList.this);
        queue.add(stringRequest);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchmenu, menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    private void filter(String text) {
        ArrayList<accmodel> filteredlist = new ArrayList<>();
        for (accmodel item : datamodel) {
            if (item.getSledgername().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
              aclistadapter.filterList(filteredlist);
        }
    }

    private void initToolbar() {

        Objects.requireNonNull(getSupportActionBar()).setTitle("Accounts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
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
