package com.kshitijpatil.roboconanalytics.subactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kshitijpatil.roboconanalytics.R;
import com.kshitijpatil.roboconanalytics.models.Institute;
import com.kshitijpatil.roboconanalytics.models.Note;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.DATA;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.NOTES;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.PRACTICE;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.REAL;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.TEAMS;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.TEAMSDATA;

public class TeamActivity extends AppCompatActivity {
    BarChart chart;
    ArrayList<BarEntry> entriesPractice = new ArrayList<>();
    ArrayList<BarEntry> entriesReal = new ArrayList<>();
    BarDataSet setPractice,setReal;
    BarData data;
    RecyclerView recyclerNotes;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference practiceRef = rootRef.child(DATA).child(PRACTICE);
    DatabaseReference realRef = rootRef.child(DATA).child(REAL);
    DatabaseReference teamDataPracticeRef = rootRef.child(TEAMSDATA).child(PRACTICE);
    DatabaseReference teamDataRealRef = rootRef.child(TEAMSDATA).child(REAL);
    private String TAG = TeamActivity.class.getSimpleName();
    float barWidth = 0.43f;
    float groupSpace = 0.1f;
    float barSpace = 0.01f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);
        recyclerNotes = findViewById(R.id.recycler_notes);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(TeamActivity.this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        recyclerNotes.setLayoutManager(mLinearLayoutManager);
        recyclerNotes.setHasFixedSize(true);
        initGraph();
        handleDataUpdates();
        initValues();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String name = intent.getExtras().get("name").toString();
        handleDataUpdates();
        DatabaseReference noteRef = rootRef.child(TEAMS).child(name).child(NOTES);
        FirebaseRecyclerAdapter<Note,NoteViewholder> adapter =
                new FirebaseRecyclerAdapter<Note, NoteViewholder>(
                        Note.class,
                        R.layout.row_note_view,
                        NoteViewholder.class,
                        noteRef
                ) {
                    @Override
                    protected void populateViewHolder(NoteViewholder viewHolder, Note model, int position) {
                       viewHolder.setNote(model.getNote());
                    }
                };
        recyclerNotes.setAdapter(adapter);
        initValues();
    }

    private void initGraph() {
        chart = findViewById(R.id.bar_chart);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setPinchZoom(true);
        chart.setMaxVisibleValueCount(100);
        chart.setHorizontalScrollBarEnabled(true);

        entriesPractice.add(new BarEntry(1.0f, 0f));
        entriesPractice.add(new BarEntry(2.0f, 0f));
        entriesPractice.add(new BarEntry(3.0f, 0f));
        entriesPractice.add(new BarEntry(4.0f, 0f));

        entriesReal.add(new BarEntry(1.0f, 0f));
        entriesReal.add(new BarEntry(2.0f, 0f));
        entriesReal.add(new BarEntry(3.0f, 0f));
        entriesReal.add(new BarEntry(4.0f, 0f));

        setPractice = new BarDataSet(entriesPractice, "Practice");
        setPractice.setColors(ColorTemplate.COLORFUL_COLORS);
        setReal = new BarDataSet(entriesReal, "Real");
        setReal.setColors(ColorTemplate.MATERIAL_COLORS);

        data = new BarData(setPractice,setReal);
        data.setBarWidth(barWidth);
        data.groupBars(0.55f,groupSpace,barSpace);
        data.setBarWidth(barWidth);
        data.setValueTextSize(8);
        data.setValueFormatter(new MyValueFormatter());

        String labels[] = new String[]{"", "TZ1", "TZ2", "TZ3", "Pass"};

        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(100);
        yAxis.setAxisMinimum(0);
        yAxis = chart.getAxisRight();
        yAxis.setAxisMaximum(100);
        yAxis.setAxisMinimum(0);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new MyXAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);

        chart.setData(data);
    }
    private void handleDataUpdates() {
        Intent intent = getIntent();
        String name = intent.getExtras().get("name").toString();
        Query queryPractice = teamDataPracticeRef.orderByKey().equalTo(name);
        queryPractice.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Institute institute = dataSnapshot.getValue(Institute.class);

                entriesPractice.clear();
                float val = institute.getTz1_accuracy();
                entriesPractice.add(new BarEntry(1.0f,val));
                val = institute.getTz2_accuracy();
                entriesPractice.add(new BarEntry(2.0f,val));
                val = institute.getTz3_accuracy();
                entriesPractice.add(new BarEntry(3.0f,val));
                val = institute.getPass_accuracy();
                entriesPractice.add(new BarEntry(4.0f,val));

                setPractice.setValues(entriesPractice);
                setPractice.setLabel(institute.getName());
                setPractice.notifyDataSetChanged();
                chart.invalidate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query queryReal = teamDataRealRef.orderByKey().equalTo(name);
        queryReal.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Institute institute = dataSnapshot.getValue(Institute.class);

                entriesReal.clear();
                float val = institute.getTz1_accuracy();
                entriesReal.add(new BarEntry(1.0f,val));
                val = institute.getTz2_accuracy();
                entriesReal.add(new BarEntry(2.0f,val));
                val = institute.getTz3_accuracy();
                entriesReal.add(new BarEntry(3.0f,val));
                val = institute.getPass_accuracy();
                entriesReal.add(new BarEntry(4.0f,val));

                setReal.setValues(entriesReal);
                setReal.setLabel(institute.getName());
                setReal.notifyDataSetChanged();
                chart.invalidate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initValues() {
        Intent intent = getIntent();
        String name = intent.getExtras().get("name").toString();
        Query queryPractice = teamDataPracticeRef.orderByKey().equalTo(name);
        final Query queryReal = teamDataRealRef.orderByKey().equalTo(name);
        queryPractice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference currentRef = null;
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    currentRef = singleSnapshot.getRef();
                }
                currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Institute institute = dataSnapshot.getValue(Institute.class);

                        entriesPractice.clear();
                        float val = institute.getTz1_accuracy();
                        entriesPractice.add(new BarEntry(1.0f,val));
                        val = institute.getTz2_accuracy();
                        entriesPractice.add(new BarEntry(2.0f,val));
                        val = institute.getTz3_accuracy();
                        entriesPractice.add(new BarEntry(3.0f,val));
                        val = institute.getPass_accuracy();
                        entriesPractice.add(new BarEntry(4.0f,val));

                        setPractice.setValues(entriesPractice);
                        setPractice.setLabel(institute.getName());
                        setPractice.notifyDataSetChanged();

                        updateRealValues();
                        //chart.invalidate();
                    }

                    private void updateRealValues() {
                        queryReal.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DatabaseReference currentRef = null;
                                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                                    currentRef = singleSnapshot.getRef();
                                }
                                currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Institute institute = dataSnapshot.getValue(Institute.class);

                                        entriesReal.clear();
                                        float val = institute.getTz1_accuracy();
                                        entriesReal.add(new BarEntry(1.0f,val));
                                        val = institute.getTz2_accuracy();
                                        entriesReal.add(new BarEntry(2.0f,val));
                                        val = institute.getTz3_accuracy();
                                        entriesReal.add(new BarEntry(3.0f,val));
                                        val = institute.getPass_accuracy();
                                        entriesReal.add(new BarEntry(4.0f,val));

                                        setReal.setValues(entriesReal);
                                        setReal.setLabel(institute.getName());
                                        setReal.notifyDataSetChanged();

                                        data = new BarData(setPractice,setReal);
                                        data.setBarWidth(barWidth);
                                        data.groupBars(0.55f,groupSpace,barSpace);
                                        data.setValueTextSize(8);
                                        data.setValueFormatter(new MyValueFormatter());

                                        chart.invalidate();
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*Query queryReal = teamDataRealRef.orderByKey().equalTo(name);
        queryReal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference currentRef = null;
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    currentRef = singleSnapshot.getRef();
                }
                currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Institute institute = dataSnapshot.getValue(Institute.class);

                        entriesReal.clear();
                        float val = institute.getTz1_accuracy();
                        entriesReal.add(new BarEntry(1.0f,val));
                        val = institute.getTz2_accuracy();
                        entriesReal.add(new BarEntry(2.0f,val));
                        val = institute.getTz3_accuracy();
                        entriesReal.add(new BarEntry(3.0f,val));
                        val = institute.getPass_accuracy();
                        entriesReal.add(new BarEntry(4.0f,val));

                        setReal.setValues(entriesReal);
                        setReal.setLabel(institute.getName());
                        setReal.notifyDataSetChanged();
                        chart.invalidate();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }


    public class MyValueFormatter implements IValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.00"); // use one decimal
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value) + " %";
        }
    }
    public class MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public MyXAxisValueFormatter(String[] values) {
            mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (mValues.length > value)
                return mValues[(int) Math.floor(value)];
            else
                return "";
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
    }

}
