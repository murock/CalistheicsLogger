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
import android.widget.Toast;


import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.AppDatabase;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;
import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;
import com.example.calistheicslogger.Tools.PropertyTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    AppDatabase appDatabase;
    List<TrackedExercise> trackedExercises;

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
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        getTrackedExercisesFromDB(currentDate);
//        LinearLayout linearLayout = findViewById(R.id.listviewBox);
//
//
//        final String[] DynamicListElements = new String[] {
//                "Android Test",
//                "PHP",
//                "Android Studio",
//                "PhpMyAdmin"
//        };
//        boolean isFirst = true;
//        for(String item : DynamicListElements)
//        {
//            PropertyTextView test = new PropertyTextView(this);
//            test.setClickable(true);
//            test.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    PropertyTextView textView = (PropertyTextView)v;
//                    Toast.makeText(MainActivity.this, "You pressed: " + textView.exerciseName ,Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            });
//            test.exerciseName = item;
//            if (isFirst){
//                test.setBackground(ContextCompat.getDrawable(this,R.drawable.top_and_sides_border));
//                isFirst = false;
//            }else{
//                test.setBackground(ContextCompat.getDrawable(this,R.drawable.sides_border));
//            }
//
//            test.setText(item);
//            linearLayout.addView(test);
//        }
//        PropertyTextView spacer = new PropertyTextView(this);
//        spacer.setBackground(ContextCompat.getDrawable(this,R.drawable.top_border));
//        linearLayout.addView(spacer);

    }

    private void getTrackedExercisesFromDB(final String date){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                trackedExercises = appDatabase.trackedExerciseDao().getTrackedExercisesFromDate(date);
                populateDaysExercises();
            }
        });
    }

    private void populateDaysExercises(){
        LinearLayout linearLayout = findViewById(R.id.listviewBox);


        String previousExercise = "";
        boolean isFirst = true;
        for(TrackedExercise exercise : trackedExercises){
            PropertyTextView textView = new PropertyTextView(MainActivity.this);
            textView.setClickable(true);
            textView.exerciseName = exercise.getName();
            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    PropertyTextView textView = (PropertyTextView)v;
                    Toast.makeText(MainActivity.this, "You pressed: " + textView.exerciseName ,Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            if (isFirst){
                textView.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.top_and_sides_border));
                isFirst = false;
            }else{
                textView.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.sides_border));
            }
            TrackActivity TrackActivity = new TrackActivity();
            String exerciseString = TrackActivity.getTrackedExerciseString(exercise);
            textView.setText(exerciseString);

            if (previousExercise != exercise.getName())
            {
                // Do something when its a new exercise
                previousExercise = exercise.getName();
                PropertyTextView spacer = new PropertyTextView(MainActivity.this);
                spacer.setBackground(ContextCompat.getDrawable(MainActivity.this,R.drawable.top_border));
                linearLayout.addView(spacer);
            }
            linearLayout.addView(textView);
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
