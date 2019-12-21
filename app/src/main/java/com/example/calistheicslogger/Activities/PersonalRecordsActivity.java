package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;

import java.io.Serializable;

public class PersonalRecordsActivity extends Activity implements Serializable {

    String currentExercise;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_records_activity);
        Intent i = getIntent();
        currentExercise = (String)i.getSerializableExtra("Exercise");
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
}
