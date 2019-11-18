package com.atharvainfo.nilkamal.Fragments;


import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import com.atharvainfo.nilkamal.Others.ConnectionDetector;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.SlidingImage_Adapter;
import com.atharvainfo.nilkamal.R;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private View view;
    private RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;

    private String VoterPartNo;

    ConnectionDetector connectivity;
    private final int jsoncode = 1;
    SQLiteDatabase mdatabase;
    PSDialogMsg psDialogMsg;
    TextView infotext;
    Button btnalllist,btnyouvalist,btnprodlist,btnjesthlist,btnfemalelist,btnupdatevoter,btnContactedList;
    private static ProgressDialog mProgressDialog;
    private String jsonURL = "http://www.atharvainfosolutions.com/myleader/api.php?apicall=getVoterListVillage";

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    //private String[] urls = new String[] {R.drawable.b1, R.drawable.b2, R.drawable.b3,
    //       R.drawable.b4, R.drawable.b5, R.drawable.b6};
    int[] mResources = {R.drawable.b1, R.drawable.b2, R.drawable.b3,
            R.drawable.b4, R.drawable.b5, R.drawable.b6, R.drawable.b7,R.drawable.b8,R.drawable.b9
    };
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        connectivity = new ConnectionDetector(getContext());

       /* btnalllist = view.findViewById(R.id.btnallvoterlist);
        btnyouvalist = view.findViewById(R.id.btnyouvalist);
        btnprodlist = view.findViewById(R.id.btnprdlist);
        btnjesthlist = view.findViewById(R.id.btnolderlist);
        btnfemalelist = view.findViewById(R.id.btnfemalelist);
        btnupdatevoter = view.findViewById(R.id.btnupdatevoter);
        btnContactedList = view.findViewById(R.id.btncontactedlist);*/


       // helper = new DatabaseHelper(getContext());
        infotext = view.findViewById(R.id.infotext);
        infotext.setMovementMethod(new ScrollingMovementMethod());

        init();
        /*if (connectivity.isConnectingToInternet()){
            init();
        } else {

            String message = getActivity().getString(R.string.no_internet_error);
            String okStr =getActivity().getString(R.string.message__ok_close);
            psDialogMsg.showErrorDialog(message,okStr);
            psDialogMsg.show();

            psDialogMsg.okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    psDialogMsg.cancel();
                    System.exit(0);
                }
            });

        }

        getInformation();*/

       /* btnalllist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoterListFragment voterListFragment = new VoterListFragment();
                FragmentTransaction fragmentTransaction =getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.add(R.id.frame, voterListFragment, "home");
                fragmentTransaction.addToBackStack("home");
                fragmentTransaction.commitAllowingStateLoss();

            }
        });

        btnyouvalist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YouvaFragment youvaFragment = new YouvaFragment();
                FragmentTransaction fragmentTransaction =getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.add(R.id.frame, youvaFragment, "home");
                fragmentTransaction.addToBackStack("home");
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        btnprodlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProdFragment prodFragment = new ProdFragment();
                FragmentTransaction fragmentTransaction =getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.add(R.id.frame, prodFragment, "home");
                fragmentTransaction.addToBackStack("home");
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        btnjesthlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JesthFragment jesthFragment = new JesthFragment();
                FragmentTransaction fragmentTransaction =getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.add(R.id.frame, jesthFragment, "home");
                fragmentTransaction.addToBackStack("home");
                fragmentTransaction.commitAllowingStateLoss();
            }
        });

        btnfemalelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MahilaFragment mahilaFragment = new MahilaFragment();
                FragmentTransaction fragmentTransaction =getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.add(R.id.frame, mahilaFragment, "home");
                fragmentTransaction.addToBackStack("home");
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
        btnupdatevoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateFragment updateFragment = new UpdateFragment();
                FragmentTransaction fragmentTransaction =getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.add(R.id.frame, updateFragment, "home");
                fragmentTransaction.addToBackStack("home");
                fragmentTransaction.commitAllowingStateLoss();
            }
        });
        btnContactedList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactedFragment contactedFragment = new ContactedFragment();
                FragmentTransaction fragmentTransaction =getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.add(R.id.frame, contactedFragment, "home");
                fragmentTransaction.addToBackStack("home");
                fragmentTransaction.commitAllowingStateLoss();
            }
        });*/

        return view;
    }

    private void init() {

        mPager = view.findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(getActivity(), mResources));

        CirclePageIndicator indicator = (CirclePageIndicator)
                view.findViewById(R.id.indicator);

        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(5 * density);

        NUM_PAGES = mResources.length;

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        // Pager listener over indicator


    }

}
