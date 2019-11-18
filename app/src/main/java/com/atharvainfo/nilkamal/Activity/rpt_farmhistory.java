package com.atharvainfo.nilkamal.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Adapter.TabAdapter;
import com.atharvainfo.nilkamal.Fragments.FarmHistorySummayFragment;
import com.atharvainfo.nilkamal.Fragments.FeedConsFragment;
import com.atharvainfo.nilkamal.Fragments.FeedInFragment;
import com.atharvainfo.nilkamal.Fragments.TabFragment1;
import com.atharvainfo.nilkamal.Fragments.TabFragment2;
import com.atharvainfo.nilkamal.Fragments.TabFragment3;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class rpt_farmhistory extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Toolbar toolbar;
    private PSDialogMsg psDialogMsg;
    private Handler mHandler;

    TextView txtspname, txtspcode, txtfarmcode, txtfarmname, txtfarmbatch, txtfarmtype, txtfarmaddress, txtplacebird, txtplacedate, txtfdsp, txtfdcon, txtmortqt, txtmortperc, txtage, txtvochno, txtbalancebird;
    String spname, spcode, farmname, farmaddress, farmbatch, farmtype, farmcode, placedate, placebird, feedsuplbag, feedconsbag, mortqty, bsalqty, balbird, farmage, lastentryno, newvoucherno;
    Button btncancle, btnnext;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;

    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    public static final String DATE_FORMAT = "yyyy-M-d";
    private String[] titles = new String[]{"Summary", "Mortality", "Feed Supply", "Feed IN", "Feed Out", "Feed Cons.", "Med.Supply", "Bird Sale"};
    public static final String PREFS = "PREFS";
    private int[] layouts;
    private FragmentStateAdapter pagerAdapter;
    private static final int NUM_PAGES = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rpt_farmhistory);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        psDialogMsg = new PSDialogMsg(this, false);
        pagerAdapter = new MyPagerAdapter(this);
        mHandler = new Handler();

        initToolbar();


        txtspcode = findViewById(R.id.txtspcode);
        txtspname = findViewById(R.id.txtspname);
        txtfarmcode = findViewById(R.id.txtglcode);
        txtfarmname = findViewById(R.id.txtfname);
        txtfarmaddress = findViewById(R.id.txtfadd);
        txtfarmtype = findViewById(R.id.txtfarmtype);
        txtfarmbatch = findViewById(R.id.txtfbatch);
        txtvochno = findViewById(R.id.txtvochno);

        txtplacebird = findViewById(R.id.txtplacebird);
        txtplacedate = findViewById(R.id.txtplacedate);
        txtfdsp = findViewById(R.id.txtfeedsuplb);
        txtfdcon = findViewById(R.id.txtfeedcons);
        txtmortqt = findViewById(R.id.txtmortqty);
        txtmortperc = findViewById(R.id.txtmortperc);
        txtage = findViewById(R.id.txtage);
        txtbalancebird = findViewById(R.id.txtbalancebird);

        //btncancle = findViewById(R.id.btncancle);
        //btnnext = findViewById(R.id.btnnext);

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        sharedPreferences.edit();
        spcode = sharedPreferences.getString("user_name", null);
        spname = sharedPreferences.getString("empname", null);
        farmcode = sharedPreferences.getString("farmcode", null);
        farmname = sharedPreferences.getString("farmname", null);
        farmaddress = sharedPreferences.getString("farmaddress", null);
        farmbatch = sharedPreferences.getString("farmbatch", null);
        farmtype = sharedPreferences.getString("farmtype", null);

//        txtspcode.setText(spcode);
//        txtspname.setText(spname);
        txtfarmname.setText(farmname);
        txtfarmaddress.setText(farmaddress);
        txtfarmbatch.setText("Batch No. :" + farmbatch);
        txtfarmcode.setText(farmcode);
        txtfarmtype.setText(farmtype);
        newvoucherno = "";

        // formattedDate have current date/time
        tabLayout = findViewById(R.id.tabLayout);
        //adapter = new TabAdapter(rpt_farmhistory.this.getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);


        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout,viewPager,(tab, position) -> tab.setText(titles[position])).attach();

        //tabLayout.setupWithViewPager(viewPager);
        //checkEntryExists(farmcode, farmbatch);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Farm History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
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
        } catch (Exception ie) {
            ie.printStackTrace();

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
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    public static long getDaysBetweenDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays;
    }

    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }

    private class MyPagerAdapter extends FragmentStateAdapter {
        public MyPagerAdapter(FragmentActivity fa) {
            super(fa);
        }
        // overriding getPageTitle()

        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment createFragment(int pos) {
            switch (pos) {
                case 0: {
                    return FarmHistorySummayFragment.newInstance("Summary");
                }
                case 1: {
                    return FeedInFragment.newInstance("Feed Supply");
                }
                case 2: {
                    return FeedConsFragment.newInstance("Feed Consumption");
                }
                default:
                    return FarmHistorySummayFragment.newInstance("Summary, Default");
            }
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }
}