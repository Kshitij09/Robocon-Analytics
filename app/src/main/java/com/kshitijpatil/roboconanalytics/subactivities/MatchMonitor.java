package com.kshitijpatil.roboconanalytics.subactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kshitijpatil.roboconanalytics.R;
import com.kshitijpatil.roboconanalytics.adapters.DataModifiersAdapter;
import com.kshitijpatil.roboconanalytics.models.DataModel;

import java.util.ArrayList;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.DATA;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.TEAMS;

public class MatchMonitor extends AppCompatActivity {
    public DataModifiersAdapter adapter;
    ArrayList<DataModel> dataModels;
    ListView listControls;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference dataRef = rootRef.child(DATA);
    DatabaseReference teamsRef = rootRef.child(TEAMS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_monitor);
        listControls = findViewById(R.id.list_controls);
        initDataModels();
    }

    private void initDataModels() {
        dataModels = new ArrayList<>();
        final String[] labels = {"TZ1 Successful hits",
                "TZ1 Miss",
                "TZ2 Successful hits",
                "TZ2 Miss",
                "TZ3 Successful hits",
                "TZ3 Miss",
                "Successful pass",
                "Not pass",};

        for (int i = 0; i < labels.length; i++) {
            dataModels.add(new DataModel(labels[i], "0"));
        }

        Intent intent = getIntent();
        long index = ((long) intent.getExtras().get("index"));
        String type = intent.getExtras().get("type").toString();

        adapter = new DataModifiersAdapter(dataModels, index, type, getApplicationContext());
        listControls.setAdapter(adapter);
        /*DatabaseReference matchRef = dataRef.child(TYPE);
        Query query = matchRef.orderByChild(INDEX).equalTo(index);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Institute institute = null;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    institute = singleSnapshot.getValue(Institute.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }
}
