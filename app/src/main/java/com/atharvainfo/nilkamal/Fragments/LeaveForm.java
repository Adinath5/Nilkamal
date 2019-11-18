package com.atharvainfo.nilkamal.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.atharvainfo.nilkamal.Others.ApiInterface;
import com.atharvainfo.nilkamal.Others.PSDialogMsg;
import com.atharvainfo.nilkamal.Others.ServerResponse;
import com.atharvainfo.nilkamal.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LeaveForm extends Fragment {
    private View view;
    String Date1,Date2,Reason,Days;
    EditText reason,days;
    Button btncancel,btnapply;
    TextView datechoice,datechoice1;
    DatePickerDialog.OnDateSetListener date;
    DatePickerDialog.OnDateSetListener date1;
    String dateformat="yyyy-MM-dd";
    SimpleDateFormat dateform=new SimpleDateFormat(dateformat, Locale.US);
    Calendar mycal=Calendar.getInstance();
    private final int jsoncode = 1;
    SQLiteDatabase mdatabase;
    PSDialogMsg psDialogMsg;
    private Date fromDate, toDate;
    String jsonURL = "http://www.atharvainfosolutions.com/nilkamal/";
    //private  Days;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.leave_form, container, false);
        datechoice=view.findViewById( R.id.datechoice2);
        datechoice1=view.findViewById(R.id.datechoice3);
        reason=view.findViewById(R.id.reason);
        days=view.findViewById(R.id.days);
        btncancel=view.findViewById(R.id.btncancel);
        btnapply=view.findViewById(R.id.btnapply);

        Long currentdate=System.currentTimeMillis();
        String datestring=dateform.format(currentdate);
        datechoice.setText(datestring);
        datechoice1.setText(datestring);
        psDialogMsg = new PSDialogMsg(getActivity(), false);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        date=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate();


            }
        };
        date1=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dateofmonth) {
                mycal.set(Calendar.YEAR,year);
                mycal.set(Calendar.MONTH,monthofyear);
                mycal.set(Calendar.DAY_OF_MONTH,dateofmonth);
                updateDate1();


            }
        };

        datechoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();

               // fromDate = mycal.getTime();

               //datechoice.setText(dateFormat.format(fromDate));
            }
        });
        datechoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(),date1,mycal.get(Calendar.YEAR),mycal.get(Calendar.MONTH),mycal.get(Calendar.DAY_OF_MONTH)).show();
              //  toDate = mycal.getTime();
              //  diffdate();


              //  datechoice1.setText(dateFormat.format(toDate));
            }
        });


      //  int days = Days.daysBetween(datechoice,datechoice1).getDays();



        btnapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              diffdate();
              int dy = Integer.valueOf(days.getText().toString());
              if(dy >= 0){
                    doUpdate();
               }else{
                  psDialogMsg.showInfoDialog("Please Select a Valid Date", "Ok");
                  psDialogMsg.show();
              }



            }
        });

        return  view;
    }
@RequiresApi(api = Build.VERSION_CODES.O)
private void diffdate()
{
    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
   // Calendar cal1 = Calendar.getInstance();
    long numberOfDays = 0;
    String inputString1 = datechoice.getText().toString();
    String inputString2 = datechoice1.getText().toString();
    try {
        Date date1 = myFormat.parse(inputString1);
        Date date2 = myFormat.parse(inputString2);

        long diff = date2.getTime() - date1.getTime();
       // numberOfDays = getUnitBetweenDates(date1, date2, TimeUnit.DAYS);
        //days.setText("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
      //  days.setText( + (int) diff + "");
       // days.setText(+(int) numberOfDays+ "");
        long dayCount = diff / (1000 * 60 * 60 * 24);
        long dy = (dayCount) + 1;


        days.setText (""+(int) dy+ "");
    } catch (ParseException e) {
        e.printStackTrace();
    }
}
   /* private static String getUnitBetweenDates(Date date1, Date date2, TimeUnit unit) {
        long timeDiff = date1.getTime() - date2.getTime();
       // return unit.convert(timeDiff, TimeUnit.DAYS);
        float dayCount = (float)timeDiff/ (24 * 60 * 60 * 1000);
        return ("" + (int) dayCount + " Days");
    }*/


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDate()
    {

        datechoice.setText(dateform.format(mycal.getTime()));

       // DateFormat formatter =
         //       DateTimeFormat.ofPattern("yyyy-MM-dd").withLocale(Locale.US);
       // LocalDate startDate = LocalDate.parse(datechoice.getText().toString(), formatter);
      //  LocalDate endDate = LocalDate.parse(datechoice1.getText().toString(), formatter);

       // Period period = Period.between(startDate, endDate);
       // days.setText(String.format(" \nNo Of Days : %d Days, ",
        //        period.getDays()));
       // long diffInMillies = fromDate.getTime() - toDate.getTime();

        //int diffInDays = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

       // Log.d("Difference in days", String.valueOf(diffInDays));
       // days.setText(diffInDays);

    }
    private void updateDate1() {


        datechoice1.setText(dateform.format(mycal.getTime()));
    }

    public void doUpdate(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Saving Leaving Form Detail...");
        progressDialog.show();

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        Date1 = datechoice.getText().toString();
        Date2=datechoice1.getText().toString();
        Reason=reason.getText().toString();
        Days=days.getText().toString();

        Log.d("Response", Date1.toString()+ " "+ Date2.toString()+ " "+ Reason.toString()+ " "+ formattedDate.toString()+ " "+ Days.toString());


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(jsonURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<ServerResponse> call = apiInterface.leavapp( formattedDate.toString(),Date1.toString(), Date2.toString(), Reason.toString(),Days.toString());


        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.i("Responsestring", response.body().toString());
                progressDialog.dismiss();

                ServerResponse resp = response.body();
                Log.d("Response", resp.getResult().toString());
                if(resp.getResult().equals("success")){
                    //prefManager.setUserName(user.getUsername().toString());
                    //prefManager.setPContact(user.getPhone().toString());
                    //goToProfile();

                    psDialogMsg.showInfoDialog("Leave Form Detail Added Successfully.", "Ok");
                    psDialogMsg.show();


                } else {
                    psDialogMsg.showInfoDialog("Leave Form Detail Not Added Successfully.", "Ok");
                    psDialogMsg.show();
                }


            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }


        });

    }
}
