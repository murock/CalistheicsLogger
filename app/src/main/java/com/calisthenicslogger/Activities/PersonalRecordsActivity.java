package com.calisthenicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.calisthenicslogger.R;
import com.calisthenicslogger.RoomDatabase.DatabaseCommunicator;
import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;
import com.calisthenicslogger.Tools.DateFunctions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;

public class PersonalRecordsActivity extends Activity implements Serializable, PropertyChangeListener {

    String currentExercise;
    DatabaseCommunicator databaseCommunicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_records_activity);
        Intent i = getIntent();
        currentExercise = (String)i.getSerializableExtra("Exercise");
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        startPersonalRecordLookUp();
    }



    public void TrackButtonClick(View v)
    {
        finish();
    }

    private void startPersonalRecordLookUp()
    {
        databaseCommunicator.getPersonalRecords(currentExercise);

    }

    private void populatePersonalRecords(){
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();

        List<TrackedExercise> trackedExercises = databaseCommunicator.personalRecordsList;
        if (trackedExercises.size() <= 0)
        {
            return;
        }
        for(int i = 0; i < trackedExercises.size(); i++)
        {
            TrackedExercise exercise = trackedExercises.get(i);
            TextView textView = new TextView(PersonalRecordsActivity.this);
            String exerciseString = MainActivity.getTrackedExerciseString(exercise, false) + " on " + DateFunctions.GetUKDateFormat(exercise.getTimestamp());
            textView.setText(exerciseString);
            linearLayout.addView(textView);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "personalRecordsPopulated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populatePersonalRecords();
                }
            });
        }
    }
}
