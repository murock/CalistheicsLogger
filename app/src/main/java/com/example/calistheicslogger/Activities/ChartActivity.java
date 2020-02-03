package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Calendar;
import java.util.Date;

public class ChartActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);
        this.createGraph();
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

    public
}
