package com.atharvainfo.nilkamal.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.atharvainfo.nilkamal.Others.DatabaseHelper.mPath;

public class SaleReport extends Fragment {

    private View view;
    private Spinner scheme;
    String[] scheme1 = { "Cash", "Bank","Cheque","Wallet","Credit Card"};
    SQLiteDatabase mdatabase;
    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener date1;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    TextView datechoice3,datechoice4,invoiceno,subtotal,discount,gst,roundoff,total,paidamt,dueamount,address1,email1,mobileno;
    EditText purchaseno;
    Button btnaddcustomer,btnadditem;
    ListView itemlist;
    TextInputEditText narration;
    ArrayList<HashMap<String, String>> saladdsublist;
    private ListView subslrtnlist;
    ArrayAdapter<String> adapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_sale, container, false);

        String mypath = DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME;
        mdatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READONLY);


        datechoice3=view.findViewById(R.id.datechoice3);
        datechoice4=view.findViewById(R.id.datechoice4);
        scheme=view.findViewById(R.id.paidamount);
        btnaddcustomer=view.findViewById(R.id.btnadd);
        btnadditem=view.findViewById(R.id.btnadd1);
        invoiceno=view.findViewById(R.id.invoice);
        purchaseno=view.findViewById(R.id.purchaseno);
      //  itemlist=view.findViewById(R.id.list);
        subtotal=view.findViewById(R.id.subtotal);
        discount=view.findViewById(R.id.discount);
        gst=view.findViewById(R.id.gst);
        roundoff=view.findViewById(R.id.roundoff);
        total=view.findViewById(R.id.total);
        paidamt=view.findViewById(R.id.paidamt);
        dueamount=view.findViewById(R.id.dueamount);
        narration=view.findViewById(R.id.narration);
        address1=view.findViewById(R.id.address1);
        email1=view.findViewById(R.id.email1);
        mobileno=view.findViewById(R.id.mobileno);



        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,scheme1);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scheme.setAdapter(aa);


        Long currentdate=System.currentTimeMillis();
        String datestring=dateform.format(currentdate);
        datechoice3.setText(datestring);
        datechoice4.setText(datestring);
        subslrtnlist = (ListView) view.findViewById(R.id.list);
        saladdsublist = new ArrayList<HashMap<String, String>>();


        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate();


            }
        };
        date1=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate1();


            }
        };

        datechoice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();


            }
        });
        datechoice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date1,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
        btnaddcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddCustomerSale.class));
            }
        });
        btnadditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddItem.class));
            }
        });
        subslrtnlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // setting onItemLongClickListener and passing the position to the function
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int position, long arg3) {
                removeItemFromList(position);

                return true;
            }
        });
        showlist();

        return view;
    }

    private void updateDate()
    {
        datechoice3.setText(dateform.format(mycal.getTime()));

    }
    private void updateDate1() {

        datechoice4.setText(dateform.format(mycal.getTime()));
    }

    protected void removeItemFromList(int position) {
        final int deletePosition = position;




        AlertDialog.Builder alert = new AlertDialog.Builder(
                getActivity());

        alert.setTitle("Delete");
        alert.setMessage("Do you want delete this item?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String delQuery = "DELETE from temp_list ";
                mdatabase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE);
                mdatabase.execSQL(delQuery);
                // TOD O Auto-generated method stub

                // main code on after clicking yes
                saladdsublist.remove(deletePosition);
                adapter.notifyDataSetChanged();
                adapter.notifyDataSetInvalidated();

            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        alert.show();

    }



    private void showlist()
    {
        String query = "select  prodname, quantity, rate, amount  from temp_list ";
        Cursor cursor = mdatabase.rawQuery(query, null);
        if (cursor != null ) {
            while(cursor.moveToNext())  {
                HashMap<String, String> itemlist = new HashMap<String, String>();
                itemlist.put("A", cursor.getString(0));
                itemlist.put("B",cursor.getString(1));
                itemlist.put("C", cursor.getString(2));
                itemlist.put("D", cursor.getString(3));
                System.out.println(itemlist);
                saladdsublist.add(itemlist);

            }

        }


        ListAdapter adapter = new SimpleAdapter(
                getActivity(), saladdsublist, R.layout.item_list, new String[]{
                "A","B","C","D"}, new int[]{R.id.txtprodname, R.id.txtprodqty, R.id.txtprodrate, R.id.txtprodamt}
        )

        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                View view = super.getView(position, convertView, parent);
                if (position % 2 == 1) {
                    // Set a background color for ListView regular row/item
                    view.setBackgroundColor(Color.parseColor("#f0f8ff"));
                } else {
                    // Set the background color for alternate row/item
                    view.setBackgroundColor(Color.parseColor("#ffffe0"));
                }
                return view;
            }
        };

        subslrtnlist.setAdapter(adapter);
        setListViewHeightBasedOnItems(subslrtnlist);
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int)px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;

        } else {
            return false;
        }

    }


}
