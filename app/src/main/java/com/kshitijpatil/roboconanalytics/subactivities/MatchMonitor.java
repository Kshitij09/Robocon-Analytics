package com.kshitijpatil.roboconanalytics.subactivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.kshitijpatil.roboconanalytics.R;
import com.kshitijpatil.roboconanalytics.adapters.DataModifiersAdapter;
import com.kshitijpatil.roboconanalytics.models.DataModel;
import com.kshitijpatil.roboconanalytics.models.Institute;
import com.kshitijpatil.roboconanalytics.models.Match;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.DATA;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.INDEX;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.STATUS;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.TEAMS;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.TEAMSDATA;

public class MatchMonitor extends AppCompatActivity {
    public static final String TAG = "MatchMonitor";
    public DataModifiersAdapter adapter;
    ArrayList<DataModel> dataModels;
    ListView listControls;
    ProgressDialog progressDialog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference dataRef = rootRef.child(DATA);
    DatabaseReference teamsRef = rootRef.child(TEAMS);
    long index;
    String type;
    BarChart chart;
    ArrayList<BarEntry> entries = new ArrayList<>();
    BarDataSet set;
    BarData data;
    FloatingActionButton btnEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_monitor);
        listControls = findViewById(R.id.list_controls);
        progressDialog = new ProgressDialog(MatchMonitor.this);
        progressDialog.setMessage("Updating records...");
        btnEnd = findViewById(R.id.btn_stop);
        initDataModels();
        initGraph();
        handleDataUpdates();
        initValues();
    }

    private void initValues() {
        DatabaseReference matchRef = dataRef.child(type);
        Query query = matchRef.orderByChild(INDEX).equalTo(index);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference currentRef = null;
                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    currentRef = singleSnapshot.getRef();
                }
                currentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Match match = dataSnapshot.getValue(Match.class);
                        Institute institute = match.getInstitute();
                        DataModel model = null;

                        entries.clear();
                        float val = institute.getTz1_accuracy();
                        entries.add(new BarEntry(1.0f, val));
                        val = institute.getTz2_accuracy();
                        entries.add(new BarEntry(2.0f, val));
                        val = institute.getTz3_accuracy();
                        entries.add(new BarEntry(3.0f, val));
                        val = institute.getPass_accuracy();
                        entries.add(new BarEntry(4.0f, val));

                        set.setValues(entries);
                        set.setLabel(institute.getName());
                        set.notifyDataSetChanged();
                        chart.invalidate();

                        String status = (dataSnapshot.hasChild(STATUS)) ? (match.getStatus()) : Match.OPEN;
                        if (status.equals(Match.CLOSE)){
                            listControls.setVisibility(View.INVISIBLE);
                            btnEnd.setVisibility(View.INVISIBLE);
                        }else {

                            model = adapter.getItem(0);
                            model.setValue(String.valueOf(institute.getTz1_successful_hits()));
                            model = adapter.getItem(1);
                            model.setValue(String.valueOf(institute.getTz1_total_hits() - institute.getTz1_successful_hits()));
                            model = adapter.getItem(2);
                            model.setValue(String.valueOf(institute.getTz2_successful_hits()));
                            model = adapter.getItem(3);
                            model.setValue(String.valueOf(institute.getTz2_total_hits() - institute.getTz2_successful_hits()));
                            model = adapter.getItem(4);
                            model.setValue(String.valueOf(institute.getTz3_successful_hits()));
                            model = adapter.getItem(5);
                            model.setValue(String.valueOf(institute.getTz3_total_hits() - institute.getTz3_successful_hits()));
                            model = adapter.getItem(6);
                            model.setValue(String.valueOf(institute.getSuccessful_pass()));
                            model = adapter.getItem(7);
                            model.setValue(String.valueOf(institute.getTotal_pass() - institute.getSuccessful_pass()));

                            adapter.notifyDataSetChanged();

                        }
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
    protected void onStart() {
        super.onStart();
        handleDataUpdates();
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

        entries.add(new BarEntry(1.0f, 0f));
        entries.add(new BarEntry(2.0f, 0f));
        entries.add(new BarEntry(3.0f, 0f));
        entries.add(new BarEntry(4.0f, 0f));

        set = new BarDataSet(entries, "Accuracy");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        data = new BarData(set);
        data.setBarWidth(0.4f);
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
        DatabaseReference matchRef = dataRef.child(type);
        Query query = matchRef.orderByChild(INDEX).equalTo(index);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Match match = dataSnapshot.getValue(Match.class);
                Institute institute = match.getInstitute();
                DataModel model = null;

                model = adapter.getItem(0);
                model.setValue(String.valueOf(institute.getTz1_successful_hits()));
                model = adapter.getItem(1);
                model.setValue(String.valueOf(institute.getTz1_total_hits()-institute.getTz1_successful_hits()));
                model = adapter.getItem(2);
                model.setValue(String.valueOf(institute.getTz2_successful_hits()));
                model = adapter.getItem(3);
                model.setValue(String.valueOf(institute.getTz2_total_hits()-institute.getTz2_successful_hits()));
                model = adapter.getItem(4);
                model.setValue(String.valueOf(institute.getTz3_successful_hits()));
                model = adapter.getItem(5);
                model.setValue(String.valueOf(institute.getTz3_total_hits()-institute.getTz3_successful_hits()));
                model = adapter.getItem(6);
                model.setValue(String.valueOf(institute.getSuccessful_pass()));
                model = adapter.getItem(7);
                model.setValue(String.valueOf(institute.getTotal_pass()-institute.getSuccessful_pass()));

                adapter.notifyDataSetChanged();


                entries.clear();
                float val = institute.getTz1_accuracy();
                entries.add(new BarEntry(1.0f,val));
                val = institute.getTz2_accuracy();
                entries.add(new BarEntry(2.0f,val));
                val = institute.getTz3_accuracy();
                entries.add(new BarEntry(3.0f,val));
                val = institute.getPass_accuracy();
                entries.add(new BarEntry(4.0f,val));

                set.setValues(entries);
                set.setLabel(institute.getName());
                set.notifyDataSetChanged();
                chart.invalidate();
                /*BarEntry modifyEntry = null;
                modifyEntry = entries.get(0);
                BigDecimal accuracy = new BigDecimal(String.valueOf(institute.getTz1_accuracy()));
                float vals[] = {modifyEntry.getX(), accuracy.floatValue()};
                modifyEntry.setVals(vals);
                chart.notifyDataSetChanged();
                chart.invalidate();

                BarEntry modifyEntry2 = entries.get(1);
                float vals2[] = {modifyEntry2.getX(), institute.getTz2_accuracy()};
                modifyEntry2.setVals(vals2);
                chart.notifyDataSetChanged();
                chart.invalidate();

                BarEntry modifyEntry3 = entries.get(2);
                float vals3[] = {modifyEntry3.getX(), institute.getTz3_accuracy()};
                modifyEntry2.setVals(vals3);
                chart.notifyDataSetChanged();
                chart.invalidate();

                BarEntry modifyEntry4 = entries.get(3);
                float vals4[] = {modifyEntry4.getX(), institute.getPass_accuracy()};
                modifyEntry2.setVals(vals4);
                chart.notifyDataSetChanged();
                chart.invalidate();

                chart.notifyDataSetChanged();
                chart.invalidate();*/
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
        index = ((long) intent.getExtras().get("index"));
        type = intent.getExtras().get("type").toString();

        adapter = new DataModifiersAdapter(dataModels, index, type, getApplicationContext());
        listControls.setAdapter(adapter);
        //DataModel temp = (DataModel) listControls.getAdapter().getItem(3);
        //temp.setValue("5");
        //listControls.deferNotifyDataSetChanged();
        //Log.d(TAG, "initDataModels: "+listControls.getAdapter().getItem(0).toString());
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

    public void launchNote(View view) {
        Intent noteIntent = new Intent(MatchMonitor.this,NoteActivity.class);
        noteIntent.putExtra("index", index);
        noteIntent.putExtra("type", type);
        startActivity(noteIntent);
    }

    public void endMatch(View view) {
        AlertDialog dialog = createAlert(index);
        dialog.show();
    }
    public AlertDialog createAlert(final long index){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_end)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        progressDialog.show();
                        DatabaseReference matchRef = dataRef.child(type);
                        Query query = matchRef.orderByChild(INDEX).equalTo(index);
                        //DatabaseReference queryRef = query.getRef();
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DatabaseReference updateRef = null;
                                String name = null;
                                for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                                    Match match = singleSnapshot.getValue(Match.class);
                                    name = match.getInstitute().getName();
                                    updateRef = singleSnapshot.getRef();
                                }
                                updateRef.child(STATUS).setValue(Match.CLOSE);
                                updateAccuracies(name,type);
                            }

                            private void updateAccuracies(final String name, final String type) {
                                DatabaseReference practiceRef = dataRef.child(type);
                                practiceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        ArrayList<Institute> listInstitutes = new ArrayList<>();
                                        for (DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                                            Match match = singleSnapshot.getValue(Match.class);
                                            Institute institute = match.getInstitute();
                                            if (institute.getName().equals(name))
                                                listInstitutes.add(institute);
                                        }
                                        Institute avgInstitute = new Institute();
                                        for (Institute institute: listInstitutes){
                                            avgInstitute.setTz1_accuracy(avgInstitute.getTz1_accuracy()+institute.getTz1_accuracy());
                                            avgInstitute.setTz2_accuracy(avgInstitute.getTz2_accuracy()+institute.getTz2_accuracy());
                                            avgInstitute.setTz3_accuracy(avgInstitute.getTz3_accuracy()+institute.getTz3_accuracy());
                                            avgInstitute.setPass_accuracy(avgInstitute.getPass_accuracy()+institute.getPass_accuracy());
                                        }
                                        avgInstitute.setTz1_accuracy(avgInstitute.getTz1_accuracy()/listInstitutes.size());
                                        avgInstitute.setTz2_accuracy(avgInstitute.getTz2_accuracy()/listInstitutes.size());
                                        avgInstitute.setTz3_accuracy(avgInstitute.getTz3_accuracy()/listInstitutes.size());
                                        avgInstitute.setPass_accuracy(avgInstitute.getPass_accuracy()/listInstitutes.size());

                                        DatabaseReference teamsDataRef = rootRef.child(TEAMSDATA).child(type).child(name);
                                        teamsDataRef.setValue(avgInstitute).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();
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
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
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
}
