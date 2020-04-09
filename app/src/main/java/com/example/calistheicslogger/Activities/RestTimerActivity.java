package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.Tools.InputFilterMinMax;

public class RestTimerActivity extends Activity {

    View.OnClickListener checkBoxListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox)v;
            SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
            editor.putBoolean((String)checkBox.getTag(), checkBox.isChecked());
            editor.apply();
        }
    };

    public int timerValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rest_timer_activity);
        this.SetUpWindowLayout();
        this.SetUpTimerValue();
        this.SetUpCheckboxes();
        this.SetUpVolume();
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
        timerValue = prefs.getInt("timerValue", 0); // 0 is default
        EditText secondsEditText = findViewById(R.id.secondsEditText);
        EditText minutesEditText = findViewById(R.id.minutesEditText);
        secondsEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        minutesEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});

        int minutesValue = timerValue/60;
        int secondsValue = timerValue - (minutesValue*60);

        secondsEditText.setText(Integer.toString(secondsValue));
        secondsEditText.addTextChangedListener(new TextWatcher() {
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
        CheckBox soundCheckbox = findViewById(R.id.soundCheckBox);
        CheckBox autoCheckbox = findViewById(R.id.autoCheckBox);
        SharedPreferences prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        Boolean vibrateOn = prefs.getBoolean((String)vibrateCheckbox.getTag(), false);
        Boolean soundOn = prefs.getBoolean((String)soundCheckbox.getTag(), true);
        Boolean autoOn = prefs.getBoolean((String)autoCheckbox.getTag(), false);
        vibrateCheckbox.setChecked(vibrateOn);
        soundCheckbox.setChecked(soundOn);
        autoCheckbox.setChecked(autoOn);

        vibrateCheckbox.setOnClickListener(checkBoxListener);
        soundCheckbox.setOnClickListener(checkBoxListener);
        autoCheckbox.setOnClickListener(checkBoxListener);
    }

    private void SetUpVolume()
    {
        SeekBar volumeSeekbar = findViewById(R.id.volumeSeekBar);
        SharedPreferences prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        int volumeValue = prefs.getInt("volume", 100);
        volumeSeekbar.setProgress(volumeValue);

        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                editor.putInt("volume", progress);
                editor.apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void OnSecondButtonPress(View button)
    {
        int amountToChange;
        if (button.getId() == R.id.positiveButton)
        {
            amountToChange = 10;
        }else
        {
            amountToChange = -10;
        }
    }
}
