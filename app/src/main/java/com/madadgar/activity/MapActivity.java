package com.madadgar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.madadgar.R;
import com.madadgar.enums.EmergencyStatus;
import com.madadgar.model.Emergency;
import com.madadgar.model.retrofit.Direction;
import com.madadgar.model.retrofit.Polyline;
import com.madadgar.model.retrofit.Route;
import com.madadgar.retrofit.ApiClient;
import com.madadgar.retrofit.ApiInterface;
import com.madadgar.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap googleMap;

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
    public void onMapReady(GoogleMap map) {
        googleMap = map;


        Emergency emergency = (Emergency) getIntent().getSerializableExtra("emergency");

        if (emergency.getEmergencyStatus() != EmergencyStatus.PENDING) {

            callRestApi(emergency);
        } else {
            drawSinglePointMap(emergency);
        }


        /*CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng) // Sets the center of the map to
                .zoom(19)                   // Sets the zoom
                .bearing(1) // Sets the orientation of the camera to east
                .tilt(90)    // Sets the tilt of the camera to 30 degrees
                .build();    // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                cameraPosition));
    */
    }

    private void drawSinglePointMap(Emergency emergency) {
        String[] emergencyLocation = emergency.getEmergencyLocation().split(":");
        double latitude = Double.parseDouble(emergencyLocation[0]);
        double longitude = Double.parseDouble(emergencyLocation[1]);

        String emergencyAddress = emergency.getEmergencyLocationAddress();
        LatLng latlng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latlng).title(emergency.getEmergencyType()).snippet(emergencyAddress)).showInfoWindow();

        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));*/

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlng) // Sets the center of the map to
                .zoom(19)                   // Sets the zoom
                .bearing(1) // Sets the orientation of the camera to east
                .tilt(90)    // Sets the tilt of the camera to 30 degrees
                .build();    // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                cameraPosition));
    }

    private void callRestApi(Emergency emergency) {

        HashMap hm = new HashMap();
        hm.put("origin", emergency.getEmergencyLocation().replace(":",","));
        hm.put("destination", emergency.getStaffLocation().replace(":",","));
        hm.put("key", getString(R.string.google_maps_key));
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<Direction> call = apiService.getDirections(hm);
        call.enqueue(new Callback<Direction>() {
            @Override
            public void onResponse(Call<Direction> call, Response<Direction> response) {
                Util.showToast(MapActivity.this, "Success");
                if (response.body().getStatus().equals("OK")) {
                    drawMap(response.body().getRoutes());
                }
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
                Util.showToast(MapActivity.this, "Failure" + t.getMessage());
            }
        });

    }

    private void drawMap(List<Route> routes) {

        double latitude = routes.get(0).getLegs().get(0).getStartLocation().getLat();
        double longitude = routes.get(0).getLegs().get(0).getStartLocation().getLng();
        //LatLng latlng = new LatLng(latitude, longitude);

        String startAddress = routes.get(0).getLegs().get(0).getStartAddress();
        String endAddress = routes.get(0).getLegs().get(0).getEndAddress();
        LatLng startLocation = new LatLng(latitude, longitude);

        latitude = routes.get(0).getLegs().get(0).getEndLocation().getLat();
        longitude = routes.get(0).getLegs().get(0).getEndLocation().getLng();

        LatLng endLocation = new LatLng(latitude, longitude);
        List<Marker> markerList = new ArrayList<>();
        Marker startMarker = googleMap.addMarker(new MarkerOptions().position(startLocation).title("User").snippet(startAddress));
        Marker endMarker = googleMap.addMarker(new MarkerOptions().position(endLocation).title("Staff").snippet(endAddress));
        startMarker.showInfoWindow();
        endMarker.showInfoWindow();
        markerList.add(startMarker);
        markerList.add(endMarker);


        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(2.0f).geodesic(true);

        polylineOptions.color(getResources().getColor(R.color.colorAccent));
        List<LatLng> pointsList = PolyUtil.decode(routes.get(0).getOverviewPolyline().getPoints());
        for (int i = 0; i < pointsList.size(); i++) {
            polylineOptions.add(pointsList.get(i));
        }

        googleMap.addPolyline(polylineOptions);
        updateCamera(googleMap, markerList);


    }

    private void updateCamera(GoogleMap map, List<Marker> markerList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < markerList.size(); i++) {
            builder.include(markerList.get(i).getPosition());
        }

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        //map.moveCamera(cameraUpdate);
        map.animateCamera(cameraUpdate);

    }

    @Override
    public void onLocationChanged(Location location) {
        Util.showToast(this, location.getLatitude() + "," + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
