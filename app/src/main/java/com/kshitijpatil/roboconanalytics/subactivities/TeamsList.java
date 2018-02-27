package com.kshitijpatil.roboconanalytics.subactivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.kshitijpatil.roboconanalytics.R;

public class TeamsList extends AppCompatActivity {
    RecyclerView recyclerTeams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_list);

        recyclerTeams = findViewById(R.id.recycler_teams);
    }
}
