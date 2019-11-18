package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class NewPartyActivity extends AppCompatActivity {

    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;
    private ArrayAdapter<String> adapter;

    CardView cardview1, cardview2, cardView3, cardView4,cardView5;
    Button btnaddopebal, btnaddcontact, btncontact, btnaddaddress, btnsaveac, btnsaveparty, btncancle,btnaddother;
    TextView txtaccountname,txtopeningbalance,txtemailid,txtphoneno,txtcrdays,txtaddress1,txtaddress2,txtcountry,txtpincode,txtgstno,txtcity;
    Spinner spactype, spdrcr,spstatename,sproutename;
    private static final int PICK_CONTACT = 1000;
    DatabaseHelper dbHelper = null;
    SQLiteDatabase mdatabase;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private PSDialogMsg psDialogMsg;
    private Toolbar toolbar;
    boolean checkaccount = false;
    private String type[] ={"Retail","HoleSale","Special"};
    AutoCompleteTextView autocompletype,autocompleteroute;
    private ArrayAdapter<String> routeadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_party);
        dbHelper = new DatabaseHelper(this);
        psDialogMsg = new PSDialogMsg(this, false);

        toolbar = findViewById(R.id.toolbarpd);
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initToolbar();

        cardview1 = findViewById(R.id.card_view1);
        cardview2 = findViewById(R.id.card_view2);
        cardView3 = findViewById(R.id.card_view3);
        cardView4 = findViewById(R.id.card_view4);
        cardView5 = findViewById(R.id.card_view5);

        spactype = findViewById(R.id.spactype);

        spdrcr = findViewById(R.id.spdrcr);
        spstatename = findViewById(R.id.spstatename);
        autocompleteroute = findViewById(R.id.txtroutename);
        autocompletype = findViewById(R.id.txtsaletype);
        btnaddopebal = findViewById(R.id.btnaddopening);
        btnaddcontact = findViewById(R.id.btnaddcontact);
        btncontact = findViewById(R.id.btncontact);
        btnaddaddress = findViewById(R.id.btnaddaddress);
        btnaddother = findViewById(R.id.btnaddother);
        //btnsaveac = findViewById(R.id.btnsave);
        btnsaveparty = findViewById(R.id.btnsaveparty);
        btncancle = findViewById(R.id.btncancle);


        txtaccountname = findViewById(R.id.txtaccountname);
        txtopeningbalance = findViewById(R.id.txtopeningbalance);
        txtemailid = findViewById(R.id.txtemalid);
        txtgstno=findViewById(R.id.txtgstin);
        txtphoneno = findViewById(R.id.txtphoneno);
        txtcrdays = findViewById(R.id.txtcrdays);
        txtaddress1 = findViewById(R.id.txtaddress1);
        txtaddress2 = findViewById(R.id.txtaddress2);
        txtcountry = findViewById(R.id.txtcountry);
        txtpincode = findViewById(R.id.txtzipcode);
        txtcity=findViewById(R.id.txtcity);

        cardview2.setVisibility(View.GONE);
        cardView3.setVisibility(View.GONE);
        cardView4.setVisibility(View.GONE);
        cardView5.setVisibility(View.GONE);
        btnaddcontact.setVisibility(View.GONE);
        btnaddaddress.setVisibility(View.GONE);
        btnaddother.setVisibility(View.GONE);
        txtopeningbalance.setText("0");

        adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, type);
        //lv.setAdapter(adapter);
        autocompletype.setThreshold(2);
        autocompletype.setAdapter(adapter);
        getRouteNameList();
        autocompleteroute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                getRouteNameList();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {




            }
        });


        btnaddopebal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardview2.setVisibility(View.VISIBLE);
                btnaddopebal.setVisibility(View.GONE);
            }
        });

        btncontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            }
        });

        btnaddcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView3.setVisibility(View.VISIBLE);
                btnaddcontact.setVisibility(View.GONE);
            }
        });

        btnaddaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView4.setVisibility(View.VISIBLE);
                btnaddaddress.setVisibility(View.GONE);
            }
        });

        btnaddother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView5.setVisibility(View.VISIBLE);
                btnaddother.setVisibility(View.GONE);

            }
        });
        loadSpinnerData();

        System.out.println("Current time =&gt; "+c.getTime());

        spactype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String label = spactype.getSelectedItem().toString();

                switch(label){
                    case "Sundry Debtors":
                        btnaddcontact.setVisibility(View.VISIBLE);
                        btnaddaddress.setVisibility(View.VISIBLE);
                        btnaddother.setVisibility(View.VISIBLE);
                        break;
                    case "Sundry Creditors":
                        btnaddcontact.setVisibility(View.VISIBLE);
                        btnaddaddress.setVisibility(View.VISIBLE);
                        btnaddother.setVisibility(View.VISIBLE);
                        break;
                    case "Cash-in-hand":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);

                        break;
                    case "Bank A/Cs":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Purchase A/C":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Purchase Return":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Sales A/C":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Sales Return":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Capital A/C":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Direct Expenses":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Indirect Expenses":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Direct Income":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Indirect Income":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Current Assets":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Current Liabilities":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Misc. Expenses":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Misc. Income":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Loans (Liability)":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Loans & Advances":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Fixed Assets":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Investments":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Bank OD A/C":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Deposits (Assets)":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Provisions":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Reserves & Surplus":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                    case "Duties & Taxes":
                        btnaddcontact.setVisibility(View.GONE);
                        btnaddaddress.setVisibility(View.GONE);
                        btnaddother.setVisibility(View.GONE);
                        cardView3.setVisibility(View.GONE);
                        cardView4.setVisibility(View.GONE);
                        cardView5.setVisibility(View.GONE);
                        break;
                }


                /*if (spactype.getSelectedItem().toString() != "Sundry Creditors") {
                    btnaddcontact.setVisibility(View.GONE);
                    btnaddaddress.setVisibility(View.GONE);

                }
                if (spactype.getSelectedItem().toString() == "Sundry Debtors"){

                    btnaddcontact.setVisibility(View.VISIBLE);
                    btnaddaddress.setVisibility(View.VISIBLE);
                }
                if (spactype.getSelectedItem().toString() == "Sundry Creditors"){

                    btnaddcontact.setVisibility(View.VISIBLE);
                    btnaddaddress.setVisibility(View.VISIBLE);
                }*/

                // Showing selected spinner item
                Toast.makeText(parent.getContext(), label,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        btnsaveparty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acname = txtaccountname.getText().toString();

                if(acname.isEmpty()){
                    txtaccountname.setError("Account Name is Not Blank");
                }else {
                    SaveAccount();
                }
            }
        });

        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                //super.onBackPressed();
                /*int backStackEntry = getActivity().getSupportFragmentManager().getBackStackEntryCount();
                if (backStackEntry > 0) {
                    for (int i = 0; i < backStackEntry; i++) {
                       getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                }*/
                //getActivity().getFragmentManager().beginTransaction().remove(this).commit();

            }
        });

    }

    private void initToolbar() {

        getSupportActionBar().setTitle("New Party");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        //String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        //String ContactNo = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //txtaccountname.setText(name);
                        //txtphoneno.setText(ContactNo);
                        String id =
                                c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone =
                                c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,
                                    null, null);
                            phones.moveToFirst();
                            String phn_no = phones.getString(phones.getColumnIndex("data1"));
                            String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.DISPLAY_NAME));
                            Toast.makeText(this, "contact info : "+ phn_no+"\n"+name, Toast.LENGTH_LONG).show();
                            txtaccountname.setText(name);
                            txtphoneno.setText(phn_no);
                        }
                    }
                }
                break;
        }
    }

    private void loadSpinnerData() {


        List<String> list = new ArrayList<String>();
        list.add("Cash-in-hand");
        list.add("Bank A/Cs");
        list.add("Sundry Debtors");
        list.add("Sundry Creditors");
        list.add("Purchase A/C");
        list.add("Purchase Return");
        list.add("Sales A/C");
        list.add("Sales Return");
        list.add("Capital A/C");
        list.add("Direct Expenses");
        list.add("Indirect Expenses");
        list.add("Direct Income");
        list.add("Indirect Income");
        list.add("Current Assets");
        list.add("Current Liabilities");
        list.add("Misc. Expenses");
        list.add("Misc. Income");
        list.add("Loans (Liability)");
        list.add("Loans & Advances");
        list.add("Fixed Assets");
        list.add("Investments");
        list.add("Bank OD A/C");
        list.add("Deposits (Assets)");
        list.add("Provisions");
        list.add("Reserves & Surplus");
        list.add("Duties & Taxes");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spactype.setAdapter(dataAdapter);
    }


    private void SaveAccount(){
        checkaccountExists();

        if(checkaccount == false) {

            showSimpleProgressDialog(this, "Saving New Party...","Please Wait ...",false);

            new AsyncTask<Void, Void, String>(){
                protected String doInBackground(Void[] params) {
                    String response="";
                    HashMap<String, String> map=new HashMap<>();
                    map.put("sledgername",txtaccountname.getText().toString());
                    map.put("acname",spactype.getSelectedItem().toString());
                    map.put("sladdress",txtaddress1.getText().toString());
                    map.put("taluka",txtaddress2.getText().toString());
                    map.put("openingamt",txtopeningbalance.getText().toString());
                    map.put("spdrcr",spdrcr.getSelectedItem().toString());
                    map.put("phoneno",txtphoneno.getText().toString());
                    map.put("emailid1",txtemailid.getText().toString());
                    map.put("dueperiod",txtcrdays.getText().toString());
                    map.put("countryname",txtcountry.getText().toString());
                    map.put("statename",spstatename.getSelectedItem().toString());
                    map.put("pincode",txtpincode.getText().toString());
                    map.put("gsttinno",txtgstno.getText().toString());
                    map.put("sltype",autocompletype.getText().toString());
                    map.put("city",txtcity.getText().toString());
                    map.put("routname",autocompleteroute.getText().toString());

                    try {
                        HttpRequest req = new HttpRequest(myConfig.URL_SAVENEWPARTY);
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
                        onSaveTaskCompleted(result,jsoncode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.execute();


        } else {

            psDialogMsg.showErrorDialog(getString(R.string.accountnameexist), getString(R.string.OkDialogTitle));
            psDialogMsg.show();
            return;
        }
    }

    public void onSaveTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());

        final List<String> flist = new ArrayList<String>();
        switch (serviceCode) {
            case jsoncode:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("Result").equals("Success")) {
                        removeSimpleProgressDialog();
                        //Message.message(getActivity(), "Account Save Successful "+ String.valueOf(gcd));

                        psDialogMsg.showSuccessDialog(getString(R.string.accountsavesuccess), getString(R.string.OkDialogTitle));
                        psDialogMsg.show();
                        txtaccountname.setText("");
                        txtaddress1.setText("");
                        txtaddress2.setText("");
                        txtopeningbalance.setText("");
                        txtphoneno.setText("");
                        txtemailid.setText("");
                        txtcrdays.setText("");
                        txtpincode.setText("");
                        autocompleteroute.setText("");
                        autocompletype.setText("");
                        txtcity.setText("");
                        txtgstno.setText("");


                    } else {
                        removeSimpleProgressDialog();
                        psDialogMsg.showErrorDialog(getString(R.string.somethingwentwrong), getString(R.string.OkDialogTitle));
                        psDialogMsg.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }


    private void checkaccountExists(){

        // showSimpleProgressDialog(this, "Saving New Party...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("sledgername",txtaccountname.getText().toString());
                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_CHECKACCOUNTEXIST);
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
                    onAccountExistCompleted(result,jsoncode);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }


    public void onAccountExistCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());


        switch (serviceCode) {
            case jsoncode:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("Result").equals("success")) {
                        removeSimpleProgressDialog();
                        checkaccount= true;
                    } else {
                        removeSimpleProgressDialog();
                        Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                        checkaccount = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }

    }

    private void getRouteNameList(){

        // showSimpleProgressDialog(this, "Getting Company List...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_ROUTENAMELIST);
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
                    onRouteNameListTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();


    }



    public void onRouteNameListTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());

        final List<String> flist = new ArrayList<String>();
        switch (serviceCode) {
            case jsoncode:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("Result").equals("success")) {
                        removeSimpleProgressDialog();

                        JSONArray json = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < json.length(); i++) {
                            JSONObject obj = json.getJSONObject(i);
                            //Ventryno = obj.getString("vochno");
                            flist.add(obj.getString("rout"));
                        }
                        ArrayAdapter<String> inewitemadapter;
                        routeadapter = new ArrayAdapter<String>(NewPartyActivity.this, android.R.layout.select_dialog_item, flist);
                        autocompleteroute.setAdapter(routeadapter);

                    } else {
                        removeSimpleProgressDialog();
                        Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
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

}
