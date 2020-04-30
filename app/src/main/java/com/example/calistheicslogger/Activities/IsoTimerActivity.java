package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;

public class IsoTimerActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.iso_timer_activity);
        SetUpWindowLayout();
    }

    private void SetUpWindowLayout()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double widthRatio = 0.8;
        double heightRatio = 0.6;

        getWindow().setLayout((int)(width*widthRatio),(int)(height*heightRatio));
    }
}
