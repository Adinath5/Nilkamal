package com.atharvainfo.nilkamal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.atharvainfo.nilkamal.Activity.LoginActivity;
import com.atharvainfo.nilkamal.Fragments.AdminHomeFragment;
import com.atharvainfo.nilkamal.Fragments.HomeFragment;
import com.atharvainfo.nilkamal.Fragments.LoginFragement;
import com.atharvainfo.nilkamal.Fragments.SupervisorHomeFragment;
import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.PrefManager;
import com.atharvainfo.nilkamal.Others.Tools;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    private NavigationView navigationView;
    private DrawerLayout drawer;

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    protected String loginUserId;
    //private FloatingActionButton add;
    private PSDialogMsg psDialogMsg;
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";


    public static String CURRENT_TAG = TAG_HOME;
    private static final int REQUEST= 112;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private String[] activityTitlessp;

    // flag to load home fragment when user presses back key
    private final boolean shouldLoadHomeFragOnBackPress = true;

    PrefManager prefManager;
    AlertDialog alertDialog;
    SliderLayout sliderLayout ;
    HashMap<String, Integer> HashMapForLocalRes ;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefManager = new PrefManager(getApplicationContext());
        psDialogMsg = new PSDialogMsg(this, false);

        Handler mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        View navHeader = navigationView.getHeaderView(0);
        ImageView imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        activityTitlessp = getResources().getStringArray(R.array.nav_item_activity_titlessp);


        String[] PERMISSIONS = {android.Manifest.permission.CAMERA,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.NFC,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            Log.d("TAG","@@@ IN IF hasPermissions");
            ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST );
        } else {
            Log.d("TAG","@@@ IN ELSE hasPermissions");
            //callNextActivity();
        }
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initToolbar();
        initNavigationMenu();

        File myDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "nilkamal");
        myDirectory.mkdir();
        File mydir = getApplicationContext().getDir("nilkamal", Context.MODE_PRIVATE); //Creating an internal dir;
        if(!mydir.exists())
        {
            mydir.mkdirs();
        }

        sliderLayout = (SliderLayout) findViewById(R.id.slider);

        AddImageUrlFormLocalRes();
        for(String name : HashMapForLocalRes.keySet()){

            TextSliderView textSliderView = new TextSliderView(MainActivity.this);

            textSliderView
                    .description(name)
                    .image(HashMapForLocalRes.get(name))
                    .setScaleType( BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());

            textSliderView.getBundle()
                    .putString("extra",name);

            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);

        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        sliderLayout.setCustomAnimation(new DescriptionAnimation());

        sliderLayout.setDuration(3000);

        sliderLayout.addOnPageChangeListener(MainActivity.this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("TAG", "@@@ PERMISSIONS grant");
                //callNextActivity();
            } else {
                Log.d("TAG", "@@@ PERMISSIONS Denied");
                Toast.makeText(this, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void AddImageUrlFormLocalRes(){

        HashMapForLocalRes = new HashMap<String, Integer>();

        HashMapForLocalRes.put("1", R.drawable.banner1);
        HashMapForLocalRes.put("2", R.drawable.banner2);
        HashMapForLocalRes.put("3", R.drawable.banner3);
        HashMapForLocalRes.put("4", R.drawable.banner4);
        // HashMapForLocalRes.put("5", R.drawable.banner5);

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        Tools.setSystemBarColor(this,R.color.global__primary);
        Tools.setSystemBarLight(this);
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    public void setToolbarTitle() {
        if (loginUserId!= null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(activityTitlessp[navItemIndex]);
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(activityTitles[navItemIndex]);
        }

        //toolbar.setNavigationIcon(R.mipmap.ic_app_logonc_round);
    }

    private void initNavigationMenu() {

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

//                actionBar.setTitle(item.getTitle());
                drawer.closeDrawers();

                String titile = String.valueOf(item.getTitle());
                if(titile.equalsIgnoreCase("Home")){
                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else
                if(titile.equalsIgnoreCase("Login")){
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    //finish();
                }
                return true;
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            String message = getBaseContext().getString(R.string.message__want_to_quit);
            String okStr =getBaseContext().getString(R.string.message__ok_close);
            String cancelStr = getBaseContext().getString(R.string.message__cancel_close);

            psDialogMsg.showConfirmDialog(message, okStr,cancelStr );

            psDialogMsg.show();

            psDialogMsg.okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    psDialogMsg.cancel();
                    finish();
                    System.exit(0);
                }
            });
            psDialogMsg.cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    psDialogMsg.cancel();
                }
            });

        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        prefManager.setNotifyStatus(null);
        super.onStop();
    }


}
