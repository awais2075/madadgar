package com.madadgar.blood;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.madadgar.blood.doner.DonerDashboard;
import com.madadgar.blood.reciever.RecieverDashboard;
import com.madadgar.helper.Current;
import com.madadgar.objects.BloodDoner;
import com.madadgar.objects.BloodReciever;

public class BDLogin extends AppCompatActivity {

    // Attributes..
    private EditText email, password;
    private FirebaseAuth auth;
    private Spinner spinner;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bdlogin);

        email = (EditText) findViewById(R.id.emailLBD);
        password = (EditText) findViewById(R.id.passwordLBD);
        spinner = findViewById(R.id.bdtypes);

        ((TextView) findViewById(R.id.registerLabelBD)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(BDLogin.this, BDRegister.class));

            }
        });

        ((Button) findViewById(R.id.mainLoginBD)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog dialog = new ProgressDialog(BDLogin.this);
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Logging In.");
                dialog.show();
                final String child = spinner.getSelectedItemPosition() == 0 ? "doner" : "accepter";
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
                                            .getReference("users").child(child);
                                    reference.child(Current.KEY).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            dialog.dismiss();

                                            if(dataSnapshot == null){

                                                Toast.makeText(getApplicationContext(), "Email or Password is wrong!", Toast.LENGTH_SHORT).show();

                                            }else{

                                                if(child.equals("doner")){
                                                    Current.bloodDoner = dataSnapshot.getValue(BloodDoner.class);
                                                    if(Current.bloodDoner == null){

                                                        Toast.makeText(getApplicationContext(), "Email or Password is wrong!", Toast.LENGTH_SHORT).show();

                                                    }else{

                                                        startActivity(new Intent(BDLogin.this, DonerDashboard.class));

                                                    }
                                                }else{
                                                    Current.bloodReciever = dataSnapshot.getValue(BloodReciever.class);
                                                    if(Current.bloodReciever == null){

                                                        Toast.makeText(getApplicationContext(), "Email or Password is wrong!", Toast.LENGTH_SHORT).show();

                                                    }else{

                                                        startActivity(new Intent(BDLogin.this, RecieverDashboard.class));

                                                    }

                                                }

                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            dialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Email or Password is wrong!", Toast.LENGTH_SHORT).show();
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
