package com.kshitijpatil.roboconanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kshitijpatil.roboconanalytics.subactivities.BrowseMatches;
import com.kshitijpatil.roboconanalytics.subactivities.MatchDetails;
import com.kshitijpatil.roboconanalytics.subactivities.TeamsList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    CustomDialogClass customDialog;
    Button btnTrack,btnBrowse,btnTeam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customDialog = new CustomDialogClass(MainActivity.this);
        btnTrack = findViewById(R.id.button_new_match);
        btnBrowse = findViewById(R.id.button_browse_matches);
        btnTeam = findViewById(R.id.button_browse_teams);
        btnTrack.setOnClickListener(this);
        btnBrowse.setOnClickListener(this);
        btnTeam.setOnClickListener(this);
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
            case R.id.button_browse_teams:
                startActivity(new Intent(MainActivity.this, TeamsList.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                signout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // User is now signed out
                        startActivity(new Intent(MainActivity.this, SplashActivity.class));
                        finish();
                    }
                });
    }
}
