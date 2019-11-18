package com.atharvainfo.nilkamal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.atharvainfo.nilkamal.model.productlist;
import com.atharvainfo.nilkamal.R;

import java.util.ArrayList;
import java.util.List;

public class productadapter extends ArrayAdapter<productlist> {
    Context context;
    private List<productlist> allproductlist;
    private List<productlist> filterproductlist;
    private List<productlist> tempItems;
    int resource, textViewResourceId;
    public productadapter(Context context, int resource, int textViewResourceId,List<productlist> productlist){
        super(context,0, productlist);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.allproductlist = productlist;
        //allproductlist = new ArrayList<>(productlist);
        tempItems =new ArrayList<productlist>(allproductlist);
        filterproductlist = new ArrayList<productlist>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_productname, parent, false);

        }
        productlist productlist = allproductlist.get(position);
        if (productlist != null){
            TextView lblname = (TextView) view.findViewById(R.id.lbl_name);
            if(lblname!=null){
                lblname.setText(productlist.getProdname());
            }
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((productlist) resultValue).getProdname();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                filterproductlist.clear();
                for (productlist productlist: tempItems){
                    if (productlist.getProdname().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filterproductlist.add(productlist);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterproductlist;
                filterResults.count = filterproductlist.size();
                return filterResults;
            }else {
                return new FilterResults();
            }

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<productlist> filterList = (ArrayList<productlist>) results.values;
            if (results!=null && results.count > 0){
                clear();
                for (productlist productlist : filterList){
                    add(productlist);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
