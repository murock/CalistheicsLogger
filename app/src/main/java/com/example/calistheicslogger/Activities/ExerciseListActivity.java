package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.AppDatabase;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.Exercise;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends Activity implements PropertyChangeListener, Serializable {

    DatabaseCommunicator databaseCommunicator;
    ArrayAdapter<String> arrayAdapter;
    List<String> exerciseNames;
    List<String> progressions;
    String currentDate;
    Boolean isIntialView = true;
    Boolean isProgressionView = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list_activity);
        Intent i = getIntent();
        currentDate = (String)i.getSerializableExtra("Date");

        databaseCommunicator.getAllProgressions();
        setUpSearchView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isIntialView = true;
        databaseCommunicator.getAllProgressions();
    }

    @Override
    public void onBackPressed() {
        if (this.isIntialView)
        {
            super.onBackPressed();
        }else{
            this.isIntialView = true;
            this.setUpInitialView();
        }
    }

    private void setUpExercisesList(List<String> exercisesList)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUpListView((ArrayList<String>)exercisesList);
            }
        });

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
    }

    private void setUpListView(final ArrayList<String> exercises){
        ListView exercisesListView = findViewById(R.id.exercisesListView);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,exercises);

        exercisesListView.setAdapter(arrayAdapter);

        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView)view;
                if (isIntialView){
                    isIntialView = false;
                    // Go into secondary view
                    if (isProgressionView) {
                        databaseCommunicator.getNamesFromProgression(textView.getText().toString());
                    }else{
                        databaseCommunicator.getNamesFromCategory(textView.getText().toString());
                    }
                }else{
                    // Open track exercise activity for selected exercise
                    newTrackAcitivity(textView.getText().toString());
                }
            }
        });
    }

    private void setUpSearchView(){

        final SearchView exerciseSearchView = findViewById(R.id.exerciseSearchView);

        // Makes the whole search view clickable not just the magnifying glass icon
        exerciseSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exerciseSearchView.setIconified(false);
            }
        });

        exerciseSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (arrayAdapter != null) {
                    arrayAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });
    }

    public void newExerciseClick(View view){
        Intent newExercise = new Intent(this,NewExerciseActivity.class);
        startActivity(newExercise);
    }

    public void newTrackAcitivity(String exercise){
        Intent trackActivity = new Intent(this, TrackActivity.class);
        trackActivity.putExtra("Exercise", exercise);
        trackActivity.putExtra("Date", currentDate);
        startActivity(trackActivity);
    }

    public void ToggleSearchClick(View view){
        ImageButton toggleButton = findViewById(R.id.toggleSearchButton);
        if (this.isProgressionView)
        {
            this.isProgressionView = false;
            toggleButton.setImageResource(R.drawable.progression_icon);
        }else{
            this.isProgressionView = true;
            toggleButton.setImageResource(R.drawable.muscle_icon);
        }
        setUpInitialView();
    }

    private void setUpInitialView(){
        if (this.isProgressionView)
        {
            databaseCommunicator.getAllProgressions();
        }else {
            databaseCommunicator.getAllCategories();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // Trigger after database has populated
        if (evt.getPropertyName() == "progressions"){
            this.progressions = databaseCommunicator.progressions;
            setUpExercisesList(this.progressions);
        }else if(evt.getPropertyName() == "exerciseNames"){
            Log.i("Alfie", "exercise names updated");
            this.exerciseNames = databaseCommunicator.exerciseNames;
            setUpExercisesList(this.exerciseNames);
        } else if(evt.getPropertyName() == "categories"){
            setUpExercisesList(databaseCommunicator.categories);
        }
    }
}
