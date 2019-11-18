package com.atharvainfo.nilkamal.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Activity.AdminMain;
import com.atharvainfo.nilkamal.Activity.LoginActivity;
import com.atharvainfo.nilkamal.Activity.ManagerMain;
import com.atharvainfo.nilkamal.Activity.SplashActivity;
import com.atharvainfo.nilkamal.Activity.SupervisorMain;
import com.atharvainfo.nilkamal.Activity.rpt_accountstatement;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TabFragment1 extends Fragment {
    private View view;
    Button btnaddparty;
    EditText txtseachparty;

    ArrayList<HashMap<String, String>> ldgrlist;
    private SimpleAdapter adapter;
    private ListView partylist;

    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String PREFS="PREFS";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_fragment1, container, false);
        btnaddparty = view.findViewById(R.id.btnaddparty);

        ldgrlist = new ArrayList<HashMap<String, String>>();
        partylist = view.findViewById(R.id.partylist);
        txtseachparty = view.findViewById(R.id.txtseachparty);

        btnaddparty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        getPartyListAll();

        txtseachparty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = txtseachparty.getText().toString().toLowerCase(Locale.getDefault());
                adapter.getFilter().filter(text);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }


    private void getPartyListAll() {
        Tools.showSimpleProgressDialog(getContext(), "Getting Partis...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETALLLEDGERLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                Tools.removeSimpleProgressDialog();
                                JSONArray json = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject obj = json.getJSONObject(i);
                                    //loginUserId = cursor.getString(cursor.getColumnIndex("username"));
                                    // if (obj.getString("amount") != "0"){
                                    double clbal = 0;
                                    String drcr = "Dr";
                                    String clamt = obj.getString("amount");
                                    if (!clamt.isEmpty()) {
                                        clbal = Double.parseDouble(obj.getString("amount"));
                                    }
                                    if (clbal >= 0){
                                        drcr = "Dr";
                                    } else {
                                        drcr = "Cr";
                                    }
                                    HashMap<String, String> parties = new HashMap<>();
                                    parties.put("A", obj.getString("sledgername"));
                                    parties.put("B", String.valueOf(NumberFormat.getInstance().format(Math.abs(clbal)))+ " "+ drcr);
                                    parties.put("C", obj.getString("grid"));
                                    parties.put("D", obj.getString("glcode"));
                                    ldgrlist.add(parties);

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter = new SimpleAdapter(getActivity(), ldgrlist, R.layout.partylist, new String[]{"A", "B", "C","D"}, new int[]{R.id.txtpname, R.id.txtamount,R.id.txtgrid,R.id.txtglcode});
                        partylist.setAdapter(null);
                        partylist.setAdapter(adapter);

                        partylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String agrid = ((TextView)view.findViewById(R.id.txtglcode)).getText().toString();

                                sharedPreferences = getActivity().getSharedPreferences(PREFS, MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("grid", agrid);
                                editor.commit();
                                Log.e("glcode", agrid);

                                Intent i = new Intent(getActivity(), rpt_accountstatement.class);
                                startActivity(i);

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
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);
    }

}
