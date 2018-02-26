package com.kshitijpatil.roboconanalytics.subactivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kshitijpatil.roboconanalytics.R;
import com.kshitijpatil.roboconanalytics.models.Institute;
import com.kshitijpatil.roboconanalytics.models.Match;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;

import java.util.ArrayList;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.DATA;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.NAME;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.PRACTICE;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.REAL;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.TEAMS;

public class MatchDetails extends AppCompatActivity {
    private static final String TAG = "MatchDetails";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference dataRef = rootRef.child(DATA);
    DatabaseReference teamsRef = rootRef.child(TEAMS);

    Button btnNext;
    ProgressDialog dialog;
    AutoCompleteTextView textTeamName;
    MultiStateToggleButton tbMatchType;
    ArrayAdapter<String> adapter;
    ArrayList<String> teamsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_details);

        btnNext = findViewById(R.id.button_next);
        textTeamName = findViewById(R.id.text_team_name);
        dialog = new ProgressDialog(MatchDetails.this);
        tbMatchType = findViewById(R.id.toggle_match_type);
        dialog.setMessage("Initiating records...");

        teamsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        initTeamsList();

    }

    private void initTeamsList() {
        teamsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                teamsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    teamsList.add(snapshot.child(NAME).getValue().toString());
                }
                adapter.clear();
                adapter.addAll(teamsList);
                textTeamName.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addNewMatch(View view) {
        if (tbMatchType.getValue() == -1) {
            Toast.makeText(this, "Please select match type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(textTeamName.getText())) {
            textTeamName.setError("Please enter team name");
            return;
        }
        final String name = textTeamName.getText().toString();
        if (!teamsList.contains(name)) {
            textTeamName.setError("Team does not exist");
            return;
        }
        dialog.show();
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DatabaseReference newMatch;
                if (tbMatchType.getValue() == 0)
                    newMatch = dataRef.child(PRACTICE);
                else
                    newMatch = dataRef.child(REAL);

                newMatch.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final long index = dataSnapshot.getChildrenCount();
                        final String type = (tbMatchType.getValue() == 0) ? PRACTICE : REAL;
                        Institute institute = new Institute(name, index);
                        DatabaseReference newInstituteRef = newMatch.push();
                        long timestamp = System.currentTimeMillis();
                        Match match = new Match(index,timestamp,institute);
                        newInstituteRef.setValue(match).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                Intent monitorIntent = new Intent(MatchDetails.this, MatchMonitor.class);
                                monitorIntent.putExtra("index", index);
                                monitorIntent.putExtra("type", type);
                                monitorIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(monitorIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
