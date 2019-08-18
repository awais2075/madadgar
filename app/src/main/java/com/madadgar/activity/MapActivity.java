package com.madadgar.activity;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.madadgar.R;
import com.madadgar.model.Emergency;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


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
        GoogleMap mMap = googleMap;

        // Add a marker in Sydney and move the camera

        Emergency emergency = (Emergency) getIntent().getSerializableExtra("emergency");

        double latitude = Double.parseDouble(emergency.getEmergencyLocation().split(":")[0]);
        double longitude = Double.parseDouble(emergency.getEmergencyLocation().split(":")[1]);

        LatLng latlng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latlng).title(emergency.getEmergencyType()).snippet(emergency.getEmergencyLocationAddress())).showInfoWindow();
        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));*/

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng) // Sets the center of the map to
                .zoom(19)                   // Sets the zoom
                .bearing(1) // Sets the orientation of the camera to east
                .tilt(90)    // Sets the tilt of the camera to 30 degrees
                .build();    // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                cameraPosition));
    }
}
