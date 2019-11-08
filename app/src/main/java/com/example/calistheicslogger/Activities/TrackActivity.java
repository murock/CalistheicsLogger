package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;

public class TrackActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);
    }
}
