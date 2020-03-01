package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.Tools.TextViewCloseArrayAdapter;

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
    SharedPreferences prefs;
    Boolean isInitialView = true;
    Boolean isProgressionView = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list_activity);
        Intent i = getIntent();
        currentDate = (String)i.getSerializableExtra("Date");

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isProgressionView = prefs.getBoolean("isProgressionView", true);

        setUpInitialView();
        setUpSearchView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isInitialView = true;
        setUpInitialView();
    }

    @Override
    public void onBackPressed() {
        if (this.isInitialView)
        {
            super.onBackPressed();
        }else{
            this.isInitialView = true;
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
    }

    private void setUpListView(final ArrayList<String> exercises){
        ListView exercisesListView = findViewById(R.id.exercisesListView);

//        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,exercises);
//
//        exercisesListView.setAdapter(arrayAdapter);

        TextViewCloseArrayAdapter textViewCloseArrayAdapter = new TextViewCloseArrayAdapter(exercises, this);
        textViewCloseArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.i("Alfie", textViewCloseArrayAdapter.removedItem);
                Alert();
            }
        });
        exercisesListView.setAdapter(textViewCloseArrayAdapter);

        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TextClick","click ok" );
                RelativeLayout test = (RelativeLayout)view;
                TextView textView = (TextView)test.getChildAt(0);
               // TextView textView = (TextView)view;
                if (isInitialView){
                    isInitialView = false;
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

    private void setUpInitialView(){
        ImageButton toggleButton = findViewById(R.id.toggleSearchButton);
        if (this.isProgressionView)
        {
            prefs.edit().putBoolean("isProgressionView", true).commit();
            toggleButton.setImageResource(R.drawable.muscle_icon);
            databaseCommunicator.getAllProgressions();
        }else{
            prefs.edit().putBoolean("isProgressionView", false).commit();
            toggleButton.setImageResource(R.drawable.progression_icon);
            databaseCommunicator.getAllCategories();
        }
    }

    public void Alert( ){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Toast.makeText(ExerciseListActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ExerciseListActivity.this,"You clicked no button",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
        this.isProgressionView = !this.isProgressionView;
        this.prefs.edit().putBoolean("isProgressionView", this.isProgressionView).commit();
        Log.i("Alfie", this.isProgressionView + "");
        setUpInitialView();
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
