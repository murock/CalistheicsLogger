package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.Angle;
import com.example.calistheicslogger.RoomDatabase.Entities.Band;
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
    TextView newBandTextView;

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if (from != to) {
                        if (isBandMode) {
                            databaseCommunicator.swapBands(from, to);
                        }else{
                            Log.i("Angle", "swapping " + from + " to " + to);
                            databaseCommunicator.swapAngles(from, to);
                        }
                    }
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
        newBandTextView = findViewById(R.id.newBandEditText);
        newBandTextView.setBackgroundColor(defaultColor);
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
    }

    private void UpdateDSLVForBands(){
        final List<Band> bands = databaseCommunicator.bandsList;
        ArrayList<String> arrayListBands = new ArrayList<>();
        for(Band band : bands)
        {
            arrayListBands.add(band.getColour() + " Band");
        }

        dslvAdapter = new ArrayAdapter<String>(this,R.layout.center_spinner_text,arrayListBands){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);
                String colourCode = bands.get(position).getColourCode();
                int color = Color.parseColor(colourCode);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;
                view.setBackgroundColor(Color.parseColor(colourCode));
                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                // Select text based on background colour
                if((r*0.299 + g*0.587 + b*0.114) > 186){
                    textView.setTextColor(Color.BLACK);
                }else{
                    textView.setTextColor(Color.WHITE);
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

    private void UpdateDSLVForAngles(){
        final List<Angle> angles = databaseCommunicator.anglesList;
        ArrayList<String> arrayListAngles = new ArrayList<>();
        for(Angle angle : angles)
        {
            arrayListAngles.add(angle.getAngle());
        }

        dslvAdapter = new ArrayAdapter<String>(this,R.layout.center_spinner_text,arrayListAngles);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dslv.setAdapter(dslvAdapter);
            }
        });
    }

    public void OnToggleClick(View view){
        ToggleButton bandsButton = findViewById(R.id.bandToggleButton);
        ToggleButton angleButton = findViewById(R.id.angleToggleButton);
        TextView easyTextView = findViewById(R.id.easyTextView);
        TextView hardTextView = findViewById(R.id.hardTextView);
        if (view.getId() == R.id.angleToggleButton && bandsButton.isChecked())
        {
            // Switching to angles
            bandsButton.setEnabled(true);
            bandsButton.setChecked(false);
            angleButton.setEnabled(false);
            isBandMode = false;
            databaseCommunicator.getAngles();
            easyTextView.setText("Lowest");
            hardTextView.setText("Highest");
        }else if(view.getId() == R.id.bandToggleButton && angleButton.isChecked()){
            // Switching to bands
            angleButton.setChecked(false);
            angleButton.setEnabled(true);
            bandsButton.setEnabled(false);
            isBandMode = true;
            databaseCommunicator.getBands();
            easyTextView.setText("Thin");
            hardTextView.setText("Thick");
        }
    }

    public void openColorPicker(View view){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;
                newBandTextView.setBackgroundColor((defaultColor));
            }
        });
        colorPicker.show();
    }

    public void addBandToDatabase(View view){
        String hexColour = Integer.toHexString(defaultColor);
        String name = newBandTextView.getText().toString();
        name = name.replaceAll(" Band", "");

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
        if(evt.getPropertyName() == "anglesPopulated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("Angle", "angles populated");
                    UpdateDSLVForAngles();
                }
            });
        }
    }

}
