package com.atharvainfo.nilkamal.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atharvainfo.nilkamal.model.spvmodellist;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import cz.msebera.android.httpclient.NameValuePair;

public class spvladapter extends ArrayAdapter<spvmodellist> implements View.OnClickListener{

    private ArrayList<spvmodellist> items;
    private Context ctx;
    private AdapterView.OnItemClickListener mOnItemClickListener;
    private int lastPosition = -1;

    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public spvladapter(Context context, ArrayList<spvmodellist> items){
        super(context, R.layout.spkmlist, items);
        this.items = items;
        ctx = context;

    }

    private static class ViewHolder {
        TextView txtspname;
        TextView txtspopkm;
        TextView txtspclkm;
        ImageView op_image;
        ImageView cl_image;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        spvmodellist spvmodellist = getItem(position);
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.spkmlist, parent, false);
            viewHolder.txtspname = (TextView) convertView.findViewById(R.id.txtspname);
            viewHolder.txtspopkm = (TextView) convertView.findViewById(R.id.txtopkm);
            viewHolder.txtspclkm = (TextView) convertView.findViewById(R.id.txtclkm);
            viewHolder.op_image = (ImageView) convertView.findViewById(R.id.opkmimage);
            viewHolder.cl_image = (ImageView) convertView.findViewById(R.id.clkmimage);



            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(ctx, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtspname.setText(spvmodellist.getSpname());
        viewHolder.txtspopkm.setText(spvmodellist.getSpopkm());
        viewHolder.txtspclkm.setText(spvmodellist.getSpclkm());
        viewHolder.op_image.setImageResource(spvmodellist.getOp_image());
        viewHolder.cl_image.setImageResource(spvmodellist.getCl_image());
       // viewHolder.op_image.setOnClickListener(this);
        //viewHolder.op_image.setTag(;);
        //viewHolder.cl_image.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        spvmodellist dataModel=(spvmodellist) object;

        switch (v.getId())
        {
            case R.id.opkmimage:
                Snackbar.make(v, "Release date " +dataModel.getOp_image(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }


    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }


    }
}
