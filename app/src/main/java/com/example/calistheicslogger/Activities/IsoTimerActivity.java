package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.Tools.InputFilterMinMax;

public class IsoTimerActivity extends Activity {

    private int holdTimerValue;
    private int positionTimerValue;
    private boolean isPlaying = false;
    private int volumeValue = 100;
    private boolean vibrateOn = true;

    private CountDownTimer positionTimer;

    View.OnClickListener checkBoxListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox)v;
            SharedPreferences.Editor editor = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE).edit();
            editor.putBoolean((String)checkBox.getTag(), checkBox.isChecked());
            editor.apply();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.iso_timer_activity);
        SetUpWindowLayout();
        SetUpHoldTimer();
        SetUpPositionTimer();
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

    private void SetUpPositionTimer()
    {
        SharedPreferences prefs = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE);
        positionTimerValue = prefs.getInt("positionTimerValue", 0);
        EditText positionEditText = findViewById(R.id.positionSecondsEditText);
        positionEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0,99)});

        if(positionTimerValue > 0){
            positionEditText.setText(Integer.toString(positionTimerValue));
        }

        positionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int seconds = 0;
                if (s.length() > 0){
                    seconds = Integer.parseInt(s.toString());
                }
                positionTimerValue = seconds;
                updateSharedPrefsPositionTimerValue(positionTimerValue);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void SetUpCheckboxValues(){
        // TODO: Set checkboxes and set booleans as field values
    }

    private void updateSharedPrefsPositionTimerValue(int newValue){
        SharedPreferences.Editor editor = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE).edit();
        editor.putInt("positionTimerValue", newValue);
        editor.apply();
    }

    private void updatePositionTimerValue(){
        EditText secondsEditText = findViewById(R.id.positionSecondsEditText);
        secondsEditText.setText(Integer.toString(positionTimerValue));
    }

    private void startPositionTimer(){
        // Time in ms
        int cacheTimerValue = positionTimerValue;
        int timerValue = positionTimerValue * 1000;
        float volume = (float)volumeValue/100;

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.stop_rest_beep);
        mediaPlayer.setVolume(volume,volume);

        //Vibrate
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        EditText positionEditText = findViewById(R.id.positionSecondsEditText);
        positionEditText.setFocusable(false);

        positionTimer = new CountDownTimer(timerValue, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                positionEditText.setText(Integer.toString((int)millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                positionEditText.setText(Integer.toString(cacheTimerValue));
                positionEditText.setFocusableInTouchMode(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrateOn) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if(vibrateOn) {
                    vibrator.vibrate(500);
                }
                mediaPlayer.start();
            }
        }.start();
    }

    private void SetUpHoldTimer()
    {
        SharedPreferences prefs = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE);
        holdTimerValue = prefs.getInt("holdTimerValue", 0); // 0 is default
        EditText secondsEditText = findViewById(R.id.holdSecondsEditText);
        EditText minutesEditText = findViewById(R.id.holdMinutesEditText);
        secondsEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 999)});
        minutesEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});

        int minutesValue = holdTimerValue /60;
        int secondsValue = holdTimerValue - (minutesValue*60);

        if (minutesValue > 0){
            minutesEditText.setText(Integer.toString(minutesValue));
        }
        minutesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int minutes = 0;
                if(s.length() > 0)
                {
                    minutes = Integer.parseInt(s.toString());
                }

                int seconds = 0;
                if (!secondsEditText.getText().toString().equals("")){
                    seconds = Integer.parseInt(secondsEditText.getText().toString());
                }

                int totalTimeSeconds = minutes*60 + seconds;
                if (totalTimeSeconds == holdTimerValue)
                {
                    return;
                }

                holdTimerValue = totalTimeSeconds;

                updateSharedPrefsHoldTimerValue(totalTimeSeconds);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if(secondsValue > 0){
            secondsEditText.setText(Integer.toString(secondsValue));
        }
        secondsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int seconds = 0;
                if(s.length() > 0){
                    seconds = Integer.parseInt(s.toString());
                }

                String minutesString = minutesEditText.getText().toString();
                int minutes = 0;
                if (minutesString.length() > 0) {
                    minutes = Integer.parseInt(minutesEditText.getText().toString());
                }

                int totalTimeSeconds = minutes * 60 + seconds;
                if (totalTimeSeconds == holdTimerValue) {
                    return;
                }

                holdTimerValue = totalTimeSeconds;
                int newMinutesValue = totalTimeSeconds / 60;
                int newSecondsValue = totalTimeSeconds - (newMinutesValue * 60);

                updateSharedPrefsHoldTimerValue(totalTimeSeconds);

                if (!minutesEditText.getText().toString().equals(Integer.toString(newMinutesValue))) {
                    minutesEditText.setText(Integer.toString(newMinutesValue));
                }
                if (!secondsEditText.getText().toString().equals(Integer.toString(newSecondsValue))) {
                    secondsEditText.setText(Integer.toString(newSecondsValue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void updateSharedPrefsHoldTimerValue(int newValue)
    {
        SharedPreferences.Editor editor = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE).edit();
        editor.putInt("holdTimerValue", newValue);
        editor.apply();
    }
    private void updateHoldTimerValues()
    {
        EditText secondsEditText = findViewById(R.id.holdSecondsEditText);
        EditText minutesEditText = findViewById(R.id.holdMinutesEditText);

        int minutesValue = holdTimerValue/60;
        int secondsValue = holdTimerValue - (minutesValue*60);

        minutesEditText.setText(Integer.toString(minutesValue));
        secondsEditText.setText(Integer.toString(secondsValue));
    }

    public void OnPositionSecondButtonPress(View button){
        if (button.getId() == R.id.positionPositiveButton)
        {
            positionTimerValue += 1;
        }else
        {
            if (positionTimerValue > 1){
                positionTimerValue -= 1;
            }else
            {
                positionTimerValue = 0;
            }
        }
        updateSharedPrefsPositionTimerValue(positionTimerValue);

        updatePositionTimerValue();
    }

    public void OnHoldSecondButtonPress(View button)
    {
        if (button.getId() == R.id.holdPositiveButton)
        {
            holdTimerValue += 10;
        }else
        {
            if (holdTimerValue > 10){
                holdTimerValue -= 10;
            }else
            {
                holdTimerValue = 0;
            }
        }
        updateSharedPrefsHoldTimerValue(holdTimerValue);

        updateHoldTimerValues();

    }

    public void PlayButtonClick(View button)
    {
        ImageButton playButton = findViewById(R.id.playButton);
        if (isPlaying){
            playButton.setImageResource(R.drawable.play);
        } else{
            playButton.setImageResource(R.drawable.pause);
            startPositionTimer();
        }
        isPlaying = !isPlaying;
    }

    public void OkButtonClick(View v){
        this.finish();
    }
}
