package com.atharvainfo.nilkamal.Fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.R;

public class AddCustomer extends Fragment {

    SQLiteDatabase mdatabase;
    String mypath;

    private View view;
    Button btnaddcustomer;
    TextInputEditText farmername,address,contactno,emailid;


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
                        "values('" +farmername.getText().toString() +"','"+address.getText().toString()+"','" +contactno .getText().toString() + "','" +emailid .getText().toString() + "')");

                Toast.makeText(getActivity(), "Data Save Sucessfully", Toast.LENGTH_LONG).show();
                // finish();
                isResumed();

            }
        });
    }

}
