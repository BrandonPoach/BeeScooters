package com.csit321mf03aproject.beescooters;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;

    //private DrawerLayout myDrawer;
    //private ActionBarDrawerToggle myToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


/*      myDrawer = (DrawerLayout) findViewById(R.id.myDrawer);
        myToggle = new ActionBarDrawerToggle(this,myDrawer,R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        myDrawer.addDrawerListener(myToggle);
        myToggle.syncState();

        getActionBar().setDisplayHomeAsUpEnabled(true);

*/
    }

/*   @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if (myToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


}
