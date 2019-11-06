package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.RoomDatabase.AppDatabase;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;
import com.example.calistheicslogger.RoomDatabase.Entities.Exercise;
import com.example.calistheicslogger.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewExerciseActivity extends Activity {

    AppDatabase appDatabase;
    List<String> stringListCategories;
    Spinner categorySpinner, progressionSpinner, typeSpinner;
    Boolean bandChecked = false, weightLoadableChecked = false;

    // Put the 3 spinners here as public variables as they need to be accessed in different methods

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        appDatabase = AppDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_exercise_activity);

        setUpCategorySpinner(R.id.categorySpinner);
        progressionSpinner = setUpSpinner(R.id.progressionSpinner,ExerciseListActivity.exercises);
        ArrayList<String> exerciseTypes = new ArrayList<String>(Arrays.asList("Isometric", "Weight and Reps", "Negative"));
        typeSpinner = setUpSpinner(R.id.typeSpinner,exerciseTypes);
    }

    private Spinner setUpSpinner(int spinnerId, ArrayList<String> list) {
        Spinner spinner = findViewById(spinnerId);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        spinner.setAdapter(arrayAdapter);
        return spinner;
    }

    private void setUpCategorySpinner(final int spinnerId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                stringListCategories = appDatabase.categoryDao().getAllNames();
                for(String category : stringListCategories){
                }
                categorySpinner = setUpSpinner(spinnerId,(ArrayList<String>)stringListCategories);
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
                    //  public Exercise(String name, String category, String type, Boolean bandAssisted, Boolean weightLoadable, String progression){
                    Exercise exercise = new Exercise(exerciseName, categorySpinner.getSelectedItem().toString(), typeSpinner.getSelectedItem().toString(),
                            bandChecked, weightLoadableChecked, progressionSpinner.getSelectedItem().toString());
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
                    Log.i("Exercise Category " + i,exercise.getCategory());
                    Log.i("Exercise Type " + i,exercise.getType());
                    Log.i("Exercise Band " + i,exercise.getBandAssisted().toString());
                    Log.i("Exercise Weighted " + i,exercise.getWeightLoadable().toString());
                    Log.i("Exercise Progression " + i,exercise.getProgression());
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
                if (checked) {
                    message = "band checkbox ticked";
                    bandChecked = true;
                }
                else {
                    message = "band checkbox unticked";
                    bandChecked = false;
                }
                break;
            case R.id.weightCheckBox:
                if (checked) {
                    message = "weight checkbox ticked";
                    weightLoadableChecked = true;
                }
                else {
                    message = "weight checkbox unticked";
                    weightLoadableChecked = false;
                }
                break;
            default:
                message = "Not possible to get here??";
        }
        Toast.makeText(NewExerciseActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
