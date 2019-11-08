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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.layout.simple_spinner_item;

public class CashReceipt extends Fragment {
    private View view;
    TextView voucherno;
   private Spinner ledger,scheme;
   EditText address,amount,chequeno,bankname,narration;
    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener date1;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    TextView datechoice3,datechoice4;
    String[] scheme1 = { "On Account", "Bill to Bill"};
    private String jsonURL = "http://103.127.29.138/nilkamal/api.php?apicall=getLedgerName";
    private ArrayList<FarmMast> FarmArrayList;
    private final int jsoncode = 1;
    ArrayAdapter<String> adapter;
    ArrayList<List<String>> farmlist;
    private static ProgressDialog mProgressDialog;
    List<String> fmlist;
    // List<String> itemList = new ArrayList<String>();

    List<String> itemList = new ArrayList<String>();
    Map<String, String> spinnerValueMap = new HashMap<String, String>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cash_receipt, container, false);
        voucherno=view.findViewById(R.id.voucherno);
        ledger=(Spinner)view.findViewById(R.id.ledger);
        address=view.findViewById(R.id.edtaddress);
        amount=view.findViewById(R.id.amount1);
        chequeno=view.findViewById(R.id.check);
        bankname=view.findViewById(R.id.bank);
        scheme=view.findViewById(R.id.scheme);
        narration=view.findViewById(R.id.narration);
        datechoice3=view.findViewById(R.id.datechoice3);
        datechoice4=view.findViewById(R.id.datechoice4);

        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,scheme1);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scheme.setAdapter(aa);

        Long currentdate=System.currentTimeMillis();
        String datestring=dateform.format(currentdate);
        datechoice3.setText(datestring);
        datechoice4.setText(datestring);
        FarmArrayList = new ArrayList<>();
        farmlist = new ArrayList<List<String>>();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate();


            }
        };
        date1=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate1();


            }
        };

        datechoice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();


            }
        });
        datechoice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date1,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        fetchJSON();
        return view;
    }

    private void updateDate()
    {
        datechoice3.setText(dateform.format(mycal.getTime()));

    }
    private void updateDate1() {

        datechoice4.setText(dateform.format(mycal.getTime()));
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
                    /// adapter = new SimpleAdapter(getContext(), ldgrlist, R.layout.list_updt, new String[]{"A", "B", "C","D","E","F","G","H","I"}, new int[]{R.id.txtvoterfullname, R.id.txtvoteragegen, R.id.txtvoterdetail,R.id.txtvotingdate,R.id.txtvotermobile,R.id.txtvoteruniqid,R.id.textvtpl,R.id.txtvtmsg,R.id.txtvsign});
                    // avoterlist.setAdapter(null);
                    // avoterlist.setAdapter(adapter);

                    // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                    // android.R.layout.simple_spinner_item, fmlist);

                    // dataAdapter
                    // .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //  name.setAdapter(dataAdapter);
                    //  ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                    //   android.R.layout.simple_spinner_dropdown_item, itemList);

                    // spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //  name.setAdapter(spinnerAdapter);

                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), simple_spinner_item, itemList);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    ledger.setAdapter(spinnerArrayAdapter);


                    //eAdapter.setClickListener(this);

                }else {
                    Toast.makeText(getContext(), getErrorCode(response), Toast.LENGTH_SHORT).show();
                }
        }
    }

    public ArrayList<FarmMast> getInfo(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equals("success")) {
                JSONArray dataArray = jsonObject.getJSONArray("farmerlist");


                for (int i = 0; i < dataArray.length(); i++) {
                    // itemList.set(i, " " + String.valueOf(i + 1));
                    // itemList[i] = "Item " + String.valueOf(i + 1);
                    FarmMast farmnameList = new FarmMast();

                    JSONObject dataobj = dataArray.getJSONObject(i);
                    //  itemList= Arrays.asList(getActivity().getResources().getStringArray(Integer.parseInt(String.valueOf(dataobj.getString("farmname")))));
                    // itemList = Collections.singletonList(dataobj.getString("farmname"));
                    String name=dataobj.getString("farmname");
                    //  itemList.add(String.valueOf(i + 1));
                    itemList.add(name);

                    // itemList.add(String.valueOf(i + 1));
                    //  HashMap<String, String> user = new HashMap<>();
                    // user.put("A", dataobj.getString("farmname"));
                    //   farmlist.add(user);
                    // itemList.add(String.valueOf(i + 1));

                 /* for (Map.Entry<String, String> entry : spinnerValueMap.entrySet()) {
                        String key = dataobj.getString("farmname");
                        String value = entry.getValue();

                        /* Build the StringWithTag List using these keys and values. */
                    // itemList.add(valueOf(i + 1));
                    //  }*/


                    // return id;

                }
                for (int i = 0; i < farmlist.size(); i++){
                    itemList.add(farmlist.get(i).toString());
                }

                Log.i("PrintList", FarmArrayList.toString());
                removeSimpleProgressDialog();  //will remove progress dialog
            } else if (jsonObject.getString("result").equals("failure")) {
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
