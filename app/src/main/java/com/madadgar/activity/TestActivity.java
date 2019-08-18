package com.madadgar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.madadgar.R;
import com.madadgar.service.BgService;

public class TestActivity extends AppCompatActivity {


    private TextView textView_startService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        textView_startService = findViewById(R.id.textView_startService);

        textView_startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView_startService.getText().toString().equals("Started")) {
                    textView_startService.setText("Stopped");
                    stopService(new Intent(v.getContext(), BgService.class));
                } else {
                    textView_startService.setText("Started");
                    startService(new Intent(v.getContext(), BgService.class));
                }
            }
        });

    }
}
