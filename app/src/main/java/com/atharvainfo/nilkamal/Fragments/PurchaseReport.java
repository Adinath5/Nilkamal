package com.atharvainfo.nilkamal.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.atharvainfo.nilkamal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PurchaseReport extends Fragment {

    private View view;
    private Spinner scheme;
    String[] scheme1 = { "Cash", "Bank","Cheque","Wallet","Credit Card"};

    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener date1;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    TextView datechoice3,datechoice4;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_purchase, container, false);

        datechoice3=view.findViewById(R.id.datechoice3);
        datechoice4=view.findViewById(R.id.datechoice4);
        scheme=view.findViewById(R.id.paidamount);

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
