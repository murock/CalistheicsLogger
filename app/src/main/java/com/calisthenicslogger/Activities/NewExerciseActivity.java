package com.calisthenicslogger.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.calisthenicslogger.RoomDatabase.AppDatabase;
import com.calisthenicslogger.RoomDatabase.AppExecutors;
import com.calisthenicslogger.RoomDatabase.Entities.Exercise;
import com.calisthenicslogger.R;
import com.calisthenicslogger.Tools.MultiSelectSpinner.Item;
import com.calisthenicslogger.Tools.MultiSelectSpinner.MultiSelectionSpinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewExerciseActivity extends Activity implements Serializable {

    AppDatabase appDatabase;
    Spinner progressionSpinner, typeSpinner;
    MultiSelectionSpinner categorySpinner;
    Boolean bandChecked = false, weightLoadableChecked = false, tempoChecked = false, toolChecked = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        appDatabase = AppDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_exercise_activity);

        Intent i = getIntent();
        final String typedExerciseString = (String)i.getSerializableExtra("TypedExercise");
        EditText nameEditText = findViewById(R.id.nameEditText);
        nameEditText.setText(typedExerciseString);

        setUpCategorySpinner(R.id.categorySpinner);
        setUpProgressionSpinner(R.id.progressionSpinner);
        ArrayList<String> exerciseTypes = new ArrayList<String>(Arrays.asList("Reps", "Isometric", "Weight and Reps"));
        typeSpinner = setUpSpinner(R.id.typeSpinner,exerciseTypes);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = typeSpinner.getSelectedItem().toString();
                CheckBox weightCheckbox = findViewById(R.id.weightCheckBox);
                if (selectedType == "Weight and Reps")
                {
                    weightCheckbox.setChecked(true);
                    weightLoadableChecked = true;
                    weightCheckbox.setEnabled(false);
                }else{
                    weightCheckbox.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
                List<String> stringListCategories = appDatabase.categoryDao().getAllNames();
                ArrayList<Item> items = new ArrayList<>();
                for (String category : stringListCategories){
                    items.add(new Item(category,false));
                }
                categorySpinner = findViewById(spinnerId);
                categorySpinner.setItems(items);
               // categorySpinner = setUpSpinner(spinnerId,(ArrayList<String>)stringListCategories);
            }
        });
    }

    // TODO: Find a better way to do this that doesn't repeat code
    private void setUpProgressionSpinner(final int spinnerId) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<String> stringListProgressions = appDatabase.finalProgressionDao().getAllNames();
                progressionSpinner = setUpSpinner(spinnerId,(ArrayList<String>)stringListProgressions);
            }
        });
    }

    private void newTrackAcitivity(String exercise){
        Intent trackActivity = new Intent(this, TrackActivity.class);
        trackActivity.putExtra("Exercise", exercise);
        startActivity(trackActivity);
    }


    public void onSaveButtonClicked(View view){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                EditText exerciseNameEditText = findViewById(R.id.nameEditText);
                String exerciseName = exerciseNameEditText.getText().toString();
                if (!exerciseName.isEmpty()) {
                    if (!ifExistsInDb(exerciseName)) {
                        String categories = new String();
                        List<Item> items = categorySpinner.getSelectedItems();
                        for (Item item : items) {
                            if (!categories.isEmpty()) {
                                // Add a space if there is more than one entry
                                categories += " ";
                            }
                            categories += item.getName();
                        }
                        Exercise exercise = new Exercise(exerciseName, categories, typeSpinner.getSelectedItem().toString(),
                                bandChecked, weightLoadableChecked, progressionSpinner.getSelectedItem().toString(), tempoChecked, toolChecked, false, 1.25);
                        appDatabase.exerciseDao().addExercise(exercise);
                        newTrackAcitivity(exerciseNameEditText.getText().toString());
                    }
                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewExerciseActivity.this, "Exercise already exists", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewExerciseActivity.this, "Please Enter an exercise name", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
    }

    private boolean ifExistsInDb(String exercise)
    {
        Cursor cursor = null;
        cursor = appDatabase.exerciseDao().checkExists(exercise);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.bandAssistCheckBox:
                if (checked) {
                    bandChecked = true;
                }
                else {
                    bandChecked = false;
                }
                break;
            case R.id.weightCheckBox:
                if (checked) {
                    weightLoadableChecked = true;
                }
                else {
                    weightLoadableChecked = false;
                }
                break;
            case R.id.tempoCheckBox:
                if(checked){
                    tempoChecked = true;
                }else {
                    tempoChecked = false;
                }
                break;
            case R.id.toolCheckBox:
                if (checked){
                    toolChecked = true;
                }else{
                    toolChecked = false;
                }
            default:
                //"Not possible to get here??";
        }
    }
}
