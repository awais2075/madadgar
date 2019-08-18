package com.madadgar;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.madadgar.helper.Current;
import com.madadgar.objects.ServiceProvider;
import com.madadgar.objects.UserLocation;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btn;
    private MarkerOptions currentLocation;
    private MarkerOptions incomingLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        btn = findViewById(R.id.completed);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"Failed to get location", Toast.LENGTH_SHORT).show();
            return;
        }
        mMap.setMyLocationEnabled(true);
        // Islamabad..
        CameraPosition position = CameraPosition.builder().target(new LatLng(33.675628,73.0677534))
                .zoom(7).bearing(0).tilt(45).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

        if(Current.databaseReference != null){
            Current.databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    ServiceProvider provider = dataSnapshot.getValue(ServiceProvider.class);
                    UserLocation location = provider.userLocation;

                    mMap.clear();
                    incomingLocation = new MarkerOptions().position(new LatLng(location.latitude, location.longitude))
                            .title("Helping Service").icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance));
                    mMap.addMarker(incomingLocation);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{

            incomingLocation = new MarkerOptions().position(new LatLng(Current.request.location.latitude, Current.request.location.longitude))
                    .title("Help Needed").icon(BitmapDescriptorFactory.fromResource(R.drawable.life));
            mMap.addMarker(incomingLocation);

        }

    }

}
