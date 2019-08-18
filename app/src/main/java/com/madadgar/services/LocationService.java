package com.madadgar.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madadgar.helper.Current;
import com.madadgar.objects.ServiceProvider;
import com.madadgar.objects.UserLocation;

public class LocationService extends Service implements LocationListener {

    private LocationManager manager;

    public LocationService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Services should be turned ON.", Toast.LENGTH_SHORT).show();
            } else {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, Criteria.ACCURACY_FINE, this);
            }
        }catch (Exception e){};

        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {

        final Location newLocation = location;
        FirebaseDatabase.getInstance().getReference("users").child("serviceprovider").child(Current.KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ServiceProvider serviceProvider = dataSnapshot.getValue(ServiceProvider.class);
                double altitude = newLocation.getAltitude();
                double longitude = newLocation.getLongitude();
                double latitude = newLocation.getLatitude();
                float speed = newLocation.getSpeed();
                float bearing = newLocation.getBearing();
                float accuracy = newLocation.getAccuracy(); //in meters
                long time = newLocation.getTime(); //when the time was obtained
                serviceProvider.userLocation = new UserLocation(altitude, longitude, latitude, speed, bearing, accuracy, time);
                FirebaseDatabase.getInstance().getReference("users").child("serviceprovider").child(Current.KEY)
                        .setValue(serviceProvider).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Location Updated", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
