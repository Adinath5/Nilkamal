package com.atharvainfo.nilkamal.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.atharvainfo.nilkamal.Adapter.FarmMast;
import com.atharvainfo.nilkamal.Others.ApiInterface;
import com.atharvainfo.nilkamal.Others.ConnectionDetector;
import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.FileHelper;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.PrefManager;
import com.atharvainfo.nilkamal.Others.ServerResponse;
import com.atharvainfo.nilkamal.Others.UserData;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginFragement extends Fragment {

    private View view;
    TextView voucherno;
    private Spinner ledger,scheme;
    EditText txtusername, txtpassword;
    Button btnlogin;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    public static final String BASE_URL = "http://103.127.29.138:8080/nilkamal/";
    private String jsonURL = "http://103.127.29.138:8080/nilkamal/api.php?apicall=postUserLogin";
    private ArrayList<FarmMast> FarmArrayList;
    private final int jsoncode = 1;
    ArrayAdapter<String> adapter;
    ArrayList<List<String>> farmlist;
    final static String fileName = "nilkamal.txt";
    final static String TAG = FileHelper.class.getName();
    private static ProgressDialog mProgressDialog;
    private ArrayList<UserData> UserArrayList;
    String[] stringArray = null;
    List<String> UserDt = new ArrayList<String>();
    String username,userpass;
    SQLiteDatabase mdatabase;
    ConnectionDetector connectivity;
    DatabaseHelper helper;
    private PSDialogMsg psDialogMsg;
    PrefManager pref;


    // List<String> itemList = new ArrayList<String>();


    public LoginFragement() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_login_fragement, container, false);

        txtusername = view.findViewById(R.id.nameEditText);
        txtpassword = view.findViewById(R.id.passwordEditText);
        btnlogin = view.findViewById(R.id.registerButton);
        connectivity = new ConnectionDetector(getActivity());
        psDialogMsg = new PSDialogMsg(getActivity(), false);
        helper = new DatabaseHelper(getActivity());
        // Inflate the layout for this fragment
        UserArrayList = new ArrayList<>();
        UserDt = new ArrayList<String>();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = txtusername.getText().toString();
                userpass = txtpassword.getText().toString();

                if (connectivity.isConnectingToInternet()){
                    if (username.equals("")) {
                        psDialogMsg.showWarningDialog(getString(R.string.error_message__blank_username), getString(R.string.app__ok));
                        psDialogMsg.show();
                        return;
                    }

                    if (userpass.equals("")) {

                        psDialogMsg.showWarningDialog(getString(R.string.error_message__blank_password), getString(R.string.app__ok));
                        psDialogMsg.show();
                        return;
                    }

                        fetchJSON();

                }else {

                    psDialogMsg.showWarningDialog(getString(R.string.no_internet_error), getString(R.string.app__ok));

                    psDialogMsg.show();

                }

            }
        });

        return view;
    }

    public void doLogin(){



    }

    @SuppressLint("StaticFieldLeak")
    private void fetchJSON(){

        username = txtusername.getText().toString();
        userpass = txtpassword.getText().toString();

        Log.i("UserName", username);
        Log.i("Pasdword", userpass);
        showSimpleProgressDialog(getContext(), "Signing Up...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("username", username);
                map.put("password", userpass);
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
//                    pref.setUserName(username.toString());
                    UserArrayList = getInfo(response);

                    long id = insertuserData(UserDt.get(0).toString(), userpass.toString(),UserDt.get(4).toString(),UserDt.get(3).toString(),UserDt.get(5).toString());

                    if (id <= 0) {
                        Message.message(getActivity(), "Insertion Unsuccessful");
                    } else {

                        saveToFile(username.toString() + "," + userpass.toString());

                        Message.message(getActivity(), "Login Successful");
                    }

                    getActivity().finish();


                }else {
                    Toast.makeText(getContext(), getErrorCode(response), Toast.LENGTH_SHORT).show();
                }
        }
    }


    public ArrayList<UserData> getInfo(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("result").equals("success")) {
                JSONArray dataArray = jsonObject.getJSONArray("farmerlist");


                for (int i = 0; i < dataArray.length(); i++) {
                    // itemList.set(i, " " + String.valueOf(i + 1));
                    // itemList[i] = "Item " + String.valueOf(i + 1);
                    UserData userData = new UserData();

                    JSONObject dataobj = dataArray.getJSONObject(i);
                    userData.setUsername(dataobj.getString("username"));
                    userData.setAddress(dataobj.getString("address"));
                    userData.setEmpname(dataobj.getString("empname"));
                    userData.setPhone(dataobj.getString("phone"));
                    userData.setUsertp(dataobj.getString("usertp"));
                    userData.setEmailid(dataobj.getString("emailid"));
                    //UserDt.add(i, dataobj.getString("username"));
                    UserDt.add(dataobj.getString("username"));
                    UserDt.add(dataobj.getString("address"));
                    UserDt.add(dataobj.getString("empname"));
                    UserDt.add(dataobj.getString("phone"));
                    UserDt.add(dataobj.getString("usertp"));
                    UserDt.add(dataobj.getString("emailid"));

                }


                Log.i("PrintList", UserArrayList.toString());
                removeSimpleProgressDialog();  //will remove progress dialog
            } else if (jsonObject.getString("result").equals("failure")) {
                removeSimpleProgressDialog();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return UserArrayList;
    }

    public long insertuserData(String username, String pass, String Desig,String Phone, String Email)
    {
//        mdatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
        //SQLiteDatabase db = helper.getWritableDatabase();
        helper.openDatabase();
        helper.close();
        mdatabase = helper.getWritableDatabase();
        String[] whereArgs ={username};

        ContentValues contentValues = new ContentValues();
        contentValues.put("userid", "1");
        contentValues.put("username", username);
        contentValues.put("password", pass);
        contentValues.put("designation", Desig);
        contentValues.put("contact", Phone);
        contentValues.put("emailid", Email);


        long id = mdatabase.insert("usermast", null , contentValues);
        return id;

    }

    public boolean saveToFile(String data){
        try {

            File file = new File(getActivity().getFilesDir().getAbsolutePath()+ fileName);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);
            fileOutputStream.write((data + System.getProperty("line.separator")).getBytes());

            Log.i("FileCreated", data.toString());
            System.out.print("Files Created Successfully..");
            return true;
        }  catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return  false;


    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
