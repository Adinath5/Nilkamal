package com.atharvainfo.nilkamal.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atharvainfo.nilkamal.Others.Constants;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.RequestHandler;
import com.atharvainfo.nilkamal.Others.Tools;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class FarmMortentryPhoto extends AppCompatActivity {

    TextView txtspname, txtspcode;
    String spname, spcode,farmname,farmaddress,farmbatch,farmtype,farmcode, placedate, placebird, feedsuplbag,feedconsbag,mortqty,bsalqty,balbird,farmage,lastentryno;
    Button btncancle, btnnext, btnmortphoto;
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

    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    public static final String DATE_FORMAT = "yyyy-M-d";

    ImageView mortphoto;
    private Bitmap mbitmap;

    public static final String UPLOAD_MTURL = "https://agrosoftprime.co.in/primebook/nilkamalpoultry/newuploadfarmmortimage.php";
    public static final String UPLOAD_MKEY = "mortimage";
    public static final String TAG = "MY MESSAGE";
    Bitmap feedPicture, mortPicture;
    boolean FIMAGE_STATUS = false;
    boolean MIMAGE_STATUS = false;
    String mCurrentPhotoPath;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String IMAGE_DIRECTORY_NAME = "imageuploadtest";
    private static final int CAMERA_REQUEST = 1888;
    public static final String PREFS="PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_mortentry_photo);

        txtspcode = findViewById(R.id.txtspcode);
        txtspname = findViewById(R.id.txtspname);

        btncancle = findViewById(R.id.btnphcancle);
        btnnext = findViewById(R.id.btnphnext);

        btnmortphoto = findViewById(R.id.btnmortphoto);

        mortphoto = findViewById(R.id.mortimage);

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

        btnmortphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //CaptureMortalityPhoto();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mortphoto.getDrawable()!=null){
                    Intent i = new Intent(FarmMortentryPhoto.this, FarmfeedEntrylast.class);
                    startActivity(i);
                } else {
                    Toast.makeText(FarmMortentryPhoto.this, "Please Take Mortality Photo First", Toast.LENGTH_SHORT).show();
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

    private void checkEntryExists(String lastentryno) {
        getPhotoMort();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                mbitmap = (Bitmap) data.getExtras().get("data");
                mortphoto.setImageBitmap(mbitmap);
                uploadMortImage();
            }
        }
    }

    private void uploadMortImage() {
        class UploadMortImage extends AsyncTask<Bitmap,Void,String> {

            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FarmMortentryPhoto.this, "Uploading Mortality Image", "Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap1 = params[0];
                String uploadMortImage = getStringImage(bitmap1);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String mdimagename = "MIMG_"+ farmcode + timeStamp + ".jpg";

                HashMap<String,String> data = new HashMap<>();
                data.put(UPLOAD_MKEY, uploadMortImage);
                data.put("farmcode", farmcode);
                data.put("fbatch", farmbatch);
                data.put("vochno", lastentryno);
                data.put("mimage", mdimagename);

                return rh.sendPostRequest(UPLOAD_MTURL,data);
            }
        }

        UploadMortImage ui = new UploadMortImage();
        ui.execute(mbitmap);
    }


    private String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null,null);

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void getPhotoMort(){
        /// showSimpleProgressDialog(this, "Getting Data...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("vochno", lastentryno);
                try {
                    HttpRequest req = new HttpRequest(myConfig.URl_GETSPMORTPHOTOFARM);
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
                    onMortPTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void onMortPTaskCompleted(String response, int serviceCode) throws JSONException {
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
                        String userprofilepic = obj.getString("mortimage");
                        //String useraddress = obj.getString("billing_address_1");
                        Picasso.with(getApplicationContext()).load(userprofilepic).placeholder(R.drawable.logo1).into(mortphoto);
                        // txtdeladdress.setText(useraddress);
                        btnmortphoto.setEnabled(false);
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

    private void getMortImage() {
        class GetMortImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FarmMortentryPhoto.this, "Uploading...", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();
                mortphoto.setImageBitmap(b);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String id = params[0];
                String add = "http://202.21.38.238:8080/nilkamalpoultry/getMortalityPhoto.php?vochno="+id;
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(add);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
        }

        GetMortImage gi = new GetMortImage();
        gi.execute(lastentryno);
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