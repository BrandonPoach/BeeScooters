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

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class MainScreen extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    //private SharedPreferences.Editor editor;
    SharedPreferences preferences;

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
    private static final String GOOGLE_DIRECTIONS_REQUEST_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_DISTANCE_MATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";

    double destinationLatitude, destinationLongitude;

    ImageView imgQuestionMark;
    String creditBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main_screen);

        preferences = this.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
        creditBalance = preferences.getString("creditBalance", "ERROR getting user credit balance");


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
                                startActivity(new Intent(MainScreen.this, LoginScreen.class));
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
                }
            }
        }
        updateLocationUI();
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

            Log.d("BEE_LOG", "1234");

            url2 = getDirectionsUrl(2);

            GetDistanceData getDistanceData = new GetDistanceData();
            getDistanceData.execute(url2);

            Bundle args = new Bundle();
            args.putString("batteryLevel", "80");
            args.putString("distanceAway", mDistance);

            //shows bottom fragment sheet
            ScooterInfoSheet scooterInfoSheet = new ScooterInfoSheet();
            scooterInfoSheet.setArguments(args);
            scooterInfoSheet.show(getSupportFragmentManager(), "scooterBottomSheet");
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
                Log.d("LAST_KNOWN", mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
                Log.d("DESTINATION", destinationLatitude + "," + destinationLongitude);
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
            Toast.makeText(this, "Your credit balance is too low. You need more than 2AUD to Ride. Proceed to Add Credit Menu.", Toast.LENGTH_SHORT).show();
        }

        else
        {
            //User clicked RIDE, jump to QR Code Scanner
            Intent intent = new Intent(this, QRCodeScannerScreen.class);
            startActivity(intent);
        }

    }
}
