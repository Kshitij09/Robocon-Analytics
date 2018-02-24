package com.kshitijpatil.roboconanalytics;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kshitijpatil.roboconanalytics.models.Institute;

import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.DATA;
import static com.kshitijpatil.roboconanalytics.FirebaseConstants.DBConstants.TEAMS;

public class CustomDialogClass extends Dialog implements View.OnClickListener {
    public Activity mActivity;
    public Button btnAdd, btnCancel;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference rootRef = database.getReference();
    DatabaseReference dataRef = rootRef.child(DATA);
    DatabaseReference teamsRef = rootRef.child(TEAMS);
    TextInputLayout textName;

    public CustomDialogClass(@NonNull Activity activity) {
        super(activity);
        this.mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_institute_name);
        btnAdd = findViewById(R.id.button_add);
        btnCancel = findViewById(R.id.button_cancel);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        textName = findViewById(R.id.text_name);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add:
                if (!TextUtils.isEmpty(textName.getEditText().getText())) {
                    addToDatabase(textName.getEditText().getText().toString().trim());
                }
                break;
            case R.id.button_cancel:
                dismiss();
                break;
        }
        dismiss();
    }

    private void addToDatabase(final String name) {
        teamsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(name)) {
                    long index = dataSnapshot.getChildrenCount();
                    Institute institute = new Institute(name, index);
                    DatabaseReference newInstituteRef = teamsRef.child(institute.getName());
                    newInstituteRef.setValue(institute).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
