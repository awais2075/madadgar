package com.madadgar;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.madadgar.serviceProvider.ServiceProviderUM;
import com.madadgar.serviceUser.SelectEmergency;

public class FrontActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        (findViewById(R.id.serviceProviderLL)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(FrontActivity.this, ServiceProviderUM.class));

            }
        });

        (findViewById(R.id.serviceUserLL)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(FrontActivity.this, SelectEmergency.class));

            }
        });

    }
}
