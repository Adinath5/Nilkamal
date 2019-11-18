package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class rpt_spdailyvisit extends AppCompatActivity {

   TextView sdate;
    Button btnshow;
    DatePickerDialog.OnDateSetListener date;
    private Toolbar toolbar;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> ldgrlist;
    private SimpleAdapter vladapter;
    private ListView visitflist;
    private Context mContext;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    String vdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_spdailyvisit);

        visitflist = findViewById(R.id.visitfarmlist);
        sdate = findViewById(R.id.sdate);
        btnshow = findViewById(R.id.btnshow);
        mContext = getApplicationContext();

        Long currentdate=System.currentTimeMillis();
        String datestring=dateform.format(currentdate);
        sdate.setText(datestring);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ldgrlist = new ArrayList<HashMap<String, String>>();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate();

            }
        };
        sdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(rpt_spdailyvisit.this,date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnshow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFarmVisitList();
            }
        });
        initToolbar();
    }

    private void updateDate()
    {
        sdate.setText(dateform.format(mycal.getTime()));
    }

    private void initToolbar() {

        Objects.requireNonNull(getSupportActionBar()).setTitle("Supervisor Daily Visit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    private void getFarmVisitList(){

        vdt = sdate.getText().toString();
        Log.i("Date", vdt);
        //showSimpleProgressDialog(getApplicationContext(), "Getting Farm Visit Data...","Please Wait ...",false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_FARMVISITLISTADMIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                removeSimpleProgressDialog();
                                JSONArray json = jsonObject.getJSONArray("spfarmvlist");
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject obj = json.getJSONObject(i);
                                    HashMap<String, String> parties = new HashMap<>();
                                    parties.put("A", obj.getString("farmname"));
                                    parties.put("B", obj.getString("farmaddress"));
                                    parties.put("C", obj.getString("farmcode"));
                                    parties.put("D", obj.getString("batchno"));
                                    parties.put("E", obj.getString("farmtype"));
                                    parties.put("F", obj.getString("farmage"));
                                    parties.put("G", "Supervisor : "+ obj.getString("spname"));
                                    parties.put("H", "F.Cons.Qty : " +obj.getString("fconqt"));
                                    parties.put("I", "Mort.Qty. : " + obj.getString("bmortqt"));
                                    parties.put("J", "Pl.Dt. : " + obj.getString("pldate"));
                                    parties.put("K", "Bal.Bird : " + obj.getString("pqty"));
                                    ldgrlist.add(parties);
                                    //}
                                    System.out.println(ldgrlist.toString());
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        vladapter = new SimpleAdapter(getApplicationContext(), ldgrlist, R.layout.spvisitfarmlist, new String[]{"A", "B", "C","D", "E","F","G","H","I","J","K"}, new int[]{R.id.txtpname, R.id.txtfadd,R.id.txtglcoe,R.id.txtbatch,
                                R.id.txtfarmtype,R.id.txtqty,R.id.txtspname, R.id.txtfedcons,R.id.txtmqt,R.id.txtpdate,R.id.txtblqt});
                        visitflist.setAdapter(null);
                        visitflist.setAdapter(vladapter);

                        visitflist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        });

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("vdate",vdt);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(rpt_spdailyvisit.this);
        queue.add(stringRequest);
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
        } catch (Exception ie) {
            ie.printStackTrace();
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
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
