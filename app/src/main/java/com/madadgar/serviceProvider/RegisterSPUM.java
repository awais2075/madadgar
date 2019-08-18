package com.madadgar.serviceProvider;

import android.app.ProgressDialog;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madadgar.R;
import com.madadgar.objects.Request;
import com.madadgar.objects.ServiceProvider;

import java.util.ArrayList;

public class RegisterSPUM extends AppCompatActivity {

    // Attributes
    private Button reg;
    private EditText name, password, email;
    private Spinner serviceType;
    private FirebaseAuth mauth;
    private String[] stArray = new String[]{"road","fire","medical","building","drowning","blast"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_spum);

        reg = (Button) findViewById(R.id.regButton);
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        serviceType = findViewById(R.id.serviceType);
        mauth = FirebaseAuth.getInstance();

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog dialog = new ProgressDialog(RegisterSPUM.this);
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

                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child("serviceprovider");

                                            ServiceProvider spUser = new ServiceProvider(name.getText().toString(), tempEmail, null,
                                                    stArray[serviceType.getSelectedItemPosition()], new ArrayList<Request>());
                                            db.child(tempEmail).setValue(spUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(),"Registration Completed Successfully", Toast.LENGTH_SHORT).show();
                                            finish();

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


}
