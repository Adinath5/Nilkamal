package com.atharvainfo.nilkamal.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity {

    private Button btnregister,btnsendotp;
    private EditText txtname, txtemailid, txtpassword,txtmobileno,txtotp;
    private CheckBox agricheck;
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    Matcher matcher;
    SharedPreferences sp;
    SharedPreferences.Editor edit;
    public static final String PREFS="PREFS";
    private final int jsoncode = 1;
    String username, useremail,userpass,usermobile;
    String musername, muserid, muserrole;
    private FirebaseAuth mAuth;
    TextInputLayout lyotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        txtname = findViewById(R.id.name);
        txtemailid = findViewById(R.id.email);
        txtpassword = findViewById(R.id.password);
        txtmobileno = findViewById(R.id.mobileno);
        txtotp = findViewById(R.id.txtotp);

        agricheck = findViewById(R.id.checkBox);
        btnregister = findViewById(R.id.button);
        btnsendotp = findViewById(R.id.buttonsendtp);
        lyotp = findViewById(R.id.layoutotp);
        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        edit = sp.edit();
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        btnsendotp.setEnabled(false);

        final TextInputLayout txtusernameWrapper = (TextInputLayout) findViewById(R.id.layoutName);
        final TextInputLayout txtuseremailWrapper = (TextInputLayout) findViewById(R.id.layoutEmail);
        final TextInputLayout txtusermobileno = (TextInputLayout) findViewById(R.id.layoutmobileno);
        final TextInputLayout txtuserpassWrapper = (TextInputLayout) findViewById(R.id.layoutPassword);

        btnregister.setEnabled(false);

        agricheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agricheck.isChecked()){
                    btnregister.setVisibility(View.VISIBLE);
                    btnregister.setEnabled(true);
                }
            }
        });
        btnsendotp.setOnClickListener(v -> {
            usermobile = "91"+txtmobileno.getText().toString();
            username = txtname.getText().toString();
            useremail = txtemailid.getText().toString();
            userpass = txtpassword.getText().toString();
            if(isValidPhone(usermobile)){
                txtusermobileno.setError("Not a Valid Mobile Number!");
            } else if(validatePassword(userpass)){
                txtuserpassWrapper.setError("Not a Valid Password");
            } else if(username.isEmpty()){
                txtusernameWrapper.setError("Name is Not Blank");
            }else if(!useremail.trim().matches(EMAIL_PATTERN)){
                txtuseremailWrapper.setError("Invalid Email ID");
            }
            else {
                txtuseremailWrapper.setErrorEnabled(false);
                txtusernameWrapper.setErrorEnabled(false);
                txtuserpassWrapper.setErrorEnabled(false);
                txtusermobileno.setErrorEnabled(false);
                new getConfirmOtp().execute();
            }
        });

        btnregister.setOnClickListener(v -> {

            username = txtname.getText().toString();
            useremail = txtemailid.getText().toString();
            userpass = txtpassword.getText().toString();
            usermobile = "91"+txtmobileno.getText().toString();

            if(isValidPhone(usermobile)){
                txtusermobileno.setError("Not a Valid Mobile Number!");
            } else if(validatePassword(userpass)){
                txtuserpassWrapper.setError("Not a Valid Password");
            } else if(username.isEmpty()){
                txtusernameWrapper.setError("Name is Not Blank");
            }else if(!useremail.trim().matches(EMAIL_PATTERN)){
                txtuseremailWrapper.setError("Invalid Email ID");
            }
            else {
                txtuseremailWrapper.setErrorEnabled(false);
                txtusernameWrapper.setErrorEnabled(false);
                txtuserpassWrapper.setErrorEnabled(false);
                txtusermobileno.setErrorEnabled(false);
                doLogin();
                lyotp.setVisibility(View.VISIBLE);
                btnsendotp.setVisibility(View.VISIBLE);
                btnregister.setEnabled(false);
                btnsendotp.setEnabled(true);
            }

        });

        txtmobileno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10){
                    getUserDetails();
                }
            }
        });
    }

    public void getUserDetails(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETUSERNAME,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("result").equals("success")) {
                            Tools.removeSimpleProgressDialog();
                            JSONArray json = jsonObject.getJSONArray("Data");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject obj = json.getJSONObject(i);
                                String tusername = obj.getString("empname");
                                txtname.setText(tusername);
                                muserrole = obj.getString("usertp");
                                musername =obj.getString("empname");
                                muserid = obj.getString("username");
                            }

                        } else if (jsonObject.optString("result").equals("failure")) {
                            Tools.removeSimpleProgressDialog();
                            Toast.makeText(getApplicationContext(), "No Such Customer Name Found", Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {

                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params=new HashMap<>();
                String phno = "91"+ txtmobileno.getText().toString();
                params.put("userphone", phno);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(RegisterUser.this);
        queue.add(stringRequest);

    }

    public static boolean isValidPhone(String phone)
    {
        String expression = "[91]{2}[7-9]{1}[0-9]{9}";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phone);
        return !matcher.matches();
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validatePassword(String password) {
        return password.length() < 6;
    }

    public void doLogin(){

        if (isConnectingToInternet(RegisterUser.this)) {

            new RegisterUserNew().execute();
        } else {
            Toast.makeText(getApplicationContext(), "Please Connect Internet First....", Toast.LENGTH_LONG).show();
        }

    }


    public static boolean isConnectingToInternet(Context context)
    {
        ConnectivityManager connectivity =
                (ConnectivityManager) context.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            @SuppressLint("MissingPermission") NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
        }
        return false;
    }

    private class RegisterUserNew extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String response="";
            HashMap<String, String> map=new HashMap<>();
            map.put("regusername", username);
            map.put("regusermobile", usermobile);
            map.put("reguserpass", userpass);
            map.put("reguseremail", useremail);
            map.put("reguserrole", muserrole);
//             map.put("reguserid", muserid.toString());
            try {
                HttpRequest req = new HttpRequest(myConfig.URL_REGISTERNEWUSER);
                response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
            } catch (Exception e) {
                response = e.getMessage();
            }
            return response;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        String UserRole= "";
        if (serviceCode == jsoncode) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("result").equals("success")) {
                    Tools.removeSimpleProgressDialog();
                    btnsendotp.setEnabled(true);
                    btnregister.setEnabled(false);

                } else if (jsonObject.optString("result").equals("failure")) {
                    Tools.removeSimpleProgressDialog();
                    Toast.makeText(getApplicationContext(), "You Are Not A Register User, Please Contact Administrator", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class getConfirmOtp extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String response="";
            final String otp = txtotp.getText().toString().trim();
            HashMap<String, String> map=new HashMap<>();
            map.put("userphone", txtmobileno.getText().toString());
            map.put("otp", otp);
            try {
                HttpRequest req = new HttpRequest(myConfig.URl_CONFIRMUSEROTP);
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
                onOtpTaskCompleted(result,jsoncode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void onOtpTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        String UserRole= "";
        if (serviceCode == jsoncode) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("result").equals("success")) {
                    Tools.removeSimpleProgressDialog();

                    sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
                    edit = sp.edit();
                    edit.putString(Constants.KEY_USERID, muserid);
                    edit.putString(Constants.KEY_USERNAME, musername);
                    edit.putString(Constants.KEY_USERROLE, muserrole);
                    edit.putString(Constants.KEY_USERPHONE, usermobile);
                    edit.putString(Constants.KEY_USEREMAIL, useremail);
                    edit.putString(Constants.KEY_USERPASS, userpass);
                    edit.commit();

                    mAuth.createUserWithEmailAndPassword(useremail, userpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(registeruser.this,"You are successfully Registered", Toast.LENGTH_SHORT).show();
                                Log.e("FirebaseRegister", "Ok");
                            } else {
                                //Toast.makeText(registeruser.this,"You are not Registered! Try again",Toast.LENGTH_SHORT).show();
                                Log.e("FirebaseRegister", "Fail");
                            }
                        }
                    });

                    Intent i = new Intent(RegisterUser.this, LoginActivity.class);
                    finish();
                    startActivity(i);

                } else if (jsonObject.optString("result").equals("failure")) {
                    Tools.removeSimpleProgressDialog();
                    Toast.makeText(getApplicationContext(), "You Are Not A Register User, Please Contact Administrator", Toast.LENGTH_LONG).show();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}