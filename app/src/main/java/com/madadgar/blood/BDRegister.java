package com.madadgar.blood;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.madadgar.R;
import com.madadgar.objects.BloodDoner;
import com.madadgar.objects.BloodGroup;
import com.madadgar.objects.BloodReciever;
import com.madadgar.objects.UserLocation;

public class BDRegister extends AppCompatActivity implements LocationListener {

    // Attributes
    private Button reg;
    private EditText name, password, email;
    private Spinner bloodType, userBD;
    private FirebaseAuth mauth;
    private LocationManager manager;
    private Location newLocation;

    private BloodGroup[] groups = new BloodGroup[]{
            BloodGroup.A_POSITIVE, BloodGroup.A_NEGATIVE,
            BloodGroup.B_POSITIVE, BloodGroup.B_NEGATIVE,
            BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE,
            BloodGroup.AB_POSITIVE, BloodGroup.AB_NEGATIVE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bdregister);

        try {
            manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Services should be turned ON.", Toast.LENGTH_LONG).show();
            } else {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, Criteria.ACCURACY_FINE, this);
            }
        }catch (Exception e){};


        reg = (Button) findViewById(R.id.regButtonBD);
        name = (EditText) findViewById(R.id.nameBD);
        password = (EditText) findViewById(R.id.passwordBD);
        email = (EditText) findViewById(R.id.emailBD);
        bloodType = findViewById(R.id.bloodTypeBD);
        userBD = findViewById(R.id.userBD);
        mauth = FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog dialog = new ProgressDialog(BDRegister.this);
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Completing Registration");
                dialog.show();

                (mauth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()))
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    (mauth.signInWithEmailAndPassword(email.getText().toString(),
                                            password.getText().toString())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            FirebaseUser user = mauth.getCurrentUser();
                                            mauth.signOut();
                                            String tempEmail = email.getText().toString().replaceAll("\\.","*");

                                            if(newLocation == null){
                                                Toast.makeText(getApplicationContext(),"Location Services should be turned on", Toast.LENGTH_LONG).show();
                                                return;
                                            }

                                            double altitude = newLocation.getAltitude();
                                            double longitude = newLocation.getLongitude();
                                            double latitude = newLocation.getLatitude();
                                            float speed = newLocation.getSpeed();
                                            float bearing = newLocation.getBearing();
                                            float accuracy = newLocation.getAccuracy(); //in meters
                                            long time = newLocation.getTime(); //when the time was obtained
                                            UserLocation userLocation = new UserLocation(altitude, longitude, latitude, speed, bearing, accuracy, time);

                                            if(userBD.getSelectedItemPosition() == 0){

                                                BloodGroup selected = groups[bloodType.getSelectedItemPosition()];
                                                BloodDoner doner = new BloodDoner(name.getText().toString(), tempEmail,
                                                        selected, null, userLocation);
                                                FirebaseDatabase.getInstance().getReference("users").child("doner")
                                                        .child(tempEmail)
                                                .setValue(doner).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(),"Error in Registration", Toast.LENGTH_LONG).show();
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(),"Registration Completed Successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });

                                            }else{

                                                BloodReciever reciever = new BloodReciever(name.getText().toString(),tempEmail,
                                                        null, userLocation);
                                                FirebaseDatabase.getInstance().getReference("users").child("accepter")
                                                        .child(tempEmail)
                                                        .setValue(reciever).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(),"Error in Registration", Toast.LENGTH_LONG).show();
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        dialog.dismiss();
                                                        Toast.makeText(getApplicationContext(),"Registration Completed Successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });

                                            }

                                        }
                                    });

                                }else{
                                    Toast.makeText(getApplicationContext(), "Email is already in use of another user.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("testingError", e.toString());
                    }
                });

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

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

}
