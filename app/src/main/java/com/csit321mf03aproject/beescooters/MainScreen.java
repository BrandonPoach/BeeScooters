package com.csit321mf03aproject.beescooters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainScreen extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //private SharedPreferences.Editor editor;
    SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    HashMap<String, String> paramHash;
    private static final String TAG = MainScreen.class.getSimpleName();
    private GoogleMap mMap;

    //Navigation Drawer
    private DrawerLayout mDrawerLayout;
    Context context;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    RequestQueue requestQueue;
    private static final String URL = "http://beescooters.net/admin/scooterLocations.php";
    private static final String URL2 = "http://beescooters.net/admin/accessToken.php";
    private static final String GOOGLE_DIRECTIONS_REQUEST_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";

    double destinationLatitude, destinationLongitude;

    ImageView imgQuestionMark;
    String creditBalance;

    Result result = new Result();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main_screen);

        preferences = this.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
        creditBalance = preferences.getString("creditBalance", "ERROR getting user credit balance");
        String access_token = preferences.getString("access_token", null);

        if (access_token == null)
            result = getAccessToken();

        String className = preferences.getString("previousActivity", "");
        Log.d("CLASS_NAME", className);
        if (className != "") {
            Class<?> activityClass;
            try {
                activityClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                activityClass = this.getClass();
                e.printStackTrace();
            }

            startActivity(new Intent(this, activityClass));
        }

        //Navigation Drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        imgQuestionMark = findViewById(R.id.toolbarQuestionMark);
        imgQuestionMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainScreen.this, HowToRide2Screen.class);
                startActivity(intent);
            }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        switch(menuItem.getItemId()){
                            //Account
                            case R.id.nav_account:
                                startActivity(new Intent(MainScreen.this, AccountScreen.class));
                                break;

                            //Payment
                            case R.id.nav_payment:
                                startActivity(new Intent(MainScreen.this, AddCreditScreen.class));
                                break;

                            //Ride History
                            case R.id.nav_history:
                                startActivity(new Intent(MainScreen.this, RideHistoryScreen.class));
                                break;

                            //How to Ride
                            case R.id.nav_howtoride:
                                startActivity(new Intent(MainScreen.this, HowToRide2Screen.class));
                                break;

                            //Safety
                            case R.id.nav_safety:
                                startActivity(new Intent(MainScreen.this, Safety2Screen.class));
                                break;

                            //Become a Charger
                            case R.id.nav_charger:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://beescooters.net")));
                                break;
                            //Logout
                            case R.id.nav_logout:
                                SharedPreferences preferences =getSharedPreferences("MYPREFS",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                                Intent intent = new Intent(MainScreen.this, LoginScreen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                break;
                        }

                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        return true;
                    }
                });

        requestQueue = Volley.newRequestQueue(this);

        // Retrieve location and camera position from saved instance state if exists.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private Result getAccessToken() {
        Result res = new Result();
        String app_secret = "3b6b744be34947288b4c6b3109212a80";
        String method = "jimi.oauth.token.get";
        String app_key = "8FB345B8693CCD0003C1DCAEFEA1FF4B";
        String sign_method = "md5";
        String v = "1.0";
        String format = "json";
        String user_id = "Joshuaodea";
        String user_pwd_md5 = "4885ada7d8acf3e71446aa0cb83d120d";
        String expires_in = "7200";

        SimpleDateFormat gmtDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gmtDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        //Current Date Time in GMT
        String timestamp = gmtDateFormat.format(new Date());

        String sign = MD5(app_secret + "app_key" + app_key + "expires_in" + expires_in + "format" + format + "method" +
                method + "sign_method" + sign_method + "timestamp" + timestamp + "user_id" + user_id + "user_pwd_md5" + user_pwd_md5 +
                "v" + v + app_secret).toUpperCase();

        Log.d("ACCESS_TOKEN", "" + sign + " " + timestamp);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://open.10000track.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JIMIAPI service = retrofit.create(JIMIAPI.class);
        service.gpsResult(method, timestamp, app_key, sign, sign_method, v, format, user_id, user_pwd_md5, expires_in).enqueue(new Callback<AccessResult>() {
            @Override
            public void onResponse(Call<AccessResult> call, retrofit2.Response<AccessResult> response) {
                Log.d("ACCESS_TOKEN", "Code : " + response.code() + " | Message : " + response.message());
                if (response.body() != null)
                {
                    Log.d("ACCESS_TOKEN", "" + response.body().code + " " + response.body().message + " " + response.body().result.getAccessToken());
                    editor = preferences.edit();
                    editor.putString("access_token", response.body().result.getAccessToken());
                    editor.commit();
                }

            }

            @Override
            public void onFailure(Call<AccessResult> call, Throwable t) {
                Log.d("ACCESS_TOKEN", "FAILLLLLLLLLLLLLL!!!!!!!!!!!");
            }
        });
        return res;
    }


    public static String MD5 (String toHash)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(toHash.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //function to retrieve scooter location from db
    public void sendJsonRequest ()
    {
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getDrawable(R.drawable.scooter_icon); //scooter icon in map
        Bitmap b=bitmapdraw.getBitmap();
        final Bitmap scooterMarker = Bitmap.createScaledBitmap(b, width, height, false);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Method.POST, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    //JSONArray coordinates = new JSONArray(response);
                    Log.d("BEE ERROR", String.valueOf(response.length()));
                    for (int i = 0; i<response.length(); i++)
                    {
                        JSONObject coordinateObject = response.getJSONObject(i);
                        Log.d("BEE ERROR", String.valueOf(coordinateObject.toString()));

                        double latitude = coordinateObject.getDouble("latitude");
                        double longitude = coordinateObject.getDouble("longitude");

                        Log.d("BEE_LATLONG", "lat : " + String.valueOf(latitude));

                        //add marker on map for each object received
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(latitude, longitude))
                                .icon(BitmapDescriptorFactory.fromBitmap(scooterMarker)));
                    }

                } catch (JSONException e) {
                    Log.d("BEE_ERROR", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BEE_ERROR", error.toString());
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
        Log.d("BEE_LOG", requestQueue.toString());
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        sendJsonRequest();

        mMap.setOnMarkerClickListener(this);
    }


    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;


        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                }
            }
        }

    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */

    public static Polyline pLine = null;
    public static String markerDistance = "";
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public static String mDistance = "";
    //when user clicks on a marker
    @Override
    public boolean onMarkerClick(Marker marker) {

        //remove previous direction line
        if (pLine != null)
            pLine.remove();

        //set destination coordinates
        destinationLatitude = marker.getPosition().latitude;
        destinationLongitude = marker.getPosition().longitude;
        Log.d("BEE_LOG", ""+destinationLatitude + "#" + destinationLongitude);

        String url, url2;

        Object dataTransfer[] = new Object[2];
        dataTransfer = new Object[3];
        url = getDirectionsUrl(1);   //retrieve directions URL which will be sent to Google

        if (url != " ")
        {
            GetDirectionsData getDirectionsData = new GetDirectionsData();
            dataTransfer[0] = mMap;
            dataTransfer[1] = url;
            dataTransfer[2] = new LatLng(destinationLatitude, destinationLongitude);

            getDirectionsData.execute(dataTransfer);    //handles polyline drawing

            url2 = getDirectionsUrl(2);

            GetDistanceData getDistanceData = new GetDistanceData();
            getDistanceData.execute(url2);



        }
        else
            Log.d("NULL_EXCEPTION", "I am here");

        return false;
    }

    //returns directions URL that will be sent to Google to get directions from A to B
    private String getDirectionsUrl (int mode)
    {
        //sometimes it loads slow so it might be null object
        if (mode == 1)
        {
            StringBuilder googleDirectionsUrl = new StringBuilder(GOOGLE_DIRECTIONS_REQUEST_URL);
            if (mLastKnownLocation ==  null)
            {
                getDeviceLocation();
                return " ";
            }

            else
            {
                //append user current location to origin part of request
                googleDirectionsUrl.append("origin=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
                //append marker LatLong to destination part of request
                googleDirectionsUrl.append("&destination=" + destinationLatitude + "," + destinationLongitude);
                //append the mode of directions, by default is driving but we want walking
                googleDirectionsUrl.append("&mode=walking");
                //append our key to key part of request
                googleDirectionsUrl.append("&key=" + "AIzaSyCeMyu0c-om4RLulVbn5uKIeYCv2-qxZBU");
                return googleDirectionsUrl.toString();
            }
        }

        else
        {

            StringBuilder googleDirectionsUrl = new StringBuilder(GOOGLE_DISTANCE_MATRIX_URL);
            if (mLastKnownLocation == null)
            {
                getDeviceLocation();
                return " ";
            }

            else
            {
                Log.d("LAST_KNOWN0", mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
                Log.d("LAST_KNOWN2", destinationLatitude + "," + destinationLongitude);
                //append user current location to origin part of request
                googleDirectionsUrl.append("&origins=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
                //append marker LatLong to destination part of request
                googleDirectionsUrl.append("&destinations=" + destinationLatitude + "," + destinationLongitude);
                //append our key to key part of request
                googleDirectionsUrl.append("&key=" + "AIzaSyCeMyu0c-om4RLulVbn5uKIeYCv2-qxZBU");

                return googleDirectionsUrl.toString();
            }
        }

    }

    public void startQRCodeScanner (View v)
    {
        //user credit balance under 2 dollars, dont allow them to ride
        if (Double.valueOf(creditBalance) <= 2.0)
        {
            Toast.makeText(this, "Your credit balance is too low. You need more than 2$ to Ride. Proceed to Add Credit.", Toast.LENGTH_SHORT).show();
        }

        else
        {
            //User clicked RIDE, jump to QR Code Scanner
            Intent intent = new Intent(this, QRCodeScannerScreen.class);
            startActivity(intent);
        }

    }

    public class GetDistanceData extends AsyncTask<String, String, String> {
        String distanceURL, distance;

        @Override
        protected String doInBackground(String... strings) {
            GoogleUrl googleUrl = new GoogleUrl();
            try
            {
                distanceURL = googleUrl.readUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //contains step by step direction on how to get to destination in JSON
            return distanceURL;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            retrieveDistance(s);
            Bundle args = new Bundle();
            args.putString("batteryLevel", "80");
            args.putString("distanceAway", distance);

            //shows bottom fragment sheet
            ScooterInfoSheet scooterInfoSheet = new ScooterInfoSheet();
            scooterInfoSheet.setArguments(args);
            scooterInfoSheet.show(getSupportFragmentManager(), "scooterBottomSheet");
        }

        public void retrieveDistance (String jsonData)
        {
            JSONArray jsonArray = null;
            JSONObject jsonObject;
            distance = "";
            Log.d("LAST_KNOWN3", jsonData);

            try
            {
                jsonObject = new JSONObject(jsonData);
                jsonArray = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
                jsonObject = jsonArray.getJSONObject(0).getJSONObject("distance");
                distance = jsonObject.getString("text");
                Log.d("JSONArray", ""+distance);
            }

            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void getTime ()
    {
        Long currentTime = System.currentTimeMillis();
        paramHash = new HashMap<>();
        paramHash.put("time", currentTime.toString());

        RequestQueue queue = Volley.newRequestQueue(MainScreen.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("REPLY_FROM_DB", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BEE_LOGS10", error.toString());
                error.printStackTrace();
            }
        })
        {
            // Ctrl + O

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (paramHash == null)
                {
                    return null;
                }

                //moving over all data from HashMap paramHash to Map params
                Map<String, String> params = new HashMap<>();
                for (String key:paramHash.keySet())
                {
                    params.put(key,paramHash.get(key));
                }
                return params;
            }

            // Ctrl + O

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map <String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        //add request to queue
        queue.add(stringRequest);
        Log.d("BEE_LOGS", stringRequest.toString());
    }
}
