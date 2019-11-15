package com.atharvainfo.nilkamal.Fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.R;

public class AddCustomer extends Fragment {

    SQLiteDatabase mdatabase;
    String mypath;

    private View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_customer, container, false);

        mypath = DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME;
        mdatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);

        return view;
    }

}
