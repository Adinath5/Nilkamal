package com.atharvainfo.nilkamal.Fragments;


import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.atharvainfo.nilkamal.Adapter.GridBaseAdapter;
import com.atharvainfo.nilkamal.Others.ImageModel;
import com.atharvainfo.nilkamal.R;
import com.atharvainfo.nilkamal.Others.ConnectionDetector;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.viewpagerindicator.CirclePageIndicator;
import com.atharvainfo.nilkamal.Others.SlidingImage_Adapter;
import com.atharvainfo.nilkamal.Others.DatabaseHelper;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminHomeFragment extends Fragment {

    private GridView gridView;


    private View view;
    private GridView gvGallery;
    private GridBaseAdapter gridBaseAdapter;
    private ArrayList<ImageModel> imageModelArrayList;
    private int[] myImageList = new int[]{R.drawable.spstart, R.drawable.spdtran,
            R.drawable.tasknew,R.drawable.leave
            ,R.drawable.enquiryn};
    private String[] myImageNameList = new String[]{"Root Start", "Daily Transaction",
            "Task","Leave Request"
            ,"Enquiry","Farm Enquiry",
            "Placement","Analysis","Aproval","Root Quit"};


    public  AdminHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_admin_home, container, false);
        gvGallery = view.findViewById(R.id.gridView);

        imageModelArrayList = populateList();

        gridBaseAdapter = new GridBaseAdapter(getActivity().getApplicationContext(),imageModelArrayList);
        gvGallery.setAdapter(gridBaseAdapter);



        gvGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 3:
                        LeaveForm leaveForm = new LeaveForm();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.add(R.id.frame, leaveForm, "SupervisorHomePage");
                        fragmentTransaction.addToBackStack("SupervisorHomePage");
                        fragmentTransaction.commitAllowingStateLoss();
                        break;
                    case 0:
                        SupplyClearance supplyclearace = new SupplyClearance();
                        FragmentTransaction fragmentTransaction1 = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction1.add(R.id.frame, supplyclearace, "SupervisorHomePage");
                        fragmentTransaction1.addToBackStack("SupervisorHomePage");
                        fragmentTransaction1.commitAllowingStateLoss();
                        break;
                    case 1:
                        CashReceipt cashReceipt = new CashReceipt();
                        FragmentTransaction fragmentTransaction2 = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction2.add(R.id.frame, cashReceipt, "SupervisorHomePage");
                        fragmentTransaction2.addToBackStack("SupervisorHomePage");
                        fragmentTransaction2.commitAllowingStateLoss();
                        break;
                    case 2:
                        PaymentReceipt paymentReceipt = new PaymentReceipt();
                        FragmentTransaction fragmentTransaction3 = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction3.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction3.add(R.id.frame, paymentReceipt, "SupervisorHomePage");
                        fragmentTransaction3.addToBackStack("SupervisorHomePage");
                        fragmentTransaction3.commitAllowingStateLoss();
                        break;

                }
                Toast.makeText(getContext(), myImageNameList[position]+" Clicked", Toast.LENGTH_SHORT).show();
            }
        });



        // Inflate the layout for this fragment
        return view;
    }

    private ArrayList<ImageModel> populateList(){

        ArrayList<ImageModel> list = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setName(myImageNameList[i]);
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }
}
