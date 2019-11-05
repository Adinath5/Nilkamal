package com.atharvainfo.nilkamal.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.atharvainfo.nilkamal.MainActivity;
import com.atharvainfo.nilkamal.R;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST= 112;
    private ImageView spimage;
    Thread splashTread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        spimage = findViewById(R.id.imageView);

        Resources res = getResources();

        Drawable drawable = ResourcesCompat.getDrawable (res,R.drawable.logo1, null);

        //this.spimage.setImageDrawable(getResources().getDrawable(ids[r]));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            VectorDrawable vectorDrawable = (VectorDrawable) drawable;
        } else {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        }

        spimage.setImageDrawable(drawable);
        callNextActivity();
    }

    public void callNextActivity(){
        // if(isConnectingToInternet(MainActivity.this)) {
        splashTread = new Thread(){

            @Override
            public void run() {

                try {
                    synchronized(this){
                        // Wait given period of time or exit on touch
                        wait(5000);
                    }
                }
                catch(InterruptedException ex){
                }

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                SplashActivity.this.finish();

            }
        };
        splashTread.start();

    }
}
