package com.madadgar.blood.doner;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.madadgar.objects.BloodReciever;
import com.madadgar.objects.BloodResponse;

public class DonerDashboard extends AppCompatActivity {

    // Attributes..
    private TextView blood, description, text;
    private EditText contact;
    private Button respond, cancel;
    private ProgressDialog dialog;
    private String key = "";
    private BloodDoner doner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doner_dashboard);

        dialog = new ProgressDialog(DonerDashboard.this);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading Requests.");
        dialog.show();

        blood = findViewById(R.id.requestIDDoner);
        description = findViewById(R.id.descriptionDoner);
        contact = findViewById(R.id.contactDetails);
        respond = findViewById(R.id.respondRRRR);
        cancel = findViewById(R.id.CancelDD);
        text = findViewById(R.id.textView80d);

        FirebaseDatabase.getInstance().getReference("users").child("doner").child(Current.KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        blood.setVisibility(View.VISIBLE);
                        doner = dataSnapshot.getValue(BloodDoner.class);
                        if(doner.request == null){
                            blood.setText("There is no any request.");
                        }else{

                            blood.setText("Blood Required: "+doner.request.blood.toString());
                            description.setVisibility(View.VISIBLE);
                            description.setText("Description: "+doner.request.description);
                            contact.setVisibility(View.VISIBLE);
                            respond.setVisibility(View.VISIBLE);
                            cancel.setVisibility(View.VISIBLE);
                            text.setVisibility(View.VISIBLE);

                        }
                        dialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        dialog.dismiss();
                    }
                });

        respond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(DonerDashboard.this);
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Processing.");
                dialog.show();

                final BloodResponse response = new BloodResponse(Current.bloodDoner.name,
                        contact.getText().toString(), Current.bloodDoner.bloodGroup);
                FirebaseDatabase.getInstance().getReference("users").child("accepter").child(doner.request.key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                BloodReciever reciever = dataSnapshot.getValue(BloodReciever.class);
                                reciever.response = response;
                                FirebaseDatabase.getInstance().getReference("users").child("accepter").child(doner.request.key)
                                        .setValue(reciever).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Response is failed.",Toast.LENGTH_LONG).show();

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(getApplicationContext(), "Responded Successfully.", Toast.LENGTH_LONG).show();
                                        blood.setText("There is no any other request");
                                        description.setVisibility(View.INVISIBLE);
                                        contact.setVisibility(View.INVISIBLE);
                                        respond.setVisibility(View.INVISIBLE);
                                        cancel.setVisibility(View.INVISIBLE);
                                        text.setVisibility(View.INVISIBLE);
                                        dialog.dismiss();
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Toast.makeText(getApplicationContext(),"Response is failed.",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(DonerDashboard.this);
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Processing.");
                dialog.show();
                doner.request = null;
                FirebaseDatabase.getInstance().getReference("users").child("doner").child(Current.KEY)
                        .setValue(doner).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed in cancelling", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                    }
                });

            }
        });

    }
}
