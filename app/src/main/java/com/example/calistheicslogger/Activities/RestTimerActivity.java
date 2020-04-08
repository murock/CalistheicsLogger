package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;

public class RestTimerActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rest_timer_activity);
        this.SetUpWindowLayout();
        this.SetUpTimerValue();
        this.SetUpCheckboxes();
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

    private void SetUpTimerValue()
    {
        SharedPreferences prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        int timerValue = prefs.getInt("timerValue", 0); // 0 is default
        EditText timerEditText = findViewById(R.id.valueEditText);

        timerEditText.setText(Integer.toString(timerValue));
        timerEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                    editor.putInt("timerValue", Integer.parseInt(s.toString()));
                    editor.apply();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void SetUpCheckboxes()
    {
        CheckBox vibrateCheckbox = findViewById(R.id.vibrateCheckBox);
        CheckBox soundCheckbox
    }
}
