package com.atharvainfo.nilkamal.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.atharvainfo.nilkamal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SupplyClearance extends Fragment {
    private View view;
    Spinner name;
    TextView datechoice1,duedate10,actualdate10,duedate6,actualdate6,duedate4,actualdate4,duedate2;
    EditText address,lastbatchno,aggrementdatefrom,aggrementdateto,batchcapacity,supervisorname,newbatchno;
    DatePickerDialog.OnDateSetListener date1;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.supply_clearance, container, false);

        name=(Spinner) view.findViewById(R.id.farmname) ;
        datechoice1=view.findViewById(R.id.datechoice3);
        address=view.findViewById(R.id.income);
        lastbatchno=view.findViewById(R.id.lastbatchno);
        aggrementdatefrom=view.findViewById(R.id.aggrementdatefrom);
        aggrementdateto=view.findViewById(R.id.aggrementdateto);
        batchcapacity=view.findViewById(R.id.batchcapacity);
        supervisorname=view.findViewById(R.id.supervisorname);
        newbatchno=view.findViewById(R.id.newbatchno);
        duedate10=view.findViewById(R.id.txtduedate);
        actualdate10=view.findViewById(R.id.txtactualdate);
        duedate6=view.findViewById(R.id.txtduedate1);
        actualdate6=view.findViewById(R.id.txtactualdate1);
        duedate4=view.findViewById(R.id.txtduedate2);
        actualdate4=view.findViewById(R.id.txtactualdate2);
        duedate2=view.findViewById(R.id.duedate4);
        Long currentdate=System.currentTimeMillis();
        String datestring=dateform.format(currentdate);
       // datechoice.setText(datestring);
        datechoice1.setText(datestring);

        date1=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate1();


            }
        };
        datechoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date1,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });



        return view;
    }

    private void updateDate1() {


        datechoice1.setText(dateform.format(mycal.getTime()));
    }

}
