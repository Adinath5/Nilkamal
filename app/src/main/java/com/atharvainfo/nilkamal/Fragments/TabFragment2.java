package com.atharvainfo.nilkamal.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Activity.Activity_webview;
import com.atharvainfo.nilkamal.Adapter.TranAdapter;
import com.atharvainfo.nilkamal.Adapter.TransacAdapter;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class TabFragment2 extends Fragment {
    private View view;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private PSDialogMsg psDialogMsg;
    ArrayList<HashMap<String, String>> transactionlist;
    private SimpleAdapter adapter;
    private ListView tranlist;
    String CompanyName, CompanyAddress,CompanyCity,CompanyTal,CompanyDist,CompanyMobile,CompanyEmail,CompanyGST,CompanyJuri;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Image image;
    private static final String LOG_TAG = "Error";

    private ArrayList<TranAdapter> TransactionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TransacAdapter mAdapter;
    String postId;
    String dateformat="dd-MM-yyyy";
    SimpleDateFormat nDateform=new SimpleDateFormat(dateformat, Locale.US);
    private String ledgername,ledgeraddress,ledgercontact,ledgeremail,ledgergst,vdate,vochtype,invoiceamount;
    Date vdt;
    private WebView mWebView;
    String btnoption = null;
    SharedPreferences sp;
    public static final String PREFS="PREFS";
    SharedPreferences.Editor edit;
    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tab_fragment2, container, false);

        psDialogMsg = new PSDialogMsg(getActivity(), false);
        transactionlist = new ArrayList<HashMap<String, String>>();
        // tranlist = view.findViewById(R.id.tranlist);

        sharedPreferences= requireContext().getApplicationContext().getSharedPreferences("Mydata",MODE_PRIVATE);
        CompanyName = sharedPreferences.getString("company_name",null);
        CompanyAddress = sharedPreferences.getString("company_addres", null);
        CompanyCity = sharedPreferences.getString("company_city",null);
        CompanyTal = sharedPreferences.getString("company_tal", null);
        CompanyDist = sharedPreferences.getString("company_dist", null);
        CompanyMobile = sharedPreferences.getString("company_mobile", null);
        CompanyGST = sharedPreferences.getString("company_gst", null);
        CompanyJuri = sharedPreferences.getString("company_jurisdiction", null);
        CompanyEmail = sharedPreferences.getString("company_email", null);

        getAllVoucherList();
        return view;
    }
    private void getAllVoucherList(){
        TransactionList.clear();

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_GETTRANSACTODAYS);
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

    public void onTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        if (serviceCode == jsoncode) {
            if (isSuccess(response)) {
                double netcash = 0;
                String tdate = dt.format(c.getTime());
                double clbal = 0;
                String drbal = "";
                String crbal = "";
                String drcr = "Dr";

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("result").equals("success")) {
                    removeSimpleProgressDialog();

                    JSONArray json = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        clbal = obj.getDouble("amount");

                        TranAdapter tranAdapter = new TranAdapter(obj.getString("invmode"), obj.getString("vochno"), obj.getString("ledgername"), obj.getString("vdate"), String.valueOf(NumberFormat.getInstance().format(Math.abs(clbal))));
                        TransactionList.add(tranAdapter);
                       // System.out.print(TransactionList);

                    }
                    recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

                    mAdapter = new TransacAdapter(getActivity(), TransactionList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(null);
                    recyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                    mAdapter.setOnItemClickListener(new TransacAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            TranAdapter tranAdapter = TransactionList.get(position);
                            postId = tranAdapter.getInvoicetype();
                            String pvochno = tranAdapter.getInvoiceno();
                            System.out.println("PostId : " + postId.toString());

                            if (postId.equals("PUR")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "Purchase");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //   Intent i = new Intent(getActivity(), PurchaseActivityModify.class);
                                //   startActivity(i);
                                Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();
                            }
                            if (postId.equals("REC")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "Receipt");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //    Intent i = new Intent(getActivity(), ActivityReceiptModify.class);
                                //    startActivity(i);
                                Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();
                            }
                            if (postId.equals("PAY")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "Payment");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //   Intent i = new Intent(getActivity(), ActivityPaymentModify.class);
                                //    startActivity(i);
                                Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();
                            }
                            if (postId.equals("SAL")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "Sale");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //    Intent i = new Intent(getActivity(), SaleActivityModity.class);
                                //    startActivity(i);
                                Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();
                            }
                            if (postId.equals("BSAL")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "EggSale");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //   Intent i = new Intent(getActivity(), EggsSale.class);
                                //   startActivity(i);
                                Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();
                            }
                            if (postId.equals("SRT")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "SaleReturn");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //  Intent i = new Intent(getActivity(), SaleReturnModify.class);
                                //   startActivity(i);
                                Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();
                            }
                            if (postId.equals("PRT")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "PurchaseReturn");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //    Intent i = new Intent(getActivity(), PurchaseReturnModify.class);
                                //    startActivity(i);
                                Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onPrintClick(int position) {
                            TranAdapter tranAdapter = TransactionList.get(position);
                            postId = tranAdapter.getInvoicetype();
                            String pvochno = tranAdapter.getInvoiceno();
                            System.out.println("PostId : " + postId.toString());
                            btnoption = "Print";
                            if (postId.equals("PUR")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                //   CreatePurchaseInvoice(pvochno.toString());
                            }
                            Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();

                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onShareClick(int position) {
                            TranAdapter tranAdapter = TransactionList.get(position);
                            postId = tranAdapter.getInvoicetype();
                            String pvochno = tranAdapter.getInvoiceno();
                            System.out.println("PostId : " + postId.toString());
                            btnoption = "Share";
                            if (postId.equals("PUR")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "Purchase");
                                editor.putString("invoiceno", pvochno);
                                editor.commit();
                                //   Intent i = new Intent(getActivity(), Activity_webview.class);
                                //    startActivity(i);

                            }
                            if (postId.equals("SAL")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                //CreatePurchaseInvoice(pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "Sale");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                Intent i = new Intent(getActivity(), Activity_webview.class);
                                startActivity(i);
                            }
                            if (postId.equals("BSAL")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                //CreatePurchaseInvoice(pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "EggSale");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //    Intent i = new Intent(getActivity(), Activity_webview.class);
                                //     startActivity(i);
                            }
                            if (postId.equals("SRT")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                //CreatePurchaseInvoice(pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "SaleReturn");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //   Intent i = new Intent(getActivity(), Activity_webview.class);
                                //   startActivity(i);
                            }
                            if (postId.equals("PRT")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                //CreatePurchaseInvoice(pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "PurchaseReturn");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //   Intent i = new Intent(getActivity(), Activity_webview.class);
                                //   startActivity(i);
                            }
                            if (postId.equals("REC")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                //CreatePurchaseInvoice(pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "Receipt");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                Intent i = new Intent(getActivity(), Activity_webview.class);
                                startActivity(i);
                            }
                            if (postId.equals("PAY")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                //CreatePurchaseInvoice(pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "Payment");
                                editor.putString("invoiceno", pvochno.toString());
                                editor.commit();
                                //  Intent i = new Intent(getActivity(), Activity_webview.class);
                                //  startActivity(i);
                            }

                            Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();

                        }

                        public void onDeleteClick(int position) {
                            TranAdapter tranAdapter = TransactionList.get(position);
                            postId = tranAdapter.getInvoicetype();
                            String pvochno = tranAdapter.getInvoiceno();
                            System.out.println("PostId : " + postId.toString());
                            btnoption = "Share";
                            if (postId.equals("PUR")) {
                                //createPDFFile(Common.getAppPath(getContext())+"purchaseinvoice.pdf", pvochno.toString());
                                sharedPreferences = getActivity().getSharedPreferences("Mydata", MODE_PRIVATE);
                                editor = sharedPreferences.edit();
                                editor.putString("vochtype", "Purchase");
                                editor.putString("invoiceno", pvochno);
                                editor.commit();


                            }
                            Toast.makeText(getContext(), tranAdapter.getInvoicetype() + " is selected!", Toast.LENGTH_SHORT).show();


                        }
                    });

                }

            } else {
                Toast.makeText(getActivity(), getErrorCode(response), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of LoginFragment");
        super.onResume();
        getAllVoucherList();
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
}
