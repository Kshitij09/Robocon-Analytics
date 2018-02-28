package com.kshitijpatil.roboconanalytics;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kshitijpatil.roboconanalytics.dialogs.CustomDialogClass;
import com.kshitijpatil.roboconanalytics.models.Institute;

import java.util.ArrayList;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.DATA;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.NAME;


public class GraphActivity extends AppCompatActivity {
    private static final String TAG = GraphActivity.class.getSimpleName();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CustomDialogClass customDialog;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference dataRef = rootRef.child(DATA);
    BarChart chart;
    ArrayList<BarEntry> entries = new ArrayList<>();
    BarDataSet set;
    BarData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        customDialog = new CustomDialogClass(GraphActivity.this);

        chart = findViewById(R.id.barchart);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);


        entries.add(new BarEntry(1.0f, 15f));
        entries.add(new BarEntry(2.0f, 20f));
        entries.add(new BarEntry(3.0f, 30f));
        entries.add(new BarEntry(4.0f, 10f));

        set = new BarDataSet(entries, "Accuracy");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        data = new BarData(set);
        data.setBarWidth(0.4f);

        String teams[] = new String[]{"", "PICT", "VIT", "COEP", "MIT", "AIT"};
        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMaximum(100);
        yAxis.setAxisMinimum(0);
        yAxis = chart.getAxisRight();
        yAxis.setAxisMaximum(100);
        yAxis.setAxisMinimum(0);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new MyXAxisValueFormatter(teams));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        //xAxis.setCenterAxisLabels(true);
        //xAxis.setAvoidFirstLastClipping(true);

        chart.setData(data);
        dataRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Institute institute = dataSnapshot.getValue(Institute.class);
                BarEntry modifyEntry = entries.get(((int) institute.getIndex()));
                float vals[] = {modifyEntry.getX(), institute.getTz1_accuracy()};
                modifyEntry.setVals(vals);
                //set.notifyDataSetChanged();
                chart.notifyDataSetChanged();
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
        /*Institute institute = dataSnapshot.getValue(Institute.class);
        Log.d(TAG, "onChildChanged: "+dataSnapshot.getValue().toString());
        entries = new ArrayList<>();
        entries.add(new BarEntry(1.0f,(float)institute.getTz1_accuracy()));
        set = new BarDataSet(entries,"Accuracy");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.notifyDataSetChanged();
        data = new BarData(set);
        data.setBarWidth(0.4f);
        data.notifyDataChanged();
        chart.setData(data);
        chart.notifyDataSetChanged();
        chart.invalidate();*/
        //new UpdateData().execute();


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

    public void addInstitute(View view) {
        customDialog.show();
        //updateMap();
    }

    public void addHit(View view) {
        Log.d(TAG, "addHit: ");
        handleUpdate(true);
    }

    public void addMiss(View view) {
        Log.d(TAG, "addMiss: ");
        handleUpdate(false);
    }

    private void handleUpdate(final boolean hit) {
        Query institueQuery = dataRef.orderByChild(NAME).equalTo("PICT");
        institueQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                DatabaseReference refInstitute = null;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    refInstitute = singleSnapshot.getRef();
                }
                refInstitute.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        final Institute institute = mutableData.getValue(Institute.class);
                        if (institute == null) {
                            return Transaction.success(mutableData);
                        }
                        if (hit)
                            institute.increamentTZ1SuccessfulHits();
                        institute.increamentTZ1TotalHits();
                        institute.calculateTZ1Accuracy();
                        Log.d(TAG, "doTransaction: " + institute.getTz1_accuracy());
                        mutableData.setValue(institute);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void signout() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // User is now signed out
                        startActivity(new Intent(GraphActivity.this, SplashActivity.class));
                        finish();
                    }
                });
    }

    private void updateMap() {
        Log.d(TAG, "addToMap: ");
        entries = new ArrayList<>();
        entries.add(new BarEntry(3.0f, 30f));
        entries.add(new BarEntry(4.0f, 10f));
        set = new BarDataSet(entries, "Accuracy");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        set.notifyDataSetChanged();
        data = new BarData(set);
        data.setBarWidth(0.4f);
        data.notifyDataChanged();
        chart.setData(data);
        chart.notifyDataSetChanged();
        chart.invalidate();
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

    class UpdateData extends AsyncTask<Void, Void, Void> {
        TextView successful;
        TextView total;
        TextView accuracy;
        BarChart chart;
        ArrayList<BarEntry> entries = new ArrayList<>();
        BarDataSet set;
        BarData data;

        public UpdateData() {
            successful = findViewById(R.id.txt_success);
            total = findViewById(R.id.txt_total);
            accuracy = findViewById(R.id.txt_accuracy);
            chart = findViewById(R.id.barchart);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            entries = new ArrayList<>();
            dataRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged: " + dataSnapshot.getValue().toString());
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
            return null;
        }
    }
}
