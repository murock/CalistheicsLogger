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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartActivity extends Activity  implements Serializable, PropertyChangeListener {

    String currentExercise;
    DatabaseCommunicator databaseCommunicator;
    boolean bandsPopulated = false, chartDataPopulated = false;

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
        databaseCommunicator.getRepsChartData(currentExercise);
    }

    private void PopulateGraph()
    {
        List<Band> bands = databaseCommunicator.bandsList;
        Map<String, Integer> bandsMap = new HashMap<>();
        List<String> bandNameList = new ArrayList<>();
        for(Band band : bands)
        {
            bandsMap.put(band.getColour(), band.getColourCode());
            bandNameList.add(band.getColour());
        }

        List<TrackedExercise> trackedExercises = databaseCommunicator.chartRepsData;
        GraphView graph = findViewById(R.id.graph);
        Map<String, LineGraphSeries<DataPoint>> seriesDictionary = new HashMap();
        Date firstDate = null, lastDate = null;

        for(TrackedExercise exercise : trackedExercises)
        {
            String timestamp = exercise.getTimestamp();
            Date date = this.getDateFromTimestamp(timestamp);
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

            int reps = exercise.getReps();
            if(seriesDictionary.containsKey(band))
            {
                LineGraphSeries<DataPoint> series = seriesDictionary.get(band);
                DataPoint dataPoint = new DataPoint(this.getDateFromTimestamp(timestamp),reps);
                series.appendData(dataPoint, true, trackedExercises.size());
            }else
            {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(this.getDateFromTimestamp(timestamp), reps)
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

    private Date getDateFromTimestamp(String timestamp)
    {
        Calendar calendar = Calendar.getInstance();
        int day = Integer.parseInt(timestamp.substring(0,2));
        int month = Integer.parseInt(timestamp.substring(3,5));
        int year = Integer.parseInt(timestamp.substring(6));
        calendar.set(year,month,day);
        return calendar.getTime();
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
        }
        if(this.bandsPopulated && this.chartDataPopulated)
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
