package nadim.uvabuildings;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.lang.Double.parseDouble;

/**
 * Map Activity, shows all buildings of a specific category
 */
public class CategorySelectActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String API_KEY = BuildConfig.API_KEY;
    private GoogleMap mMap;
    private String newString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_select);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //get category passed from previous class
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                newString = null;
            } else {
                newString = extras.getString("category");
            }
        }
        else {
            newString = (String) savedInstanceState.getSerializable("category");
        }

    }

    //query all buildings with category
    private void query() {
        //replace spaces with url encoding
        newString = newString.replace(" ", "%20");
        //api call
        String url = "http://138.197.11.189:3000/api/" + API_KEY +"/categories/" + newString;
        HttpConnectionJsonVariable con = new HttpConnectionJsonVariable();
        //create hashmap to store info
        HashMap<String, LatLng> markInfo = new HashMap<String, LatLng>();
        try {
            markInfo = con.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //loop through hashmap
        ArrayList<Marker> markers = new ArrayList<Marker>();
        Iterator it = markInfo.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            MarkerOptions options = new MarkerOptions()
                    .position(((LatLng) pair.getValue()))
                    .title(pair.getKey().toString());
            markers.add(mMap.addMarker(options));
            it.remove(); // avoids a ConcurrentModificationException
        }

        //create builder for marker bounds
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds bounds = builder.build();
                int padding = 100; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
            }
        });


        //set window adapter
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                //if marker is clicked, show marker descriptio activity
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(CategorySelectActivity.this, MarkerDescription.class);
                        String data = marker.getTitle();
                        intent.putExtra("marker", data);
                        startActivity(intent);

                    }
                });

                return null;

            }

        });
    }


    /**
     * Manipulates the map once available.
     **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        query();
    }
}
