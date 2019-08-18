package com.madadgar.serviceUser;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.madadgar.FrontActivity;
import com.madadgar.R;
import com.madadgar.blood.BDLogin;

public class ServiceSelector extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_selector);

        findViewById(R.id.emergencyserviceLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ServiceSelector.this, FrontActivity.class));
            }
        });

        findViewById(R.id.blooddonationLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ServiceSelector.this, BDLogin.class));
            }
        });

    }
}
