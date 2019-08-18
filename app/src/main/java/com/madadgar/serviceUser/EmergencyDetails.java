package com.madadgar.serviceUser;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.madadgar.MapsActivity;
import com.madadgar.R;
import com.madadgar.helper.Current;
import com.madadgar.objects.Request;
import com.madadgar.objects.ServiceProvider;
import com.madadgar.objects.UserLocation;

import java.io.IOException;
import java.util.UUID;

public class EmergencyDetails extends AppCompatActivity  implements LocationListener {

    private int PICK_IMAGE_REQUEST = 1;
    private ProgressDialog progressDialog;
    private Bitmap bitmapImage;
    private Uri uri;
    private boolean providerFound = false;
    private LocationManager manager;
    private Location newLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_details);

        try {
            manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Services should be turned ON.", Toast.LENGTH_LONG).show();
            } else {

                Log.d("testingER","started..");
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, Criteria.ACCURACY_FINE, this);
            }
        }catch (Exception e){

            Log.d("testingER","err: "+e.toString());
        };

        (findViewById(R.id.eventImage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

        (findViewById(R.id.sendrequestBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(EmergencyDetails.this);
                progressDialog.setMessage("Searching...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                // Uploading image.
                final String filename = UUID.randomUUID().toString()+".png";
                StorageReference reference = FirebaseStorage.getInstance().getReference();
                StorageReference childReference = reference.child(filename);
                UploadTask task = childReference.putFile(uri);
                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        DatabaseReference reference = FirebaseDatabase.getInstance()
                                .getReference("users").child("serviceprovider");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                                UserLocation userLocations = null;

                                DataSnapshot snapshotSelected = null;
                                double locationValue = Double.MAX_VALUE;
                                if(newLocation == null){
                                    Toast.makeText(getApplicationContext(), "PLEASE GIVE ACCESS OF LOCATION.", Toast.LENGTH_LONG).show();
                                    return;
                                }else{
                                    double altitude = newLocation.getAltitude();
                                    double longitude = newLocation.getLongitude();
                                    double latitude = newLocation.getLatitude();
                                    float speed = newLocation.getSpeed();
                                    float bearing = newLocation.getBearing();
                                    float accuracy = newLocation.getAccuracy(); //in meters
                                    long time = newLocation.getTime(); //when the time was obtained
                                    userLocations = new UserLocation(altitude, longitude, latitude, speed, bearing, accuracy, time);
                                }
                                for(DataSnapshot snapshot: snapshotIterator){

                                    final ServiceProvider provider = snapshot.getValue(ServiceProvider.class);
                                    if(userLocations != null && provider.userLocation != null){

                                        double newValue = getLocationDifference(userLocations, provider.userLocation);

                                        if(provider.service.equals(Current.emergencyType) && locationValue > newValue){

                                            providerFound = true;
                                            locationValue = newValue;
                                            snapshotSelected = snapshot;

                                        }
                                    }

                                }
                                if(!providerFound){
                                    Log.d("testingER","not found..");
                                    Toast.makeText(getApplicationContext(),"No any service provider found.", Toast.LENGTH_LONG).show();
                                }else{
                                    final ServiceProvider provider = snapshotSelected.getValue(ServiceProvider.class);
                                    Current.emergencyProviderKey = provider.key;
                                    String description = ((EditText)findViewById(R.id.detailsOfEvent)).getText().toString();
                                    Request request = new Request(description, filename, userLocations);
                                    provider.emergencyRequests.add(request);
                                    FirebaseDatabase.getInstance()
                                            .getReference("users").child("serviceprovider")
                                            .child(snapshotSelected.getKey()).setValue(provider)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    progressDialog.dismiss();
                                                    Current.databaseReference = FirebaseDatabase.getInstance().getReference("users").child("serviceprovider")
                                                            .child(provider.key);
                                                    startActivity(new Intent(EmergencyDetails.this, MapsActivity.class));
                                                    finish();


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"Error"+e.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                        Log.d("testingER",e.toString());
                        Toast.makeText(getApplicationContext(), "Fail while updating image.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uri = data.getData();

            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageView = (ImageView) findViewById(R.id.eventImage);
                imageView.setImageBitmap(bitmapImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d("testingER","updated..");
        this.newLocation = location;

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

    private double  getLocationDifference(UserLocation location1, UserLocation location2){

        double value = Math.abs(location1.latitude - location2.latitude);
        value += Math.abs(location1.longitude - location2.longitude);
        return value;

    }

}
