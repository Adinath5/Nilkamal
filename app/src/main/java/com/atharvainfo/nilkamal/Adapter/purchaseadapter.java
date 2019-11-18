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

import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.pitemmodel;

import java.util.List;

public class purchaseadapter extends RecyclerView.Adapter<purchaseadapter.ViewHolder> {

    private List<pitemmodel> dataset;
    Context context;
    private ItemClickListener mOnItemClickListener;

    public purchaseadapter(List<pitemmodel> data, Context context){
        this.dataset = data;
        this.context = context;
    }

    public interface ItemClickListener{
        void onItemClick(View view, pitemmodel obj, int position);
    }

    public void setOnItemClickListener(ItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    @NonNull
    @Override
    public purchaseadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purchase_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull purchaseadapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        pitemmodel pitemmodel = dataset.get(position);
        holder.txtprodno.setText(pitemmodel.getProdno());
        holder.txtproductname.setText(pitemmodel.getProdname());
        holder.txtmfgcomp.setText(pitemmodel.getMfgcomp());
        holder.txtprodtype.setText(pitemmodel.getProdtype());
        holder.txtgstperc.setText(pitemmodel.getTaxperc());
        holder.txtgstamount.setText(pitemmodel.getTaxamount());
        holder.txtquantity.setText(pitemmodel.getQuantity());
        holder.txtrate.setText(pitemmodel.getRateinctax());
        holder.txtamount.setText(pitemmodel.getAmountinctax());
        holder.txtmrp.setText(pitemmodel.getMrp());
        holder.txtpacking.setText(pitemmodel.getPacking());

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

        TextView txtprodno,txtproductname,txtmfgcomp,txtprodtype,txtgstperc,txtgstamount,txtquantity,txtrate,txtamount,txtmrp,txtpacking,txtprodamount;
        Button btndeleteitem;
        private final RecyclerView mHorizontalListView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtprodno = (TextView)itemView.findViewById(R.id.lblitemno);
            txtproductname = (TextView) itemView.findViewById(R.id.lblitemname);
            txtmfgcomp =  (TextView) itemView.findViewById(R.id.lblitemmfg);
            txtprodtype =  (TextView) itemView.findViewById(R.id.lblitemcat);
            txtgstperc =  (TextView) itemView.findViewById(R.id.lblitemgstperc);
            txtgstamount =  (TextView) itemView.findViewById(R.id.lblitemgstamt);
            txtquantity =  (TextView) itemView.findViewById(R.id.lblitemqty);
            txtrate =  (TextView) itemView.findViewById(R.id.lblitemrate);
            txtamount =  (TextView) itemView.findViewById(R.id.lblitemamount);
            txtmrp =  (TextView) itemView.findViewById(R.id.lblitemmrp);
            txtpacking =  (TextView) itemView.findViewById(R.id.lblitempack);
            btndeleteitem = (Button) itemView.findViewById(R.id.btndeleteitem);
            mHorizontalListView = (RecyclerView) itemView.findViewById(R.id.list);
        }
    }
}
