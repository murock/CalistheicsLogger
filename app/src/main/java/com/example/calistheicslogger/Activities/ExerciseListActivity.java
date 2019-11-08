package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.AppDatabase;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends Activity {

    AppDatabase appDatabase;
    ArrayAdapter<String> arrayAdapter;
    List<String> stringListExercises;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        appDatabase = AppDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list_activity);

        setUpExercisesList();
        setUpSearchView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setUpExercisesList();
    }

    private void setUpExercisesList()
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                stringListExercises = appDatabase.exerciseDao().getAllNames();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUpListView((ArrayList<String>)stringListExercises);
                    }
                });

            }
        });
    }

    private void setUpListView(final ArrayList<String> exercises){
        ListView exercisesListView = findViewById(R.id.exercisesListView);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,exercises);

        exercisesListView.setAdapter(arrayAdapter);

        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ExerciseListActivity.this, "You pressed: " + exercises.get(position),Toast.LENGTH_SHORT).show();
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
                    arrayAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    public void newExerciseClick(View view){
        Intent newExercise = new Intent(this,NewExerciseActivity.class);
        startActivity(newExercise);
    }

}
