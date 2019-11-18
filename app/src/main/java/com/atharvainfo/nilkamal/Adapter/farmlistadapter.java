package com.atharvainfo.nilkamal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.farmlistmodel;

import java.util.List;

public class farmlistadapter extends RecyclerView.Adapter<farmlistadapter.ViewHolder> {

    private List<farmlistmodel> dataset;
    Context context;
    private ItemClickListener mOnItemClickListener;
    public farmlistadapter(List<farmlistmodel> data, Context context){
        this.dataset = data;
        this.context = context;
    }
    public interface ItemClickListener{
        void onItemClick(View view, farmlistmodel obj, int position);
    }

    public void setOnItemClickListener(ItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public farmlistadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.farmlistsp, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull farmlistadapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        farmlistmodel farmlist = dataset.get(position);
        holder.txtfarmcode.setText(farmlist.getFarmcode());
        holder.txtfarmname.setText(farmlist.getFarmname());
        holder.txtfarmaddress.setText(farmlist.getFarmaddress());
        holder.txtfarmbatch.setText(farmlist.getFarmbatch());
        holder.txtbirdplace.setText(farmlist.getPlaceqty());
        holder.txtfeespqty.setText(farmlist.getFeedsqty());
        holder.txtfeeconqty.setText(farmlist.getFeedcqty());
        holder.txtbmortqty.setText(farmlist.getBmortqty());
        holder.txtbsalqty.setText(farmlist.getBsalqty());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, dataset.get(position), position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtfarmcode, txtfarmaddress, txtfarmbatch, txtbirdplace, txtfarmname, txtfeespqty, txtfeeconqty, txtbmortqty, txtbsalqty;
        private final RecyclerView mHorizontalListView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtfarmcode = itemView.findViewById(R.id.txtglcoe);
            txtfarmname = itemView.findViewById(R.id.txtpname);
            txtfarmaddress = itemView.findViewById(R.id.txtfadd);
            txtfarmbatch = itemView.findViewById(R.id.txtbatch);
            txtbirdplace = itemView.findViewById(R.id.txtqty);
            txtfeespqty = itemView.findViewById(R.id.txtfpqty);
            txtfeeconqty = itemView.findViewById(R.id.txtfcqty);
            txtbmortqty = itemView.findViewById(R.id.txtbmqty);
            txtbsalqty = itemView.findViewById(R.id.txtbsqty);

            mHorizontalListView = (RecyclerView) itemView.findViewById(R.id.farmlist);
        }
    }
}
