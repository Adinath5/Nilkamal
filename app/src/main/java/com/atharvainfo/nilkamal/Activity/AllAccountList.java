package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AllAccountList extends AppCompatActivity {

    private ListView partylist;
    private Toolbar actool;
    private PSDialogMsg psDialogMsg;
    private SimpleAdapter prodadapter;
    private SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> ldgrlist;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    String dateformat="dd-MM-yyyy";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    Button btnaddac;
    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_account_list);

        partylist = findViewById(R.id.paclist);
        actool = findViewById(R.id.toolbarp);
        btnaddac = findViewById(R.id.btnaddac);

        setSupportActionBar(actool);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initToolbar();

        psDialogMsg = new PSDialogMsg(this, false);
        ldgrlist = new ArrayList<HashMap<String, String>>();

        getPartyListAll();


        partylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String partyname =((TextView)view.findViewById(R.id.txtpname)).getText().toString();
                String pbal = ((TextView)view.findViewById(R.id.txtamount)).getText().toString();
                String glcode = ((TextView)view.findViewById(R.id.txtglcode)).getText().toString();

                Intent intent = new Intent();
                intent.putExtra("PartyName", partyname.toString());
                intent.putExtra("PartyBalance", pbal.toString());
                intent.putExtra("grid", glcode.toString());
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        btnaddac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent partylist = new Intent(AllAccountList.this, NewPartyActivity.class);
                //startActivity(partylist);
                //finish();
            }
        });

    }

    private void getPartyListAll(){

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                //map.put("username", loginUserId);

                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_GETALLACCOUNTLIST);
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
                    onTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();


    }

    public void onTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();
                        JSONArray json = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            //loginUserId = cursor.getString(cursor.getColumnIndex("username"));
                            // if (obj.getString("amount") != "0"){
                            double clbal = 0;
                            String drcr = "Dr";
                            String clamt = obj.getString("amount");
                            if (clamt != null && !clamt.isEmpty()) {
                                clbal = Double.valueOf(obj.getString("amount"));
                            }
                            if (clbal >= 0){
                                drcr = "Dr";
                            } else {
                                drcr = "Cr";
                            }
                            HashMap<String, String> parties = new HashMap<>();
                            parties.put("A", obj.getString("sledgername"));
                            parties.put("B", String.valueOf(NumberFormat.getInstance().format(Math.abs(clbal)))+ " "+ drcr);
                            parties.put("C", "");
                            parties.put("D", obj.getString("glcode"));
                            ldgrlist.add(parties);

                            //}
                            //System.out.println(ldgrlist.toString());
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter = new SimpleAdapter(AllAccountList.this, ldgrlist, R.layout.partylist, new String[]{"A", "B", "C","D"}, new int[]{R.id.txtpname, R.id.txtamount,R.id.txtgrid,R.id.txtglcode});
                partylist.setAdapter(null);
                partylist.setAdapter(adapter);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchmenu, menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void initToolbar() {

        getSupportActionBar().setTitle("Accounts");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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