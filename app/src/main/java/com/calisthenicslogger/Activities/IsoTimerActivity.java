package com.calisthenicslogger.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.calisthenicslogger.R;
import com.calisthenicslogger.RoomDatabase.DatabaseCommunicator;
import com.calisthenicslogger.Tools.InputFilterMinMax;

public class IsoTimerActivity extends Activity {

    private int holdTimerValue;
    private int positionTimerValue;
    private boolean isPlaying = false, countDown = true, autoSave  = false;
    private int volumeValue = 100;

    int cachedPositionValue = 0;
    int cachedHoldValue = 0;

    private CountDownTimer timer;

    EditText positionEditText;
    EditText holdSecondsEditText;
    EditText holdMinEditText;
    ImageButton positionPosButton;
    ImageButton positionNegButton;
    ImageButton holdPosButton;
    ImageButton holdNegButton;

    AudioManager audioManager;
    boolean focusTaken = false;

    DatabaseCommunicator databaseCommunicator;

    View.OnClickListener checkBoxListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox)v;
            SharedPreferences.Editor editor = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE).edit();
            editor.putBoolean((String)checkBox.getTag(), checkBox.isChecked());
            editor.apply();
            if (v.getId() == R.id.voiceCountdownCheckBox)
            {
                countDown = checkBox.isChecked();
            }
            else if (v.getId() == R.id.autoSaveCheckBox)
            {
                autoSave = checkBox.isChecked();
            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.iso_timer_activity);
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        positionEditText = findViewById(R.id.positionSecondsEditText);
        holdSecondsEditText = findViewById(R.id.holdSecondsEditText);
        holdMinEditText = findViewById(R.id.holdMinutesEditText);
        positionPosButton = findViewById(R.id.positionPositiveButton);
        positionNegButton = findViewById(R.id.positionNegativeButton);
        holdPosButton = findViewById(R.id.holdPositiveButton);
        holdNegButton = findViewById(R.id.holdNegativeButton);
        audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        SetUpWindowLayout();
        SetUpHoldTimer();
        SetUpPositionTimer();
        SetUpVolume();
        SetUpCheckboxValues();
    }

    private void SetUpWindowLayout()
    {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double widthRatio = 0.8;
        double heightRatio = 0.7;

        getWindow().setLayout((int)(width*widthRatio),(int)(height*heightRatio));
    }

    private void SetUpPositionTimer()
    {
        SharedPreferences prefs = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE);
        positionTimerValue = prefs.getInt("positionTimerValue", 0);
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
        // Countdown
        CheckBox countDownCheckbox = findViewById(R.id.voiceCountdownCheckBox);
        SharedPreferences prefs = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE);
        countDown = prefs.getBoolean((String)countDownCheckbox.getTag(), true);
        countDownCheckbox.setChecked(countDown);
        countDownCheckbox.setOnClickListener(checkBoxListener);

        // Auto save
        CheckBox autoSaveCheckbox = findViewById(R.id.autoSaveCheckBox);
        autoSave = prefs.getBoolean((String)autoSaveCheckbox.getTag(), false);
        autoSaveCheckbox.setChecked(autoSave);
        autoSaveCheckbox.setOnClickListener(checkBoxListener);
    }

    private void SetUpVolume()
    {
        SeekBar volumeSeekbar = findViewById(R.id.volumeSeekBar);
        SharedPreferences prefs = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE);
        volumeValue = prefs.getInt("volume", 100);
        volumeSeekbar.setProgress(volumeValue);

        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences.Editor editor = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE).edit();
                editor.putInt("volume", progress);
                editor.apply();
                volumeValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void updateSharedPrefsPositionTimerValue(int newValue){
        SharedPreferences.Editor editor = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE).edit();
        editor.putInt("positionTimerValue", newValue);
        editor.apply();
    }

    private void updatePositionTimerValue(){
        positionEditText.setText(Integer.toString(positionTimerValue));
    }

    private void startPositionTimer(){
        // Time in ms
        cachedPositionValue = positionTimerValue;
        int timerValue = positionTimerValue * 1000;
        float volume = (float)volumeValue/100;

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.hold);
        mediaPlayer.setVolume(volume,volume);

        //Vibrate
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        positionEditText.setFocusable(false);
        holdMinEditText.setFocusable(false);
        holdSecondsEditText.setFocusable(false);
        positionNegButton.setVisibility(View.INVISIBLE);
        positionPosButton.setVisibility(View.INVISIBLE);
        holdNegButton.setVisibility(View.INVISIBLE);
        holdPosButton.setVisibility(View.INVISIBLE);

        cancelTimer();


        timer = new CountDownTimer(timerValue, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int)millisUntilFinished/1000;
                positionEditText.setText(Integer.toString(seconds));
                if (countDown && seconds < 11){
                    if (!focusTaken){
                        focusTaken = true;
                        audioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
                    }
                    playNumberSound(seconds, volume);
                }
            }

            @Override
            public void onFinish() {
                positionEditText.setText(Integer.toString(cachedPositionValue));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(500);
                }
                mediaPlayer.start();
                startHoldTimer();
            }
        }.start();
    }

    private void playNumberSound(int number, float volume){

        int fileToPlay = 0;
        switch  (number){
            case 10:
                fileToPlay = R.raw.ten;
                break;
            case 9:
                fileToPlay = R.raw.nine;
                break;
            case 8:
                fileToPlay = R.raw.eight;
                break;
            case 7:
                fileToPlay = R.raw.seven;
                break;
            case 6:
                fileToPlay = R.raw.six;
                break;
            case 5:
                fileToPlay = R.raw.five;
                break;
            case 4:
                fileToPlay = R.raw.four;
                break;
            case 3:
                fileToPlay = R.raw.three;
                break;
            case 2:
                fileToPlay = R.raw.two;
                break;
            case 1:
                fileToPlay = R.raw.one;
                break;
        }
        if (fileToPlay != 0){
            MediaPlayer mediaPlayer = MediaPlayer.create(this, fileToPlay);
            mediaPlayer.setVolume(volume,volume);
            mediaPlayer.start();
        }
    }

    private void SetUpHoldTimer()
    {
        SharedPreferences prefs = getSharedPreferences("IsoSharedPreferences", MODE_PRIVATE);
        holdTimerValue = prefs.getInt("holdTimerValue", 0); // 0 is default
        holdSecondsEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 999)});
        holdMinEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});

        int minutesValue = holdTimerValue /60;
        int secondsValue = holdTimerValue - (minutesValue*60);

        if (minutesValue > 0){
            holdMinEditText.setText(Integer.toString(minutesValue));
        }
        holdMinEditText.addTextChangedListener(new TextWatcher() {
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
                if (!holdSecondsEditText.getText().toString().equals("")){
                    seconds = Integer.parseInt(holdSecondsEditText.getText().toString());
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
            holdSecondsEditText.setText(Integer.toString(secondsValue));
        }
        holdSecondsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int seconds = 0;
                if(s.length() > 0){
                    seconds = Integer.parseInt(s.toString());
                }

                String minutesString = holdMinEditText.getText().toString();
                int minutes = 0;
                if (minutesString.length() > 0) {
                    minutes = Integer.parseInt(holdMinEditText.getText().toString());
                }

                int totalTimeSeconds = minutes * 60 + seconds;
                if (totalTimeSeconds == holdTimerValue) {
                    return;
                }

                holdTimerValue = totalTimeSeconds;
                int newMinutesValue = totalTimeSeconds / 60;
                int newSecondsValue = totalTimeSeconds - (newMinutesValue * 60);

                updateSharedPrefsHoldTimerValue(totalTimeSeconds);

                if (!holdMinEditText.getText().toString().equals(Integer.toString(newMinutesValue))) {
                    holdMinEditText.setText(Integer.toString(newMinutesValue));
                }
                if (!holdSecondsEditText.getText().toString().equals(Integer.toString(newSecondsValue))) {
                    holdSecondsEditText.setText(Integer.toString(newSecondsValue));
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

    private void startHoldTimer(){
        // Time in ms
        cachedHoldValue = holdTimerValue;
        int timerValue = holdTimerValue * 1000;
        float volume = (float)volumeValue/100;

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.stop_rest_beep);
        mediaPlayer.setVolume(volume,volume);

        //Vibrate
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        timer = new CountDownTimer(timerValue, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsToFinish = (int)millisUntilFinished/1000;
                int minutesValue = secondsToFinish/60;
                int secondsValue = secondsToFinish - (minutesValue*60);
                holdMinEditText.setText(Integer.toString(minutesValue));
                holdSecondsEditText.setText(Integer.toString(secondsValue));
            }

            @Override
            public void onFinish() {
                int minutesValue = cachedHoldValue/60;
                int secondsValue = cachedHoldValue - (minutesValue*60);
                holdMinEditText.setText(Integer.toString(minutesValue));
                holdSecondsEditText.setText(Integer.toString(secondsValue));

                reactivateControls();
                togglePlaying();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(500);
                }
                mediaPlayer.start();
                audioManager.abandonAudioFocus(null);
                focusTaken = false;
                if (autoSave){
                    databaseCommunicator.savePendingIsoExercise(Integer.toString(minutesValue), Integer.toString(secondsValue));
                }
            }
        }.start();
    }

    private void cancelTimer(){
        if (timer != null){
            timer.cancel();
            positionEditText.setText(Integer.toString(cachedPositionValue));
            audioManager.abandonAudioFocus(null);
            focusTaken = false;

            if (cachedHoldValue != 0){
                int minutesValue = cachedHoldValue/60;
                int secondsValue = cachedHoldValue - (minutesValue*60);
                holdMinEditText.setText(Integer.toString(minutesValue));
                holdSecondsEditText.setText(Integer.toString(secondsValue));
            }
        }
    }

    private void reactivateControls()
    {
        positionEditText.setFocusableInTouchMode(true);
        holdMinEditText.setFocusableInTouchMode(true);
        holdSecondsEditText.setFocusableInTouchMode(true);
        positionNegButton.setVisibility(View.VISIBLE);
        positionPosButton.setVisibility(View.VISIBLE);
        holdNegButton.setVisibility(View.VISIBLE);
        holdPosButton.setVisibility(View.VISIBLE);
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
            holdTimerValue += 1;
        }else
        {
            if (holdTimerValue > 1){
                holdTimerValue -= 1;
            }else
            {
                holdTimerValue = 0;
            }
        }
        updateSharedPrefsHoldTimerValue(holdTimerValue);

        updateHoldTimerValues();

    }

    private void togglePlaying(){
        ImageButton playButton = findViewById(R.id.playButton);
        if (isPlaying){
            playButton.setImageResource(R.drawable.play);
        } else{
            playButton.setImageResource(R.drawable.faded_play_icon);
            startPositionTimer();
        }
        isPlaying = !isPlaying;
    }

    public void PlayButtonClick(View button)
    {
        if (holdTimerValue > 0 && !isPlaying){
            togglePlaying();
        }
    }

    public void StopButtonClick(View button) {
        if (isPlaying){
            cancelTimer();
            ImageButton playButton = findViewById(R.id.playButton);
            playButton.setImageResource(R.drawable.play);
            isPlaying = false;
            reactivateControls();
        }
    }

    public void OkButtonClick(View v){
        this.finish();
    }
}
