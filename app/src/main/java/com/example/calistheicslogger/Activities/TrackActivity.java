package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.AppDatabase;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;
import com.example.calistheicslogger.RoomDatabase.Entities.Exercise;

import java.io.Serializable;


public class TrackActivity extends Activity implements Serializable {

    AppDatabase appDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        appDatabase = AppDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);
        Intent i = getIntent();
        final String exerciseString = (String)i.getSerializableExtra("Exercise");
        SetUpActivity(exerciseString);
    }

    private void SetUpActivity(final String exerciseName){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Exercise exercise = appDatabase.exerciseDao().getExerciseFromName(exerciseName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SetUpControls(exercise);
                    }
                });
            }
        });
    }

    private void SetUpControls(Exercise exercise)
    {
        String type = exercise.getType();
        //"Isometric", "Weight and Reps", "Negative"
        switch(type){
            case "Isometric":
                Group group = findViewById(R.id.timeGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.bandGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.tempoGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.weightGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.distanceGroup);
                group.setVisibility(View.GONE);
                break;
            case "Weight and Reps":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.bandGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.tempoGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.weightGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.distanceGroup);
                group.setVisibility(View.GONE);
                break;
            case "Negative":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.bandGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.tempoGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.weightGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.distanceGroup);
                group.setVisibility(View.GONE);
                break;
                default:
        }
        Boolean bandAssisted = exercise.getBandAssisted();
        Boolean weighted = exercise.getWeightLoadable();
        if (bandAssisted)
        {
            Group group = findViewById(R.id.bandGroup);
            group.setVisibility(View.VISIBLE);
        }
        if (weighted){
            Group group = findViewById(R.id.weightGroup);
            group.setVisibility(View.VISIBLE);
        }
    }

}
