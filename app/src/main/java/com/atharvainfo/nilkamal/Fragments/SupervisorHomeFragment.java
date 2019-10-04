package com.atharvainfo.nilkamal.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Adapter.GridBaseAdapter;
import com.atharvainfo.nilkamal.Others.ImageModel;
import com.atharvainfo.nilkamal.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupervisorHomeFragment extends Fragment {
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


    public SupervisorHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_supervisor_home, container, false);
        gvGallery = view.findViewById(R.id.gridView);

        imageModelArrayList = populateList();

        gridBaseAdapter = new GridBaseAdapter(getActivity().getApplicationContext(),imageModelArrayList);
        gvGallery.setAdapter(gridBaseAdapter);

        /*gvGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        NewClassFragment classFragment = new NewClassFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                                android.R.anim.fade_out);
                        fragmentTransaction.add(R.id.frame, classFragment, "AdminHome");
                        fragmentTransaction.addToBackStack("AdminHome");
                        fragmentTransaction.commitAllowingStateLoss();
                }
                Toast.makeText(getContext(), myImageNameList[position]+" Clicked", Toast.LENGTH_SHORT).show();
            }
        });*/

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
