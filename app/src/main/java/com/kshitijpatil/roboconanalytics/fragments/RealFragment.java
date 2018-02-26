package com.kshitijpatil.roboconanalytics.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kshitijpatil.roboconanalytics.R;
import com.kshitijpatil.roboconanalytics.Utils;
import com.kshitijpatil.roboconanalytics.models.Institute;
import com.kshitijpatil.roboconanalytics.models.Match;
import com.kshitijpatil.roboconanalytics.subactivities.MatchMonitor;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.DATA;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.INDEX;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.REAL;

/**
 * A simple {@link Fragment} subclass.
 */
public class RealFragment extends Fragment {
    RecyclerView recyclerReal;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference dataRef = rootRef.child(DATA).child(REAL);

    public RealFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_real, container, false);
        recyclerReal = view.findViewById(R.id.recycler_real);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerReal.setLayoutManager(mLinearLayoutManager);
        recyclerReal.setHasFixedSize(true);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Match, TeamViewholder> adapter =
                new FirebaseRecyclerAdapter<Match, TeamViewholder>(
                        Match.class,
                        R.layout.row_match_view,
                        TeamViewholder.class,
                        dataRef
                ){

                    @Override
                    protected void populateViewHolder(TeamViewholder viewHolder, Match model, final int position) {
                        Institute institute = model.getInstitute();
                        viewHolder.setTeamName(institute.getName());
                        viewHolder.setTxtTime(model.getTimestamp());
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent monitorIntent = new Intent(getActivity(), MatchMonitor.class);
                                monitorIntent.putExtra("index", ((long) position));
                                monitorIntent.putExtra("type", REAL);
                                getContext().startActivity(monitorIntent);
                            }
                        });
                        viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {
                                AlertDialog dialog = createAlert(position);
                                dialog.show();
                                return true;
                            }
                        });
                    }
                };
        recyclerReal.setAdapter(adapter);
    }
    public AlertDialog createAlert(final long index){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_delete)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        Query query = dataRef.orderByChild(INDEX).equalTo(index);
                        DatabaseReference queryRef = query.getRef();
                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DatabaseReference deleteRef = null;
                                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                                    deleteRef = singleSnapshot.getRef();
                                }
                                deleteRef.removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        dialog.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
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
        }
        void setTxtTime(long timestamp){
            String time = Utils.getTimeDiff(timestamp);
            TextView txtTime = mView.findViewById(R.id.txt_time);
            txtTime.setText(time);
        }
    }
}
