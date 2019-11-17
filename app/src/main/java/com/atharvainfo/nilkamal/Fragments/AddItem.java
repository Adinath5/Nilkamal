package com.atharvainfo.nilkamal.Fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import com.atharvainfo.nilkamal.R;

public class AddItem extends Fragment {

    SQLiteDatabase mdatabase;
    String mypath;

    private View view;
    Button btnadditem;
    TextInputEditText prodname,qty,rate1,amount1;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_item, container, false);

        mypath = DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME;
        mdatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);

        btnadditem=view.findViewById(R.id.btnadd);
        prodname=view.findViewById(R.id.prodname);
        qty=view.findViewById(R.id.qty);
        rate1=view.findViewById(R.id.rate1);
        amount1=view.findViewById(R.id.amount1);

        qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int qty1;
                int rate;

                if(qty.getText().toString() != "" && qty.getText().length() > 0) {
                    qty1 = Integer.parseInt(qty.getText().toString());
                } else {
                    qty1 = 0;
                }
                if(rate1.getText().toString() != "" && rate1.getText().length() > 0) {
                    rate = Integer.parseInt(rate1.getText().toString());
                } else {
                    rate = 0;
                }


                int amount=qty1*rate;

                amount1.setText( Integer.toString(amount));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rate1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int qty1;
                int rate;

                if(rate1.getText().toString() != "" && rate1.getText().length() > 0) {
                    qty1 = Integer.parseInt(qty.getText().toString());
                } else {
                    qty1 = 0;
                }
                if(rate1.getText().toString() != "" && rate1.getText().length() > 0) {
                    rate = Integer.parseInt(rate1.getText().toString());
                } else {
                    rate = 0;
                }


                int amount=qty1*rate;

                amount1.setText( Integer.toString(amount));

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        savedata();




        return view;

    }

    private void savedata()
    {

        btnadditem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mdatabase.execSQL("CREATE TABLE IF NOT EXISTS temp_list (prodname TEXT,quantity INTIGER,rate INTEGER,amount DOUBLE)");


                mdatabase.execSQL("insert into temp_productlist (prodname,quantity,rate,amount)" +
                        "values('" +prodname.getText().toString() +"','"+qty.getText().toString()+"','" +rate1 .getText().toString() + "','" +amount1 .getText().toString() + "')");

                Toast.makeText(getActivity(), "Data Save Sucessfully", Toast.LENGTH_LONG).show();
               // finish();
                isResumed();

                

            }
        });
    }

}
