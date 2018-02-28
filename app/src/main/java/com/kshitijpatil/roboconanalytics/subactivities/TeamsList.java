package com.kshitijpatil.roboconanalytics.subactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kshitijpatil.roboconanalytics.R;
import com.kshitijpatil.roboconanalytics.models.Team;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.TEAMS;

public class TeamsList extends AppCompatActivity {
    RecyclerView recyclerTeams;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference teamsRef = rootRef.child(TEAMS);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams_list);

        recyclerTeams = findViewById(R.id.recycler_teams);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerTeams.setLayoutManager(mLinearLayoutManager);
        recyclerTeams.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Team, TeamViewholder> adapter =
                new FirebaseRecyclerAdapter<Team, TeamViewholder>(
                        Team.class,
                        R.layout.row_match_view,
                        TeamViewholder.class,
                        teamsRef
                ) {
                    @Override
                    protected void populateViewHolder(TeamViewholder viewHolder, final Team model, int position) {
                        viewHolder.setTeamName(model.getName());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent teamIntent = new Intent(TeamsList.this,TeamActivity.class);
                                teamIntent.putExtra("name",model.getName());
                                startActivity(teamIntent);
                            }
                        });
                    }
                };
        recyclerTeams.setAdapter(adapter);
    }
    public static class TeamViewholder extends RecyclerView.ViewHolder{
        View mView;
        TextView txtName;
        TextView txtTime;
        public TeamViewholder(View itemView) {
            super(itemView);
            mView = itemView;
            txtName = itemView.findViewById(R.id.txt_team_name);
            txtTime = itemView.findViewById(R.id.txt_time);
        }
        void setTeamName(String name){
            TextView txtName = mView.findViewById(R.id.txt_team_name);
            txtName.setText(name);
            txtTime.setVisibility(View.GONE);
        }
    }
}
