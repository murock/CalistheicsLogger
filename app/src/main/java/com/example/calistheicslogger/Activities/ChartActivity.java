package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;
import com.example.calistheicslogger.RoomDatabase.Entities.Band;
import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;
import com.example.calistheicslogger.Tools.DateFunctions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChartActivity extends Activity  implements Serializable, PropertyChangeListener {

    String currentExercise;
    DatabaseCommunicator databaseCommunicator;
    String exerciseType;
    boolean bandsPopulated = false, chartDataPopulated = false, exerciseTypePopulated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);
        Intent i = getIntent();
        currentExercise = (String)i.getSerializableExtra("Exercise");
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(currentExercise);
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        this.RequestData();
     //   this.createGraph();
    }

    private void RequestData()
    {
        databaseCommunicator.getBands();
        databaseCommunicator.getExerciseTypeFromName(currentExercise);
        databaseCommunicator.getRepsChartData(currentExercise);
    }

    private int TimeToSeconds(String time)
    {
        String[] times = time.split(":");
        int hour = Integer.parseInt(times[0]);
        int mins = Integer.parseInt(times[1]);
        int seconds = Integer.parseInt(times[2]);
        return  (hour*3600) + (mins*60) + seconds;
    }


    private void PopulateGraph()
    {
        if(!this.exerciseType.equals("Weight and Reps") && !this.exerciseType.equals("Reps") && !this.exerciseType.equals("Isometric"))
        {
            TextView titleTextView = findViewById(R.id.titleTextView);
            titleTextView.setText("Only Weight and Reps charts supported more soon");
            return;
        }

        List<Band> bands = databaseCommunicator.bandsList;
        Map<String, Integer> bandsMap = new HashMap<>();
        List<String> bandNameList = new ArrayList<>();
        for(Band band : bands)
        {
            if (band.getColour().equals("No"))
            {
                // TODO: make band colours editable including No set the base color of blue there
                bandsMap.put(band.getColour(), Color.BLUE);
                bandNameList.add(band.getColour());
            }else {
                bandsMap.put(band.getColour(), band.getColourCode());
                bandNameList.add(band.getColour());
            }
        }

        List<TrackedExercise> trackedExercises = databaseCommunicator.chartRepsData;
        GraphView graph = findViewById(R.id.graph);
        Map<String, LineGraphSeries<DataPoint>> seriesDictionary = new HashMap();
        Date firstDate = null, lastDate = null;

        for(TrackedExercise exercise : trackedExercises)
        {
            String timestamp = exercise.getTimestamp();
            Date date = DateFunctions.GetDateFromTimestamp(timestamp);
            if (firstDate == null)
            {
                firstDate = date;
                lastDate = date;
            }
            if (date.compareTo(firstDate) < 0)
            {
                firstDate = date;
            }
            if(date.compareTo(lastDate) > 0)
            {
                lastDate = date;
            }
            String band = exercise.getBand();

            int yAxisDataPoint;
            if (this.exerciseType.equals("Isometric"))
            {
                yAxisDataPoint =  this.TimeToSeconds(exercise.getTime());
            }
            else
            {
                yAxisDataPoint = exercise.getReps();
            }
            if(seriesDictionary.containsKey(band))
            {
                LineGraphSeries<DataPoint> series = seriesDictionary.get(band);
                Log.i("Alfie x value is ", timestamp + "");
                DataPoint dataPoint = new DataPoint(DateFunctions.GetDateFromTimestamp(timestamp),yAxisDataPoint);
                series.appendData(dataPoint, true, trackedExercises.size());
            }else
            {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(DateFunctions.GetDateFromTimestamp(timestamp), yAxisDataPoint)
                });
                seriesDictionary.put(band,series);
            }
        }

        for(String bandName : bandNameList)
        {
            LineGraphSeries<DataPoint> series;
            if (seriesDictionary.containsKey(bandName))
            {
                series = seriesDictionary.get(bandName);
                series.setColor(bandsMap.get(bandName));
                series.setTitle(bandName);
                graph.addSeries(series);
            }
        }

        // Set up graph legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        // Set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(firstDate.getTime());
        graph.getViewport().setMaxX(lastDate.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }


    public void TrackButtonClick(View v)
    {
        finish();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "chartRepsData"){
            this.chartDataPopulated = true;
        }else if(evt.getPropertyName() == "bandsPopulated")
        {
            this.bandsPopulated = true;
        } else if(evt.getPropertyName() == "exerciseTypePopulated")
        {
            this.exerciseType = databaseCommunicator.exerciseType;
            Log.i("Alfie Type",this.exerciseType );
            this.exerciseTypePopulated = true;
        }
        if(this.bandsPopulated && this.chartDataPopulated && this.exerciseTypePopulated)
        {
            this.bandsPopulated = false;
            this.chartDataPopulated = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PopulateGraph();
                }
            });
        }
    }
}
