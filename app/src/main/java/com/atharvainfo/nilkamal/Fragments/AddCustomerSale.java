package com.atharvainfo.nilkamal.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.atharvainfo.nilkamal.R;

public class AddCustomerSale extends Fragment {
    private View view;
    ImageButton imgButton;
    private AppCompatAutoCompleteTextView autoTextView;
    Button btnaddcustomer;
    TextInputEditText address,contactno,emailid;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_customersale, container, false);
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
        return view;
    }

    }
