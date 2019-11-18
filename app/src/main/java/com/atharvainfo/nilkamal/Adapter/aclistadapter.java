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
import com.atharvainfo.nilkamal.model.accmodel;

import java.util.List;

public class aclistadapter extends RecyclerView.Adapter<aclistadapter.ViewHolder> {

    private List<accmodel> dataset;
    Context context;
    private ItemClickListener mOnItemClickListener;

    public aclistadapter(List<accmodel> data, Context context){
        this.dataset = data;
        this.context = context;
    }
    public void filterList(List<accmodel> acsearchlist){
        dataset = acsearchlist;
        notifyDataSetChanged();
    }

    public interface ItemClickListener{
        void onItemClick(View view, accmodel obj, int position);
    }

    public void setOnItemClickListener(ItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public aclistadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partylist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull aclistadapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        accmodel accmodel = dataset.get(position);
        holder.txtglcode.setText(accmodel.getGlcode());
        holder.txtsledgername.setText(accmodel.getSledgername());
        holder.txtsladdress.setText(accmodel.getSladdress());
        holder.txtclosebal.setText(accmodel.getClbal());
        holder.txtbaltype.setText(accmodel.getDrcr1());
        holder.txtcontactno.setText(accmodel.getContactno());
        holder.txttaluka.setText(accmodel.getTaluka());
        holder.txtcity.setText(accmodel.getCity());
        holder.txtcontactperson.setText(accmodel.getContactperson());
        holder.txtaclimit.setText(accmodel.getAclimit());
        holder.txtroutname.setText(accmodel.getRoutname());
        holder.txtacstatus.setText(accmodel.getAcstatus());
        holder.txtpanno.setText(accmodel.getPanno());
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
        TextView txtglcode, txtsledgername, txtsladdress,txtcontactno, txtcontactperson, txtpanno, txtclosebal, txtbaltype,txttaluka,txtcity,txtaclimit, txtroutname,txtacstatus;
        private final RecyclerView mHorizontalListView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtglcode = itemView.findViewById(R.id.txtglcode);
            txtsledgername = itemView.findViewById(R.id.txtpname);
            txtsladdress = itemView.findViewById(R.id.txtsladdress);
            txtclosebal = itemView.findViewById(R.id.txtamount);
            txtbaltype = itemView.findViewById(R.id.txtbaltype);
            txtcontactno = itemView.findViewById(R.id.txtcontactno);
            txttaluka = itemView.findViewById(R.id.txttaluke);
            txtcity = itemView.findViewById(R.id.txtcity);
            txtcontactperson = itemView.findViewById(R.id.txtcontactperson);
            txtaclimit = itemView.findViewById(R.id.txtaclimit);
            txtroutname = itemView.findViewById(R.id.txtroutname);
            txtacstatus = itemView.findViewById(R.id.txtacstatus);
            txtpanno = itemView.findViewById(R.id.txtpanno);
            mHorizontalListView = (RecyclerView) itemView.findViewById(R.id.paclist);
        }
    }
}
