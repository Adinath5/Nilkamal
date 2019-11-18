package com.atharvainfo.nilkamal.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.atharvainfo.nilkamal.R;

public class StartActivity extends AppCompatActivity {

    public static boolean isAppRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
    }
}
