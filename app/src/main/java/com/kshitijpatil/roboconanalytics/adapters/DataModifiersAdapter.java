package com.kshitijpatil.roboconanalytics.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kshitijpatil.roboconanalytics.R;
import com.kshitijpatil.roboconanalytics.models.DataModel;
import com.kshitijpatil.roboconanalytics.models.Institute;
import com.kshitijpatil.roboconanalytics.models.Match;

import java.util.ArrayList;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.DATA;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.INDEX;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.TEAMS;

public class DataModifiersAdapter extends ArrayAdapter<DataModel> {
    public static final String TAG = "DataModifiersAdapter";
    Context mContext;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference dataRef = rootRef.child(DATA);
    DatabaseReference teamsRef = rootRef.child(TEAMS);
    private ArrayList<DataModel> dataSet;
    private long index;
    private String type;

    public DataModifiersAdapter(ArrayList<DataModel> data, long index, String type, Context context) {
        super(context, R.layout.data_records_row, data);
        this.dataSet = data;
        this.mContext = context;
        this.index = index;
        this.type = type;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DataModel model = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.data_records_row, parent, false);
            viewHolder.txtHeading = convertView.findViewById(R.id.txt_heading);
            viewHolder.txtValue = convertView.findViewById(R.id.txt_value);
            viewHolder.imgMiss = convertView.findViewById(R.id.btn_miss);
            viewHolder.imgHit = convertView.findViewById(R.id.btn_add);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtHeading.setText(model.getHeading());
        viewHolder.txtValue.setText(model.getValue());
        viewHolder.imgHit.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_add_small));
        viewHolder.imgMiss.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_remove_small));

        viewHolder.imgHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + position + " Hit");
                handleUpdates(position, true);
            }
        });
        viewHolder.imgMiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + position + " Miss");
                handleUpdates(position, false);
            }
        });

        return convertView;
    }

    private void handleUpdates(final int position, final boolean hit) {
        DatabaseReference matchRef = dataRef.child(type);
        Query query = matchRef.orderByChild(INDEX).equalTo(index);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d(TAG, "onDataChange: "+type+" "+index);
                //Log.d(TAG, "onDataChange: "+dataSnapshot.getValue().toString());
                DatabaseReference refMatch = null;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    refMatch = singleSnapshot.getRef();
                }
                //DatabaseReference refInstitute = refMatch.child(INSTITUTE);
                //Log.d(TAG, "onDataChange: "+refInstitute.toString());
                refMatch.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Match match = mutableData.getValue(Match.class);
                        final Institute institute = match.getInstitute();
                        if (institute == null) {
                            return Transaction.success(mutableData);
                        }
                        if (hit) {
                            switch (position) {
                                case 0:
                                    institute.increamentTZ1SuccessfulHits();
                                    institute.increamentTZ1TotalHits();
                                    institute.calculateTZ1Accuracy();
                                    break;
                                case 1:
                                    institute.increamentTZ1TotalHits();
                                    institute.calculateTZ1Accuracy();
                                    break;
                                case 2:
                                    institute.increamentTZ2SuccessfulHits();
                                    institute.increamentTZ2TotalHits();
                                    institute.calculateTZ2Accuracy();
                                    break;
                                case 3:
                                    institute.increamentTZ2TotalHits();
                                    institute.calculateTZ2Accuracy();
                                    break;
                                case 4:
                                    institute.increamentTZ3SuccessfulHits();
                                    institute.increamentTZ3TotalHits();
                                    institute.calculateTZ3Accuracy();
                                    break;
                                case 5:
                                    institute.increamentTZ3TotalHits();
                                    institute.calculateTZ3Accuracy();
                                    break;
                                case 6:
                                    institute.increamentSuccessfulPass();
                                    institute.increamentTotalPass();
                                    institute.calculatePassAccuracy();
                                    break;
                                case 7:
                                    institute.increamentTotalPass();
                                    institute.calculatePassAccuracy();
                                    break;
                            }
                        } else {
                            switch (position) {
                                case 0:
                                    institute.decrementTZ1SuccessfulHits();
                                    institute.decrementTZ1TotalHits();
                                    institute.calculateTZ1Accuracy();
                                    break;
                                case 1:
                                    institute.decrementTZ1TotalHits();
                                    institute.calculateTZ1Accuracy();
                                    break;
                                case 2:
                                    institute.decrementTZ2SuccessfulHits();
                                    institute.decrementTZ2TotalHits();
                                    institute.calculateTZ2Accuracy();
                                    break;
                                case 3:
                                    institute.decrementTZ2TotalHits();
                                    institute.calculateTZ2Accuracy();
                                    break;
                                case 4:
                                    institute.decrementTZ3SuccessfulHits();
                                    institute.decrementTZ3TotalHits();
                                    institute.calculateTZ3Accuracy();
                                    break;
                                case 5:
                                    institute.decrementTZ3TotalHits();
                                    institute.calculateTZ3Accuracy();
                                    break;
                                case 6:
                                    institute.decrementSuccessfulPass();
                                    institute.decrementTotalPass();
                                    institute.calculatePassAccuracy();
                                    break;
                                case 7:
                                    institute.decrementTotalPass();
                                    institute.calculatePassAccuracy();
                                    break;
                            }
                        }
                        Log.d(TAG, "doTransaction: " + institute.getTz1_accuracy());
                        mutableData.setValue(match);

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

    private static class ViewHolder {
        TextView txtHeading;
        TextView txtValue;
        ImageView imgHit;
        ImageView imgMiss;
    }
}
