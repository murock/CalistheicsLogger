package com.example.calistheicslogger.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.Exercise;
import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;
import com.example.calistheicslogger.Tools.DateFunctions;
import com.example.calistheicslogger.Tools.InputFilterMinMax;
import com.example.calistheicslogger.Tools.MultiSelectSpinner.Item;
import com.example.calistheicslogger.Tools.MultiSelectSpinner.MultiSelectionSpinner;
import com.example.calistheicslogger.Tools.PropertyTextView;
import com.example.calistheicslogger.Tools.TextViewPRArrayAdapter;
import com.example.calistheicslogger.Tools.dslv.DragSortController;
import com.example.calistheicslogger.Tools.dslv.DragSortListView;
import com.google.android.material.navigation.NavigationView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TrackActivity extends AppCompatActivity implements Serializable, PropertyChangeListener {
    DatabaseCommunicator databaseCommunicator;
    Exercise exercise;
    String currentExercise;
    String currentDate;
    DragSortListView dslv;
    DragSortController dragSortController;
    ArrayAdapter<String> bandArrayAdapter;
    TextViewPRArrayAdapter dslvAdapter;
    List<TrackedExercise> trackedExercises;
    ArrayList<Integer> personalRecordPositions = new ArrayList<>();
    ArrayList<Integer> clusterRepsList = new ArrayList<>();
    // TODO: re-order set numbers when dragged
    int globalSetNumber = 1;

    int selectedPosition = -1;

    Boolean bandAssisted, weighted, tempoControlled, toolsRequired, isCluster = false;
    Menu menu;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    static CountDownTimer restTimer;

    String exerciseString;

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
                    ImageButton saveButton = findViewById(R.id.saveButton);
                    if (position == selectedPosition)
                    {
                        // Toggle off
                        deleteButton.setEnabled(false);
                        deleteButton.setImageResource(R.drawable.faded_remove_icon);
                        saveButton.setImageResource(R.drawable.add_icon);
                        selectedPosition = -1;
                    }else
                    {
                        // Toggle on
                        for(TrackedExercise trackedExercise : trackedExercises){
                            if(trackedExercise.getSetNumber() == position + 1){
                                populateControlsWithExercise(trackedExercise);
                            }
                        }
                        deleteButton.setEnabled(true);
                        deleteButton.setImageResource(R.drawable.remove_icon);
                        saveButton.setImageResource(R.drawable.update_icon);
                        selectedPosition = position;
                    }
                    dslvAdapter.setSelectedPostion(selectedPosition);
                    dslvAdapter.notifyDataSetChanged();
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.tracker_menu, menu);
        super.onCreateOptionsMenu(menu);
        MenuItem menuItem;
        if (bandAssisted)
        {
            menuItem = menu.findItem(R.id.bandAssisted);
            menuItem.setChecked(true);
        }
        if (weighted){
            menuItem = menu.findItem(R.id.weighted);
            menuItem.setChecked(true);
        }
        if(tempoControlled){
            menuItem = menu.findItem(R.id.tempo);
            menuItem.setChecked(true);
        }
        if (toolsRequired){
            menuItem = menu.findItem(R.id.tools);
            menuItem.setChecked(true);
        }
        if (isCluster){
            menuItem = menu.findItem(R.id.cluster);
            menuItem.setChecked(true);
        }
        return true;
    }

    // Handle clicks on elipsis menu items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (t.onOptionsItemSelected(item)){
           return true;
        }
        item.setChecked(!item.isChecked());
        switch(item.getItemId()){
            case R.id.tools:
                exercise.setTool(item.isChecked());
                break;
            case R.id.tempo:
                exercise.setTempoControlled(item.isChecked());
                break;
            case R.id.bandAssisted:
                exercise.setBandAssisted(item.isChecked());
                break;
            case R.id.weighted:
                exercise.setWeightLoadable(item.isChecked());
                break;
            case R.id.cluster:
                exercise.setCluster(item.isChecked());
                break;
            case R.id.historyButton:
                startActivity(HistoryActivity.class);
                break;
            case R.id.chartButton:
                startActivity(ChartActivity.class);
                break;
            case R.id.personalRecordButton:
                startActivity(PersonalRecordsActivity.class);
                break;
            case R.id.restButton:
                startActivity(RestTimerActivity.class);
                break;
            case R.id.stopWatchButton:
                startActivity(StopwatchActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        databaseCommunicator.updateExercise(exercise);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
       // appDatabase = AppDatabase.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_activity);
        Intent i = getIntent();
        exerciseString = (String)i.getSerializableExtra("Exercise");
        currentDate = (String)i.getSerializableExtra("Date");
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        SetUpNavigationDrawer();
        SetDate();
        databaseCommunicator.getExerciseFromName(exerciseString);
        databaseCommunicator.getBandColours();
        SetUpFilters();
        SetUpDSLV();
        databaseCommunicator.getTrackedExercisesFromNameAndDate(exerciseString, currentDate);
        databaseCommunicator.getPersonalRecords(exerciseString);
        PopulateExercisesNavigationDrawer();
    }

    @Override
    protected  void onResume()
    {
        super.onResume();
        // Reloads the tool list among other things
        databaseCommunicator.getExerciseFromName(exerciseString);
        // Reloads the trackedExercise list, can be updated by iso timer
        if (currentExercise != null && currentDate != null &&
                currentExercise.length() > 0 && currentDate.length() > 0){
            databaseCommunicator.getTrackedExercisesFromNameAndDate(currentExercise, currentDate);
        }
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

    private void SetUpNavigationDrawer()
    {
        dl = (DrawerLayout)findViewById(R.id.activity_track);
        t = new ActionBarDrawerToggle(this, dl,R.string.open,R.string.close );

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.navigatorDrawer);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.home:
                        startActivity(MainActivity.class);
                        break;
                    case R.id.tools:
                        startActivity(LockerActivity.class);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
    }

    private void PopulateExercisesNavigationDrawer(){
        Menu drawerMenu = nv.getMenu();
        // TODO: avoid hardcoded index
        MenuItem exercisesItem = drawerMenu.getItem(2);
        Menu exercisesSubMenu = exercisesItem.getSubMenu();
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Human Flag (2 sets)");
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Really really really long Human Flag (2 sets)");
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Human Flag (2 sets)");
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Really really really long Human Flag (2 sets)");
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Human Flag (2 sets)");
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Really really really long Human Flag (2 sets)");
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Human Flag (2 sets)");
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Really really really long Human Flag (2 sets)");
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Human Flag (2 sets)");
        exercisesSubMenu.add(Menu.NONE, 1, Menu.NONE, "Really really really long Human Flag (2 sets)");
//        LinearLayout linearLayout = drawerMenu.findItem(findViewById(R.id.listViewBox));
//        ScrollView scrollView = drawerMenu.findViewById(R.id.scrollView);
//        Log.i("Alfie scroll", scrollView + "");
//        Log.i("Alfie", linearLayout + "");
       // MainActivity.populateDaysExercises(linearLayout, handleExerciseClick, TrackActivity.this);
    }

    private View.OnClickListener handleExerciseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PropertyTextView textView = (PropertyTextView)v;
            Toast.makeText(TrackActivity.this, "You pressed: " + textView.exerciseName ,Toast.LENGTH_SHORT).show();
        }
    };

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
        dslvAdapter = new TextViewPRArrayAdapter(items,this);//ArrayAdapter<String>(this,R.layout.center_spinner_text,items){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        minText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 59)});
        secondText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 59)});
        lowerEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        pause1EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        liftEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        pause2EditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        // TODO: Fix this to limit weight size
     //   weightText.setFilters(new InputFilter[]{new InputFilterMinMax(0,9999)});
    }

    private void SetUpActivity(final String exerciseName){
        currentExercise = exerciseName;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SetUpControls(exercise);
                        databaseCommunicator.getPersonalRecords(currentExercise);
                    }
                });

    }

    private void setUpBandSpinner(){
                List<String> bands = databaseCommunicator.bandColours;

                Spinner bandSpinner = findViewById(R.id.bandSpinner);
                bandArrayAdapter = new ArrayAdapter<String>(TrackActivity.this, R.layout.center_spinner_text, bands);
                bandSpinner.setAdapter(bandArrayAdapter);
    }

    private void setUpToolsSpinner(List<String> tools){
                ArrayList<Item> items = new ArrayList<>();
                for (String tool : tools){
                    items.add(new Item(tool,false));
                }
                MultiSelectionSpinner toolsSpinner = findViewById(R.id.toolSpinner);
                toolsSpinner.setItems(items);
    }

    private void swapTrackedExercises(final int SetNo1, final int SetNo2)
    {
        databaseCommunicator.swapTrackedExercisesBySetNumber(SetNo1, SetNo2);
    }

    private void updateTrackingList(){
                globalSetNumber = trackedExercises.size() + 1;
                ArrayList<String> trackedExercisesArrayList = new ArrayList<>();
                for(TrackedExercise exercise : trackedExercises){
                    trackedExercisesArrayList.add(MainActivity.getTrackedExerciseString(exercise,true));
                }
                UpdateDSLV(trackedExercisesArrayList);
    }

    private void checkForPR(){
        if(trackedExercises == null)
        {
            return;
        }
        this.personalRecordPositions.clear();
        for(int i = 0; i < trackedExercises.size(); i++)
        {
            for(TrackedExercise trackedExercise:  databaseCommunicator.personalRecordsList){
                if (trackedExercise.getId() == trackedExercises.get(i).getId()){
                    this.personalRecordPositions.add(i);
                }
            }
        }
        dslvAdapter.setPrPositions(personalRecordPositions);
        dslvAdapter.notifyDataSetChanged();
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

    public static String AddPrefixToItem(String item, int desiredLength, String preFix)
    {
        while (item.length() < desiredLength)
        {
            item = preFix + item;
        }
        return item;
    }

    private void SetUpControls(Exercise exercise)
    {
        Log.i("Alfie", "setting visibilty");
        TextView title = findViewById(R.id.titleTextView);

        title.setText(exercise.getName() + " - " +  DateFunctions.GetUKDateFormat(currentDate));
        String type = exercise.getType();
        //"Isometric", "Weight and Reps"
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
        group = findViewById(R.id.clusterGroup);
        group.setVisibility(View.GONE);
        switch(type){
            case "Weight and Reps":
            case "Reps":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.GONE);
                this.repsGroupSetVisibility(View.VISIBLE);
                break;
            case "Isometric":
                group = findViewById(R.id.timeGroup);
                group.setVisibility(View.VISIBLE);
                this.repsGroupSetVisibility(View.GONE);
                break;
                default:
        }
        bandAssisted = exercise.getBandAssisted();
        weighted = exercise.getWeightLoadable();
        tempoControlled = exercise.getTempoControlled();
        toolsRequired = exercise.getTool();
        isCluster = exercise.getCluster();
        if (bandAssisted)
        {
            group = findViewById(R.id.bandGroup);
            group.setVisibility(View.VISIBLE);
        }
        if (weighted){
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
        if(isCluster){
            group = findViewById(R.id.clusterGroup);
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
        MultiSelectionSpinner toolSpinner = findViewById(R.id.toolSpinner);

        repsEditText.setText(Integer.toString(trackedExercise.getReps()));
        weightEditText.setText(Double.toString(trackedExercise.getWeight()));
        String time = trackedExercise.getTime();
        if (time.length() == 6){
            hourEditText.setText(time.substring(0,2));
            minuteEditText.setText(time.substring(2,4));
            secondEditText.setText(time.substring(4,6));
        }

        distanceEditText.setText(Integer.toString(trackedExercise.getDistance()));
        String tempo = trackedExercise.getTempo();
        String[] times = tempo.split(":");
        if (times.length == 4) {
            tempoEccentricEditText.setText(times[0]);
            tempoPause1EditText.setText(times[1]);
            tempoConcentricEditText.setText(times[2]);
            tempoPause2EditText.setText(times[3]);
        }
        String band = trackedExercise.getBand();
        int spinnerPos = bandArrayAdapter.getPosition(band);
        bandSpinner.setSelection(spinnerPos);
        String tool = trackedExercise.getTool();
        String[] tools = tool.split(",");
        ArrayList<Item> items = new ArrayList<>();
        for(String item : tools)
        {
            item = item.trim();
            items.add(new Item(item, false));
        }
        toolSpinner.setSelection(items);
    }

    private void startActivity(Class<?> activityToStart){
        Intent activity = new Intent(this, activityToStart);
        activity.putExtra("Exercise", currentExercise);
        startActivity(activity);
    }

    public void DeleteButtonClick(View view){
        if(this.selectedPosition !=  -1)
        {
            databaseCommunicator.deleteTrackedExercise(this.currentExercise,this.currentDate,this.selectedPosition + 1);
        }
        this.UnSelectSet();
    }

    public void SaveButtonClick(View view){
        if (this.selectedPosition == -1)
        {
            final TrackedExercise trackedExercise = createTrackedExercise(globalSetNumber);
            globalSetNumber++;
            databaseCommunicator.addTrackedExercise(trackedExercise);
        }else{
            this.updateExercise();
            this.UnSelectSet();
        }

    }

    private void repsGroupSetVisibility(int visibility){
        EditText repsText = findViewById(R.id.repsEditText);
        ImageButton repsMinusButton = findViewById(R.id.repsMinusButton);
        ImageButton repsPositiveButton = findViewById(R.id.repsPlusButton);
        TextView repsTitle = findViewById(R.id.repsTextView);
        TextView repsClusterText = findViewById(R.id.repsClusterTextView);
        repsClusterText.setVisibility(visibility);
        repsText.setVisibility(visibility);
        repsMinusButton.setVisibility(visibility);
        repsPositiveButton.setVisibility(visibility);
        repsTitle.setVisibility(visibility);
    }

    private TrackedExercise createTrackedExercise(int setNumber){
        EditText repsText = findViewById(R.id.repsEditText);
        String repString = repsText.getText().toString();
        int repValue = -1;

        if (repsText.getVisibility() == View.VISIBLE && !repString.isEmpty())
        {
            repValue = Integer.parseInt((repString.trim()));
        }
        if (repsText.getVisibility() == View.VISIBLE && repString.isEmpty())
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
        String time = AddPrefixToItem(hourText.getText().toString(), 2, "0") +  AddPrefixToItem(minText.getText().toString(), 2, "0")
                + AddPrefixToItem(secondText.getText().toString(), 2, "0");

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
        Group tempoGroup = findViewById(R.id.tempoGroup);
        String tempo = "";
        if(tempoGroup.getVisibility() == View.VISIBLE)
        {
            EditText lowerEditText = findViewById(R.id.tempoEccentricEditText);
            EditText pause1EditText = findViewById(R.id.tempoPause1EditText);
            EditText liftEditText = findViewById(R.id.tempoConcentricEditText);
            EditText pause2EditText = findViewById(R.id.tempoPause2EditText);
            tempo = lowerEditText.getText().toString() + ":" + pause1EditText.getText().toString() + ":" +
                    liftEditText.getText().toString() + ":" + pause2EditText.getText().toString();
        }

        Group bandGroup = findViewById(R.id.bandGroup);
        String bandValue;
        if (bandGroup.getVisibility() == View.VISIBLE)
        {
            bandValue = bandSpinner.getSelectedItem().toString();
        }else{
            bandValue = "No";
        }

        int rest = getRestTime();

        String cluster = "";
        boolean isFirst = true;
        for(int clusterReps : this.clusterRepsList){
            if (isFirst){
                cluster += Integer.toString(clusterReps);
                isFirst = false;
            }else{
                cluster += " " + clusterReps;
            }
        }


        return new TrackedExercise(currentExercise, currentDate, setNumber,
                repValue,weightValue,time,bandValue,
                distance,tempo, toolString, rest, cluster);
    }

    private void UnSelectSet(){
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        ImageButton saveButton = findViewById(R.id.saveButton);
        // Toggle off
        deleteButton.setEnabled(false);
        deleteButton.setImageResource(R.drawable.faded_remove_icon);
        saveButton.setImageResource(R.drawable.add_icon);
        selectedPosition = -1;

        dslvAdapter.setSelectedPostion(selectedPosition);
        dslvAdapter.notifyDataSetChanged();
    }

    private void updateExercise(){
        this.databaseCommunicator.updateTrackedExercise(this.createTrackedExercise(this.selectedPosition + 1), this.currentExercise,this.currentDate,this.selectedPosition + 1);
    }

    private void saveExercise(){
        databaseCommunicator.getTrackedExercisesFromNameAndDate(currentExercise, currentDate);
        databaseCommunicator.getPersonalRecords(currentExercise);
        SharedPreferences prefs = getSharedPreferences("RestSharedPreferences", MODE_PRIVATE);
        Boolean timerOn = prefs.getBoolean("timerOn", false);
        if(timerOn)
        {
            startRestTimer();
        }
    }

    // Returns the rest time, returns -1 if no rest set
    private int getRestTime(){
        SharedPreferences prefs = getSharedPreferences("RestSharedPreferences", MODE_PRIVATE);
        int timerValue = prefs.getInt("timerValue", 0); // 0 is default
        Boolean timerOn = prefs.getBoolean("timerOn", false);
        if (timerOn && timerValue > 0)
        {
            return timerValue;
        }
        return -1;
    }

    private void startRestTimer(){
        SharedPreferences prefs = getSharedPreferences("RestSharedPreferences", MODE_PRIVATE);
        int timerValue = prefs.getInt("timerValue", 0); // 0 is default
        int timerValueMilli = timerValue * 1000;

        int volumeValue = prefs.getInt("volume", 100);
        float volumeFloatValue = (float)volumeValue/100;
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.stop_rest_beep);
        boolean soundOn = prefs.getBoolean("soundOn", true);
        if (!soundOn){
            // 0 volume is sound set to off
            volumeFloatValue = 0;
        }
        mediaPlayer.setVolume(volumeFloatValue,volumeFloatValue);

        // Vibrate
        Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        boolean vibrateOn = prefs.getBoolean("vibrateOn", true);

        MenuItem restItem = menu.findItem(R.id.restButton);
        if(restTimer != null)
        {
            restTimer.cancel();
        }
        restTimer = new CountDownTimer(timerValueMilli, 1000) {

            public void onTick(long millisUntilFinished) {
                restItem.setIcon(null);
                restItem.setTitle(Integer.toString((int)millisUntilFinished/1000));
            }

            public void onFinish() {
                restItem.setIcon(R.drawable.rest_timer_icon);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrateOn) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else if(vibrateOn) {
                    vibrator.vibrate(500);
                }
                mediaPlayer.start();
            }
        }.start();
    }

    public void OnButtonClick(View view){
        //TODO: Hardcoded fix later
        int repIncrement = 1;
        double weightIncrement = 1.25;
        EditText repsText = findViewById(R.id.repsEditText);
        EditText weightText = findViewById(R.id.weightEditText);
        EditText clusterEditText = findViewById(R.id.clusterEditText);
        if (TextUtils.isEmpty(repsText.getText().toString())) {
            repsText.setText("0");
        }
        if (TextUtils.isEmpty(weightText.getText().toString())){
            weightText.setText("0");
        }
        if (TextUtils.isEmpty(clusterEditText.getText().toString())){
            clusterEditText.setText("0");
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
           case R.id.isoTimerButton:
               databaseCommunicator.setPendingIsoExercise(createTrackedExercise(globalSetNumber), globalSetNumber);
               startActivity(IsoTimerActivity.class);
               break;
           case R.id.clusterAddButton:
               ImageButton repsPositiveButton = findViewById(R.id.repsPlusButton);
               ImageButton repsNegButton = findViewById(R.id.repsMinusButton);
               String clusterReps = clusterEditText.getText().toString();
               if (clusterReps.length() > 0){
                   this.clusterRepsList.add(Integer.parseInt(clusterReps));
                   this.setClusterText();
                   repsPositiveButton.setVisibility(View.GONE);
                   repsNegButton.setVisibility(View.GONE);
               }

               break;
           case R.id.clusterRemoveButton:
               repsPositiveButton = findViewById(R.id.repsPlusButton);
               repsNegButton = findViewById(R.id.repsMinusButton);
               TextView repsClusterText = findViewById(R.id.repsClusterTextView);
               if (this.clusterRepsList.size() > 0){
                   this.clusterRepsList.remove(this.clusterRepsList.size() - 1);
                   setClusterText();
               }
               if(this.clusterRepsList.size() == 0){
                   repsClusterText.setText("");
                   repsPositiveButton.setVisibility(View.VISIBLE);
                   repsNegButton.setVisibility(View.VISIBLE);
               }
               break;
           case R.id.clusterMinusButton:
               value = Integer.parseInt(clusterEditText.getText().toString().trim());
               value -= repIncrement;
               if (value >= 0) {
                   clusterEditText.setText(Integer.toString(value));
               }
               break;
           case R.id.clusterPlusButton:
               value = Integer.parseInt(clusterEditText.getText().toString().trim());
               value += repIncrement;
               clusterEditText.setText(Integer.toString(value));
               break;
       }
    }

    private void setClusterText(){
        TextView repsClusterText = findViewById(R.id.repsClusterTextView);
        String clusterText = "";
        for(int reps : this.clusterRepsList){
            clusterText += "+" + reps + " ";
        }
        repsClusterText.setText(clusterText);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "trackedExercisesUpdated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    databaseCommunicator.getTrackedExercisesFromNameAndDate(currentExercise, currentDate);
                    databaseCommunicator.getPersonalRecords(currentExercise);
                }
            });
        } else if(evt.getPropertyName() == "latestExercise"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    populateControlsWithExercise(databaseCommunicator.latestExercise);
                }
            });
        } else if(evt.getPropertyName() == "exerciseUpdated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SetUpControls(exercise);
                }
            });
        } else if (evt.getPropertyName() == "personalRecordsPopulated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    checkForPR();
                }
            });
        } else if (evt.getPropertyName() == "exercise"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    exercise = databaseCommunicator.exercise;
                    SetUpActivity(databaseCommunicator.exercise.getName());
                    databaseCommunicator.getAllNamesProgMatch(exercise);
                }
            });
        } else if (evt.getPropertyName() == "bandColours"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setUpBandSpinner();
                }
            });
        }  else if (evt.getPropertyName() == "swapComplete"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    databaseCommunicator.getTrackedExercisesFromNameAndDate(currentExercise, currentDate);
                }
            });
        } else if (evt.getPropertyName() == "toolNames"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   setUpToolsSpinner(databaseCommunicator.toolNames);
                   // Tool spinner needs to be set up before it can be populated
                   databaseCommunicator.getLatestExercise(currentExercise);
                }
            });
        } else if (evt.getPropertyName() == "trackedExercises"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    trackedExercises = databaseCommunicator.trackedExercises;
                    updateTrackingList();
                }
            });
        } else if (evt.getPropertyName() == "trackedExercisesAdded"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    saveExercise();
                }
            });
        }
    }
}
