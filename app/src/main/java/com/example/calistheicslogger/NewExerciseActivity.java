package com.example.calistheicslogger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class NewExerciseActivity extends Activity {

    AppDatabase appDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_exercise_activity);

        setUpSpinner(R.id.categorySpinner);
        setUpSpinner(R.id.progressionSpinner);
        appDatabase = AppDatabase.getInstance(this);
    }

    private void setUpSpinner(int spinnerId) {
        Spinner categorySpinner = findViewById(spinnerId);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, ExerciseListActivity.exercises);
        categorySpinner.setAdapter(arrayAdapter);

    }

    public void onSaveButtonClicked(View view){
        EditText exerciseNameEditText = findViewById(R.id.nameEditText);
        String exerciseName = exerciseNameEditText.getText().toString();
        if (!exerciseName.isEmpty()) {
            Exercise exercise = new Exercise(exerciseName);
            appDatabase.exerciseDao().addExercise(exercise);
        }else{
            Toast.makeText(NewExerciseActivity.this, "Please Enter an exercise name", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        String message;
        switch (view.getId()) {
            case R.id.bandAssistCheckBox:
                if (checked)
                    message = "band checkbox ticked";
                else
                    message = "band checkbox unticked";
                break;
            case R.id.weightCheckBox:
                if (checked)
                    message = "weight checkbox ticked";
                else
                    message = "weight checkbox unticked";
                break;
            default:
                message = "Not possible to get here??";
        }
        Toast.makeText(NewExerciseActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
