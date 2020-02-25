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
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.Band;
import com.example.calistheicslogger.RoomDatabase.Entities.Tool;
import com.example.calistheicslogger.Tools.Utilities;
import com.example.calistheicslogger.Tools.dslv.DragSortController;
import com.example.calistheicslogger.Tools.dslv.DragSortListView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

public class LockerActivity extends Activity implements PropertyChangeListener {

    DragSortListView dslv;
    ArrayAdapter<String> dslvAdapter;
    DragSortController dragSortController;
    DatabaseCommunicator databaseCommunicator;
    boolean isBandMode = true;
    int defaultColor;
    EditText newBandEditText;
    // Indicates the total number of bands
    int numBands, numTools;

    int selectedPosition = -1;
    Button addRemoveButton, colorPickerButton;

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        if (isBandMode) {
                            // Stop No band from being moved
                            if (from != databaseCommunicator.bandsList.size() - 1 &&
                                to != databaseCommunicator.bandsList.size() - 1)
                            {
                                databaseCommunicator.swapBands(from, to);
                            }
                        }else{
                            databaseCommunicator.swapTools(from, to);
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
                        // Toggle off
                        if (isBandMode)
                        {
                            newBandEditText.setText("Enter Band Name");
                        }else
                        {
                            newBandEditText.setText("Enter Tool Name");
                        }
                        newBandEditText.setFocusable(true);
                        newBandEditText.setFocusableInTouchMode(true);
                        selectedPosition = -1;
                        addRemoveButton.setText("+");
                        colorPickerButton.setText("pick color");

                        newBandEditText.setTextColor(Color.BLACK);
                        newBandEditText.setBackgroundColor(defaultColor);
                        // Stop No band from being deleted
                    }else if(position == databaseCommunicator.bandsList.size() - 1) {
                        selectedPosition = -1;
                    }else
                    {
                        // Toggle on
                        newBandEditText.setFocusable(false);
                        newBandEditText.setFocusableInTouchMode(false);
                        selectedPosition = position;
                        addRemoveButton.setText("-");
                        colorPickerButton.setText("delete");
                    }

                    dslvAdapter.notifyDataSetChanged();
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locker_activity);
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        SetUpDSLV();
        databaseCommunicator.getBands();
        defaultColor = ContextCompat.getColor(LockerActivity.this, R.color.colorPrimary);
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
        ArrayList<String> arrayListBands = new ArrayList<>();
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
        ArrayList<String> arrayListTools = new ArrayList<>();
        for(Tool tool : tools)
        {
            arrayListTools.add(tool.getName());
        }

        dslvAdapter = new ArrayAdapter<String>(this,R.layout.center_spinner_text,arrayListTools);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dslv.setAdapter(dslvAdapter);
            }
        });
    }

    public void OnToggleClick(View view){
        ToggleButton bandsButton = findViewById(R.id.bandToggleButton);
        ToggleButton toolButton = findViewById(R.id.toolToggleButton);
        TextView easyTextView = findViewById(R.id.easyTextView);
        TextView hardTextView = findViewById(R.id.hardTextView);
        if (view.getId() == R.id.toolToggleButton && bandsButton.isChecked())
        {
            // Switching to tools
            newBandEditText.setText("Enter Tool Name");
            colorPickerButton.setEnabled(false);
            bandsButton.setEnabled(true);
            bandsButton.setChecked(false);
            toolButton.setEnabled(false);
            isBandMode = false;
            databaseCommunicator.getTools();
            easyTextView.setText("Most\nAssist");
            hardTextView.setText("Least\nAssist");
        }else if(view.getId() == R.id.bandToggleButton && toolButton.isChecked()){
            // Switching to bands
            newBandEditText.setText("Enter Band Name");
            colorPickerButton.setEnabled(true);
            toolButton.setChecked(false);
            toolButton.setEnabled(true);
            bandsButton.setEnabled(false);
            isBandMode = true;
            databaseCommunicator.getBands();
            easyTextView.setText("Thick");
            hardTextView.setText("Thin");
        }
    }

    public void openColorPicker(View view){
        if (selectedPosition != -1)
        {
            if (isBandMode)
            {
                removeBandFromDatabase();
            }else{
                removeToolFromDatabase();
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

    private void addBandToDatabase(){
        String name = newBandEditText.getText().toString();
        // stops view from displaying e.g 'Green Band Band'
        name = name.replaceAll(" Band", "");
        Band newBand = new Band(name,defaultColor,this.numBands);
        this.databaseCommunicator.addBand(newBand);
    }

    private void removeBandFromDatabase(){
        this.databaseCommunicator.removeBand(selectedPosition);
    }

    private void addToolToDatabase(){
        String name = newBandEditText.getText().toString();
        Tool newTool = new Tool(name,this.numTools);
        this.databaseCommunicator.addTool(newTool);
    }

    private void removeToolFromDatabase(){
        //TODO implement tool removal in DB
    }

    public void addRemoveButtonPress(View view){
        Utilities.hideKeyboard(this);
        if (selectedPosition != -1)
        {
            if (isBandMode)
            {
                removeBandFromDatabase();
            }else{
                removeToolFromDatabase();
            }
        }else{
            if (isBandMode)
            {
                addBandToDatabase();
            }else {
                addToolToDatabase();
            }

        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "bandsPopulated"){
            Log.i("Alfie", "band populated");
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
    }

}
