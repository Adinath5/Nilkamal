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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;


public class SupervisorList extends AppCompatActivity {

    private JSONArray result;
    public static final String JSON_ARRAY = "result";

    ProgressDialog progressDialog;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    AutoCompleteTextView edit_search;
    LinearLayout empty_view;

    String spname,empname,spcode;
    TextView txtspcode, txtspname;
    ListView spnamelist;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> listspname;
    private SimpleAdapter adapter;
    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;
    public static final String PREFS="PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_list);

        listspname = new ArrayList<HashMap<String, String>>();

        initToolbar();
       spnamelist = (ListView) findViewById(R.id.listsupervisor);

       new getSupervisorList().execute();

       spnamelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String spcode =((TextView)view.findViewById(R.id.txtspcode)).getText().toString();
               String spname = ((TextView)view.findViewById(R.id.txtspname)).getText().toString();

               sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
               editor = sp.edit();
               editor.putString("supervisorcode", spcode);
               editor.putString("supervisorname", spname);
               editor.commit();

               Intent i = new Intent(SupervisorList.this, PathGoogleMapActivity.class);
               startActivity(i);

           }
       });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Supervisor List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    private class getSupervisorList extends AsyncTask<Void, Void, String>{


        protected void onPreExecute(){
            showSimpleProgressDialog(SupervisorList.this, "Signing Up...","Please Wait ...",false);
        }

        @Override
        protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_GETALLSPLIST);
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

    }

    public void onTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        if (serviceCode == jsoncode) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("result").equals("success")) {
                    removeSimpleProgressDialog();
                    JSONArray json = jsonObject.getJSONArray("listspname");
                    result = jsonObject.getJSONArray("listspname");
                    getRecord(result);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getRecord(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);

                HashMap<String, String> parties = new HashMap<>();
                parties.put("A", json.getString("username"));
                parties.put("B", json.getString("empname"));
                parties.put("C", json.getString("contact"));
                parties.put("D", json.getString("emailid"));
                listspname.add(parties);
               // System.out.print(listspname);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter = new SimpleAdapter(getApplicationContext(), listspname, R.layout.supervisorlst, new String[]{"A", "B", "C", "D"}, new int[]{R.id.txtspcode, R.id.txtspname, R.id.txtspcontact,R.id.txtspemail});
        spnamelist.setAdapter(null);
        spnamelist.setAdapter(adapter);


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
