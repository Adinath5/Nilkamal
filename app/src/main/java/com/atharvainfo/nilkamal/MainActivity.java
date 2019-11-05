package com.atharvainfo.nilkamal;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Fragments.AdminHomeFragment;
import com.atharvainfo.nilkamal.Fragments.HomeFragment;
import com.atharvainfo.nilkamal.Fragments.LoginFragement;
import com.atharvainfo.nilkamal.Fragments.SupervisorHomeFragment;
import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.PrefManager;


public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    SQLiteDatabase mdatabase;
    private DatabaseHelper helper;
    protected String loginUserId;
    protected String EmpName, UserDesignation,UserEmail,UserContact;
    //private FloatingActionButton add;
    private PSDialogMsg psDialogMsg;
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_SPHOME = "SupervisorHomePage";
    private static final String TAG_SPLOGIN = "splogin";
    private static final String TAG_ALLLIST = "allist";
    private static final String TAG_YUVALIST = "yova";
    private static final String TAG_PRODLIST = "proud";
    private static final String TAG_JESTHLIST = "jesth";
    private static final String TAG_FEMALELIST = "femail";
    private static final String TAG_CONTACTEDLIST = "contact";
    private static final String TAG_NONCONTACT = "noncontact";
    private static final String TAG_VOTINGLIST = "voting";
    private static final String TAG_UVOTERLIST = "voterupdate";



    public static String CURRENT_TAG = TAG_HOME;
    private static final int REQUEST= 112;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private String[] activityTitlessp;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    PrefManager prefManager;
    AlertDialog alertDialog;


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

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
//        add = (FloatingActionButton) header.findViewById(R.id.add_profile);
//        add.setOnClickListener(this);


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        final LinearLayout holder = findViewById(R.id.holder);

        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        activityTitlessp = getResources().getStringArray(R.array.nav_item_activity_titlessp);
        helper = new DatabaseHelper(this);
        // initializing navigation menu
        setUpNavigationView();



        String[] PERMISSIONS = {android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.NFC,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            Log.d("TAG","@@@ IN IF hasPermissions");
            ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST );
        } else {
            Log.d("TAG","@@@ IN ELSE hasPermissions");
            callNextActivity();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG","@@@ PERMISSIONS grant");
                    callNextActivity();
                } else {
                    Log.d("TAG","@@@ PERMISSIONS Denied");
                    Toast.makeText(this, "PERMISSIONS Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void callNextActivity(){


        String data = getuserData();

        if(TextUtils.isEmpty(data)){
            // String is empty or null
            loginUserId = "";
            //Intent intent = new Intent(MainActivity.this, RegisterUser.class);
            //startActivity(intent);
            //finish();
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
            setUpNavigationView();

        }else {
            // loginUserId = udata[1];
            Log.d("UserName ", loginUserId.toString());

            prefManager.setUserName(loginUserId.toString());
            //callNextActivity();
            if (UserDesignation == "Supervisor") {
                navItemIndex = 1;
                CURRENT_TAG = TAG_SPHOME;
                loadHomeFragment();
                setUpNavigationViewSp();
            } else
            {
                navItemIndex = 1;
                CURRENT_TAG = TAG_SPHOME;
                loadHomeFragment();
                setUpNavigationViewSp();
            }
        }



    }


    private void loadHomeFragment() {

        setToolbarTitle();

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }


        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        if (loginUserId == null) {
        switch (navItemIndex) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;

            case 5:
                LoginFragement loginFragement = new LoginFragement();
                return loginFragement;
            /*case 1:
                VoterListFragment voterListFragment = new VoterListFragment();
                return voterListFragment;
            case 2:
                YouvaFragment youvaFragment = new YouvaFragment();
                return youvaFragment;
            case 3:
                ProdFragment prodFragment = new ProdFragment();
                return prodFragment;
            case 4:
                JesthFragment jesthFragment = new JesthFragment();
                return jesthFragment;
            case 5:
                MahilaFragment mahilaFragment = new MahilaFragment();
                return mahilaFragment;
            case 6:
                UpdateFragment updateFragment = new UpdateFragment();
                return updateFragment;
            case 7:
                ContactedFragment contactedFragment = new ContactedFragment();
                return contactedFragment;
            case 8:
                NonContactFragment nonContactFragment = new NonContactFragment();
                return nonContactFragment;
            case 9:
                VotingFragment votingFragment = new VotingFragment();
                return votingFragment;*/

            default:
                return new HomeFragment();
        }
        } else {
            switch (navItemIndex) {
                case 0:
                    SupervisorHomeFragment sphomeFragment = new SupervisorHomeFragment();
                    return sphomeFragment;

                default:
                    return new SupervisorHomeFragment();
            }
        }
    }

    public void setToolbarTitle() {
        if (loginUserId!= null) {
            getSupportActionBar().setTitle(activityTitlessp[navItemIndex]);
        } else {
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        }



        //toolbar.setNavigationIcon(R.mipmap.ic_app_logonc_round);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_login:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_SPLOGIN;
                        break;
                    /*case R.id.nav_alllist:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ALLLIST;
                        break;
                    case R.id.nav_yuvalist:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_YUVALIST;
                        break;
                    case R.id.nav_prdvalist:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PRODLIST;
                        break;
                    case R.id.nav_agelist:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_JESTHLIST;
                        break;
                    case R.id.nav_femalelist:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_FEMALELIST;
                        break;
                    case R.id.nav_updatelist:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_UVOTERLIST;
                        break;
                    case R.id.nav_contactvlist:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_CONTACTEDLIST;
                        break;
                    case R.id.nav_ncontactvlist:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_NONCONTACT;
                        break;
                    case R.id.nav_voting:
                        navItemIndex = 9;
                        CURRENT_TAG = TAG_VOTINGLIST;
                        break;*/
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
//                if (menuItem.isChecked()) {
//                    menuItem.setChecked(false);
//                } else {
//                    menuItem.setChecked(true);
//                }
//                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawer.getWindowToken(), 0);
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void setUpNavigationViewSp() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_SPHOME;
                        break;
                    /*case R.id.nav_alllist:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ALLLIST;
                        break;
                    case R.id.nav_yuvalist:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_YUVALIST;
                        break;
                    case R.id.nav_prdvalist:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PRODLIST;
                        break;
                    case R.id.nav_agelist:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_JESTHLIST;
                        break;
                    case R.id.nav_femalelist:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_FEMALELIST;
                        break;
                    case R.id.nav_updatelist:
                        navItemIndex = 6;
                        CURRENT_TAG = TAG_UVOTERLIST;
                        break;
                    case R.id.nav_contactvlist:
                        navItemIndex = 7;
                        CURRENT_TAG = TAG_CONTACTEDLIST;
                        break;
                    case R.id.nav_ncontactvlist:
                        navItemIndex = 8;
                        CURRENT_TAG = TAG_NONCONTACT;
                        break;
                    case R.id.nav_voting:
                        navItemIndex = 9;
                        CURRENT_TAG = TAG_VOTINGLIST;
                        break;*/
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
//                if (menuItem.isChecked()) {
//                    menuItem.setChecked(false);
//                } else {
//                    menuItem.setChecked(true);
//                }
//                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        final LinearLayout holder = findViewById(R.id.holder);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer)
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {

                //this code-block is the real player behind this beautiful ui
                // basically, it's a mathemetical calculation which handles the shrinking of
                // our content view.

                float scaleFactor = 7f;
                float slideX = drawerView.getWidth() * slideOffset;

                holder.setTranslationX(slideX);
                holder.setScaleX(1 - (slideOffset / scaleFactor));
                holder.setScaleY(1 - (slideOffset / scaleFactor));

                super.onDrawerSlide(drawerView, slideOffset);
            }
        };



        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress && prefManager.getNotifyStatus() == null) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
            super.onBackPressed();
        } else {
            /*String message = getBaseContext().getString(R.string.message__want_to_quit);
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
            });*/
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //if (item.getItemId() == R.id.action_message) {

            /*Fragment fragment1 = new NotificationsFragment();
            if (prefManager.getNotifyStatus() == null) {
                getSupportFragmentManager().beginTransaction().add(R.id.frame, fragment1, CURRENT_TAG).addToBackStack(CURRENT_TAG).commit();
                prefManager.setNotifyStatus("1");
                Log.e("1", "1");
            } else {
                getSupportFragmentManager().popBackStack();
                prefManager.setNotifyStatus(null);
                Log.e("Null", "Null");
            }*/
        // }

        return true;
    }




    @Override
    protected void onStop() {
        prefManager.setNotifyStatus(null);
        super.onStop();
    }

    public String getuserData() {
        helper.openDatabase();
        helper.close();
        mdatabase = helper.getWritableDatabase();
        //SQLiteDatabase db = mdatabase.getWritableDatabase();
        String[] columns = {"userid", "username", "password", "designation", "contact", "emailid"};
        Cursor cursor = mdatabase.query("usermast", columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex("userid"));
            String username = cursor.getString(cursor.getColumnIndex("username"));
            //String usertp =cursor.getString(cursor.getColumnIndex("usertp"));
            //String userbranch =cursor.getString(cursor.getColumnIndex("userbranch"));
            buffer.append(cid + "," + username);
            loginUserId = cursor.getString(cursor.getColumnIndex("username"));
            EmpName = cursor.getString(cursor.getColumnIndex("username"));
            UserDesignation = cursor.getString(cursor.getColumnIndex("designation"));
            UserContact = cursor.getString(cursor.getColumnIndex("contact"));
            UserEmail = cursor.getString(cursor.getColumnIndex("emailid"));
            SharedPreferences sharedPreferences = this.getSharedPreferences("com.atharvainfo.nilkamal.prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("UserName", loginUserId);
            editor.putString("UserTp",  UserDesignation);
            editor.putString("UserContact", UserContact);
            editor.putString("UserEmail", UserEmail);
            editor.apply();

            Log.i("UserName", loginUserId);
        }
        return buffer.toString();


    }
}
