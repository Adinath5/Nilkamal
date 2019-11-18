package com.atharvainfo.nilkamal.Fragments;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.R;

public class AddCustomerSale extends Fragment {
    SQLiteDatabase mdatabase;
    String mypath;
    private View view;
    ImageButton imgButton;
    private AppCompatAutoCompleteTextView autoTextView;
    Button btnaddcustomer;
    TextInputEditText address,contactno,emailid;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_customersale, container, false);
        mypath = DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME;
        mdatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
        imgButton =(ImageButton)view.findViewById(R.id.imageButton);
        btnaddcustomer=view.findViewById(R.id.btnadd);

        address=view.findViewById(R.id.address);
        contactno=view.findViewById(R.id.contactno);
        emailid=view.findViewById(R.id.emailid);

        autoTextView = (AppCompatAutoCompleteTextView)view.findViewById(R.id.custname);

        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddCustomer.class));

            }
        });
        savedata();
        return view;
    }

    private void savedata()
    {

        btnaddcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mdatabase.execSQL("CREATE TABLE IF NOT EXISTS temp_customerlist (farmername TEXT,address TEXT,contactno INTEGER,emailid TEXT)");


                mdatabase.execSQL("insert into temp_productlist (prodname,quantity,rate,amount)" +
                        "values('" +autoTextView.getText().toString() +"','"+address.getText().toString()+"','" +contactno .getText().toString() + "','" +emailid .getText().toString() + "')");

                Toast.makeText(getActivity(), "Data Save Sucessfully", Toast.LENGTH_LONG).show();
                // finish();
                getActivity().finish();

            }
        });
    }
    }
