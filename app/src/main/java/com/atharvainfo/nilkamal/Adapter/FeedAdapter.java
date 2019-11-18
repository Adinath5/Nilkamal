package com.atharvainfo.nilkamal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atharvainfo.nilkamal.model.feeditemmodel;
import com.atharvainfo.nilkamal.R;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private List<feeditemmodel> dataset;
    Context context;
    private ItemClickListener mOnItemClickListener;

    public FeedAdapter(List<feeditemmodel> data, Context context){
        this.dataset = data;
        this.context = context;
    }
    public interface ItemClickListener{
        void onItemClick(View view, feeditemmodel obj, int position);
    }

    public void setOnItemClickListener(ItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sale_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        feeditemmodel feeditemmodel =dataset.get(position);
        holder.lblitemno.setText(feeditemmodel.getProdno());
        holder.lblitemname.setText(feeditemmodel.getProdname());
        holder.lblitemqty.setText(feeditemmodel.getPqty());
        holder.lblitemrate.setText(feeditemmodel.getPrate());
        holder.lblitemamount.setText(feeditemmodel.getPamount());
        holder.lblitemmfg.setText(feeditemmodel.getPmfgcomp());
        holder.lblitemcat.setText(feeditemmodel.getPcat());
        holder.lblitempack.setText(feeditemmodel.getPpack());
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
        Button btndeleteitem;
        TextView lblitemqty,lblitemrate,lblitemno,lblitemcat,lblitemmfg,lblitemgstperc,lblitemmrp,lblitemhsn,lblitempack;
        TextView lblitemname,lblitemamount,lblbatchno,lblexpdate,lblitemsubtotalamt;
        TextView lblitemdiscountamt,lblitemgstamt;
        private final RecyclerView mHorizontalListView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblitemno = (TextView) itemView.findViewById(R.id.lblitemno);
            lblitemname = (TextView) itemView.findViewById(R.id.lblitemname);
            lblitemqty = (TextView) itemView.findViewById(R.id.lblitemqty);
            lblitemrate = (TextView) itemView.findViewById(R.id.lblitemrate);
            lblitemamount = (TextView) itemView.findViewById(R.id.lblitemamount);
            lblitemcat = (TextView) itemView.findViewById(R.id.lblitemcat);
            lblitemmfg = (TextView) itemView.findViewById(R.id.lblitemmfg);
            lblitemmrp = (TextView) itemView.findViewById(R.id.lblitemmrp);
            lblitemhsn = (TextView) itemView.findViewById(R.id.lblitemhsn);
            lblitempack = (TextView) itemView.findViewById(R.id.lblitempack);
            lblitemgstperc = (TextView) itemView.findViewById(R.id.lblitemgstperc);
            lblitemgstamt = (TextView) itemView.findViewById(R.id.lblitemgstamt);
            btndeleteitem = (Button) itemView.findViewById(R.id.btndeleteitem);
            mHorizontalListView = (RecyclerView) itemView.findViewById(R.id.list);

        }
    }
}
