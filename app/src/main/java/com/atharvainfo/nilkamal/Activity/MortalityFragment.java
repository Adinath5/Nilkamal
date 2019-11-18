package com.atharvainfo.nilkamal.Activity;


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
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MortalityFragment extends Fragment {

    private View view;
    WebView webView;
    Context context;
    String spname, spcode,farmname,farmaddress,farmbatch,farmtype,farmcode, placedate, placebird, feedsuplbag,feedconsbag,mortqty,bsalqty,balbird,farmage,lastentryno;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> ldgrlist;
    private SimpleAdapter mortadapter;
    private ListView mortalitylist;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private final int jsoncode = 1;
    private static ProgressDialog mProgressDialog;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";

    public MortalityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mortality, container, false);

        mortalitylist = view.findViewById(R.id.mortlist);

        sharedPreferences=getActivity().getApplicationContext().getSharedPreferences("Mydata",MODE_PRIVATE);
        sharedPreferences.edit();
        spcode = sharedPreferences.getString("user_name",null);
        spname = sharedPreferences.getString("empname",null);
        farmcode =sharedPreferences.getString("farmcode",null);
        farmbatch = sharedPreferences.getString("farmbatch",null);
        ldgrlist = new ArrayList<HashMap<String, String>>();

        getMortalityDetail();


        // Inflate the layout for this fragment
        return view;
    }




    private void getMortalityDetail(){

        //userpass = txtpassword.getText().toString();


        showSimpleProgressDialog(getContext(), "Getting Mortality Data...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("farmcode", farmcode);
                map.put("farmbatch", farmbatch);

                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_FARMMORTLISTADMIN);
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
                double mtot = 0;
                double mtotal =0;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();
                        JSONArray json = jsonObject.getJSONArray("farmmortlist");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            mtot = Double.valueOf(obj.getString("bmortqty"));
                            mtotal = mtotal+ mtot;

                            HashMap<String, String> parties = new HashMap<>();
                            parties.put("A", obj.getString("vdate"));
                            parties.put("B", obj.getString("farmage"));
                            parties.put("C", obj.getString("mortreason"));
                            parties.put("D", obj.getString("bmortqty"));
                            parties.put("E", String.valueOf(mtotal));
                            mtot=0;
                            ldgrlist.add(parties);

                            //}
                            System.out.println(ldgrlist.toString());
                        }


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mortadapter = new SimpleAdapter(getContext(), ldgrlist, R.layout.mortality_historylist, new String[]{"A", "B", "C","D", "E"}, new int[]{R.id.txtdate, R.id.txtage,R.id.txtreason,R.id.txtquantity,R.id.txttotal});
                mortalitylist.setAdapter(null);
                mortalitylist.setAdapter(mortadapter);


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
