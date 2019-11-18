package com.atharvainfo.nilkamal.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.atharvainfo.nilkamal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SaleReport extends Fragment {

    private View view;
    private Spinner scheme;
    String[] scheme1 = { "Cash", "Bank","Cheque","Wallet","Credit Card"};

    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener date1;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    TextView datechoice3,datechoice4,invoiceno,subtotal,discount,gst,roundoff,total,paidamt,dueamount;
    EditText purchaseno;
    Button btnaddcustomer,btnadditem;
    ListView itemlist;
    TextInputEditText narration;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_sale, container, false);

        datechoice3=view.findViewById(R.id.datechoice3);
        datechoice4=view.findViewById(R.id.datechoice4);
        scheme=view.findViewById(R.id.paidamount);
        btnaddcustomer=view.findViewById(R.id.btnadd);
        btnadditem=view.findViewById(R.id.btnadd1);
        invoiceno=view.findViewById(R.id.invoice);
        purchaseno=view.findViewById(R.id.purchaseno);
        itemlist=view.findViewById(R.id.list);
        subtotal=view.findViewById(R.id.subtotal);
        discount=view.findViewById(R.id.discount);
        gst=view.findViewById(R.id.gst);
        roundoff=view.findViewById(R.id.roundoff);
        total=view.findViewById(R.id.total);
        paidamt=view.findViewById(R.id.paidamt);
        dueamount=view.findViewById(R.id.dueamount);
        narration=view.findViewById(R.id.narration);


        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,scheme1);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scheme.setAdapter(aa);


        Long currentdate=System.currentTimeMillis();
        String datestring=dateform.format(currentdate);
        datechoice3.setText(datestring);
        datechoice4.setText(datestring);

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

        return view;
    }

    private void updateDate()
    {
        datechoice3.setText(dateform.format(mycal.getTime()));

    }
    private void updateDate1() {

        datechoice4.setText(dateform.format(mycal.getTime()));
    }
}
