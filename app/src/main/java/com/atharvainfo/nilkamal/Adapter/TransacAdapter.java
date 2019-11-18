package com.atharvainfo.nilkamal.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import com.atharvainfo.nilkamal.R;

public class TransacAdapter extends RecyclerView.Adapter<TransacAdapter.ViewHolder> {

    private ArrayList<TranAdapter> TransactionList;
    private OnItemClickListener listener;
    Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_list, parent, false);
        return new ViewHolder(itemView, listener);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Integer index = position;
        //holder.bind(TransactionList.get(position), listener);

        holder.txtinvoicetype.setText(TransactionList.get(position).getInvoicetype());
        holder.txtinvoiceno.setText(TransactionList.get(position).getInvoiceno());
        holder.txtinvoicedate.setText(TransactionList.get(position).getInvoicedate());
        holder.txtledgername.setText(TransactionList.get(position).getLedgername());
        holder.txtinvoiceamount.setText(TransactionList.get(position).getInvoiceamount());

        String pt = holder.txtinvoicetype.getText().toString();
        if (pt.equals("PUR")){
            int color = R.color.md_blue_500;
            holder.trframe.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, color)));
            //holder.trframe.setBackgroundColor(R.color.md_blue_500);
        } else if(pt.equals("REC")){
            int color = R.color.md_green_900;
            holder.trframe.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, color)));

            //holder.trframe.setBackgroundColor(R.color.md_red_300);
        }else if(pt.equals("PAY")){
            int color = R.color.md_red_700;
            holder.trframe.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, color)));

            //holder.trframe.setBackgroundColor(R.color.md_red_300);
        }else if(pt.equals("BPR")){
            int color = R.color.md_blue_A200;
            holder.trframe.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, color)));

            //holder.trframe.setBackgroundColor(R.color.md_red_300);
        }else if(pt.equals("SRT")){
            int color = R.color.md_teal_600;
            holder.trframe.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, color)));

            //holder.trframe.setBackgroundColor(R.color.md_red_300);
        }else if(pt.equals("SAL")){
            int color = R.color.md_green_500;
            holder.trframe.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, color)));

            //holder.trframe.setBackgroundColor(R.color.md_red_300);
        }else if(pt.equals("BSAL")){
            int color = R.color.md_green_700;
            holder.trframe.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, color)));

            //holder.trframe.setBackgroundColor(R.color.md_red_300);
        }
        else if(pt.equals("PRT")){
            int color = R.color.md_blue_A200;
            holder.trframe.setForeground(new ColorDrawable(ContextCompat.getColor(mContext, color)));

            //holder.trframe.setBackgroundColor(R.color.md_red_300);
        }

    }
    public TranAdapter getItem(Integer position){
        return TransactionList.get(position);
    }

    @Override
    public int getItemCount() {
        return TransactionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtinvoicetype, txtinvoiceno, txtinvoicedate, txtinvoiceamount, txtledgername;
        public ImageButton btnprint, btnshare, btndelete;
        public FrameLayout trframe;

        public ViewHolder(View view, final OnItemClickListener listener){
            super(view);
            txtinvoicetype = (TextView) view.findViewById(R.id.txtinvtype);
            txtinvoiceno = (TextView) view.findViewById(R.id.txtinvno);
            txtinvoicedate = (TextView)view.findViewById(R.id.txtinvdate);
            txtledgername = (TextView)view.findViewById(R.id.txtledgername);
            txtinvoiceamount = (TextView)view.findViewById(R.id.txtamount);
            btnprint = (ImageButton)view.findViewById(R.id.btnprintvoch);
            btnshare = (ImageButton)view.findViewById(R.id.btnsharevoch);
            btndelete = (ImageButton) view.findViewById(R.id.btndelvoch);
            trframe = (FrameLayout) view.findViewById(R.id.trframe);

            int pt = getAdapterPosition();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            btnprint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onPrintClick(position);
                        }
                    }
                }
            });
            btnshare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onShareClick(position);
                        }
                    }
                }
            });
            btndelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

        }

    }

    public TransacAdapter(Context mContext, ArrayList<TranAdapter> TransactionList){
        this.mContext = mContext;
        this.TransactionList = TransactionList;
        this.listener = listener;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onPrintClick(int position);
        void onShareClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

}
