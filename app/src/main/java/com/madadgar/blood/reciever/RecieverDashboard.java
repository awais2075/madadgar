package com.madadgar.blood.reciever;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.madadgar.R;

public class RecieverDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciever_dashboard);


        (findViewById(R.id.requestRD)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(RecieverDashboard.this, BRCreateRequest.class));

            }
        });

        (findViewById(R.id.responseRD)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(RecieverDashboard.this, ResponseDashboard.class));

            }
        });

    }

}
