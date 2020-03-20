package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.core.graphics.ColorUtils;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.AppDatabase;
import com.example.calistheicslogger.RoomDatabase.AppExecutors;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.Exercise;
import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;
import com.example.calistheicslogger.Tools.DateFunctions;
import com.example.calistheicslogger.Tools.InputFilterMinMax;
import com.example.calistheicslogger.Tools.MultiSelectSpinner.Item;
import com.example.calistheicslogger.Tools.MultiSelectSpinner.MultiSelectionSpinner;
import com.example.calistheicslogger.Tools.dslv.DragSortController;
import com.example.calistheicslogger.Tools.dslv.DragSortListView;
import com.example.calistheicslogger.Tools.dslv.SimpleFloatViewManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TrackActivity extends Activity implements Serializable, PropertyChangeListener {

    AppDatabase appDatabase;
    DatabaseCommunicator databaseCommunicator;
    String currentExercise;
    String currentDate;
    DragSortListView dslv;
    DragSortController dragSortController;

    ArrayAdapter<String> dslvAdapter;
    // TODO: re-order set numbers when dragged
    int globalSetNumber = 1;

    int selectedPosition = -1;

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        swapTrackedExercises(from + 1,to + 1);
                        String item = dslvAdapter.getItem(from);
                        dslvAdapter.remove(item);
                        dslvAdapter.insert(item, to);
                    }
                }
            };

    private DragSortListView.OnItemClickListener onClick =
            new DragSortListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View v, int position, long arg)
                {
                    ImageButton deleteButton = findViewById(R.id.deleteButton);
                    if (position == selectedPosition)
                    {
                        // Toggle off
                        deleteButton.setEnabled(false);
                        deleteButton.setImageResource(R.drawable.faded_remove_icon);
                        selectedPosition = -1;
                    }else
                    {
                        // Toggle on
                        deleteButton.setEnabled(true);
                        deleteButton.setImageResource(R.drawable.remove_icon);
                        selectedPosition = position;
                    }
                    dslvAdapter.notifyDataSetChanged();
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        appDatabase = AppDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);
        Intent i = getIntent();
        final String exerciseString = (String)i.getSerializableExtra("Exercise");
        currentDate = (String)i.getSerializableExtra("Date");
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        SetDate();
        SetUpActivity(exerciseString);
        setUpBandSpinner();
        setUpToolsSpinner();
        SetUpFilters();
        SetUpDSLV();
        databaseCommunicator.getLatestExercise(exerciseString);
    }

    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        DragSortController controller = new DragSortController(dslv);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_LONG_PRESS);
        controller.setRemoveMode(DragSortController.FLING_REMOVE);
        return controller;
    }

    private void SetDate()
    {
        if(currentDate == null || currentDate.isEmpty())
        {
            currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
    }

    private void SetUpDSLV()
    {
        dslv = findViewById(R.id.trackedExerciseDSLV);
        dslv.setDropListener(onDrop);
        dragSortController = buildController(dslv);
        dslv.setFloatViewManager(dragSortController);
        dslv.setOnTouchListener(dragSortController);
        dslv.setOnItemClickListener(onClick);
    }

    private void UpdateDSLV(ArrayList<String> items){
        dslvAdapter = new ArrayAdapter<String>(this,R.layout.center_spinner_text,items){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                final View view = super.getView(position,convertView,parent);
                if (position == selectedPosition)
                {
                    int backgroundColor = Color.parseColor("#c2d4f0");
                    view.setBackgroundColor(backgroundColor);
                }else {
                    view.setBackgroundColor(Color.WHITE);
                }
                return view;
            }
        };
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //dslv = findViewById(R.id.trackedExerciseDSLV);
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
        EditText weightText = findViewById(R.id.weightEditText);

        hourEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        minText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 60)});
        secondText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 60)});
        lowerEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        pause1EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        liftEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        pause2EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        // TODO: Fix this to limit weight size
     //   weightText.setFilters(new InputFilter[]{new InputFilterMinMax(0,9999)});
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
                        updateTrackingList();
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
                bands.add(0,"");
                Spinner bandSpinner = findViewById(R.id.bandSpinner);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TrackActivity.this, R.layout.center_spinner_text, bands);
                bandSpinner.setAdapter(arrayAdapter);
            }
        });
    }

    // TODO: Do not repeat code fix
    private void setUpToolsSpinner(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<String> tools = appDatabase.toolDao().getAllNames();
                ArrayList<Item> items = new ArrayList<>();
                for (String tool : tools){
                    items.add(new Item(tool,false));
                }
                MultiSelectionSpinner toolsSpinner = findViewById(R.id.toolSpinner);
                toolsSpinner.setItems(items);
            }
        });
    }

    private void swapTrackedExercises(final int SetNo1, final int SetNo2)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.trackedExerciseDao().swapBySetNumber(SetNo1, SetNo2);
                updateTrackingList();
            }
        });
    }

    private void updateTrackingList(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                List<TrackedExercise> trackedExercises = appDatabase.trackedExerciseDao().getTrackedExercisesFromNameAndDate(currentExercise,currentDate);
                globalSetNumber = trackedExercises.size() + 1;
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
        ArrayList<String> trackedComponents = new ArrayList<String>();
        //String result = Integer.toString(exercise.getSetNumber());
        trackedComponents.add(Integer.toString(exercise.getSetNumber()));
        Group repGroup = findViewById(R.id.repsGroup);
        if (repGroup.getVisibility() == View.VISIBLE)
        {
          //  result += "    " + exercise.getReps() + " reps";
            trackedComponents.add(exercise.getReps() + " reps");
        }
        Group weightGroup = findViewById(R.id.weightGroup);
        if (weightGroup.getVisibility() == View.VISIBLE )
        {
          //  result += "    " + exercise.getWeight() + " kgs";
            trackedComponents.add(exercise.getWeight() + " kgs");
        }
        Group timeGroup = findViewById(R.id.timeGroup);
        if (timeGroup.getVisibility() == View.VISIBLE && !exercise.getTime().isEmpty())
        {
            trackedComponents.add(exercise.getTime());
        }
        Group bandGroup = findViewById(R.id.bandGroup);
        if (bandGroup.getVisibility() == View.VISIBLE && !exercise.getBand().isEmpty())
        {
          //  result += "    " + exercise.getBand() + " band";
            trackedComponents.add(exercise.getBand() + " band");
        }
        Group toolsGroup = findViewById(R.id.toolGroup);
        if (toolsGroup.getVisibility() == View.VISIBLE && !exercise.getTool().isEmpty())
        {
            trackedComponents.add(exercise.getTool());
        }
        int distance = exercise.getDistance();
        if (distance != -1)
        {
            trackedComponents.add(distance + " m");
        }
        if (!exercise.getTempo().isEmpty())
        {
            trackedComponents.add(exercise.getTempo());
        }
        return ListToRow(trackedComponents);
    }

    public static String ListToRow(ArrayList<String> list)
    {
        String result = new String();
        String spacer = "";
        switch(list.size()){
            case 1:
                spacer = "       ";
                break;
            case 2:
                spacer = "      ";
                break;
            case 3:
                spacer = "     ";
                break;
            case 4:
                spacer = "    ";
                break;
            case 5:
                spacer = "   ";
                break;
            case 6:
                spacer = "  ";
                break;
                default:

        }
        for (String item : list){
            result += item + spacer;
        }
        result = result.substring(0,result.length() - spacer.length());
        return result;
    }

    private String AddPrefixToItem(String item, int desiredLength, String preFix)
    {
        while (item.length() < desiredLength)
        {
            item = preFix + item;
        }
        return item;
    }

    private void SetUpControls(Exercise exercise)
    {
        TextView title = findViewById(R.id.titleTextView);
        Log.i("Alfie current date", currentDate);

        title.setText(exercise.getName() + " - " +  DateFunctions.GetUKDateFormat(currentDate));
        String type = exercise.getType();
        //"Isometric", "Weight and Reps", "Negative"
        Group group;
        group = findViewById(R.id.distanceGroup);
        group.setVisibility(View.GONE);
        group = findViewById(R.id.bandGroup);
        group.setVisibility(View.GONE);
        group = findViewById(R.id.tempoGroup);
        group.setVisibility(View.GONE);
        group = findViewById(R.id.toolGroup);
        group.setVisibility(View.GONE);
        group = findViewById(R.id.weightGroup);
        group.setVisibility(View.GONE);
        switch(type){
            case "Weight and Reps":
            case "Reps":
            case "Negative":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.GONE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.VISIBLE);
                break;
            case "Isometric":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.VISIBLE);
                group = findViewById(R.id.repsGroup);
                group.setVisibility(View.GONE);
                break;
                default:
        }
        Boolean bandAssisted = exercise.getBandAssisted();
        Boolean weighted = exercise.getWeightLoadable();
        Boolean tempoControlled = exercise.getTempoControlled();
        Boolean toolsRequired = exercise.getTool();
        if (bandAssisted)
        {
            group = findViewById(R.id.bandGroup);
            group.setVisibility(View.VISIBLE);
        }
        if (weighted){
            Log.i("Alfie", "Exercise is weighted");
            group = findViewById(R.id.weightGroup);
            group.setVisibility(View.VISIBLE);
        }
        if(tempoControlled){
            group = findViewById(R.id.tempoGroup);
            group.setVisibility(View.VISIBLE);
        }
        if (toolsRequired){
            group = findViewById(R.id.toolGroup);
            group.setVisibility(View.VISIBLE);
        }

    }

    private void populateControlsWithExercise(TrackedExercise trackedExercise){
        if (trackedExercise == null)
        {
            return;
        }
        EditText repsEditText = findViewById(R.id.repsEditText);
        EditText weightEditText = findViewById(R.id.weightEditText);
        EditText hourEditText = findViewById(R.id.hourEditText);
        EditText minuteEditText = findViewById(R.id.minuteEditText);
        EditText secondEditText = findViewById(R.id.secondEditText);
        EditText distanceEditText = findViewById(R.id.distanceEditText);
        EditText tempoEccentricEditText = findViewById(R.id.tempoEccentricEditText);
        EditText tempoPause1EditText = findViewById(R.id.tempoPause1EditText);
        EditText tempoConcentricEditText = findViewById(R.id.tempoConcentricEditText);
        EditText tempoPause2EditText = findViewById(R.id.tempoPause2EditText);
        Spinner bandSpinner = findViewById(R.id.bandSpinner);
        Spinner toolSpinner = findViewById(R.id.toolSpinner);

        repsEditText.setText(Integer.toString(trackedExercise.getReps()));
        weightEditText.setText(Double.toString(trackedExercise.getWeight()));
        String time = trackedExercise.getTime();
        String[] times = time.split(":");
        hourEditText.setText(times[0]);
        minuteEditText.setText(times[1]);
        secondEditText.setText(times[2]);
        //distanceEditText.setText(trackedExercise.getDistance());
        String tempo = trackedExercise.getTempo();
        Log.i("Tempo", tempo);
      //  tempoEccentricEditText.setText(trackedExercise);
    }

    private void startActivity(Class<?> activityToStart){
        Intent activity = new Intent(this, activityToStart);
        activity.putExtra("Exercise", currentExercise);
        startActivity(activity);
    }


    public void HistoryButtonClick(View view)
    {
        startActivity(HistoryActivity.class);
    }

    public void RecordClick(View view){ startActivity(PersonalRecordsActivity.class);}

    public void ChartsButtonClick(View view){
        startActivity(ChartActivity.class);
    }

    public void DeleteButtonClick(View view){
        if(this.selectedPosition !=  -1)
        {
            databaseCommunicator.deleteTrackedExercise(this.currentExercise,this.currentDate,this.selectedPosition + 1);
        }
    }

    public void SaveButtonClick(View view){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                EditText repsText = findViewById(R.id.repsEditText);
                String repString = repsText.getText().toString();
                Group repGroup = findViewById(R.id.repsGroup);
                int repValue = -1;

                if (repGroup.getVisibility() == View.VISIBLE && !repString.isEmpty())
                {
                    repValue = Integer.parseInt((repString.trim()));
                }
                if (repGroup.getVisibility() == View.VISIBLE && repString.isEmpty())
                {
                    repValue = 0;
                }
                EditText weightText = findViewById(R.id.weightEditText);
                // weight
                String weightString = weightText.getText().toString();//AddPrefixToItem(weightText.getText().toString(),7," ");
                Group weightGroup = findViewById(R.id.weightGroup);
                double weightValue = -1;
                if (weightGroup.getVisibility() == View.VISIBLE && !weightString.isEmpty())
                {
                   weightValue = Double.parseDouble(weightString.trim());
                }
                if (weightGroup.getVisibility() == View.VISIBLE && weightString.isEmpty())
                {
                    weightValue = 0.0;
                }
                // time here
                EditText hourText = findViewById(R.id.hourEditText);
                EditText minText = findViewById(R.id.minuteEditText);
                EditText secondText = findViewById(R.id.secondEditText);
                String time = AddPrefixToItem(hourText.getText().toString(), 2, "0") + ":" + AddPrefixToItem(minText.getText().toString(),2, "0" ) + ":"
                        + AddPrefixToItem(secondText.getText().toString(),2, "0");

                Spinner bandSpinner = findViewById(R.id.bandSpinner);
                Spinner toolSpinner = findViewById(R.id.toolSpinner);
                String toolString = "";
                Group toolGroup = findViewById(R.id.toolGroup);
                if (toolGroup.getVisibility() == View.VISIBLE){
                    toolString = toolSpinner.getSelectedItem().toString();
                }
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
                String tempo = lowerEditText.getText().toString() + ":" + pause1EditText.getText().toString() +
                        liftEditText.getText().toString() + pause2EditText.getText().toString();

                Group bandGroup = findViewById(R.id.bandGroup);
                String bandValue;
                if (bandGroup.getVisibility() == View.VISIBLE)
                {
                    bandValue = bandSpinner.getSelectedItem().toString();
                }else{
                    bandValue = "No";
                }

                final TrackedExercise trackedExercise = new TrackedExercise(currentExercise, currentDate, globalSetNumber,
                        repValue,weightValue,time,bandValue,
                        distance,tempo, toolString);
                globalSetNumber++;
                appDatabase.trackedExerciseDao().addTrackedExercise(trackedExercise);
                updateTrackingList();
            }
        });
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "trackedExerciseDeleted"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateTrackingList();
                }
            });
        } else if(evt.getPropertyName() == "latestExercise"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateControlsWithExercise(databaseCommunicator.latestExercise);
                }
            });
        }
    }
}
