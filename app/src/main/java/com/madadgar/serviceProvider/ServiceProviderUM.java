package com.madadgar.serviceProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.madadgar.R;
import com.madadgar.helper.Current;
import com.madadgar.objects.ServiceProvider;
import com.madadgar.services.LocationService;

public class ServiceProviderUM extends AppCompatActivity {

    // Attributes..
    private EditText email, password;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_um);

        email = (EditText) findViewById(R.id.emailL);
        password = (EditText) findViewById(R.id.passwordL);

        ((Button) findViewById(R.id.mainLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog dialog = new ProgressDialog(ServiceProviderUM.this);
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Logging In.");
                dialog.show();
                auth = FirebaseAuth.getInstance();
                (auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()))
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    firebaseUser = auth.getCurrentUser();
                                    Current.EMAIL = email.getText().toString();
                                    Current.KEY = Current.EMAIL.replaceAll("\\.","*");
                                    DatabaseReference reference = FirebaseDatabase.getInstance()
                                            .getReference("users").child("serviceprovider");
                                    reference.child(Current.KEY).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            ServiceProvider user = dataSnapshot.getValue(ServiceProvider.class);
                                            Current.NAME = user.name;
                                            Current.userLocation = user.userLocation;
                                            dialog.dismiss();
                                            startService(new Intent(ServiceProviderUM.this, LocationService.class));
                                            startActivity(new Intent(ServiceProviderUM.this, ServerProviderDashboard.class));
                                            finish();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Toast.makeText(getApplicationContext(), "Error in fetching details.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else{
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Email or Password is wrong!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        });

    }
}
