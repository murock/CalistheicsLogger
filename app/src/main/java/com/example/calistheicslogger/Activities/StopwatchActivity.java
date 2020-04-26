package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;

public class StopwatchActivity extends Activity {

    boolean isPlaying = false;
    Chronometer chronometer;
    long pauseOffset = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stopwatch_activity);
        SetUpWindowLayout();
        chronometer = findViewById(R.id.chronometer);
    }

    private void SetUpWindowLayout()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double widthRatio = 0.8;
        double heightRatio = 0.5;

        getWindow().setLayout((int)(width*widthRatio),(int)(height*heightRatio));
    }

    public void PlayButtonClick(View v){
        ImageButton playButton = findViewById(R.id.playButton);
        if (isPlaying){
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            playButton.setImageResource(R.drawable.play);
        }else{
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            playButton.setImageResource(R.drawable.pause);
        }
        isPlaying = !isPlaying;
    }

    public void StopButtonClick(View v){
        chronometer.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    public void OkButtonClick(View v){
        this.finish();
    }
}
