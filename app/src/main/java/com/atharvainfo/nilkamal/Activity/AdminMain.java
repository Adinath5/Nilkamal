package com.atharvainfo.nilkamal.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.atharvainfo.nilkamal.Adapter.FinishStockAdapter;
import com.atharvainfo.nilkamal.Adapter.TabAdapter;
import com.atharvainfo.nilkamal.Fragments.TabFragment1;
import com.atharvainfo.nilkamal.Fragments.TabFragment2;
import com.atharvainfo.nilkamal.Fragments.TabFragment3;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.Utils;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.itemlistf;
import com.firebase.client.AuthData;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


public class AdminMain extends AppCompatActivity {

    private Toolbar toolbar;
    private PSDialogMsg psDialogMsg;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    protected String loginUserId;
    private ActionBarDrawerToggle toggle;
    String spname;
    Boolean currentlyTracking;
    private SharedPreferences sp;
    private boolean isLogout = false;
    public static final String PREFS="PREFS";
    SharedPreferences.Editor edit;
    private TextView txtcashbalance,txtrecamout,txttopayamt,txtsaleamt,txtpurchaseamt;
    private FloatingActionButton fab;
    private FloatingActionMenu fam;
    String CompanyCode,CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri,FyStartDate,FyEndDate,BrCode,ClaintApiUrl,ClaintDb;
    CardView dashboardlist2,dashboardlist3;
    MaterialCardView card_view5,card_view1,card_view6,card_view7;
    List<itemlistf> datamodel;
    RecyclerView finishplist, bestplist;
    FinishStockAdapter finishStockAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        psDialogMsg = new PSDialogMsg(this, false);

        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);

        AuthData authData;
        drawer = findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.nav_view);

        View v = navigationView.getHeaderView(0);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        fam = findViewById(R.id.fab);
        datamodel = new ArrayList<>();

        spname = sp.getString(Constants.KEY_USERNAME,null);
        String usernamelog= sp.getString(Constants.KEY_USERNAME,null);
        String useremail = sp.getString(Constants.KEY_USEREMAIL, null);
        String userDesignation = sp.getString(Constants.KEY_USERROLE, null);
        currentlyTracking = sp.getBoolean("currentlyTracking", false);
        loginUserId = sp.getString(Constants.KEY_USERPHONE, null);
        CompanyName = sp.getString(Constants.COMPANY_NAME, null);
        CompanyAddress = sp.getString(Constants.COMPANY_ADDRESS, null);
        CompanyCity = sp.getString(Constants.COMPANY_CITY, null);
        CompanyTal = sp.getString(Constants.COMPANY_TALUKA, null);
        CompanyDist = sp.getString(Constants.COMPANY_DISTRICT, null);
        CompanyMobile = sp.getString(Constants.COMPANY_MOBILE, null);
        CompanyGST = sp.getString(Constants.COMPANY_GST, null);
        CompanyJuri = sp.getString(Constants.COMPANY_JURISDICTION, null);
        CompanyEmail = sp.getString(Constants.COMPANY_EMAIL, null);
        FyStartDate = sp.getString(Constants.COMPANY_FYSTARTDATE, null);
        FyEndDate = sp.getString(Constants.COMPANY_FYENDDATE, null);

        edit = sp.edit();
        edit.putString(Constants.KEY_APPID,  UUID.randomUUID().toString());
        edit.putString(Constants.KEY_SESSIONID,  UUID.randomUUID().toString());
        edit.apply();
        sp.edit().putString(Constants.KEY_DISPLAY_NAME, usernamelog).apply();
        sp.edit().putString(Constants.KEY_EMAIL, useremail).apply();
        sp.edit().putString(Constants.KEY_TRAVEL_REF, usernamelog).apply();
        finishplist = findViewById(R.id.recycler_view);

        initToolbar();
        initNavigationMenu();

        dashboardlist2 = findViewById(R.id.dashboardlist2);
        dashboardlist3 = findViewById(R.id.dashboardlist3);
        txtcashbalance = findViewById(R.id.txtcashbal);
        txtrecamout = findViewById(R.id.txtcusttbal);
        txttopayamt = findViewById(R.id.txtsupbal);
        txtsaleamt = findViewById(R.id.txtbankbal);
        //txtpurchaseamt = findViewById(R.id.txtpurchaseamt);
        card_view5 = findViewById(R.id.card_view5);
        card_view1 = findViewById(R.id.card_view1);
        card_view6 = findViewById(R.id.card_view6);
        card_view7 = findViewById(R.id.card_view7);

        getRcPyLedgerBalance();
        getFinishStockList();
        
        showCustomDialog();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Exit();
            }
        });

        dashboardlist2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminMain.this, ReportsAdmin.class);
                startActivity(i);
            }
        });
        dashboardlist3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(AdminMain.this, AllProductList.class);
                //startActivity(i);
            }
        });
        fam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFarmTransacDialog();
            }
        });
    }

    private void initToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(CompanyName);
        Tools.setSystemBarColor(this,R.color.global__primary_dark);
        Tools.setSystemBarLight(this);
    }

    private void initNavigationMenu() {
        navigationView.setNavigationItemSelectedListener(item -> {
            drawer.closeDrawers();
            int id = item.getItemId();
            if(id ==  R.id.nav_item_one){
                Intent i = new Intent(AdminMain.this, AdminMain.class);
                startActivity(i);
                finish();
            } else
            if(id == R.id.nav_item_Four){
                Intent i = new Intent(AdminMain.this, PurchaseActivity.class);
                startActivity(i);
            } else
            if(id == R.id.nav_item_three){
                Intent i = new Intent(AdminMain.this, SaleActivity.class);
                startActivity(i);
            } else
            if(id == R.id.nav_item_two){
                Intent i = new Intent(AdminMain.this, ReportsAdmin.class);
                startActivity(i);
            } else
            if (id == R.id.nav_item_Five){
                Intent i = new Intent(AdminMain.this, ActivityReceipt.class);
                startActivity(i);
            }
            else
            if (id == R.id.nav_item_Six){
                Intent i = new Intent(AdminMain.this, ActivityPayment.class);
                startActivity(i);
            }
            else
            if (id == R.id.nav_item_seven){
                Intent i = new Intent(AdminMain.this, PulletQuotation.class);
                startActivity(i);
            }
            else
            if (id == R.id.nav_logout_login){
                psDialogMsg.showConfirmDialog(getString(R.string.edit_setting__logout_question), getString(R.string.app__ok), getString(R.string.app__cancel));
                psDialogMsg.show();
                psDialogMsg.okButton.setOnClickListener(view -> {
                    psDialogMsg.cancel();
                    setToolbarText(toolbar, getString(R.string.app_name));
                    isLogout = true;
                    edit.clear().apply();
                    invalidateOptionsMenu();
                    Intent it = new Intent(AdminMain.this, LoginActivity.class);
                    startActivity(it);
                    finish();
                    Utils.psLog("nav_logout_login");
                });
                psDialogMsg.cancelButton.setOnClickListener(view -> psDialogMsg.cancel());
            }
            return true;
        });


    }
    public void setToolbarText(Toolbar toolbar, String text) {
        Utils.psLog("Set Toolbar Text : " + text);
        toolbar.setTitle(Utils.getSpannableString(toolbar.getContext(), text, Utils.Fonts.GOTHIC));
    }

    public void Exit() {
        new android.app.AlertDialog.Builder(this)
                .setIcon(R.drawable.logo1)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.backbutton))
                .setPositiveButton(getString(R.string.yes_dialog), (dialog, which) -> {
                    dialog.dismiss();

                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_MAIN);
                    i.addCategory(Intent.CATEGORY_HOME);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();

                })
                .setNegativeButton(getString(R.string.no_dialog), (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.my_dialog);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ((TextView) dialog.findViewById(R.id.price)).setText(getString(R.string.WelComeUser, spname));

        dialog.findViewById(R.id.bt_close).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.buttonOk).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void getRcPyLedgerBalance(){
        Tools.showSimpleProgressDialog(this, "Getting Data ...","Please Wait ...",false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URL_GETRCPYBALANCE,
                response -> {
                    double netdrbal = 0;
                    double netcrbal = 0;
                    double netcash=0;
                    double netbank=0;
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("result").equals("success")) {
                            Tools.removeSimpleProgressDialog();
                            JSONArray json = jsonObject.getJSONArray("Data");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject obj = json.getJSONObject(i);
                                netdrbal = obj.getDouble("rcamount");
                                netcrbal = obj.getDouble("pyamount");
                                netcash = obj.getDouble("cashbal");
                                netbank = obj.getDouble("bankbal");
                                txtcashbalance.setText(NumberFormat.getInstance().format(Math.abs(netcash)));
                                txtrecamout.setText(NumberFormat.getInstance().format(Math.abs(netdrbal)));
                                txttopayamt.setText(NumberFormat.getInstance().format(Math.abs(netcrbal)));
                                txtsaleamt.setText(NumberFormat.getInstance().format(Math.abs(netbank)));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AdminMain.this, "No Data Found", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {

                })
        {
            @Override
            protected Map<String, String> getParams(){
                return new HashMap<>();
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AdminMain.this);
        queue.add(stringRequest);
    }

    private void showFarmTransacDialog() {
        final Dialog dialog = new Dialog(AdminMain.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_farmtransac);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;


        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.lyt_suplclr)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent supcler = new Intent(AdminMain.this, SupplyClearance.class);
                startActivity(supcler);
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.lyt_birdsupl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((LinearLayout) dialog.findViewById(R.id.lyt_feedsupl)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((LinearLayout) dialog.findViewById(R.id.lyt_Eggsprod)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent birdmort = new Intent(AdminMain.this, LayerEggsProduction.class);
                startActivity(birdmort);
                dialog.dismiss();
            }
        });

        ((LinearLayout) dialog.findViewById(R.id.lyt_birdsale)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
    private void getFinishStockList(){

        Tools.showSimpleProgressDialog(AdminMain.this, "Nilkamal Poultry...","Please Wait ...",false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, myConfig.URl_GETFINISHSTOCKPRODUCTLISTDATA,
                response -> {
                    Log.e("responsejson", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("Result").equals("Success")) {
                            Tools.removeSimpleProgressDialog();
                            JSONArray json = jsonObject.getJSONArray("Data");
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject obj = json.getJSONObject(i);
                                double inqt = Double.parseDouble(obj.getString("inqty"));
                                double otqt = Double.parseDouble(obj.getString("outqty"));
                                double clqt = (inqt)-(otqt);
                                if (clqt != 0 ){
                                    if (clqt <=20) {
                                        datamodel.add(new itemlistf(obj.getString("prodno"),
                                                obj.getString("prodname"), "",
                                                String.valueOf(NumberFormat.getInstance().format(clqt)), ""));
                                    }
                                }
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            finishplist.setLayoutManager(mLayoutManager);
                            finishplist.setItemAnimator(new DefaultItemAnimator());
                            finishplist.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                            finishStockAdapter = new FinishStockAdapter(datamodel, getApplicationContext());
                            finishplist.setAdapter(finishStockAdapter);

                        } else {
                            Tools.removeSimpleProgressDialog();
                            Toast.makeText(AdminMain.this, "User Not Found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show())
        {
            @Override
            protected Map<String, String> getParams()
            {
                HashMap<String, String> params = new HashMap<>();
                params.put("apikey", myConfig.API_KEY);
                Log.e("Selected Mobile:",myConfig.API_KEY);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(AdminMain.this);
        queue.add(stringRequest);
    }
}
