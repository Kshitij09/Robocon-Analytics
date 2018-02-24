package com.kshitijpatil.roboconanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kshitijpatil.roboconanalytics.subactivities.MatchDetails;

public class MainActivity extends AppCompatActivity {
    CustomDialogClass customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customDialog = new CustomDialogClass(MainActivity.this);
        ((Button) findViewById(R.id.button_new_match)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MatchDetails.class));
            }
        });
    }

    public void addInstitute(View view) {
        customDialog.show();
    }
}
