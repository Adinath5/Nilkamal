package com.atharvainfo.nilkamal.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.CheckInternetConnection;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.OnEmailCheckListener;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private EditText txtpassword;
    private TextView txtmobileno;
    SharedPreferences sp;
    public static final String PREFS="PREFS";
    SharedPreferences.Editor edit;
    Button btncontinue, btnregister, btnbiometrilogin;
    String username,userrole,UserId, userMobile,AllowCod,userEmail,userlognpass;
    private FirebaseAuth mAuth;
    private static final String TAG = "Loginactivity";
    String token;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtmobileno = findViewById(R.id.txtmobileno);
        btncontinue = findViewById(R.id.continuebtn);
        btnregister = findViewById(R.id.registerbtn);
        txtpassword = findViewById(R.id.txtpassword);
        btnbiometrilogin = findViewById(R.id.biometric_login);
        AllowCod = "No";
        mAuth = FirebaseAuth.getInstance();
        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        userrole = sp.getString(Constants.KEY_USERROLE, null);
        UserId = sp.getString(Constants.KEY_USERID, null);
        username = sp.getString(Constants.KEY_USERNAME, null);
        userMobile = sp.getString(Constants.KEY_USERPHONE, null);
        userEmail = sp.getString(Constants.KEY_USEREMAIL, null);
        userlognpass = sp.getString(Constants.KEY_USERPASS,null);
        if (userMobile != null && !userMobile.equals("null") && !userMobile.isEmpty()) {
            txtmobileno.setText(userMobile);
        }

        btncontinue.setOnClickListener(v -> {
            if (!txtmobileno.getText().toString().equals("") && !txtmobileno.getText().toString().isEmpty()) {
                getUserLogin();
            }
        });

        btnregister.setOnClickListener(v -> {
            sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
            edit = sp.edit();
            edit.putString("mmobileno", txtmobileno.getText().toString());
            edit.commit();

            Intent intent = new Intent(getApplicationContext(),
                    RegisterUser.class);
            startActivity(intent);
            finish();
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        token = task.getResult();
                        edit = sp.edit();
                        edit.putString("device_tokan", token);
                        edit.commit();
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                getUserLoginBiometric();
                //Toast.makeText(getApplicationContext(),
                //        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Nilkamal Poultry Login")
                .setSubtitle("Log in Using your Biometric Credential")
                .setNegativeButtonText("Use Account Password")
                .build();

        btnbiometrilogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
            }
        });

    }

    public void isCheckEmail(final String email,final OnEmailCheckListener listener){
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>()
        {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task)
            {
                if (task.isSuccessful()) {
                    SignInMethodQueryResult result = task.getResult();
                    List<String> signInMethods = result.getSignInMethods();
                    if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                        // User can sign in with email/password
                    } else if (signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)) {
                        // User can sign in with email/link
                    }
                } else {
                    Log.e(TAG, "Error getting sign in methods for user", task.getException());
                }

            }
        });

    }

    public void getUserLogin() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_LOGIN,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("result").equals("success")) {
                            Tools.removeSimpleProgressDialog();

                            mAuth.signInWithEmailAndPassword(userEmail, txtpassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task){
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(getApplicationContext(),"Login successful!!",Toast.LENGTH_LONG).show();
                                        Log.e("FirebaseRegister", "Login Success");
                                    } else {
                                        //Toast.makeText(getApplicationContext(),"Login failed!!",Toast.LENGTH_LONG).show();
                                        Log.e("FirebaseRegister", "Login Fail");
                                    }
                                }
                            });
                            edit = sp.edit();
                            edit.putString(Constants.KEY_USERPASS, txtpassword.getText().toString());
                            edit.commit();

                            if (userrole.equals("Supervisor")) {
                                Intent intent = new Intent(LoginActivity.this, SupervisorMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else if (userrole.equals("Manager")) {

                                Intent intent = new Intent(LoginActivity.this, ManagerMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else if (userrole.equals("Admin")) {
                                Intent intent = new Intent(LoginActivity.this, AdminMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else if (userrole.equals("Operator")) {

                                Intent intent = new Intent(LoginActivity.this, AdminMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else if (userrole.equals("Layer Supervisor")) {
                                Intent intent = new Intent(LoginActivity.this, LayerFarmSpMain.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else if (userrole.equals("Feed Mill Supervisor")) {
                                Intent intent = new Intent(LoginActivity.this, FeedMillSpMain.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }

                        } else {
                            Tools.removeSimpleProgressDialog();
                            Toast.makeText(LoginActivity.this,"User Not Found",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, RegisterUser.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this,"Failed to Login",Toast.LENGTH_LONG).show();
                    }
                },
                error -> {

                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("userphone", txtmobileno.getText().toString());
                params.put("userpass", txtpassword.getText().toString());
                params.put("userrole", userrole);
                params.put("regtoken", token);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(stringRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    private boolean canAuthenticateWithStrongBiometrics() {
        return BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public void getUserLoginBiometric() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_BIOMETRICLOGIN,
                response -> {

                    Log.e("Loginresponce", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("result").equals("success")) {
                            Tools.removeSimpleProgressDialog();
                            String spcd="";
                            JSONArray json = jsonObject.getJSONArray("Data");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject obj = json.getJSONObject(i);
                                spcd = obj.getString("spcode");
                            }

                            Log.e("Pass", spcd);
                            mAuth.signInWithEmailAndPassword(userEmail, userlognpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task){
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(getApplicationContext(),"Login successful!!",Toast.LENGTH_LONG).show();
                                        Log.e("FirebaseRegister", "Login Success");
                                    } else {
                                        //Toast.makeText(getApplicationContext(),"Login failed!!",Toast.LENGTH_LONG).show();
                                        Log.e("FirebaseRegister", "Login Fail");
                                    }
                                }
                            });

                            if (userrole.equals("Supervisor")) {
                                Intent intent = new Intent(LoginActivity.this, SupervisorMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else if (userrole.equals("Manager")) {

                                Intent intent = new Intent(LoginActivity.this, ManagerMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else if (userrole.equals("Admin")) {
                                Intent intent = new Intent(LoginActivity.this, AdminMain.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else if (userrole.equals("Operator")) {

                               // Intent intent = new Intent(LoginActivity.this, AdminMain.class);
                               // intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                               // startActivity(intent);
                               // LoginActivity.this.finish();
                            } else if (userrole.equals("Layer Supervisor")) {
                                Intent intent = new Intent(LoginActivity.this, LayerFarmSpMain.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            } else if (userrole.equals("Feed Mill Supervisor")) {
                                Intent intent = new Intent(LoginActivity.this, FeedMillSpMain.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }

                        } else {
                            Tools.removeSimpleProgressDialog();
                            Toast.makeText(LoginActivity.this,"User Not Found",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, RegisterUser.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            LoginActivity.this.finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this,"Failed to Login",Toast.LENGTH_LONG).show();
                    }
                },
                error -> {

                })
        {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("userphone", txtmobileno.getText().toString());
                params.put("userrole", userrole);
                params.put("regtoken", token);
                Log.e("LoginBio", txtmobileno.getText().toString()+" "+ userrole+ " "+ token);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(stringRequest);
    }
}
