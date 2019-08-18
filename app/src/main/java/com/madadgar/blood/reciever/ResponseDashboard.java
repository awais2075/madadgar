package com.madadgar.blood.reciever;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.madadgar.objects.BloodReciever;

public class ResponseDashboard extends AppCompatActivity {

    // Attributes..
    private TextView name, blood, contact;
    private Button dimiss;
    private ProgressDialog dialog;
    private BloodReciever reciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_dashboard);

        dialog = new ProgressDialog(ResponseDashboard.this);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Loading Response.");
        dialog.show();

        name = findViewById(R.id.nameREPBlood);
        blood = findViewById(R.id.REPblood);
        contact = findViewById(R.id.contactREP);
        dimiss = findViewById(R.id.dismissDD);

        dimiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new ProgressDialog(ResponseDashboard.this);
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Processing.");
                dialog.show();

                reciever.response = null;
                FirebaseDatabase.getInstance().getReference("users").child("accepter").child(Current.KEY)
                        .setValue(reciever).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(getApplicationContext(),"Dismissed",Toast.LENGTH_LONG).show();
                        blood.setText("There is no any other response");
                        name.setVisibility(View.INVISIBLE);
                        contact.setVisibility(View.INVISIBLE);
                        dimiss.setVisibility(View.INVISIBLE);
                        dialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getApplicationContext(),"Error with database.",Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
            }
        });

        FirebaseDatabase.getInstance().getReference("users").child("accepter").child(Current.KEY)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        blood.setVisibility(View.VISIBLE);
                        reciever = dataSnapshot.getValue(BloodReciever.class);
                        if(reciever.response == null){
                            blood.setText("There is no any response.");
                        }else{

                            blood.setText("Blood: "+reciever.response.bloodGroup);
                            name.setVisibility(View.VISIBLE);
                            name.setText("Name: "+reciever.response.name);
                            contact.setVisibility(View.VISIBLE);
                            contact.setText("Contact: "+reciever.response.contactDetails);
                            dimiss.setVisibility(View.VISIBLE);

                        }
                        dialog.dismiss();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
}
