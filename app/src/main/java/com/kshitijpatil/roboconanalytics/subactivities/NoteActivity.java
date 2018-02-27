package com.kshitijpatil.roboconanalytics.subactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.kshitijpatil.roboconanalytics.models.Match;
import com.kshitijpatil.roboconanalytics.models.Note;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.DATA;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.INDEX;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.NOTES;

public class NoteActivity extends AppCompatActivity {
    private static final String TAG = "NoteActivity";
    RecyclerView recyclerNotes;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference dataRef = rootRef.child(DATA);
    DatabaseReference matchRef;
    Query query;
    String type;
    long index;
    EditText noteText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Intent intent = getIntent();
        type = intent.getExtras().get("type").toString();
        index = ((long) intent.getExtras().get("index"));
        noteText = findViewById(R.id.text_note);

        matchRef = dataRef.child(type);
        query = matchRef.orderByChild(INDEX).equalTo(index);

        recyclerNotes = findViewById(R.id.recycler_notes);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(NoteActivity.this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerNotes.setLayoutManager(mLinearLayoutManager);
        recyclerNotes.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference matchRef = dataRef.child(type);
        Query query = matchRef.orderByChild(INDEX).equalTo(index).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference singleRef = null;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    singleRef = snapshot.getRef();
                }
                Query noteQuery = singleRef.child(NOTES).orderByKey();
                FirebaseRecyclerAdapter<Note,NoteViewholder> adapter =
                        new FirebaseRecyclerAdapter<Note, NoteViewholder>(
                                Note.class,
                                R.layout.row_note_view,
                                NoteViewholder.class,
                                noteQuery
                        ) {
                            @Override
                            protected void populateViewHolder(NoteViewholder viewHolder, Note model, int position) {
                                viewHolder.setNote(model.getNote());
                            }
                        };
                recyclerNotes.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*FirebaseRecyclerAdapter<Note,NoteViewholder> adapter =
                new FirebaseRecyclerAdapter<Note, NoteViewholder>(
                        Note.class,
                        R.layout.row_note_view,
                        NoteViewholder.class,
                        queryRef
                ) {
                    @Override
                    protected void populateViewHolder(NoteViewholder viewHolder, Note model, int position) {
                        Log.d(TAG, "populateViewHolder: "+model.getNote());
                    }
                };
        recyclerNotes.setAdapter(adapter);*/
    }

    public void addNote(View view) {
        if (!TextUtils.isEmpty(noteText.getText())){
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference matchRef = null;
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                        matchRef = snapshot.getRef();
                    }
                    matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Match match = dataSnapshot.getValue(Match.class);
                            match.addNote(new Note(noteText.getText().toString()));
                            noteText.setText("");
                            dataSnapshot.getRef().setValue(match);
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

    public static class NoteViewholder extends RecyclerView.ViewHolder{
        View mView;
        TextView txtNote;
        ImageView imgBullet;
        public NoteViewholder(View itemView) {
            super(itemView);
            mView = itemView;
            txtNote = itemView.findViewById(R.id.txt_note);
            imgBullet = itemView.findViewById(R.id.img_bullet);
        }
        void setNote(String note){
            imgBullet.setImageDrawable(mView.getResources().getDrawable(R.drawable.ic_bullet));
            txtNote.setText(note);
        }
        void setTxtTime(long timestamp){
            String time = Utils.getTimeDiff(timestamp);
            TextView txtTime = mView.findViewById(R.id.txt_time);
            txtTime.setText(time);
        }
    }
}
