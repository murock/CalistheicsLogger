package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;

public class RestTimerActivity extends Activity {

    View.OnClickListener checkBoxListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox)v;
            SharedPreferences.Editor editor = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
            editor.putBoolean((String)checkBox.getTag(), checkBox.isChecked());
        }
    };

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
        CheckBox soundCheckbox = findViewById(R.id.soundCheckBox);
        CheckBox autoCheckbox = findViewById(R.id.autoCheckBox);
        SharedPreferences prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);
        Boolean vibrateOn = prefs.getBoolean("vibrateOn", false);
        Boolean soundOn = prefs.getBoolean("vibrateOn", true);
        Boolean autoOn = prefs.getBoolean("vibrateOn", true);
        vibrateCheckbox.setChecked(vibrateOn);
        soundCheckbox.setChecked(soundOn);
        autoCheckbox.setChecked(autoOn);

        vibrateCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("vibrateOn", vibrateCheckbox.isChecked());
            }
        });

    }
}
