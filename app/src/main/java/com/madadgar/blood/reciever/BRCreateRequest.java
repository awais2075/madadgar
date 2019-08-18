package com.madadgar.blood.reciever;

import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madadgar.R;
import com.madadgar.helper.Current;
import com.madadgar.objects.BloodDoner;
import com.madadgar.objects.BloodGroup;
import com.madadgar.objects.BloodRequest;
import com.madadgar.objects.UserLocation;

public class BRCreateRequest extends AppCompatActivity {

    private EditText description;
    private Spinner bloodtype;
    private BloodGroup[] groups = new BloodGroup[]{
            BloodGroup.A_POSITIVE, BloodGroup.A_NEGATIVE,
            BloodGroup.B_POSITIVE, BloodGroup.B_NEGATIVE,
            BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE,
            BloodGroup.AB_POSITIVE, BloodGroup.AB_NEGATIVE
    };
    private ProgressDialog dialog;
    private boolean found = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brcreate_request);

        description = findViewById(R.id.descriptionBRR);
        bloodtype = findViewById(R.id.bloodTypeBRequest);
        (findViewById(R.id.createRequestBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(BRCreateRequest.this);
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Creating request.");
                dialog.show();
                found = false;
                final BloodGroup toFind = groups[bloodtype.getSelectedItemPosition()];
                Log.d("testingER",toFind+"");
                FirebaseDatabase.getInstance().getReference("users").child("doner")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                                DataSnapshot snapshotSelected = null;
                                double locationValue = Double.MAX_VALUE;
                                UserLocation recieverLocation = Current.bloodReciever.userLocation;
                                for(DataSnapshot snapshot: snapshotIterator){

                                    BloodDoner doner = snapshot.getValue(BloodDoner.class);
                                    double newValue = getLocationDifference(recieverLocation,
                                            doner.userLocation);
                                    if(doner.bloodGroup == toFind && locationValue > newValue){

                                        found = true;
                                        snapshotSelected = snapshot;snapshot.getValue(BloodDoner.class);
                                        locationValue = newValue;

                                    }

                                }
                                if(!found){
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(),"No any doner found.",Toast.LENGTH_LONG).show();
                                }else{

                                    BloodDoner doner = snapshotSelected.getValue(BloodDoner.class);
                                    doner.request = new BloodRequest(Current.KEY, description.getText().toString(),
                                            toFind);
                                    FirebaseDatabase.getInstance().getReference("users").child("doner")
                                            .child(snapshotSelected.getKey()).setValue(doner).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Request created successfully.",Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(getApplicationContext(),"Error in creating request", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();
                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Error in creating request",Toast.LENGTH_LONG).show();

                            }
                        });

            }
        });

    }


    private double  getLocationDifference(UserLocation location1, UserLocation location2){

        double value = Math.abs(location1.latitude - location2.latitude);
        value += Math.abs(location1.longitude - location2.longitude);
        return value;

    }

}
