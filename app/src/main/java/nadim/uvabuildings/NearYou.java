package nadim.uvabuildings;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.Manifest;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import nadim.uvabuildings.R;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;

/**
 * NearYou class, as of now, it will be used to show all UVA buildings on map
 * Nadim El-Jaroudi 04/07/2016
 */
public class NearYou extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //declare variables for map, apiClient, etc.
    private static final String API_KEY = BuildConfig.API_KEY;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 111;
    Map<String, Marker> markers = new HashMap<String,Marker>();

    @Override
    /**
     * OnCreate method, set activity xml. initialize the API client, and locationRequest
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_you);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    }


    /**
     * Manipulates the map once available.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //start location services
        Location location = null;
        //check permissions to use location
        boolean hasPermissionLocation = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionLocation) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);
        }
        //get the current location of the user
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        //if the location is empty, go ahead and query for buildings
        if(location == null){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            query(location);
        }else {
            //call handle new location to track user movement, but also query for buildings
            handleNewLocation(location);
            query(location);
        }
    }

    //query method, used to call api storing building information
    private void query(Location location) {
        //url for api call
        String url = "http://138.197.11.189:3000/api/" + API_KEY + "/buildings/";
        //call class to run get request in background
        HttpUrlConnectionJson con = new HttpUrlConnectionJson();
        //create a hashmap to store name and location data for each building
        HashMap<String, LatLng> markInfo = new HashMap<String, LatLng>();
        //execute the api call and save the hashmap
        try {
            markInfo = con.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //create iterator
        Iterator it = markInfo.entrySet().iterator();
        //create arraylist for markers
        ArrayList<Marker> markers2 = new ArrayList<Marker>();
        //loop through hashmap
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            //create MarkerOption with hashmap info
            MarkerOptions options = new MarkerOptions()
                    .position(((LatLng) pair.getValue()))
                    .title(pair.getKey().toString());
            //add marker to map
            Marker m = mMap.addMarker(options);
            //put markers into arraylist and hashmap, for search later
            markers2.add(m);
            markers.put(pair.getKey().toString(), m);
            it.remove(); // avoids a ConcurrentModificationException
        }


        //initialize builder, will be used to calculate map bounds
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //add marker position to bounds
        for (Marker marker : markers2) {
            builder.include(marker.getPosition());
        }

        //make sure map has been loaded before setting bounds
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //get bounds from builder
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
                //update bounds for camera and set the camera
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
            }
        });




        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {


            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }


            @Override
            public View getInfoContents(Marker marker) {

                //set the onclick listener
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        //if the info window of a marker is clicked, open marker description class
                        Intent intent = new Intent(NearYou.this, MarkerDescription.class);
                        String data = marker.getTitle();
                        //pass the data for marker onto the next activity, start activity
                        intent.putExtra("marker", data);
                        startActivity(intent);

                    }
                });

                return null;

            }

        });

        }

    @Override
    public void onConnectionSuspended(int i) {
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        //reconnect app to map onResume
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if the app is paused disconnect from updates
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    //handle new location, adjust the marker titled current location
    private void handleNewLocation(Location location) {

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Current Location");
        Marker m = mMap.addMarker(options);
        markers.put(m.getTitle(), m);
    }

    //Search function for map
    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        location = location.toUpperCase();
        //search the hashmap for the key
            if(markers.containsKey(location)) {
                mMap.clear(); // clear the map, add the marker, move the camera
                LatLng latLng = new LatLng(markers.get(location).getPosition().latitude, markers.get(location).getPosition().longitude);
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
}
