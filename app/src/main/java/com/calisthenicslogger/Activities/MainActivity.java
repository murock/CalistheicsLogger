package com.calisthenicslogger.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.calisthenicslogger.R;
import com.calisthenicslogger.RoomDatabase.AppDatabase;
import com.calisthenicslogger.RoomDatabase.DatabaseCommunicator;
import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;
import com.calisthenicslogger.Tools.DateFunctions;
import com.calisthenicslogger.Tools.PropertyTextView;
import com.calisthenicslogger.ViewModels.MainActivityViewModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PropertyChangeListener, Serializable {

    AppDatabase appDatabase;
    static DatabaseCommunicator databaseCommunicator;
    String selectedDate;
    MainActivityViewModel viewModel;


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
            case R.id.about:
                Intent about = new Intent(this, AboutActivity.class);
                startActivity(about);
                return true;
            case R.id.locker:
                Intent locker = new Intent(this,LockerActivity.class);
                startActivity(locker);
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
        Intent i = getIntent();
        selectedDate= (String)i.getSerializableExtra("Timestamp");
        if (selectedDate == null || selectedDate.isEmpty())
        {
            // Default to todays date
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        databaseCommunicator.getExercisesFromDate(selectedDate);
        populateDateTitle();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        populateDateTitle();
        databaseCommunicator.getExercisesFromDate(selectedDate);
    }


    public void navigationButtonClick(View view) throws ParseException {
        int hours;
        if (view.getId() == R.id.nextButton){
            hours = 24;
        }else{
            hours = -24;
        }
        LinearLayout linearLayout = findViewById(R.id.listviewBox);
        linearLayout.removeAllViews();
        Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate);
        date = addHoursToJavaUtilDate(date, hours);
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
        databaseCommunicator.getExercisesFromDate(selectedDate);
        populateDateTitle();
    }

    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }

    private void populateDateTitle(){
        TextView dateTextView = findViewById(R.id.dateTextView);
        if (selectedDate == null)
        {
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
        dateTextView.setText(DateFunctions.GetUKDateFormat(selectedDate));
    }

    // TODO: Move to viewmodel/utlities class
    public static LinearLayout populateDaysExercises(LinearLayout linearLayout, View.OnClickListener clickListener, Context context){
        linearLayout.removeAllViews();
        List<TrackedExercise> trackedExercises = databaseCommunicator.trackedExercisesFromDate;
        if (trackedExercises.size() <= 0)
        {
            return null;
        }
        boolean isNewExercise = true;
          for (int i = 0; i < trackedExercises.size(); i++){
            TrackedExercise exercise = trackedExercises.get(i);
            String exerciseName = exercise.getName();
            PropertyTextView textView = new PropertyTextView(context);
            textView.setClickable(true);
            textView.exerciseName = exerciseName;
            textView.setOnClickListener(clickListener);
            if (isNewExercise){
                PropertyTextView title = new PropertyTextView(context);
                title.setClickable(true);
                title.exerciseName = exerciseName;
                title.setText(exerciseName);
                title.setOnClickListener(clickListener);
                linearLayout.addView(title);
                textView.setBackground(ContextCompat.getDrawable(context,R.drawable.top_and_sides_border));
                isNewExercise = false;
            }else{
                textView.setBackground(ContextCompat.getDrawable(context,R.drawable.sides_border));
            }
            String exerciseString = getTrackedExerciseString(exercise, true);
            textView.setText(exerciseString);
            linearLayout.addView(textView);

            if ( trackedExercises.size() == i + 1|| trackedExercises.size() > i + 1 && !exerciseName.equals(trackedExercises.get(i + 1).getName()) )
            {
                // Do something when its a new exercise
                isNewExercise = true;
                PropertyTextView spacer = new PropertyTextView(context);
                spacer.setBackground(ContextCompat.getDrawable(context,R.drawable.top_border));
                linearLayout.addView(spacer);
            }
        }
          return linearLayout;
    }

    public static String getTrackedExerciseString(TrackedExercise exercise, boolean includeSetNum){
        // TODO: adapt this for many different units e.g kgs/m etc
        ArrayList<String> trackedComponents = new ArrayList<String>();

        if (includeSetNum)
        {
            trackedComponents.add(Integer.toString(exercise.getSetNumber()));
        }
        if (!(exercise.getReps() == -1)) {
            //  result += "    " + exercise.getReps() + " reps";
            trackedComponents.add(exercise.getReps() + " reps");
        }

        if (exercise.getCluster().length() > 0 && exercise.getCluster() != "0"){
            String[] clusterArray = exercise.getCluster().split(" ");
            String clusterString = "";
            for(String clusterRep : clusterArray){
                clusterString += " +" + clusterRep;
            }
            trackedComponents.add(clusterString);
        }

        if (!(exercise.getWeight() == -1))
        {
            trackedComponents.add(exercise.getWeight() + " kgs");
        }
        if (!exercise.getTime().isEmpty() && !exercise.getTime().equals("000000"))
        {
            String time = exercise.getTime();
            String displayTime = time.substring(0,2) + ":" + time.substring(2,4) + ":" + time.substring(4,6);
            trackedComponents.add(displayTime);
        }
        if (!exercise.getBand().isEmpty() && !exercise.getBand().equals("No"))
        {
            trackedComponents.add(exercise.getBand() + " band");
        }
        if (!exercise.getTool().isEmpty())
        {
            trackedComponents.add(exercise.getTool());
        }
        int distance = exercise.getDistance();
        if (distance != -1)
        {
            trackedComponents.add(distance + " m");
        }
        if (!exercise.getTempo().isEmpty() && !exercise.getTempo().equals(":::"))
        {
            trackedComponents.add(exercise.getTempo());
        }
        return TrackActivity.ListToRow(trackedComponents);
    }

    private void newTrackAcitivity(String exercise){
        Intent trackActivity = new Intent(this, TrackActivity.class);
        trackActivity.putExtra("Exercise", exercise);
        trackActivity.putExtra("Date", selectedDate);
        startActivity(trackActivity);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "exerciseFromDatePopulated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout linearLayout = findViewById(R.id.listviewBox);
                    populateDaysExercises(linearLayout, handleExerciseClick, MainActivity.this);
                }
            });
        }
    }


    private View.OnClickListener handleExerciseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PropertyTextView textView = (PropertyTextView)v;
            Toast.makeText(MainActivity.this, "You pressed: " + textView.exerciseName ,Toast.LENGTH_SHORT).show();
            newTrackAcitivity(textView.exerciseName);
        }
    };

    public void addExerciseClick(View view){
        Intent addExercise = new Intent(this,ExerciseListActivity.class);
        addExercise.putExtra("Date", selectedDate);
        startActivity(addExercise);
    }

    public void calendarClick(View view)
    {
        Intent calendar = new Intent(this, CalendarActivity.class);
        startActivity(calendar);
    }

    public void toolsClick(View view)
    {
        Intent locker = new Intent(this,LockerActivity.class);
        startActivity(locker);
    }

    public void homeClick(View view)
    {
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        populateDateTitle();
        databaseCommunicator.getExercisesFromDate(selectedDate);
    }

}
