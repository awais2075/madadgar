package com.madadgar.serviceUser;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.madadgar.R;
import com.madadgar.helper.Current;

public class SelectEmergency extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_emergency);

        (findViewById(R.id.roadAccident)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Current.emergencyType = "road";
                startActivity(new Intent(SelectEmergency.this, EmergencyDetails.class));
            }
        });

        (findViewById(R.id.fireService)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Current.emergencyType = "fire";
                startActivity(new Intent(SelectEmergency.this, EmergencyDetails.class));
            }
        });

        (findViewById(R.id.medicalEmergency)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Current.emergencyType = "medical";
                startActivity(new Intent(SelectEmergency.this, EmergencyDetails.class));
            }
        });

        (findViewById(R.id.buildingCollapse)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Current.emergencyType = "building";
                startActivity(new Intent(SelectEmergency.this, EmergencyDetails.class));
            }
        });

        (findViewById(R.id.drowning)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Current.emergencyType = "drowning";
                startActivity(new Intent(SelectEmergency.this, EmergencyDetails.class));
            }
        });

        (findViewById(R.id.blast)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Current.emergencyType = "blast";
                startActivity(new Intent(SelectEmergency.this, EmergencyDetails.class));
            }
        });

    }

}
