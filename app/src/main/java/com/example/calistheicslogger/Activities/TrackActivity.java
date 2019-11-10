package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.example.calistheicslogger.R;


public class TrackActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);
    }

    public void MinusButtonClick(View view)
    {
        Group testButton = findViewById(R.id.repsGroup);
        testButton.setVisibility(View.GONE);
    }

    public void AddButtonClick(View view){
        Group testButton = findViewById(R.id.repsGroup);
        testButton.setVisibility(View.VISIBLE);
    }
}
