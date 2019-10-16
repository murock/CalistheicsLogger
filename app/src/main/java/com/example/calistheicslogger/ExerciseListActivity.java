package com.example.calistheicslogger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ExerciseListActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_list_activity);

        setUpListView();

    }

    private void setUpListView(){
        ListView exercisesListView = findViewById(R.id.exercisesListView);

        // Hardcoded exercises delete later
        final ArrayList<String> exercises = new ArrayList<String>();
        exercises.add("Human Flag");
        exercises.add("Back Lever");
        exercises.add("Handstand");
        exercises.add("Front lever");

        ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,exercises);

        exercisesListView.setAdapter(arrayAdapter);

        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ExerciseListActivity.this, "You pressed: " + exercises.get(position),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpSearchView(){
        SearchView exerci
    }

}
