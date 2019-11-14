package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.AppDatabase;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;
import com.example.calistheicslogger.RoomDatabase.Entities.Exercise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class TrackActivity extends Activity implements Serializable {

    AppDatabase appDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        appDatabase = AppDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);
        Intent i = getIntent();
        final String exerciseString = (String)i.getSerializableExtra("Exercise");
        SetUpActivity(exerciseString);
        setUpBandSpinner();
    }

    private void SetUpActivity(final String exerciseName){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Exercise exercise = appDatabase.exerciseDao().getExerciseFromName(exerciseName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SetUpControls(exercise);
                    }
                });
            }
        });
    }

    private void setUpBandSpinner(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<String> bands = appDatabase.bandDao().getAllBandColours();
                Spinner bandSpinner = findViewById(R.id.bandSpinner);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TrackActivity.this, R.layout.center_spinner_text, bands);
                bandSpinner.setAdapter(arrayAdapter);
            }
        });
    }

    private void updateTrackingList(){

    }

//    private void setUpExercisesList()
//    {
//        AppExecutors.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                stringListExercises = appDatabase.exerciseDao().getAllNames();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        setUpListView((ArrayList<String>)stringListExercises);
//                    }
//                });
//
//            }
//        });
//    }

//    private void setUpListView(final ArrayList<String> exercises){
//        ListView exercisesListView = findViewById(R.id.exercisesListView);
//
//        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,exercises);
//
//        exercisesListView.setAdapter(arrayAdapter);
//
//        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ExerciseListActivity.this, "You pressed: " + exercises.get(position),Toast.LENGTH_SHORT).show();
//                newTrackAcitivity(exercises.get(position));
//            }
//        });
//    }


    private void SetUpControls(Exercise exercise)
    {
        TextView title = findViewById(R.id.titleTextView);
        title.setText(exercise.getName());
        String type = exercise.getType();
        //"Isometric", "Weight and Reps", "Negative"
        switch(type){
            case "Isometric":
                Group group = findViewById(R.id.timeGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.bandGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.tempoGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.weightGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.distanceGroup);
                group.setVisibility(View.GONE);
                break;
            case "Weight and Reps":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.bandGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.tempoGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.weightGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.distanceGroup);
                group.setVisibility(View.GONE);
                break;
            case "Negative":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.bandGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.tempoGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.weightGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.distanceGroup);
                group.setVisibility(View.GONE);
                break;
                default:
        }
        Boolean bandAssisted = exercise.getBandAssisted();
        Boolean weighted = exercise.getWeightLoadable();
        if (bandAssisted)
        {
            Group group = findViewById(R.id.bandGroup);
            group.setVisibility(View.VISIBLE);
        }
        if (weighted){
            Group group = findViewById(R.id.weightGroup);
            group.setVisibility(View.VISIBLE);
        }
    }

    public void OnButtonClick(View view){
        //TODO: Hardcoded fix later
        int repIncrement = 1;
        double weightIncrement = 1.25;
        EditText repsText = findViewById(R.id.repsEditText);
        EditText weightText = findViewById(R.id.weightEditText);
        if (TextUtils.isEmpty(repsText.getText().toString())) {
            repsText.setText("0");
        }
        if (TextUtils.isEmpty(weightText.getText().toString())){
            weightText.setText("0");
        }
       switch(view.getId()) {
           case R.id.repsPlusButton:
               int value = Integer.parseInt(repsText.getText().toString().trim());
               value += repIncrement;
               repsText.setText(Integer.toString(value));
               break;
           case R.id.repsMinusButton:
               value = Integer.parseInt(repsText.getText().toString().trim());
               value -= repIncrement;
               if (value >= 0) {
                   repsText.setText(Integer.toString(value));
               }
               break;
           case R.id.weightPlusButton:
               double weightValue = Double.parseDouble(weightText.getText().toString().trim());
               weightValue += weightIncrement;
               weightText.setText(Double.toString(weightValue));
               break;
           case R.id.weightMinusButton:
               weightValue = Double.parseDouble(weightText.getText().toString().trim());
               weightValue -= weightIncrement;
               if (weightValue >= 0){
                   weightText.setText(Double.toString(weightValue));
               }
               break;
       }
    }

}
