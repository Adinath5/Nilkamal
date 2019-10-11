package com.atharvainfo.nilkamal.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Adapter.FarmMast;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class SupplyClearance extends Fragment {
    private View view;
    Spinner name;
    TextView datechoice1,duedate10,actualdate10,duedate6,actualdate6,duedate4,actualdate4,duedate2;
    EditText address,lastbatchno,aggrementdatefrom,aggrementdateto,batchcapacity,supervisorname,newbatchno;
    DatePickerDialog.OnDateSetListener date1;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    private String jsonURL = "http://www.atharvainfosolutions.com/nilkamal/api.php?apicall=getFarmName";
    private ArrayList<FarmMast> FarmArrayList;
    private final int jsoncode = 1;
    ArrayList<HashMap<String, String>> farmlist;
    private static ProgressDialog mProgressDialog;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.supply_clearance, container, false);

        name=(Spinner) view.findViewById(R.id.farmname) ;
        datechoice1=view.findViewById(R.id.datechoice3);
        address=view.findViewById(R.id.income);
        lastbatchno=view.findViewById(R.id.lastbatchno);
        aggrementdatefrom=view.findViewById(R.id.aggrementdatefrom);
        aggrementdateto=view.findViewById(R.id.aggrementdateto);
        batchcapacity=view.findViewById(R.id.batchcapacity);
        supervisorname=view.findViewById(R.id.supervisorname);
        newbatchno=view.findViewById(R.id.newbatchno);
        duedate10=view.findViewById(R.id.txtduedate);
        actualdate10=view.findViewById(R.id.txtactualdate);
        duedate6=view.findViewById(R.id.txtduedate1);
        actualdate6=view.findViewById(R.id.txtactualdate1);
        duedate4=view.findViewById(R.id.txtduedate2);
        actualdate4=view.findViewById(R.id.txtactualdate2);
        duedate2=view.findViewById(R.id.duedate4);

        FarmArrayList = new ArrayList<>();
        farmlist = new ArrayList<HashMap<String, String>>();

        Long currentdate=System.currentTimeMillis();
        String datestring=dateform.format(currentdate);
       // datechoice.setText(datestring);
        datechoice1.setText(datestring);

        date1=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate1();


            }
        };
        datechoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date1,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });



        return view;
    }

    private void updateDate1() {


        datechoice1.setText(dateform.format(mycal.getTime()));
    }


    @SuppressLint("StaticFieldLeak")
    private void fetchJSON(){

        showSimpleProgressDialog(getContext(), "Loading...","Downloading Farm List",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                try {
                    HttpRequest req = new HttpRequest(jsonURL);
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

    @SuppressLint("WrongConstant")
    public void onTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:

                if (isSuccess(response)) {

                    Log.d("ResponseList", response);
                    FarmArrayList = getInfo(response);
                    adapter = new SimpleAdapter(getContext(), ldgrlist, R.layout.list_updt, new String[]{"A", "B", "C","D","E","F","G","H","I"}, new int[]{R.id.txtvoterfullname, R.id.txtvoteragegen, R.id.txtvoterdetail,R.id.txtvotingdate,R.id.txtvotermobile,R.id.txtvoteruniqid,R.id.textvtpl,R.id.txtvtmsg,R.id.txtvsign});
                    avoterlist.setAdapter(null);
                    avoterlist.setAdapter(adapter);


                    //eAdapter.setClickListener(this);

                }else {
                    Toast.makeText(getContext(), getErrorCode(response), Toast.LENGTH_SHORT).show();
                }
        }
    }

    public ArrayList<FarmMast> getInfo(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("status").equals("true")) {
                JSONArray dataArray = jsonObject.getJSONArray("farmerlist");


                for (int i = 0; i < dataArray.length(); i++) {
                    FarmMast farmnameList = new FarmMast();

                    JSONObject dataobj = dataArray.getJSONObject(i);
                    HashMap<String, String> user = new HashMap<>();
                    user.put("A", dataobj.getString("farmname"));
                    farmlist.add(user);

                    // return id;

                }
                Log.i("PrintList", FarmArrayList.toString());
                removeSimpleProgressDialog();  //will remove progress dialog
            } else if (jsonObject.getString("status").equals("false")) {
                removeSimpleProgressDialog();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return FarmArrayList;
    }


    public boolean isSuccess(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("status").equals("true")) {
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

    private JSONArray getJSonData(String fileName){
        JSONArray jsonArray=null;
        try {
            InputStream is = getResources().getAssets().open(fileName);
            int size = is.available();
            byte[] data = new byte[size];
            is.read(data);
            is.close();
            String json = new String(data, "UTF-8");
            jsonArray=new JSONArray(json);
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException je){
            je.printStackTrace();
        }
        return jsonArray;
    }
}
