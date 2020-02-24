package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;
import com.example.calistheicslogger.Tools.PropertyTextView;

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
        startExerciseHistoryLookUp();
    }

    public void TrackButtonClick(View v)
    {
        finish();
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
        for(int i = 0; i < exerciseHistory.size(); i++)
        {
            TrackedExercise exercise = exerciseHistory.get(i);
            TextView textView = new TextView(HistoryActivity.this);
            if (isNewDate){
                TextView title = new TextView(HistoryActivity.this);
                title.setText(exercise.getName() + " : " + exercise.getTimestamp());
                linearLayout.addView(title);
                textView.setBackground(ContextCompat.getDrawable(HistoryActivity.this,R.drawable.top_and_sides_border));
                isNewDate = false;
            }else {
                textView.setBackground(ContextCompat.getDrawable(HistoryActivity.this,R.drawable.sides_border));
            }
            textView.setText(MainActivity.getTrackedExerciseString(exercise, false));
            linearLayout.addView(textView);

            if ( exerciseHistory.size() == i + 1|| exerciseHistory.size() > i + 1 && ! exercise.getTimestamp().equals(exerciseHistory.get(i + 1).getTimestamp()))
            {
                // Do something when its a new exercise
                isNewDate = true;
                TextView spacer = new TextView(HistoryActivity.this);
                spacer.setBackground(ContextCompat.getDrawable(HistoryActivity.this,R.drawable.top_border));
                linearLayout.addView(spacer);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
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
