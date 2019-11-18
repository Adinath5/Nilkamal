package com.atharvainfo.nilkamal.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Activity.rpt_receivablepayable;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FarmHistorySummayFragment extends Fragment {

    private View view;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static ProgressDialog mProgressDialog;
    public static final String JSON_ARRAY = "result";
    Context context;
    String spname, spcode,farmname,farmaddress,farmbatch,farmtype,farmcode, placedate, placebird, feedsuplbag,feedconsbag,mortqty,bsalqty,balbird,farmage,lastentryno;
    TextView txtspname, txtspcode, txtfarmcode, txtfarmname,txtfarmbatch,txtfarmtype,txtfarmaddress,txtplacebird,txtplacedate,txtfdsp, txtfdcon,txtmortqt, txtmortperc,txtage,txtvochno,txtbalancebird;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private PSDialogMsg psDialogMsg;
    public static final String DATE_FORMAT = "yyyy-M-d";
    public static final String PREFS = "PREFS";

    public FarmHistorySummayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_farm_history_summay, container, false);

        sharedPreferences= requireActivity().getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        sharedPreferences.edit();
        spcode = sharedPreferences.getString("user_name",null);
        spname = sharedPreferences.getString("empname",null);
        farmcode =sharedPreferences.getString("farmcode",null);
        farmbatch = sharedPreferences.getString("farmbatch",null);


        txtspcode = view.findViewById(R.id.txtspcode);
        txtspname = view.findViewById(R.id.txtspname);
        txtfarmcode = view.findViewById(R.id.txtglcode);
        txtfarmname = view.findViewById(R.id.txtfname);
        txtfarmaddress = view.findViewById(R.id.txtfadd);
        txtfarmtype = view.findViewById(R.id.txtfarmtype);
        txtfarmbatch = view.findViewById(R.id.txtfbatch);
        txtvochno = view.findViewById(R.id.txtvochno);

        txtplacebird = view.findViewById(R.id.txtplacebird);
        txtplacedate = view.findViewById(R.id.txtplacedate);
        txtfdsp = view.findViewById(R.id.txtfeedsuplb);
        txtfdcon = view.findViewById(R.id.txtfeedcons);
        txtmortqt = view.findViewById(R.id.txtmortqty);
        txtmortperc = view.findViewById(R.id.txtmortperc);
        txtage = view.findViewById(R.id.txtage);
        txtbalancebird = view.findViewById(R.id.txtbalancebird);

        getFarmPurchaseBirdDetail(farmcode, farmbatch);
        getFarmFeedMortDetail(farmcode, farmbatch);

        // Inflate the layout for this fragment
        return view;
    }

    public static FarmHistorySummayFragment newInstance(String text){
        FarmHistorySummayFragment f = new FarmHistorySummayFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
    private void getFarmFeedMortDetail(String farmcode, String farmbatch) {

        Log.i("spname", farmcode);

        showSimpleProgressDialog(getActivity(), "Getting Summary...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETFARMDETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                removeSimpleProgressDialog();
                                JSONObject json = jsonObject.getJSONObject("spfarmerdetailtwo");

                                feedsuplbag = json.getString("feedsuplbag");
                                bsalqty = json.getString("bsalqty");
                                placebird = json.getString("bplaceqty");
                                feedconsbag = json.getString("feedconbag");
                                mortqty = json.getString("bmortqty");
                                txtfdsp.setText(feedsuplbag);
                                txtfdcon.setText(feedconsbag);
                                txtmortqt.setText(mortqty);
                                double pbq = Double.parseDouble(placebird);
                                double bsl = Double.parseDouble(bsalqty);
                                double bmq = Double.parseDouble(mortqty);
                                double balb = (pbq)-((bsl)+(bmq));
                                double mp= (((bmq)*100)/(pbq));
                                DecimalFormat precision = new DecimalFormat("0.00");
                                txtbalancebird.setText(String.valueOf(balb));
                                txtmortperc.setText(precision.format(mp));

                                Calendar c = Calendar.getInstance();
                                System.out.println("Current time => "+c.getTime());
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String formattedDate = df.format(c.getTime());

                                long bage = getDaysBetweenDates(placedate, formattedDate);
                                bage = bage+1;
                                farmage = String.valueOf(bage);
                                txtage.setText(String.valueOf(bage));

                            } else if (jsonObject.optString("result").equals("failure")){
                                removeSimpleProgressDialog();
                                Toast.makeText(getActivity(), "Farm Data Not Found...", Toast.LENGTH_SHORT).show();
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
                params.put("batchno", farmbatch);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);


    }

    private void getFarmPurchaseBirdDetail(String farmcode, String farmbatch) {

        Log.i("spname", farmcode);

        showSimpleProgressDialog(getActivity(), "Getting Data...","Please Wait ...",false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETFARMBIRDSUPPLY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("result").equals("success")) {
                                removeSimpleProgressDialog();
                                JSONObject json = jsonObject.getJSONObject("spfarmerdetailone");

                                farmtype = json.getString("farmtype");
                                placedate = json.getString("vdate");
                                placebird = json.getString("pqty");
                                txtplacedate.setText(placedate);
                                txtplacebird.setText(placebird);

                            } else if (jsonObject.optString("result").equals("failure")){
                                removeSimpleProgressDialog();
                                Toast.makeText(getActivity(), "Farm Data Not Found...", Toast.LENGTH_SHORT).show();
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
                params.put("batchno", farmbatch);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
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

    public static long getDaysBetweenDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }
}
