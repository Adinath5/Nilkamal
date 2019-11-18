package com.atharvainfo.nilkamal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.model.StatementModel;

import java.util.ArrayList;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.ViewHolder> {
    private ArrayList<StatementModel> VoucherList;
    private OnItemClickListener listener;
    Context mContext;

    public StatementAdapter(Context context, ArrayList<StatementModel> VoucherList){
        this.mContext = context;
        this.VoucherList = VoucherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StatementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.statementlist, parent, false);
        return new StatementAdapter.ViewHolder(itemView, listener);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final StatementAdapter.ViewHolder holder, final int position) {
        final Integer index = position;
        //holder.bind(TransactionList.get(position), listener);

        holder.txtvdate.setText(VoucherList.get(position).getInvoicedate());
        holder.txtperticular.setText(VoucherList.get(position).getInvdesc());
        holder.txtdramount.setText(VoucherList.get(position).getInvdramount());
        holder.txtcramount.setText(VoucherList.get(position).getInvcramount());

    }

    @Override
    public int getItemCount() {
        return VoucherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtvdate, txtperticular, txtdramount, txtcramount;
        public ImageButton btnprint, btnshare, btndelete;
        public FrameLayout trframe;

        public ViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            txtvdate = (TextView) view.findViewById(R.id.txtvdate);
            txtperticular = (TextView) view.findViewById(R.id.txtperticulars);
            txtdramount = (TextView) view.findViewById(R.id.txtdramt);
            txtcramount = (TextView) view.findViewById(R.id.txtcramt);

            int pt = getAdapterPosition();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public StatementModel getItem(Integer position){
        return VoucherList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
