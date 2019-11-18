package com.atharvainfo.nilkamal.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Activity.rpt_cashbook;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment3 extends Fragment {

    private View view;
    EditText txtseachprod;
    Button btnaddproduct;
    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> itemlist;
    private SimpleAdapter adapter;
    private ListView prodlist;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;
    public static final String PREFS="PREFS";

    public TabFragment3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_fragment3, container, false);

        btnaddproduct = view.findViewById(R.id.btnaddproduct);
        psDialogMsg = new PSDialogMsg(getActivity(), false);
        itemlist = new ArrayList<HashMap<String, String>>();
        prodlist = view.findViewById(R.id.prodlist);

        txtseachprod = view.findViewById(R.id.txtseachprod);

        getProductListData();

        btnaddproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent i = new Intent(getActivity(), NewProductActivity.class);
               // startActivity(i);
                //finish();
            }
        });

        txtseachprod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String text = txtseachprod.getText().toString().toLowerCase(Locale.getDefault());
                adapter.getFilter().filter(text);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void getProductListData(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETPRODUCTLISTALL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("Result").equals("success")) {
                                removeSimpleProgressDialog();
                                JSONArray json = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < json.length(); i++) {
                                    JSONObject obj = json.getJSONObject(i);

                                    HashMap<String, String> parties = new HashMap<>();
                                    parties.put("A", obj.getString("prodname"));
                                    parties.put("B", obj.getString("prodtype"));
                                    parties.put("C", obj.getString("prate"));
                                    parties.put("D", obj.getString("crate"));
                                    parties.put("E", obj.getString("prodno"));
                                    itemlist.add(parties);
                                }
                                System.out.println(itemlist.toString());
                                //Intent feedconsentry = new Intent(FarmHistoy.this, FeedConsumptionEntry.class);
                                //startActivity(feedconsentry);


                            } else if (jsonObject.optString("result").equals("failure")) {
                                removeSimpleProgressDialog();
                                Toast.makeText(getActivity(), "Item Data Not Found...", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter = new SimpleAdapter(getContext(), itemlist, R.layout.itemlist, new String[]{"A", "B", "C", "D", "E"}, new int[]{R.id.txtpname, R.id.txtcatname, R.id.txtprate, R.id.txtsrate, R.id.txtgrid});
                        prodlist.setAdapter(null);
                        prodlist.setAdapter(adapter);

                        prodlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String agrid = ((TextView) view.findViewById(R.id.txtgrid)).getText().toString();
                                String pname = ((TextView) view.findViewById(R.id.txtpname)).getText().toString();

                                sharedPreferences = requireActivity().getSharedPreferences(PREFS, MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("grid", agrid);
                                editor.putString("prodname", pname);
                                editor.commit();
                                // Intent i = new Intent(getActivity(), activity_sale_purchase_itemlist.class);
                                //  startActivity(i);

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
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);

    }

    public void onEntryExists(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        if (serviceCode == jsoncode) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("Result").equals("success")) {
                    removeSimpleProgressDialog();
                    JSONArray json = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);

                        HashMap<String, String> parties = new HashMap<>();
                        parties.put("A", obj.getString("prodname"));
                        parties.put("B", obj.getString("prodtype"));
                        parties.put("C", obj.getString("prate"));
                        parties.put("D", obj.getString("crate"));
                        parties.put("E", obj.getString("prodno"));
                        itemlist.add(parties);
                    }
                    System.out.println(itemlist.toString());
                    //Intent feedconsentry = new Intent(FarmHistoy.this, FeedConsumptionEntry.class);
                    //startActivity(feedconsentry);


                } else if (jsonObject.optString("result").equals("failure")) {
                    removeSimpleProgressDialog();
                    Toast.makeText(getActivity(), "Item Data Not Found...", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter = new SimpleAdapter(getContext(), itemlist, R.layout.itemlist, new String[]{"A", "B", "C", "D", "E"}, new int[]{R.id.txtpname, R.id.txtcatname, R.id.txtprate, R.id.txtsrate, R.id.txtgrid});
            prodlist.setAdapter(null);
            prodlist.setAdapter(adapter);

            prodlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String agrid = ((TextView) view.findViewById(R.id.txtgrid)).getText().toString();
                    String pname = ((TextView) view.findViewById(R.id.txtpname)).getText().toString();

                    sharedPreferences = requireActivity().getSharedPreferences(PREFS, MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putString("grid", agrid);
                    editor.putString("prodname", pname);
                    editor.commit();
                    // Intent i = new Intent(getActivity(), activity_sale_purchase_itemlist.class);
                    //  startActivity(i);

                }
            });
        }
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
}
