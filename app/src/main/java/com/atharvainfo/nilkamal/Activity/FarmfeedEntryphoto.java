package com.atharvainfo.nilkamal.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.BuildConfig;
import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.RequestHandler;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class FarmfeedEntryphoto extends AppCompatActivity {

    TextView txtspname, txtspcode, txtfarmcode, txtfarmname,txtfarmbatch,txtfarmtype,txtfarmaddress,txtplacebird,txtplacedate,txtfdsp, txtfdcon,txtmortqt, txtmortperc,txtage,txtvochno,txtbalancebird;
    String spname, spcode,farmname,farmaddress,farmbatch,farmtype,farmcode, placedate, placebird, feedsuplbag,feedconsbag,mortqty,bsalqty,balbird,farmage,lastentryno;
    Button btncancle, btnnext, btnfeedphoto,btnmortphoto;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ListView feedconlist;
    private SimpleAdapter prodadapter;
    String mortreasone;
    Spinner spmortreason;
    ArrayList<String> MortReasonList;
    Boolean FeedEntryExists = false;
    Boolean MortEntryExists = false;

    ArrayList<HashMap<String, String>> itmlist;

    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;
    public static final String JSON_ARRAY = "result";
    public static final String DATE_FORMAT = "yyyy-M-d";

    ImageView feedphoto,mortphoto;
      private static final int CAMERA_REQUEST = 1888;

    private Bitmap fbitmap, mbitmap;

    private InputStream inputStreamImg;
    private String imgPath = null;
    private File fdestination = null;
    private File mdestination = null;

    public static final String UPLOAD_FDURL = "https://agrosoftprime.co.in/primebook/nilkamalpoultry/newuploadfarmfeedimage.php";
    public static final String UPLOAD_KEY = "stockimage";
    public static final String TAG = "MY MESSAGE";

    String mCurrentPhotoPath;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String IMAGE_DIRECTORY_NAME = "imageuploadtest";
    public static final String PREFS="PREFS";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmfeed_entryphoto);

        txtspcode = findViewById(R.id.txtspcode);
        txtspname = findViewById(R.id.txtspname);

        btncancle = findViewById(R.id.btnphcancle);
        btnnext = findViewById(R.id.btnphnext);
        btnfeedphoto = findViewById(R.id.btnfeedphoto);
        feedphoto = findViewById(R.id.feedimage);
        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        sharedPreferences.edit();
        spcode = sharedPreferences.getString(Constants.KEY_USERID,null);
        spname = sharedPreferences.getString(Constants.KEY_USERNAME,null);
        farmcode =sharedPreferences.getString("farmcode",null);
        farmname = sharedPreferences.getString("farmname",null);
        farmaddress = sharedPreferences.getString("farmaddress",null);
        farmbatch = sharedPreferences.getString("farmbatch",null);
        lastentryno = sharedPreferences.getString("newvoucherno",null);
        farmtype = sharedPreferences.getString("farmtype",null);
        farmage = sharedPreferences.getString("farmage",null);

        txtspcode.setText(spcode);
        txtspname.setText(spname);

        initToolbar();

        checkEntryExists(lastentryno);

        btnfeedphoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
             Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);

            }
        });
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(feedphoto.getDrawable()!= null ){
                    Intent i = new Intent(FarmfeedEntryphoto.this, FarmMortentryPhoto.class);
                    startActivity(i);
                } else {
                    Toast.makeText(FarmfeedEntryphoto.this, "Please Take Feed Stock Photo First", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createImageFile() throws IOException {
        // Create an image file name
        String state = Environment.getExternalStorageState();

        //***File nfile =  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/nilkamal");***//
        File nfile = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        System.out.print("NFilePath"+ nfile.toString());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
       //File file = new File(nfile, imageFileName+".jpg");
       File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                nfile      /* directory */
        );
       mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("Getpath", "Cool" + mCurrentPhotoPath);
        return image;
    }

    private void checkEntryExists(String lastentryno) {
        getPhotoFeed();
    }
    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }else {
            return null;
        }
        return mediaFile;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Feed / Mort. Photo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.global__primary);
    }

    @Override
    public void onBackPressed() {

        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                fbitmap = (Bitmap) data.getExtras().get("data");
                feedphoto.setImageBitmap(fbitmap);
                uploadFeedImage();
            }
        }
    }
    private void uploadFeedImage() {
        class UploadFeedImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FarmfeedEntryphoto.this, "Uploading Feed Image", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];
                String uploadFeedImage = getStringImage(bitmap);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String fdimagename = "FIMG_"+ farmcode + timeStamp + ".jpg";

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadFeedImage);
                data.put("vochno", lastentryno);
                data.put("farmcode", farmcode);
                data.put("fbatch", farmbatch);
                data.put("simage", fdimagename);

                return rh.sendPostRequest(UPLOAD_FDURL,data);
            }
        }

        UploadFeedImage ui = new UploadFeedImage();
        ui.execute(fbitmap);


    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    private void getPhotoFeed(){
       /// showSimpleProgressDialog(this, "Getting Data...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("vochno", lastentryno);
                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_GETSPFEEDPHOTOFARM);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response=e.getMessage();
                }
                return response;
            }
            protected void onPostExecute(String result) {
                //do something with response
                Log.d("newwwss",result);
                try {
                    onGetTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void onGetTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());

        int imageid =0;
        if (serviceCode == jsoncode) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.optString("Result").equals("success")) {
                    removeSimpleProgressDialog();

                    JSONArray json = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject obj = json.getJSONObject(i);
                        String userprofilepic = obj.getString("stkimage");
                        //String useraddress = obj.getString("billing_address_1");
                        Picasso.with(getApplicationContext()).load(userprofilepic).placeholder(R.drawable.logo1).into(feedphoto);
                        // txtdeladdress.setText(useraddress);
                        btnfeedphoto.setEnabled(false);
                    }
                } else {
                    removeSimpleProgressDialog();
                    // Toast.makeText(this, getErrorCode(response), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public String getErrorCode(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            removeSimpleProgressDialog();
            return jsonObject.getString("message");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "No data";
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception ie) {
            ie.printStackTrace();

        }

    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
