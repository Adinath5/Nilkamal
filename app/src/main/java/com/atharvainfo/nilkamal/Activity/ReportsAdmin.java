package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ListAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Adapter.ExpandableListAdapter;
import com.atharvainfo.nilkamal.Others.ReportExpandableListData;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.rptrowitemmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ReportsAdmin extends AppCompatActivity {

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;
    ListView reportlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_admin);

        reportlist = findViewById(R.id.rptlistview);


        initToolbar();

        ArrayList<rptrowitemmodel> itemsList = new ArrayList<>();
        ListView list = findViewById(R.id.rptlistview);
        itemsList = sortAndAddSections(getItems());
        ListAdapter adapter = new ListAdapter(this, itemsList);
        list.setAdapter(adapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 1:
                        Intent salesregi = new Intent(ReportsAdmin.this, rpt_salesregister.class);
                        startActivity(salesregi);
                        //Toast.makeText(ReportsAdmin.this , "Sales Register",Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Intent purchaseregi = new Intent(ReportsAdmin.this, rpt_purchaseregister.class);
                        startActivity(purchaseregi);
                        //Toast.makeText(ReportsAdmin.this , "Purchase Register",Toast.LENGTH_SHORT).show();
                        break;
                    case 13:
                        Intent i = new Intent(ReportsAdmin.this, AdminreportFarmhistory.class);
                        startActivity(i);
                        //Toast.makeText(ReportsAdmin.this , "Farm Histoy",Toast.LENGTH_SHORT).show();
                        break;
                    case 14:
                        Intent spdailyvisit = new Intent(ReportsAdmin.this, rpt_spdailyvisit.class);
                        startActivity(spdailyvisit);
                        break;
                    case 15:
                        Intent spkmreport = new Intent(ReportsAdmin.this, rpt_spkmreport.class);
                        startActivity(spkmreport);
                        break;
                    case 16:
                        Intent spgraph = new Intent(ReportsAdmin.this, SupervisorList.class);
                        startActivity(spgraph);
                        break;
                    case 17:
                        Intent dfeedcons = new Intent(ReportsAdmin.this, rpt_dailyfeedcons.class);
                        startActivity(dfeedcons);
                        break;
                    case 23:
                        Intent rcpyreport = new Intent(ReportsAdmin.this, rpt_receivablepayable.class);
                        startActivity(rcpyreport);
                        break;
                    case 22:
                        Intent rptacstate = new Intent(ReportsAdmin.this, rpt_accountstatementone.class);
                        startActivity(rptacstate);
                        break;
                    case 21:
                        Intent rptcashbook = new Intent(ReportsAdmin.this, rpt_cashbook.class);
                        startActivity(rptcashbook);
                        break;
                }

            }
        });

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Reports");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ReportsAdmin.this, AdminMain.class);
        startActivity(i);
        finish();
    }


    private ArrayList sortAndAddSections(ArrayList<rptrowitemmodel> itemList)
    {

        ArrayList<rptrowitemmodel> tempList = new ArrayList<>();
        //First we sort the array
        Collections.sort(itemList);

        //Loops thorugh the list and add a section before each sectioncell start
        String header = "";
        for(int i = 0; i < itemList.size(); i++)
        {
            //If it is the start of a new section we create a new listcell and add it to our array
            if(!(header.equals(itemList.get(i).getRptheadername()))) {
                rptrowitemmodel sectionCell = new rptrowitemmodel(null, itemList.get(i).getRptheadername());
                sectionCell.setToSectionHeader();
                tempList.add(sectionCell);
                header = itemList.get(i).getRptheadername();
            }
            tempList.add(itemList.get(i));
        }

        return tempList;
    }

    public class ListAdapter extends ArrayAdapter {

        LayoutInflater inflater;
        public ListAdapter(Context context, ArrayList items) {
            super(context, 0, items);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            rptrowitemmodel cell = (rptrowitemmodel) getItem(position);

            //If the cell is a section header we inflate the header layout
            if(cell.isSectionHeader())
            {
                v = inflater.inflate(R.layout.rptsection_header, null);
                v.setClickable(false);
                TextView header = (TextView) v.findViewById(R.id.section_header);
                header.setText(cell.getRptheadername());
            }
            else
            {
                v = inflater.inflate(R.layout.rptrow_item, null);
                TextView reportname = (TextView) v.findViewById(R.id.rptname);
                reportname.setText(cell.getRptname());
            }
            return v;
        }
    }

    private ArrayList<rptrowitemmodel>  getItems(){
        ArrayList<rptrowitemmodel> items = new ArrayList<>();
        items.add(new rptrowitemmodel("Sale Register", "Transaction")); //1
        items.add(new rptrowitemmodel("Purchase Register", "Transaction")); //2
        items.add(new rptrowitemmodel("All Transaction", "Transaction"));//3
        items.add(new rptrowitemmodel("Cash Receipt Register", "Transaction"));//4
        items.add(new rptrowitemmodel("Cash Payment Register", "Transaction"));//5
        items.add(new rptrowitemmodel("Cheque Receipt/Paid Register", "Transaction"));//6

        items.add(new rptrowitemmodel("Stock Summary Report", "Stock Reports"));//8
        items.add(new rptrowitemmodel("Productwise Stock Report", "Stock Reports"));//9
        items.add(new rptrowitemmodel("Product Ledger", "Stock Reports"));//10
        items.add(new rptrowitemmodel("MFG Companywise Stock Report", "Stock Reports"));//11

        items.add(new rptrowitemmodel("Farm History", "Farm Reports"));//13
        items.add(new rptrowitemmodel("Supervisor Daily Visit Report", "Farm Reports"));//14
        items.add(new rptrowitemmodel("Supervisor Kilometer Report", "Farm Reports"));//15
        items.add(new rptrowitemmodel("Supervisor Visit Graph Report", "Farm Reports"));//16
        items.add(new rptrowitemmodel("Daily Feed Consumption Report", "Farm Reports"));//17
        items.add(new rptrowitemmodel("Daily Mortality Report", "Farm Reports"));//18
        items.add(new rptrowitemmodel("Daily Eggs Production Report", "Farm Reports"));//19

        items.add(new rptrowitemmodel("Cash Book", "Account Reports"));//21
        items.add(new rptrowitemmodel("Account Statement", "Account Reports"));//22
        items.add(new rptrowitemmodel("Receivable Payable Report", "Account Reports"));//23


        return items;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);


    }
}
