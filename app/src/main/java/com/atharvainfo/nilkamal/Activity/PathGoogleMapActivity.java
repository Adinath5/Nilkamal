package com.atharvainfo.nilkamal.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.atharvainfo.nilkamal.Others.DirectionFinder;
import com.atharvainfo.nilkamal.Others.DirectionFinderListener;
import com.atharvainfo.nilkamal.Others.GMapV2Direction;
import com.atharvainfo.nilkamal.Others.HttpConnection;
import com.atharvainfo.nilkamal.Others.HttpRequest;
import com.atharvainfo.nilkamal.Others.PathJSONParser;
import com.atharvainfo.nilkamal.Others.myConfig;
import com.atharvainfo.nilkamal.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import com.atharvainfo.nilkamal.Others.Route;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.atharvainfo.nilkamal.Others.GoogleMapHelper.buildCameraUpdate;
import static com.atharvainfo.nilkamal.Others.GoogleMapHelper.defaultMapSettings;
import static com.atharvainfo.nilkamal.Others.GoogleMapHelper.getDefaultPolyLines;


public class PathGoogleMapActivity extends AppCompatActivity implements DirectionFinderListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static ProgressDialog mProgressDialog;
    private final int jsoncode = 1;
    private JSONArray result;
    public static final String JSON_ARRAY = "result";
    Button btngetDirection;

    GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";
    private static final int PATTERN_GAP_LENGTH_PX = 10;  // 1
    private static final Gap GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    private static final Dot DOT = new Dot();
    private static final List<PatternItem> PATTERN_DOTTED = Arrays.asList(DOT, GAP);


    private enum PolylineStyle {
        DOTTED,
        PLAIN
    }

    private List<Polyline> polylineList;
    private static final String[] POLYLINE_STYLE_OPTIONS = new String[]{
            "PLAIN",
            "DOTTED"
    };
    private static final int[] COLORS = new int[]{R.color.md_blue_600, R.color.global__primary, R.color.md_amber_700, R.color.md_brown_400, R.color.primary_dark_material_light};

    private static final LatLng SANGAMNER = new LatLng(19.5761, 74.2070);

    GMapV2Direction md = new GMapV2Direction();
    private PolylineStyle polylineStyle = PolylineStyle.PLAIN;
    SupportMapFragment mapFragment;
    private Polyline polyline;
    String spname, spcode;
    String startlatlan, newstartlatlan;
    String lastlatlang, newlastlatlan;
    LatLng fslat, fllan;
    LatLng eslat, ellan;
    protected LatLng start;
    protected LatLng end;
    List<LatLng> list;
    List<LatLng> latLngs = new ArrayList<>();
    ArrayList<LatLng> points;
    MarkerOptions markerOptions;
    public List<LatLng> routeArray = new ArrayList<LatLng>();
    private List<Polyline> polylines;

    LatLng previousLatLng;
    LatLng currentLatLng;
    private Polyline polyline1;

    private List<LatLng> polylinePoints = new ArrayList<>();
    private Marker mCurrLocationMarker;
    public static final String PREFS="PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_google_map);

        btngetDirection = findViewById(R.id.getDirectionButton);
        points = new ArrayList<LatLng>();
        polylines = new ArrayList<>();


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(googleMap -> {
            defaultMapSettings(googleMap);
            this.googleMap = googleMap;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        });
        //googleMap = fm.getMap();

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFS,MODE_PRIVATE);
        sharedPreferences.edit();
        spcode = sharedPreferences.getString("supervisorcode",null);
        spname = sharedPreferences.getString("supervisorname",null);
        //spname = sharedPreferences.getString("user_name",null);


        showPointerOnMap(19.5761, 74.2070);
        //LatLng latLng = new LatLng(19.5761, 74.2070);

        btngetDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("SpCode : "+ spcode);
                getSupervisorLocationList(spcode);
            }
        });
    }

    private void showPointerOnMap(final double latitude, final double longitude) {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                        .anchor(0.0f, 1.0f)
                        .position(latLng));
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                // Updates the location and zoom of the MapView
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                googleMap.moveCamera(cameraUpdate);
            }
        });
    }

    private void fetchLocationUpdates() {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        System.out.println("User : "+ spcode);
        ref.child("supervisors").child(spcode);
        ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.i("tag", "New location updated:" + dataSnapshot.getValue());
                    Log.i("GetChildren", dataSnapshot.getChildren().toString());
                    updateMap(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    private void updateMap(DataSnapshot dataSnapshot) {
        double latitude = 0, longitude = 0;

        //Iterable<DataSnapshot> data = dataSnapshot.getChildren();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            Log.e(TAG, "Lat "+postSnapshot.child("latitude").getValue());
            Log.e(TAG, "Lang "+postSnapshot.child("longitude").getValue());

            latitude = (Double) postSnapshot.child("latitude").getValue();
            longitude = (Double) postSnapshot.child("longitude").getValue();
        }


        /*for(DataSnapshot d: data){
            if(d.getKey().equals("latitude")){
                latitude = (Double) d.getValue();
            }else if(d.getKey().equals("longitude")){
                longitude = (Double) d.getValue();
            }
        }*/

        currentLatLng = new LatLng(latitude, longitude);

        if(previousLatLng ==null || previousLatLng != currentLatLng){
            // add marker line
            if(googleMap!=null) {
                previousLatLng  = currentLatLng;
                polylinePoints.add(currentLatLng);
                polyline1.setPoints(polylinePoints);
                Log.w("tag", "Key:" + currentLatLng);
                if(mCurrLocationMarker!=null){
                    mCurrLocationMarker.setPosition(currentLatLng);
                }else{
                    mCurrLocationMarker = googleMap.addMarker(new MarkerOptions()
                            .position(currentLatLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                            .title("Delivery"));
                }
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16));
            }

        }
    }



    private void getSupervisorLocationList(String spcode) {

        showSimpleProgressDialog(this, "Signing Up...","Please Wait ...",false);

        new AsyncTask<Void, Void, String>(){
            protected String doInBackground(Void[] params) {
                String response="";
                HashMap<String, String> map=new HashMap<>();
                map.put("spname", spname);

                try {
                    HttpRequest req = new HttpRequest(myConfig.URL_GETUSERLOCATIONLIST);
                    response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                } catch (Exception e) {
                    response=e.getMessage();
                }
                return response;
            }
            @RequiresApi(api = Build.VERSION_CODES.O)
            protected void onPostExecute(String result) {
                //do something with response
                Log.d("newwwss",result);
                try {
                    onTaskCompleted(result,jsoncode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onTaskCompleted(String response, int serviceCode) throws JSONException {
        Log.d("responsejson", response.toString());
        switch (serviceCode) {
            case jsoncode:

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.optString("result").equals("success")) {
                        removeSimpleProgressDialog();
                        JSONArray json = jsonObject.getJSONArray("spdirection");

                        //j = new JSONObject(response);
                        //result = j.getJSONArray(JSON_ARRAY);
                        result = jsonObject.getJSONArray("spdirection");
                        getRecord(result);


                    } else if (jsonObject.optString("result").equals("failure")){
                        removeSimpleProgressDialog();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getRecord(JSONArray j){
        PolylineOptions lineOptions = null;
        Polyline line;
        PolylineOptions line1 = new PolylineOptions();
        // also create a LatLngBounds so we can zoom to fit
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
         for(int i=0;i<j.length();i++){
            try {

                lineOptions = new PolylineOptions();

                JSONObject json = j.getJSONObject(i);
                fslat = new LatLng(Double.valueOf(json.getString("latitude")), Double.valueOf(json.getString("longitude")));
                startlatlan = json.getString("latitude")+","+ json.getString("longitude");
               // final LatLng lat = json.getString("latitude");


                //list = startlatlan;
                //list = decodePoly(String.valueOf(fslat));
                //list = decodePoly(String.valueOf(fslat));
                //latLngs = startlatlan;
                LatLng latLng = new LatLng(Double.parseDouble(json.getString("latitude")),Double.parseDouble(json.getString("longitude")));
                if (!routeArray.contains(latLng)){
                    routeArray.add(fslat);
                }
                String provider = json.getString("locationMethod");
                Location loc = new Location(provider);
                loc.setLongitude(Double.parseDouble(json.getString("longitude")));
                loc.setLatitude(Double.parseDouble(json.getString("latitude")));
                double altt = 9.0;
                loc.setAltitude(altt);
                String dateString = json.getString("gpsTime");
                SimpleDateFormat formatter5=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date5=formatter5.parse(dateString);

//                LocalDate date = LocalDate.parse(dateString);
                long dt = date5.getTime();
                loc.setTime(dt);

                LatLng latLng1 = new LatLng(loc.getLatitude(), loc.getLongitude());

                if (i == 0){
                    start = new LatLng(Double.valueOf(json.getString("latitude")), Double.valueOf(json.getString("longitude")));
                    String startDate = new Date(loc.getTime()).toString();
                    MarkerOptions startMarkerOptions = new MarkerOptions()
                            .position(latLng1)
                            .title(getResources().getString(R.string.report_start))
                            .snippet(getResources().getString(R.string.report_started_at_format, startDate));
                    googleMap.addMarker(startMarkerOptions);
                } else {
                    String endDate = new Date(loc.getTime()).toString();
                    MarkerOptions finishMarkerOptions = new MarkerOptions()
                            .position(latLng1)
                            .title(getResources().getString(R.string.report_finish))
                            .snippet(getResources().getString(R.string.report_finished_at_format, endDate));
                    googleMap.addMarker(finishMarkerOptions);
                }
                line1.add(latLng1);
                latLngBuilder.include(latLng1);

                Log.i("StartLat", String.valueOf(fslat));


            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                return;
            }
        }

        if(lineOptions != null) {
            googleMap.addPolyline(lineOptions);
        }
        else {
            Log.d("onPostExecute","without Polylines drawn");
        }
      // drawPolyLineOnMap(routeArray);

        googleMap.addPolyline(line1);
        // make the map zoom to show the track, with some padding
        // use the size of the current display in pixels as a bounding box
        Display display = this.getWindowManager().getDefaultDisplay();
        // construct a movement instruction for the map camera
        CameraUpdate movement = CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(),
                display.getWidth(), display.getHeight(), 15);
        googleMap.moveCamera(movement);

        /*googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag))
                .anchor(0.0f, 1.0f)
                .position(fslat));
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(fslat, 15);
        googleMap.moveCamera(cameraUpdate);*/
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(fslat, 15);
        //googleMap.moveCamera(cameraUpdate);

    }

    public void drawLine(List<LatLng> points) {
        if (points == null) {
            Log.e("Draw Line", "got null as parameters");
            return;
        }

        Polyline line = googleMap.addPolyline(new PolylineOptions().width(3).color(Color.RED).geodesic(true));
        line.setPoints(points);
    }
    public void drawPolyLineOnMap(List<LatLng> list) {
        polylines = new ArrayList<>();

        for (int i = 0; i <latLngs.size(); i++) {

          /*  int colorIndex = i % COLORS.length;
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(5);
            polyOptions.addAll(points);
            polyOptions.toString();
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polylines.add(polyline);*/
            polyline1 = googleMap.addPolyline(new PolylineOptions().addAll(points));
            //fetchLocationUpdates();
        }

        /*PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(5);

        polylineOptions.addAll(points);
        googleMap.addPolyline(polylineOptions);
        //googleMap.addMarker(markerOptions);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), 15));*/

        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);

        MarkerOptions options = new MarkerOptions();
        options.position(fslat);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
        googleMap.addMarker(options);
        googleMap.moveCamera(zoom);

        //List<LatLng> snappedPoints = new ArrayList<>();
        //new GetSnappedPointsAsyncTask().execute(latLngs, null, snappedPoints);

        /*PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(Color.RED);
        polyOptions.width(5);
        polyOptions.addAll(list);

        googleMap.clear();
        googleMap.addPolyline(polyOptions);*/

      /*  LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : list) {
            builder.include(latLng);
        }

        final LatLngBounds bounds = builder.build();

        //BOUND_PADDING is an int to specify padding of bound.. try 100.
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        googleMap.animateCamera(cu);*/




       /* LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : latLngs) {
            boundsBuilder.include(latLngPoint);
        }
        LatLngBounds latLngBounds = boundsBuilder.build();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
        */

    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }



    private void fetchDirections(String origin, String destination) {
        try {
            new DirectionFinder(this, origin, destination).execute(); // 1
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        //if (materialDialog == null)
        //    materialDialog = showAlwaysCircularProgressDialog(this, "Fetching Directions...");
        //else materialDialog.show();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
       // if (materialDialog != null && materialDialog.isShowing())
       //     materialDialog.dismiss();
        if (!routes.isEmpty() && polyline != null) polyline.remove();
        try {
            for (Route route : routes) {
                PolylineOptions polylineOptions = getDefaultPolyLines(route.points);
                if (polylineStyle == PolylineStyle.DOTTED)
                    polylineOptions = getDottedPolylines(route.points);
                polyline = googleMap.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error occurred on finding the directions...", Toast.LENGTH_SHORT).show();
        }
        Log.i("EndLocation", String.valueOf(routes.get(0).endLocation));

        googleMap.animateCamera(buildCameraUpdate(routes.get(0).endLocation), 10, null);
    }

    static PolylineOptions getDottedPolylines(List<LatLng> points) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .color(Color.BLUE)  // 3
                .pattern(PATTERN_DOTTED); // 4
        for (LatLng point : points) polylineOptions.add(point);   // 5
        return polylineOptions;
    }



    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }

    public boolean isSuccess(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("result").equals("success")) {
                removeSimpleProgressDialog();
                return true;
            } else {
                removeSimpleProgressDialog();
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
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
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
