package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.AppDatabase;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;
import com.example.calistheicslogger.RoomDatabase.Entities.Exercise;
import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;
import com.example.calistheicslogger.Tools.InputFilterMinMax;
import com.example.calistheicslogger.Tools.dslv.DragSortListView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TrackActivity extends Activity implements Serializable {

    AppDatabase appDatabase;
    String currentExercise;

    ArrayAdapter<String> dslvAdapter;
    // TODO: set this number based off current state of DB
    int globalSetNumber = 1;

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        String item = dslvAdapter.getItem(from);
                        dslvAdapter.remove(item);
                        dslvAdapter.insert(item, to);
                    }
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        appDatabase = AppDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);
        Intent i = getIntent();
        final String exerciseString = (String)i.getSerializableExtra("Exercise");
        SetUpActivity(exerciseString);
        setUpBandSpinner();
        SetUpFilters();
        TESTDSLV();
        updateTrackingList();
    }


    private void TESTDSLV()
    {
        DragSortListView testdslv = findViewById(R.id.trackedExerciseDSLV);
        testdslv.setDropListener(onDrop);

//        String[] columns = new String[]{"Test1", "Test2"};
//        int[] ids = new int[]{1,2};
//        SimpleDragSortCursorAdapter simpleDragSortCursorAdapter = new SimpleDragSortCursorAdapter(this,R.layout.exercise_list_activity, null, columns,ids,0);
//        Cursor cursor =

//        dslvAdapter = new ArrayAdapter<String>(this, R.layout.center_spinner_text);
//        dslvAdapter.add("1          First item");
//        dslvAdapter.add("2          Second item");
//        testdslv.setAdapter(dslvAdapter);


    }

    private void UpdateDSLV(ArrayList<String> items){
        dslvAdapter = new ArrayAdapter<>(this,R.layout.center_spinner_text,items);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DragSortListView dslv = findViewById(R.id.trackedExerciseDSLV);
                dslv.setAdapter(dslvAdapter);
            }
        });

    }

    private void SetUpFilters(){
        EditText hourEditText = findViewById(R.id.hourEditText);
        EditText minText = findViewById(R.id.minuteEditText);
        EditText secondText = findViewById(R.id.secondEditText);
        EditText lowerEditText = findViewById(R.id.tempoEccentricEditText);
        EditText pause1EditText = findViewById(R.id.tempoPause1EditText);
        EditText liftEditText = findViewById(R.id.tempoConcentricEditText);
        EditText pause2EditText = findViewById(R.id.tempoPause2EditText);

        hourEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        minText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 60)});
        secondText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 60)});
        lowerEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        pause1EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        liftEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        pause2EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
    }

    private void SetUpActivity(final String exerciseName){
        currentExercise = exerciseName;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Exercise exercise = appDatabase.exerciseDao().getExerciseFromName(exerciseName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SetUpControls(exercise);
                    }
                });
            }
        });
    }

    private void setUpBandSpinner(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<String> bands = appDatabase.bandDao().getAllBandColours();
                Spinner bandSpinner = findViewById(R.id.bandSpinner);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TrackActivity.this, R.layout.center_spinner_text, bands);
                bandSpinner.setAdapter(arrayAdapter);
            }
        });
    }

    private void updateTrackingList(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                Log.i("Alfie", currentDate);
                List<TrackedExercise> trackedExercises = appDatabase.trackedExerciseDao().getTrackedExercisesFromNameAndDate(currentExercise,currentDate);
                ArrayList<String> trackedExercisesArrayList = new ArrayList<>();
                for(TrackedExercise exercise : trackedExercises){
                    trackedExercisesArrayList.add(getTrackedExerciseString(exercise));
                }
                UpdateDSLV(trackedExercisesArrayList);
            }
        });
    }

    //    public TrackedExercise(int id, String name, String timestamp, int setNumber, String reps, String weight, String time, String band, int distance, String tempo){
    private String getTrackedExerciseString(TrackedExercise exercise)
    {
        // TODO: adapt this for many different units e.g kgs/m etc
        ArrayList<String> exercises = new ArrayList<String>();
        String result = Integer.toString(exercise.getSetNumber());
        if (!exercise.getReps().isEmpty())
        {
            result += "    " + exercise.getReps() + " reps";
            exercises.add(exercise.getReps() + " reps");
        }
        if (!exercise.getWeight().isEmpty())
        {
            result += "    " + exercise.getWeight() + " kgs";
            exercises.add(exercise.getWeight() + " kgs");
        }
        if (!exercise.getTime().isEmpty())
        {
            exercises.add(exercise.getTime());
        }
        if (!exercise.getBand().isEmpty())
        {
            result += "    " + exercise.getBand() + " band";
            exercises.add(exercise.getBand() + " band");
        }
        int distance = exercise.getDistance();
        if (distance != -1)
        {
            exercises.add(distance + " m");
        }
        if (!exercise.getTempo().isEmpty())
        {
            exercises.add(exercise.getTempo());
        }
        return result;
    }


//    private void setUpExercisesList()
//    {
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
//    }

//    private void setUpListView(final ArrayList<String> exercises){
//        ListView exercisesListView = findViewById(R.id.exercisesListView);
//
//        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,exercises);
//
//        exercisesListView.setAdapter(arrayAdapter);
//
//        exercisesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ExerciseListActivity.this, "You pressed: " + exercises.get(position),Toast.LENGTH_SHORT).show();
//                newTrackAcitivity(exercises.get(position));
//            }
//        });
//    }

    //     public TrackedExercise(String name, String timestamp, int setNumber, String reps, String weight, String time, String band, int distance, String tempo){

    public void SaveButtonClick(View view){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.i("Alfie","1");
                TextView titleText = findViewById(R.id.titleTextView);
                EditText repsText = findViewById(R.id.repsEditText);
                EditText weightText = findViewById(R.id.weightEditText);
                // time here
                EditText hourText = findViewById(R.id.hourEditText);
                EditText minText = findViewById(R.id.minuteEditText);
                EditText secondText = findViewById(R.id.secondEditText);
                String time = hourText.getText().toString() + minText.getText().toString() + secondText.getText().toString();

                Spinner bandSpinner = findViewById(R.id.bandSpinner);
                EditText distanceText = findViewById(R.id.distanceEditText);
                int distance = -1;
                if (distanceText.getText().toString() == ""){
                    distance = Integer.parseInt(distanceText.getText().toString());
                }

                // Tempo
                EditText lowerEditText = findViewById(R.id.tempoEccentricEditText);
                EditText pause1EditText = findViewById(R.id.tempoPause1EditText);
                EditText liftEditText = findViewById(R.id.tempoConcentricEditText);
                EditText pause2EditText = findViewById(R.id.tempoPause2EditText);
                String tempo = lowerEditText.getText().toString() + pause1EditText.getText().toString() +
                               liftEditText.getText().toString() + pause2EditText.getText().toString();

                //TODO: make someway of saving exercises in the past/future
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                Log.i("Alfie","2");


                final TrackedExercise trackedExercise = new TrackedExercise(titleText.getText().toString(), currentDate, globalSetNumber,
                        repsText.getText().toString(),weightText.getText().toString(),time,bandSpinner.getSelectedItem().toString(),
                        distance,tempo);
                Log.i("Alfie","3");
                globalSetNumber++;
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Alfie","4");
                        appDatabase.trackedExerciseDao().addTrackedExercise(trackedExercise);
                        Log.i("Alfie","5");
                        updateTrackingList();
                    }
                });
            }
        });
    }

//    public void onSaveButtonClicked(View view){
//        AppExecutors.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                EditText exerciseNameEditText = findViewById(R.id.nameEditText);
//                String exerciseName = exerciseNameEditText.getText().toString();
//                if (!exerciseName.isEmpty()) {
//                    String categories = new String();
//                    List<Item> items =  categorySpinner.getSelectedItems();
//                    for (Item item : items)
//                    {
//                        if (!categories.isEmpty()){
//                            // Add a space if there is more than one entry
//                            categories += " ";
//                        }
//                        categories += item.getName();
//                    }
//                    Exercise exercise = new Exercise(exerciseName, categories, typeSpinner.getSelectedItem().toString(),
//                            bandChecked, weightLoadableChecked, progressionSpinner.getSelectedItem().toString(), tempoChecked);
//                    appDatabase.exerciseDao().addExercise(exercise);
//                }else{
//                    Toast.makeText(NewExerciseActivity.this, "Please Enter an exercise name", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private void SetUpControls(Exercise exercise)
    {
        TextView title = findViewById(R.id.titleTextView);
        title.setText(exercise.getName());
        String type = exercise.getType();
        //"Isometric", "Weight and Reps", "Negative"
        switch(type){
            case "Isometric":
                Group group = findViewById(R.id.timeGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.bandGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.tempoGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.weightGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.distanceGroup);
                group.setVisibility(View.GONE);
                break;
            case "Weight and Reps":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.bandGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.tempoGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.weightGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.distanceGroup);
                group.setVisibility(View.GONE);
                break;
            case "Negative":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.bandGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.tempoGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.weightGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.distanceGroup);
                group.setVisibility(View.GONE);
                break;
                default:
        }
        Boolean bandAssisted = exercise.getBandAssisted();
        Boolean weighted = exercise.getWeightLoadable();
        Boolean tempoControlled = exercise.getTempoControlled();
        if (bandAssisted)
        {
            Group group = findViewById(R.id.bandGroup);
            group.setVisibility(View.VISIBLE);
        }
        if (weighted){
            Group group = findViewById(R.id.weightGroup);
            group.setVisibility(View.VISIBLE);
        }
        if(tempoControlled){
            Group group = findViewById(R.id.tempoGroup);
            group.setVisibility(View.VISIBLE);
        }
    }

    public void OnButtonClick(View view){
        //TODO: Hardcoded fix later
        int repIncrement = 1;
        double weightIncrement = 1.25;
        EditText repsText = findViewById(R.id.repsEditText);
        EditText weightText = findViewById(R.id.weightEditText);
        if (TextUtils.isEmpty(repsText.getText().toString())) {
            repsText.setText("0");
        }
        if (TextUtils.isEmpty(weightText.getText().toString())){
            weightText.setText("0");
        }
       switch(view.getId()) {
           case R.id.repsPlusButton:
               int value = Integer.parseInt(repsText.getText().toString().trim());
               value += repIncrement;
               repsText.setText(Integer.toString(value));
               break;
           case R.id.repsMinusButton:
               value = Integer.parseInt(repsText.getText().toString().trim());
               value -= repIncrement;
               if (value >= 0) {
                   repsText.setText(Integer.toString(value));
               }
               break;
           case R.id.weightPlusButton:
               double weightValue = Double.parseDouble(weightText.getText().toString().trim());
               weightValue += weightIncrement;
               weightText.setText(Double.toString(weightValue));
               break;
           case R.id.weightMinusButton:
               weightValue = Double.parseDouble(weightText.getText().toString().trim());
               weightValue -= weightIncrement;
               if (weightValue >= 0){
                   weightText.setText(Double.toString(weightValue));
               }
               break;
       }
    }

}
