package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.Band;
import com.example.calistheicslogger.RoomDatabase.Entities.FinalProgression;
import com.example.calistheicslogger.RoomDatabase.Entities.Tool;
import com.example.calistheicslogger.Tools.MultiSelectSpinner.Item;
import com.example.calistheicslogger.Tools.MultiSelectionButton;
import com.example.calistheicslogger.Tools.Utilities;
import com.example.calistheicslogger.Tools.dslv.DragSortController;
import com.example.calistheicslogger.Tools.dslv.DragSortListView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class LockerActivity extends Activity implements PropertyChangeListener {

    DragSortListView dslv;
    ArrayAdapter<String> dslvAdapter;
    DragSortController dragSortController;
    DatabaseCommunicator databaseCommunicator;
    int defaultColor;
    EditText newBandEditText;
    // Indicates the total number of bands
    int numBands, numTools, numProgressions;

    int selectedPosition = -1;
    ImageButton addRemoveButton, colorPickerButton;
    MultiSelectionButton multiSelectionProgressionButton;
    ArrayList<String> arrayListTools;
    ArrayList<String> arrayListBands;
    ArrayList<String> arrayListProgressions;

    enum Mode{
        BAND,
        TOOL,
        PROGRESSION
    }

    Mode mode = Mode.BAND;

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        if (mode == Mode.BAND) {
                            databaseCommunicator.swapBands(from, to);
                        }else if (mode == Mode.TOOL){
                            databaseCommunicator.swapTools(from, to);
                        }else if(mode == Mode.PROGRESSION){
                            databaseCommunicator.swapProgressions(from, to);
                        }
                    }
                }
            };


    private DragSortListView.OnItemClickListener onClick =
            new DragSortListView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView,View v, int position, long arg)
                {
                    if (position == selectedPosition)
                    {
                        newBandEditText.setText("");
                        if (mode == Mode.BAND)
                        {
                            newBandEditText.setHint("Enter Band Name");
                        }else if(mode == Mode.TOOL)
                        {
                            newBandEditText.setHint("Enter Tool Name");
                        }else if(mode == Mode.PROGRESSION)
                        {
                            newBandEditText.setHint("Enter Progression Name");
                        }
                        newBandEditText.setFocusable(true);
                        newBandEditText.setFocusableInTouchMode(true);
                        selectedPosition = -1;
                        addRemoveButton.setImageResource(R.drawable.add_icon);
                        colorPickerButton.setEnabled(true);

                        newBandEditText.setTextColor(Color.BLACK);
                        newBandEditText.setBackgroundColor(defaultColor);
                    }else
                    {
                        // Toggle on
                        newBandEditText.setFocusable(false);
                        newBandEditText.setFocusableInTouchMode(false);
                        selectedPosition = position;
                        addRemoveButton.setImageResource(R.drawable.remove_icon);
                        colorPickerButton.setEnabled(false);
                        if (mode == Mode.TOOL){
                            databaseCommunicator.getProgressionsByRank(position);
                        }
                    }

                    dslvAdapter.notifyDataSetChanged();
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locker_activity);
        multiSelectionProgressionButton = findViewById(R.id.progressionsMultiButton);
        multiSelectionProgressionButton.addPropertyChangeListener(this);
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        SetUpDSLV();
        databaseCommunicator.getBands();
        databaseCommunicator.getAllProgressionsForButton();
        defaultColor = Color.WHITE;
        newBandEditText = findViewById(R.id.newBandEditText);
        newBandEditText.setBackgroundColor(defaultColor);
        addRemoveButton = findViewById(R.id.addButton);
        colorPickerButton = findViewById(R.id.colorButton);
    }

    public DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_LONG_PRESS);
        controller.setRemoveMode(DragSortController.FLING_REMOVE);
        return controller;
    }


    private void SetUpDSLV()
    {
        dslv = findViewById(R.id.lockerDSLV);
        dslv.setDropListener(onDrop);
        dragSortController = buildController(dslv);
        dslv.setFloatViewManager(dragSortController);
        dslv.setOnTouchListener(dragSortController);
        dslv.setOnItemClickListener(onClick);
    }

    private void UpdateDSLVForBands(){
        final List<Band> bands = databaseCommunicator.bandsList;
        this.numBands = bands.size();
        arrayListBands = new ArrayList<>();
        for(Band band : bands)
        {
            arrayListBands.add(band.getColour() + " Band");
        }

        dslvAdapter = new ArrayAdapter<String>(this,R.layout.center_spinner_text,arrayListBands){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                final View view = super.getView(position,convertView,parent);
                int color = bands.get(position).getColourCode();
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;
                view.setBackgroundColor(color);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                // Select text based on background colour
                int textColor;
                if((r*0.299 + g*0.587 + b*0.114) > 186){
                    textColor = Color.BLACK;
                }else{
                    textColor = Color.WHITE;
                }
                textView.setTextColor(textColor);

                if (position == selectedPosition)
                {
                    ColorDrawable viewColor = (ColorDrawable)view.getBackground();
                    int backgroundColor = viewColor.getColor();
                    backgroundColor = ColorUtils.blendARGB(backgroundColor, Color.WHITE, 0.4f);
                    newBandEditText.setText(bands.get(position).getColour() + " Band");
                    newBandEditText.setBackgroundColor(viewColor.getColor());
                    newBandEditText.setTextColor(textColor);
                    view.setBackgroundColor(backgroundColor);
                }
                return view;
            }
        };
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dslv.setAdapter(dslvAdapter);
            }
        });
    }

    private void UpdateDSLVForTools(){
        final List<Tool> tools = databaseCommunicator.toolsList;
        this.numTools = tools.size();
        arrayListTools = new ArrayList<>();
        for(Tool tool : tools)
        {
            arrayListTools.add(tool.getName());
        }

        dslvAdapter = new ArrayAdapter<String>(this,R.layout.center_spinner_text,arrayListTools){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                final View view = super.getView(position,convertView,parent);

                if (position == selectedPosition)
                {
                    int backgroundColor = ColorUtils.blendARGB(Color.WHITE, Color.GRAY, 0.4f);
                    newBandEditText.setText(tools.get(position).getName());
                    newBandEditText.setBackgroundColor(Color.WHITE);
                    view.setBackgroundColor(backgroundColor);
                }else{
                    view.setBackgroundColor(Color.WHITE);
                }

                return  view;
            }
        };
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dslv.setAdapter(dslvAdapter);
            }
        });
    }

    private void UpdateMultiButton(String progressions){
        if (progressions == null){
            return;
        }
        // remove first and last comma
        progressions = progressions.substring(1,progressions.length() - 1);
        String[] progressionsArray = progressions.split(",");
        List<String> progressionsList = Arrays.asList(progressionsArray);
        multiSelectionProgressionButton.setUpSelection(progressionsList);
    }

    private void PopulateMulitSelectionButton(){
        ArrayList<Item> items = new ArrayList<>();
        items.add(new Item("All", true));
        for (String progression : databaseCommunicator.progressions){
            Log.i("Alfie prog is", progression);
            items.add(new Item(progression,false));
        }
        multiSelectionProgressionButton.setItems(items);
    }

    private void UpdateDSLVForProgressions(){
        final List<String> progressions = databaseCommunicator.progressions;
        this.numProgressions = progressions.size();

        arrayListProgressions = new ArrayList<>(progressions);
        dslvAdapter = new ArrayAdapter<String>(this,R.layout.center_spinner_text,arrayListProgressions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the current item from ListView
                final View view = super.getView(position,convertView,parent);

                if (position == selectedPosition)
                {
                    int backgroundColor = ColorUtils.blendARGB(Color.WHITE, Color.GRAY, 0.4f);
                    newBandEditText.setText(progressions.get(position));
                    newBandEditText.setBackgroundColor(Color.WHITE);
                    view.setBackgroundColor(backgroundColor);
                }else{
                    view.setBackgroundColor(Color.WHITE);
                }

                return  view;
            }
        };
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dslv.setAdapter(dslvAdapter);
            }
        });
    }

    public void OnToggleClick(View view){
        this.selectedPosition = -1;
        newBandEditText.setFocusable(true);
        newBandEditText.setFocusableInTouchMode(true);
        newBandEditText.setText("");

        TextView easyTextView = findViewById(R.id.easyTextView);
        TextView hardTextView = findViewById(R.id.hardTextView);
        if (view.getId() == R.id.toolToggleButton && this.mode != Mode.TOOL)
        {
            // Switching to tools
            this.mode = Mode.TOOL;

            newBandEditText.setHint("Enter Tool Name");
            newBandEditText.setBackgroundColor(Color.WHITE);

            colorPickerButton.setEnabled(false);

            this.updateButtons(true,false,false);

            databaseCommunicator.getTools();

            easyTextView.setText("Most\nAssist");
            hardTextView.setText("Least\nAssist");
        }else if(view.getId() == R.id.bandToggleButton && this.mode != Mode.BAND){
            // Switching to bands
            this.mode = Mode.BAND;

            newBandEditText.setHint("Enter Band Name");
            newBandEditText.setBackgroundColor(defaultColor);

            colorPickerButton.setEnabled(true);

            this.updateButtons(false,true,false);

            databaseCommunicator.getBands();

            easyTextView.setText("Thick");
            hardTextView.setText("Thin");
        } else if(this.mode != Mode.PROGRESSION)
        {
            // Switching to progressions
            this.mode = Mode.PROGRESSION;

            newBandEditText.setHint("Enter Progression Name");
            newBandEditText.setBackgroundColor(Color.WHITE);

            colorPickerButton.setEnabled(false);

            this.updateButtons(false, false, true);

            //TODO: add db call here
            databaseCommunicator.getAllProgressions();

            easyTextView.setText("");
            hardTextView.setText("");
        }
    }

    private void updateButtons(boolean toolsEnabled, boolean bandsEnabled,boolean progsEnabled){
        ImageButton bandsButton = findViewById(R.id.bandToggleButton);
        ImageButton toolButton = findViewById(R.id.toolToggleButton);
        ImageButton progressionButton = findViewById(R.id.progressionsButton);
        ImageButton multiProgressionButton = findViewById(R.id.progressionsMultiButton);
        ImageButton colorPickerButton = findViewById(R.id.colorButton);
        bandsButton.setEnabled(!bandsEnabled);
        toolButton.setEnabled(!toolsEnabled);
        progressionButton.setEnabled(!progsEnabled);

        if (toolsEnabled){
            toolButton.setImageResource(R.drawable.faded_tools_icon);
            multiProgressionButton.setVisibility(View.VISIBLE);
        }else{
            toolButton.setImageResource(R.drawable.tools_icon);
            multiProgressionButton.setVisibility(View.GONE);
        }

        if (bandsEnabled){
            bandsButton.setImageResource(R.drawable.faded_band_icon);
            colorPickerButton.setVisibility(View.VISIBLE);
        }else{
            bandsButton.setImageResource(R.drawable.band_icon);
            colorPickerButton.setVisibility(View.GONE);
        }

        if (progsEnabled){
            progressionButton.setImageResource(R.drawable.faded_progressions_icon);
        }else {
            progressionButton.setImageResource(R.drawable.progressions_icon);
        }
    }

    public void openColorPicker(View view){
        if (selectedPosition != -1)
        {
            if (this.mode == Mode.BAND)
            {
                removeBandFromDatabase();
            }else if (this.mode == Mode.TOOL)
            {
                removeToolFromDatabase();
            } else if (this.mode == Mode.PROGRESSION)
            {

            }

        }else {
            AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {

                }

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    defaultColor = color;
                    newBandEditText.setBackgroundColor((defaultColor));
                }
            });
            colorPicker.show();
        }
    }

    private void addProgressionToDatabase(){
        String name = newBandEditText.getText().toString();
        if (!arrayListBands.contains(name) && name.length() > 0) {
            FinalProgression newProg = new FinalProgression(name, this.numProgressions);
            this.databaseCommunicator.addProgression(newProg);
        }
        newBandEditText.setText("");
    }

    private void removeProgressionFromDatabase(){
        this.databaseCommunicator.removeProgression(selectedPosition);
    }

    private void addBandToDatabase(){

        String name = newBandEditText.getText().toString();
        if (!arrayListBands.contains(name) && name.length() > 0) {
            // stops view from displaying e.g 'Green Band Band'
            name = name.replaceAll(" Band", "");
            Band newBand = new Band(name, defaultColor, this.numBands);
            this.databaseCommunicator.addBand(newBand);
        }
        newBandEditText.setText("");
    }

    private void removeBandFromDatabase(){
        this.databaseCommunicator.removeBand(selectedPosition);
    }

    private void addToolToDatabase(){
        String name = newBandEditText.getText().toString();
        if (!arrayListTools.contains(name) && name.length() > 0)
        {
            Tool newTool = new Tool(name,getProgressionStringFromButton(),this.numTools);
            this.databaseCommunicator.addTool(newTool);
        }
        newBandEditText.setText("");
        // Reset multiSelect item selection
        multiSelectionProgressionButton.Reset();
    }

    private String getProgressionStringFromButton(){
        String progressions = ",";
        for(Item item : multiSelectionProgressionButton.getSelectedItems()){
            progressions += item.getName() + ",";
        }
        return progressions;
    }

    private void removeToolFromDatabase(){
        this.databaseCommunicator.removeTool(selectedPosition);
    }

    public void addRemoveButtonPress(View view){
        Utilities.hideKeyboard(this);
        if (selectedPosition != -1)
        {
            if (this.mode == Mode.BAND)
            {
                removeBandFromDatabase();
            }else if (this.mode == Mode.TOOL)
            {
                removeToolFromDatabase();
            } else if (this.mode == Mode.PROGRESSION)
            {
                removeProgressionFromDatabase();
            }
        }else{
            if (this.mode == Mode.BAND)
            {
                addBandToDatabase();
            } else if (this.mode == Mode.TOOL){
                addToolToDatabase();
            } else if (this.mode == Mode.PROGRESSION)
            {
                addProgressionToDatabase();
            }

        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "bandsPopulated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UpdateDSLVForBands();
                }
            });
        }
        if(evt.getPropertyName() == "toolsPopulated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UpdateDSLVForTools();
                }
            });
        }
        if(evt.getPropertyName() == "progressions"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UpdateDSLVForProgressions();
                }
            });
        }
        if(evt.getPropertyName() == "MultiSelectionButtonOKClick"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    databaseCommunicator.updateToolByRank(selectedPosition, getProgressionStringFromButton());
                }
            });
        }
        if(evt.getPropertyName() == "toolProgressions"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UpdateMultiButton(databaseCommunicator.toolProgressions);
                }
            });
        }
        if(evt.getPropertyName() == "progressionsForButton"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PopulateMulitSelectionButton();
                }
            });
        }
    }

}
