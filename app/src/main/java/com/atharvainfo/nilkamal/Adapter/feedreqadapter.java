package com.atharvainfo.nilkamal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.feedreqmodel;

import java.util.List;

public class feedreqadapter extends RecyclerView.Adapter<feedreqadapter.ViewHolder> {
    private List<feedreqmodel> dataset;
    Context context;
    private ItemClickListener mOnItemClickListener;

    public feedreqadapter(List<feedreqmodel> data, Context context){
        this.dataset = data;
        this.context = context;

    }
    public interface ItemClickListener{
        void onItemClick(View view, feedreqmodel obj, int position);
    }
    public void setOnItemClickListener(ItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    @NonNull
    @Override
    public feedreqadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedrequiredlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull feedreqadapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        feedreqmodel feedreqmodel = dataset.get(position);
        holder.txtprodno.setText(feedreqmodel.getProdno());
        holder.txtprodname.setText(feedreqmodel.getProdname());
        holder.txtprodqty.setText(feedreqmodel.getProdqty());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, dataset.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtprodno, txtprodname, txtprodqty;
        ImageView btndeleteitem;
        private final RecyclerView mHorizontalListView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtprodno =itemView.findViewById(R.id.txtprodno);
            txtprodname = itemView.findViewById(R.id.txtfprodname);
            txtprodqty = itemView.findViewById(R.id.txtfqty);
            btndeleteitem = itemView.findViewById(R.id.imagedelete);
            mHorizontalListView = (RecyclerView) itemView.findViewById(R.id.list);
        }
    }
}
