package com.calisthenicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.calisthenicslogger.R;
import com.calisthenicslogger.RoomDatabase.DatabaseCommunicator;
import com.calisthenicslogger.RoomDatabase.Entities.Band;
import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;
import com.calisthenicslogger.Tools.DateFunctions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChartActivity extends Activity  implements Serializable, PropertyChangeListener {

    String currentExercise;
    DatabaseCommunicator databaseCommunicator;
    String exerciseType;
    GraphView graph;
    String yAxisTitle;
    Date firstDate = null, lastDate = null;
    boolean bandsPopulated = false, chartDataPopulated = false, exerciseTypePopulated = false, isBandMode = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);
        Intent i = getIntent();
        currentExercise = (String)i.getSerializableExtra("Exercise");
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(currentExercise);
        graph = findViewById(R.id.graph);
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
        String[] times = splitStringEvery(time,2);
        int hour = Integer.parseInt(times[0]);
        int mins = Integer.parseInt(times[1]);
        int seconds = Integer.parseInt(times[2]);
        return  (hour*3600) + (mins*60) + seconds;
    }

    public String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        } //Add the last bit
        result[lastIndex] = s.substring(j);

        return result;
    }

    private void PopulateGraph()
    {
        graph.removeAllSeries();
        if(!this.exerciseType.equals("Weight and Reps") && !this.exerciseType.equals("Reps") && !this.exerciseType.equals("Isometric"))
        {
            TextView titleTextView = findViewById(R.id.titleTextView);
            titleTextView.setText("More exercise types soon");
            return;
        }

        // TODO: Can the below two functions be merged to reduce repeating code?
        if(!isBandMode){
            SetUpToolChart();
        } else{
            SetUpBandChart();
        }

        // Set up graph legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        // Set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
        graph.getGridLabelRenderer().setVerticalAxisTitle(yAxisTitle);
        graph.getGridLabelRenderer().setPadding(50);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(firstDate.getTime());
        graph.getViewport().setMaxX(lastDate.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    private void SetUpToolChart()
    {
        ArrayList<String> toolsList = new ArrayList<>();
        List<TrackedExercise> trackedExercises = databaseCommunicator.chartRepsData;
        if(trackedExercises.size() == 0){
            return;
        }

        Map<String, LineGraphSeries<DataPoint>> seriesDictionary = new HashMap();
        yAxisTitle = "Reps";
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
            String tool = exercise.getTool();
            if (!toolsList.contains(tool))
            {
                toolsList.add(tool);
            }
            int yAxisDataPoint;
            if (this.exerciseType.equals("Isometric"))
            {
                yAxisDataPoint =  this.TimeToSeconds(exercise.getTime());
                yAxisTitle = "Hold Time";
            }
            else
            {
                yAxisDataPoint = exercise.getReps();
            }
            if(seriesDictionary.containsKey(tool))
            {
                LineGraphSeries<DataPoint> series = seriesDictionary.get(tool);
                DataPoint dataPoint = new DataPoint(DateFunctions.GetDateFromTimestamp(timestamp),yAxisDataPoint);
                series.appendData(dataPoint, true, trackedExercises.size());
            }else
            {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(DateFunctions.GetDateFromTimestamp(timestamp), yAxisDataPoint)
                });
                seriesDictionary.put(tool,series);
            }
        }

        for(String toolName : toolsList)
        {
            LineGraphSeries<DataPoint> series;
            if (seriesDictionary.containsKey(toolName))
            {
                series = seriesDictionary.get(toolName);
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                series.setColor(color);
                series.setTitle(toolName);
                if (toolName.isEmpty())
                {
                    series.setTitle("None");
                }
                graph.addSeries(series);
            }
        }
    }

    private void SetUpBandChart()
    {
        // Band Set-up
        List<Band> bands = new ArrayList<>(databaseCommunicator.bandsList);
        bands.add(new Band("No",bands.size(),Color.BLUE));
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
        if (trackedExercises.size() == 0){
            return;
        }
        Map<String, LineGraphSeries<DataPoint>> seriesDictionary = new HashMap();

        yAxisTitle = "Reps";
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
            if (band.isEmpty()){
                band = "No";
            }
            int yAxisDataPoint;
            if (this.exerciseType.equals("Isometric"))
            {
                yAxisDataPoint =  this.TimeToSeconds(exercise.getTime());
                yAxisTitle = "Hold Time";
            }
            else
            {
                yAxisDataPoint = exercise.getReps();
            }
            if(seriesDictionary.containsKey(band))
            {
                LineGraphSeries<DataPoint> series = seriesDictionary.get(band);
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
                if (bandName.isEmpty())
                {
                    series.setTitle("None");
                }
                graph.addSeries(series);
            }
        }
    }

    public void TrackButtonClick(View v)
    {
        finish();
    }

    public void ToggleButtonClick(View v)
    {
        ImageButton bandsButton = findViewById(R.id.bandsButton);
        ImageButton toolsButton = findViewById(R.id.toolsButton);
        if(v.getId() == R.id.bandsButton && !isBandMode)
        {
            isBandMode = true;
            bandsButton.setEnabled(true);
            bandsButton.setImageResource(R.drawable.faded_band_icon);
            toolsButton.setEnabled(true);
            toolsButton.setImageResource(R.drawable.tools_icon);
        }else if(v.getId() == R.id.toolsButton && isBandMode)
        {
            isBandMode = false;
            bandsButton.setEnabled(true);
            bandsButton.setImageResource(R.drawable.band_icon);
            toolsButton.setEnabled(false);
            toolsButton.setImageResource(R.drawable.faded_tools_icon);
        }
        this.RequestData();
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
