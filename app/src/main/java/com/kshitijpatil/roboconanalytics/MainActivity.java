package com.kshitijpatil.roboconanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kshitijpatil.roboconanalytics.subactivities.BrowseMatches;
import com.kshitijpatil.roboconanalytics.subactivities.MatchDetails;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    CustomDialogClass customDialog;
    Button btnTrack,btnBrowse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customDialog = new CustomDialogClass(MainActivity.this);
        btnTrack = findViewById(R.id.button_new_match);
        btnBrowse = findViewById(R.id.button_browse_matches);
        btnTrack.setOnClickListener(this);
        btnBrowse.setOnClickListener(this);
    }

    public void addInstitute(View view) {
        customDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_new_match:
                startActivity(new Intent(MainActivity.this, MatchDetails.class));
                break;
            case R.id.button_browse_matches:
                startActivity(new Intent(MainActivity.this, BrowseMatches.class));
                break;
        }
    }
}
