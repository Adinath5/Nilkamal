package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.MainActivity;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class FarmEnquiry extends AppCompatActivity {

    Button btncontact, btncancle, btnsavefarm;
    private static final int PICK_CONTACT = 1000;
    TextView txtfarmname, txtfarmername, txtvillage,txttaluka,txtdistrict,txtcontact,txtwidth,txtcapcity,txtprevrec,txtdetails;
    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String spname;
    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_enquiry);

        txtfarmname = findViewById(R.id.txtfarmname);
        txtfarmername = findViewById(R.id.txtfarmername);
        txtcontact = findViewById(R.id.txtcontact);
        txtvillage = findViewById(R.id.txtvillage);
        txttaluka = findViewById(R.id.txttaluka);
        txtdistrict =findViewById(R.id.txtdistrict);
        txtwidth = findViewById(R.id.txtwidth);
        txtcapcity = findViewById(R.id.txtcapacity);
        txtprevrec = findViewById(R.id.txtprevcomp);
        txtdetails = findViewById(R.id.txtdetails);
        btncancle = findViewById(R.id.btncancle);
        btnsavefarm = findViewById(R.id.btnsavefarm);

        sharedPreferences=getApplicationContext().getSharedPreferences("Mydata",MODE_PRIVATE);
        sharedPreferences.edit();
        spname = sharedPreferences.getString("empname",null);

        initToolbar();


        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FarmEnquiry.this, SupervisorMain.class);
                startActivity(i);
                finish();
            }
        });

        btnsavefarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String farmname = txtfarmname.getText().toString();
                String farmername = txtfarmername.getText().toString();
                String contact = txtcontact.getText().toString();

                if (!farmname.isEmpty() && !farmername.isEmpty() && !contact.isEmpty()){
                    SaveNewFarmEnquiry();
                } else {
                     txtfarmername.setError("Farmer Name is Not Empty!");
                }
            }
        });
    }


    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("New Farm Enquiry");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(FarmEnquiry.this, SupervisorMain.class);
        startActivity(i);
        finish();
    }


    private void SaveNewFarmEnquiry(){
        final String farmname = txtfarmname.getText().toString();
        final String farmername = txtfarmername.getText().toString();
        final String village = txtvillage.getText().toString();
        final String taluka = txttaluka.getText().toString();
        final String district = txtdistrict.getText().toString();
        final String contact = txtcontact.getText().toString();
        final String wl = txtwidth.getText().toString();
        final String farmcap = txtcapcity.getText().toString();
        final String prvcomp = txtprevrec.getText().toString();
        final String detail = txtdetails.getText().toString();


        Log.i("farmname", farmname);
        showSimpleProgressDialog(this, "Registring...","Please Wait ...",false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_NEWFARMENQ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("Result").equals("success")) {
                                removeSimpleProgressDialog();
                                SendNoticifationMessage();
                                Toast.makeText(FarmEnquiry.this, "New Farm Enquiry Save Successfully...", Toast.LENGTH_SHORT).show();
                            }  else {
                                removeSimpleProgressDialog();
                                Toast.makeText(FarmEnquiry.this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("farmname", farmname);
                params.put("farmername", farmername);
                params.put("village", village);
                params.put("taluka", taluka);
                params.put("district", district);
                params.put("contact", contact);
                params.put("wl", wl);
                params.put("farmcap", farmcap);
                params.put("prvcomp", prvcomp);
                params.put("detail", detail);
                params.put("spname", spname);
                Log.e("PostUserid", spname);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FarmEnquiry.this);
        queue.add(stringRequest);


    }

    public boolean isSuccess(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("Result").equals("success")) {
                removeSimpleProgressDialog();
                return true;
            }else {
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

    private void SendNoticifationMessage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_SENDFARMNOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                removeSimpleProgressDialog();

                            } else if (jsonObject.optString("result").equals("failure")) {
                                removeSimpleProgressDialog();
                                Toast.makeText(getApplicationContext(), "No Such Customer Name Found", Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
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
                Map<String, String> params=new HashMap<>();
                final String farmname = txtfarmname.getText().toString();
                final String farmername = txtfarmername.getText().toString();
                final String village = txtvillage.getText().toString();
                final String taluka = txttaluka.getText().toString();
                final String district = txtdistrict.getText().toString();
                final String contact = txtcontact.getText().toString();
                final String wl = txtwidth.getText().toString();
                final String farmcap = txtcapcity.getText().toString();
                final String prvcomp = txtprevrec.getText().toString();
                final String detail = txtdetails.getText().toString();

                String msg = "Farm Name :" + farmname + "\n Farmer Name :" + farmername + "\n At Post : " + village + " Taluka : " + taluka + "\n Farm Capacity : " + farmcap + " Contact No : " + contact + "\n Visit By : " + spname;
                TOPIC = "/topics/Nilkamal"; //topic must match with what the receiver subscribed to
                NOTIFICATION_TITLE = "New Farm Enquiry By " + spname;
                NOTIFICATION_MESSAGE = msg;
                params.put("mtitle", NOTIFICATION_TITLE);
                params.put("mtext", NOTIFICATION_MESSAGE);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(FarmEnquiry.this);
        queue.add(stringRequest);
    }
}
