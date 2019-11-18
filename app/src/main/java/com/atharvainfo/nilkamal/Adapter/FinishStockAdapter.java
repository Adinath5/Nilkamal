package com.atharvainfo.nilkamal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.itemlistf;

import java.util.ArrayList;
import java.util.List;

public class FinishStockAdapter extends RecyclerView.Adapter<FinishStockAdapter.ViewHolder> implements Filterable {
    Context context;
    List<itemlistf> dataset;
    private List<itemlistf> datasetfiltered;
    private ItemClickListener OnItemClickListener;

    public FinishStockAdapter(List<itemlistf> data, Context context){
        this.dataset = data;
        this.context = context;
        this.OnItemClickListener = OnItemClickListener;
        datasetfiltered = new ArrayList<>(dataset);
    }

    public interface ItemClickListener{
        void onItemClick(View view, itemlistf obj, int position);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.OnItemClickListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stockitemlist, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        itemlistf itemlistf = dataset.get(position);
        holder.lblproductname.setText(itemlistf.getProdname());
        holder.lblprodno.setText(itemlistf.getProdno());
        holder.lblmfgcomp.setText(itemlistf.getMfgcomp());
        holder.lblstkval.setText(itemlistf.getClqty());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OnItemClickListener != null) {
                    OnItemClickListener.onItemClick(v, dataset.get(position), position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblproductname,lblstkval,lblprodno,lblmfgcomp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lblproductname = itemView.findViewById(R.id.lblproductname);
            lblstkval = itemView.findViewById(R.id.lblstkval);
            lblprodno = itemView.findViewById(R.id.lblprodno);
            lblmfgcomp = itemView.findViewById(R.id.lblmfgcomp);

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                List<itemlistf> filteredList = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0) {
                    filteredList.addAll(datasetfiltered);
                } else {
                    String filterPattern = charSequence.toString().toLowerCase().trim();
                    for (itemlistf row : datasetfiltered) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getProdname().toLowerCase().contains(charString.toLowerCase()) || row.getProdno().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dataset.clear();
                dataset.addAll((List) results.values);
                //datasetfiltered = (ArrayList<registermodel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
