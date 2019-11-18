package com.atharvainfo.nilkamal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atharvainfo.nilkamal.Others.ItemClickListener;
import com.atharvainfo.nilkamal.model.spkmlist;

import java.util.List;
import com.atharvainfo.nilkamal.R;
import com.squareup.picasso.Picasso;

public class spkmadapter extends RecyclerView.Adapter<spkmadapter.ViewHolder> {
    private List<spkmlist> splistdata;
    Context context;
    private ItemClickListener mOnItemClickListener;

    public spkmadapter(Context context, List<spkmlist> splistdata){
        this.context = context;
        this.splistdata = splistdata;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public spkmadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spkmlist, parent, false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    public interface ItemClickListener{
        void onOpImageCick(int position);
        void onClImageCick(int position);
        void onItemClick(int position);


    }

    public void setOnItemClickListener(ItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull spkmadapter.ViewHolder holder, int position) {
        spkmlist spdata =splistdata.get(position);
        String opkimg = spdata.getOp_image();
        String clkimg = spdata.getCl_image();

        holder.txtspname.setText(spdata.getSpname());
        holder.txtvisitdate.setText(spdata.getVdate());
        holder.txtopkm.setText(spdata.getSpopkm());
        holder.txtclkm.setText(spdata.getSpclkm());
        Picasso.with(context).load(opkimg).placeholder(R.drawable.logo1).into(holder.opimage);
        Picasso.with(context).load(clkimg).placeholder(R.drawable.logo1).into(holder.climage);
        int index = position;

    }

    @Override
    public int getItemCount() {
        return splistdata.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtspname,txtvisitdate,txtopkm,txtopkmpath,txtclkm,txtclkmpath;
        ImageView opimage, climage;

        public ViewHolder(@NonNull View itemView, final ItemClickListener mOnItemClickListener) {
            super(itemView);
            txtspname = itemView.findViewById(R.id.txtspname);
            txtvisitdate = itemView.findViewById(R.id.txtvisitdate);
            txtopkm = itemView.findViewById(R.id.txtopkm);
            txtclkm = itemView.findViewById(R.id.txtclkm);
            opimage = itemView.findViewById(R.id.opkmimage);
            climage = itemView.findViewById(R.id.clkmimage);

            int pt = getAdapterPosition();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mOnItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
            opimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mOnItemClickListener.onOpImageCick(position);
                        }
                    }
                }
            });
            climage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mOnItemClickListener.onClImageCick(position);
                        }
                    }
                }
            });

        }
    }
}
