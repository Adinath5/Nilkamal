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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.layout.simple_spinner_item;
import static java.lang.String.valueOf;

public class SupplyClearance extends Fragment {
    private View view;
    Spinner name;
    TextView datechoice1,duedate10,actualdate10,duedate6,actualdate6,duedate4,actualdate4,duedate2,actualdate2,duedate1,actualdate1,duedate0,actualdate0;
    EditText address,lastbatchno,aggrementdatefrom,aggrementdateto,batchcapacity,supervisorname,newbatchno;
    DatePickerDialog.OnDateSetListener date1;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    private String jsonURL = "http://103.127.29.138/nilkamal/api.php?apicall=getFarmName";
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
        duedate2=view.findViewById(R.id.txtduedate3);
        actualdate2=view.findViewById(R.id.txtactualdate3);
        duedate1=view.findViewById(R.id.txtduedate4);
        actualdate1=view.findViewById(R.id.txtactualdate4);
        duedate0=view.findViewById(R.id.txtduedate5);
        actualdate0=view.findViewById(R.id.txtactualdate5);
       // name.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        FarmArrayList = new ArrayList<>();
        farmlist = new ArrayList<List<String>>();

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


        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date
        String CurrentDate = mYear + "/" + mMonth + "/" + mDay;

        String dateInString = datechoice1.toString(); // Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        mycal = Calendar.getInstance();

        try {
            mycal.setTime(sdf.parse(dateInString));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mycal.add(Calendar.DATE, 10);//insert the number of days you want to be added to the current date
        sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date resultdate = new Date(c.getTimeInMillis());
        dateInString = sdf.format(resultdate);

        //Display the Result in the Edit Text or Text View your Choice
       // EditText etDOR = (EditText)findViewById(R.id.etDateOfReturn);
        duedate10.setText(dateInString);


        fetchJSON();

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
                    name.setAdapter(spinnerArrayAdapter);

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
