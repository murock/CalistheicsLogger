package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

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
        Map<String, Integer> bandsmap = new HashMap<>();
        List<String> bandNameList = new ArrayList<>();
        for(Band band : bands)
        {
            bandsmap.put(band.getColour(), band.getColourCode());
            bandNameList.add(band.getColour());
        }

        List<TrackedExercise> trackedExercises = databaseCommunicator.chartRepsData;
        GraphView graph = (GraphView) findViewById(R.id.graph);
      //  List<LineGraphSeries<DataPoint>> seriesList = new ArrayList<LineGraphSeries<DataPoint>>();
        Map<String, LineGraphSeries<DataPoint>> seriesDictionary = new HashMap();
        Calendar calendar = Calendar.getInstance();
        for(TrackedExercise exercise : trackedExercises)
        {

            String timestamp = exercise.getTimestamp();
            String band = exercise.getBand();
            Log.i("Alfie timestamp: ", timestamp);
            Log.i("Alfie band: ", exercise.getBand());

            int day = Integer.parseInt(timestamp.substring(0,1));
            int month = Integer.parseInt(timestamp.substring(3,4));
            int year = Integer.parseInt(timestamp.substring(6));
            calendar.set(year,month,day);
            int reps = exercise.getReps();
            if(seriesDictionary.containsKey(band))
            {
                LineGraphSeries<DataPoint> series = seriesDictionary.get(band);
                DataPoint dataPoint = new DataPoint(calendar.getTime(),reps);
                series.appendData(dataPoint, true, trackedExercises.size());
            }else
            {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(calendar.getTime(), reps)
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
            }

        }
    }

    private void createGraph()
    {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020,1,1);
        Date d1 = calendar.getTime();
        calendar.set(2020,1,4);
        Date d2 = calendar.getTime();
        calendar.set(2020,1,10);
        Date d3 = calendar.getTime();
        calendar.set(2020,1,12);
        Date d4 = calendar.getTime();
        calendar.set(2020,1,15);
        Date d5 = calendar.getTime();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d1, 1),
                new DataPoint(d2, 2),
                new DataPoint(d3, 4),
                new DataPoint(d4, 5),
                new DataPoint(d5, 9)
        });
        series.setColor(Color.RED);
        series.setTitle("tucked flag");

        calendar.set(2020,1,17);
        Date d6 = calendar.getTime();
        calendar.set(2020,1,20);
        Date d7 = calendar.getTime();
        calendar.set(2020,1,27);
        Date d8 = calendar.getTime();
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(d4, 1),
                new DataPoint(d5, 1),
                new DataPoint(d6, 2),
                new DataPoint(d7, 2),
                new DataPoint(d8, 4)
        });
        series2.setColor(Color.GREEN);
        series2.setTitle("full flag");

        graph.addSeries(series);
        graph.addSeries(series2);
        // Set up graph legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        // Set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(d1.getTime());
        graph.getViewport().setMaxX(d8.getTime());
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
        }
        if(this.bandsPopulated && this.chartDataPopulated)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    PopulateGraph();
                }
            });
        }
    }
}
