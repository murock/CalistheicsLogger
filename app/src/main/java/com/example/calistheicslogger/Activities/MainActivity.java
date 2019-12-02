package com.example.calistheicslogger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.AppDatabase;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;
import com.example.calistheicslogger.Tools.PropertyTextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PropertyChangeListener {

    AppDatabase appDatabase;
    DatabaseCommunicator databaseCommunicator;
    String selectedDate;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Handle clicks on elipsis menu items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.settings:
                Log.i("Item selected","Settings");
                return true;
            case R.id.locker:
                Log.i("Item selected","locker");
                return true;
            default:
                return  false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        appDatabase = AppDatabase.getInstance(this);
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addExerciseClick(View view){
        Log.i("Button Pressed:", "Add Exercise");
        Intent addExercise = new Intent(this,ExerciseListActivity.class);
        startActivity(addExercise);
    }

    public void calendarClick(View view)
    {
        Log.i("alfie","0" );
        selectedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        databaseCommunicator.getExercisesFromDate(selectedDate);
        populateDateTitle();
    }

    private void populateDateTitle(){
        TextView dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setText(selectedDate);
    }

    private void populateDaysExercises(){
        LinearLayout linearLayout = findViewById(R.id.listviewBox);
        List<TrackedExercise> trackedExercises = databaseCommunicator.trackedExercisesFromDate;
        if (trackedExercises.size() <= 0)
        {
            return;
        }
        boolean isNewExercise = true;
        //for(TrackedExercise exercise : databaseCommunicator.trackedExercisesFromDate){
          for (int i = 0; i < trackedExercises.size(); i++){
            TrackedExercise exercise = trackedExercises.get(i);
            String exerciseName = exercise.getName();
            PropertyTextView textView = new PropertyTextView(MainActivity.this);
            textView.setClickable(true);
            textView.exerciseName = exerciseName;
            Log.i("Alfie: ", exercise.getName());
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    PropertyTextView textView = (PropertyTextView)v;
                    Toast.makeText(MainActivity.this, "You pressed: " + textView.exerciseName ,Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            if (isNewExercise){
                PropertyTextView title = new PropertyTextView(MainActivity.this);
                title.setClickable(true);
                title.exerciseName = exerciseName;
                title.setText(exerciseName);
                title.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        PropertyTextView textView = (PropertyTextView)v;
                        Toast.makeText(MainActivity.this, "You pressed: " + textView.exerciseName ,Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                linearLayout.addView(title);
                textView.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.top_and_sides_border));
                isNewExercise = false;
            }else{
                textView.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sides_border));
            }
            String exerciseString = getTrackedExerciseString(exercise);
            textView.setText(exerciseString);
            linearLayout.addView(textView);


            if ( trackedExercises.size() == i + 1|| trackedExercises.size() > i + 1 && !exerciseName.equals(trackedExercises.get(i + 1).getName()) )
            {
                // Do something when its a new exercise
                isNewExercise = true;
                PropertyTextView spacer = new PropertyTextView(MainActivity.this);
                spacer.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.top_border));
                linearLayout.addView(spacer);
            }

        }
    }

    private String getTrackedExerciseString(TrackedExercise exercise){
        // TODO: adapt this for many different units e.g kgs/m etc
        ArrayList<String> trackedComponents = new ArrayList<String>();
        //String result = Integer.toString(exercise.getSetNumber());
        trackedComponents.add(Integer.toString(exercise.getSetNumber()));
        if (!exercise.getReps().isEmpty()) {
            //  result += "    " + exercise.getReps() + " reps";
            trackedComponents.add(exercise.getReps() + " reps");
        }
        if (!exercise.getWeight().trim().isEmpty() && !exercise.getWeight().contains(" 0"))
        {
            Log.i("weight is:", exercise.getWeight());
            //  result += "    " + exercise.getWeight() + " kgs";
            trackedComponents.add(exercise.getWeight() + " kgs");
        }
        if (!exercise.getTime().isEmpty() && !exercise.getTime().equals("00:00:00"))
        {
            trackedComponents.add(exercise.getTime());
        }
        if (!exercise.getBand().isEmpty() && !exercise.getBand().equals("No"))
        {
            //  result += "    " + exercise.getBand() + " band";
            trackedComponents.add(exercise.getBand() + " band");
        }
        int distance = exercise.getDistance();
        if (distance != -1)
        {
            trackedComponents.add(distance + " m");
        }
        if (!exercise.getTempo().isEmpty())
        {
            trackedComponents.add(exercise.getTempo().replaceAll(".(?=.)", "$0:"));
        }
        return TrackActivity.ListToRow(trackedComponents);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Log.i("Alfie prop name: ", evt.getPropertyName());
        if (evt.getPropertyName() == "exerciseFromDatePopulated"){
            Log.i("Alfie","got here");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateDaysExercises();
                }
            });
        }
    }

//    private void updateTrackingList(){
//        AppExecutors.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//                Log.i("Alfie", currentDate);
//                List<TrackedExercise> trackedExercises = appDatabase.trackedExerciseDao().getTrackedExercisesFromNameAndDate(currentExercise,currentDate);
//                globalSetNumber = trackedExercises.size() + 1;
//                ArrayList<String> trackedExercisesArrayList = new ArrayList<>();
//                for(TrackedExercise exercise : trackedExercises){
//                    trackedExercisesArrayList.add(getTrackedExerciseString(exercise));
//                }
//                UpdateDSLV(trackedExercisesArrayList);
//            }
//        });
//    }

}
