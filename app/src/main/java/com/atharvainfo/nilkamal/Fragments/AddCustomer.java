package com.atharvainfo.nilkamal.Fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.textfield.TextInputEditText;

public class AddCustomer extends Fragment {

    SQLiteDatabase mdatabase;
    String mypath;

    private View view;
    Button btnaddcustomer;
    TextInputEditText farmername,address,contactno,emailid,openingbal;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_customer, container, false);

        mypath = DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME;
        mdatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
        btnaddcustomer=view.findViewById(R.id.btnadd);
        farmername=view.findViewById(R.id.farmername);
        address=view.findViewById(R.id.address);
        contactno=view.findViewById(R.id.contactno);
        emailid=view.findViewById(R.id.emailid);
        openingbal=view.findViewById(R.id.openingbal);

        savedata();

        return view;
    }

    private void savedata()
    {

        btnaddcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mdatabase.execSQL("CREATE TABLE IF NOT EXISTS temp_customerlist (farmername TEXT,address TEXT,contactno INTEGER,emailid TEXT,openingbal DOUBLE)");


                mdatabase.execSQL("insert into temp_customerlist (farmername,address,contactno,emailid,openingbal)" +
                        "values('" +farmername.getText().toString() +"','"+address.getText().toString()+"','" +contactno.getText().toString() + "','" +emailid.getText().toString() + "','" +openingbal.getText().toString() + "')");

                Toast.makeText(getActivity(), "Data Save Sucessfully", Toast.LENGTH_LONG).show();
                // finish();
                getActivity().finish();

            }
        });
    }

}
