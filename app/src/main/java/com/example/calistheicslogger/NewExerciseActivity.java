package com.example.calistheicslogger;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;

public class NewExerciseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_exercise_activity);

        setUpSpinner();
    }

    private void setUpSpinner(){
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,ExerciseListActivity.exercises);
        categorySpinner.setAdapter(arrayAdapter);

    }
}
