package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.calistheicslogger.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        //series.setBackgroundColor(Color.RED);
        series.setColor(Color.RED);

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 4),
                new DataPoint(1, 2),
                new DataPoint(2, 5),
                new DataPoint(3, 2),
                new DataPoint(4, 1)
        });
        //series.setBackgroundColor(Color.GREEN);
        series.setColor(Color.GREEN);

        graph.addSeries(series);
        graph.addSeries(series2);
        
    }
}
