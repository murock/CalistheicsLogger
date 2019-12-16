package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;


public class HistoryActivity extends Activity implements Serializable, PropertyChangeListener {

    DatabaseCommunicator databaseCommunicator;
    String currentExercise;
    List<TrackedExercise> exerciseHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        setContentView(R.layout.history_activity);
        Intent i = getIntent();
        currentExercise = (String)i.getSerializableExtra("Exercise");
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(currentExercise);
    }

    private void newTrackAcitivity(String exercise){
        Intent trackActivity = new Intent(this, TrackActivity.class);
        trackActivity.putExtra("Exercise", exercise);
        startActivity(trackActivity);
    }


    public void TrackButtonClick(View v)
    {
        newTrackAcitivity(currentExercise);
    }

    private void startExerciseHistoryLookUp()
    {
        databaseCommunicator.getExerciseHistory(currentExercise);
    }

    private void populateHistoryGrid()
    {
        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        linearLayout.removeAllViews();
        exerciseHistory = databaseCommunicator.exerciseHistoryList;
        if (exerciseHistory.size() <= 0)
        {
            return;
        }
        boolean isNewDate = true;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Log.i("Alfie prop name: ", evt.getPropertyName());
        if (evt.getPropertyName() == "exerciseFromNamePopulated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateHistoryGrid();
                }
            });
        }
    }
}
