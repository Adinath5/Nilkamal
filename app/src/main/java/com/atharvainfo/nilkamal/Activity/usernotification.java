package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.Constants;
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

public class usernotification extends AppCompatActivity {

    private ListView msglist;
    private SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> ldgrlist;
    SharedPreferences sp;
    public static final String PREFS="PREFS";
    String useremail,userphone, userid,username;
    private static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usernotification);

        msglist = findViewById(R.id.paclist);
        Toolbar actool = findViewById(R.id.toolbarp);
        setSupportActionBar(actool);

        Objects.requireNonNull(this.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initToolbar();

        ldgrlist = new ArrayList<HashMap<String, String>>();

        sp=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        sp.edit();
        userid = sp.getString(Constants.KEY_USERID, null);
        useremail = sp.getString(Constants.KEY_USEREMAIL, null);
        username = sp.getString(Constants.KEY_USERNAME, null);

        getNotification();

    }

    private void getNotification() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETUSERNOTIFICATION,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("Result").equals("success")) {
                            double crbal=0;
                            String drcr = "Dr";
                            JSONArray json = jsonObject.getJSONArray("Data");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject obj = json.getJSONObject(i);
                                HashMap<String, String> parties = new HashMap<>();
                                parties.put("A", obj.getString("mtext"));
                                parties.put("B", obj.getString("isread"));
                                ldgrlist.add(parties);
                                crbal=0;
                            }

                            System.out.println(ldgrlist.toString());
                            adapter = new SimpleAdapter(usernotification.this, ldgrlist, R.layout.notilist, new String[]{"A", "B"},
                                    new int[]{R.id.txtpname, R.id.txtisread});
                            msglist.setAdapter(null);
                            msglist.setAdapter(adapter);

                            msglist.setOnItemClickListener((parent, view, position, id) -> {
                                String partyname =((TextView)view.findViewById(R.id.txtpname)).getText().toString();
                                String pbal = ((TextView)view.findViewById(R.id.txtamount)).getText().toString();
                                String grid = ((TextView)view.findViewById(R.id.txtgrid)).getText().toString();

                             });

                        } else {
                            Toast.makeText(usernotification.this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emailid", useremail);
                Log.e("PostUserid", useremail);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(usernotification.this);
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

    private void initToolbar() {

        Objects.requireNonNull(getSupportActionBar()).setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
}