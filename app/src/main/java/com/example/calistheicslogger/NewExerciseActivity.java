package com.example.calistheicslogger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NewExerciseActivity extends Activity {

    AppDatabase appDatabase;
    List<Category> categoriesInDatabase;
    List<String> stringListCategories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        appDatabase = AppDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_exercise_activity);

        setUpCategorySpinner(R.id.categorySpinner);
        setUpSpinner(R.id.progressionSpinner,ExerciseListActivity.exercises);
    }

    private void setUpSpinner(int spinnerId, ArrayList<String> list) {
        Spinner categorySpinner = findViewById(spinnerId);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        categorySpinner.setAdapter(arrayAdapter);
    }

    private void setUpCategorySpinner(final int spinnerId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                stringListCategories = appDatabase.categoryDao().getAllNames();
                for(String category : stringListCategories){
                }
                setUpSpinner(spinnerId,(ArrayList<String>)stringListCategories);
            }
        });
    }

    public void onSaveButtonClicked(View view){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                EditText exerciseNameEditText = findViewById(R.id.nameEditText);
                String exerciseName = exerciseNameEditText.getText().toString();
                if (!exerciseName.isEmpty()) {
                    Exercise exercise = new Exercise(exerciseName);
                    appDatabase.exerciseDao().addExercise(exercise);
                }else{
                    Toast.makeText(NewExerciseActivity.this, "Please Enter an exercise name", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public void onPrintExerciseButtonClick(View view){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<Exercise> exercises = appDatabase.exerciseDao().getAll();
                int i = 1;
                for(Exercise exercise : exercises) {
                    Log.i("Exercise " + i,exercise.getName());
                    i++;
                }
            }
        });


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
