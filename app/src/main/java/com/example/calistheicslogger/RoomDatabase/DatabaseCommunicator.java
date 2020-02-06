package com.example.calistheicslogger.RoomDatabase;

import android.content.Context;
import android.util.Log;

import com.example.calistheicslogger.RoomDatabase.Entities.Angle;
import com.example.calistheicslogger.RoomDatabase.Entities.Band;
import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class DatabaseCommunicator {

    private PropertyChangeSupport support;
    private AppDatabase appDatabase;
    private static DatabaseCommunicator instance;

    // TODO: send lists over the prop change event?
    public static List<TrackedExercise> trackedExercisesFromDate;
    public static List<TrackedExercise> exerciseHistoryList;
    public static List<TrackedExercise> personalRecordsList;
    public static List<TrackedExercise> chartRepsData;
    public static List<Band> bandsList;
    public static  List<Angle> anglesList;


    protected DatabaseCommunicator(Context context){
        appDatabase = AppDatabase.getInstance(context);
        support = new PropertyChangeSupport(this);
    }

    public static DatabaseCommunicator getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DatabaseCommunicator(context);
        }
        return instance;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    public void getExercisesFromDate(final String date)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                trackedExercisesFromDate = appDatabase.trackedExerciseDao().getTrackedExercisesFromDate(date);
                // Fire event
                Log.i("tracked exercise null", "blank");
                support.firePropertyChange("exerciseFromDatePopulated", null, null);
            }
        });
    }

    public void getExerciseHistory(final String exerciseName){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                exerciseHistoryList = appDatabase.trackedExerciseDao().getTrackedExercisesFromName(exerciseName);
                support.firePropertyChange("exerciseFromNamePopulated", null, null);
            }
        });
    }

    public void getPersonalRecords(final String exerciseName){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                personalRecordsList = appDatabase.trackedExerciseDao().getPersonalRecords(exerciseName);
                support.firePropertyChange("personalRecordsPopulated", null, null);
            }
        });
    }

    public void getRepsChartData(final String exerciseName){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                chartRepsData = appDatabase.trackedExerciseDao().getRepsChartData(exerciseName);
                support.firePropertyChange("chartRepsData", null, null);
            }
        });
    }

    public void getBands(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                bandsList = appDatabase.bandDao().getAll();
                support.firePropertyChange("bandsPopulated", null, null);
            }
        });
    }

    public void addBand(final Band band){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.bandDao().addBand(band);
                bandsList = appDatabase.bandDao().getAll();
                support.firePropertyChange("bandsPopulated", null, null);
            }
        });
    }

    public void removeBand(final int bandPos){
        // TODO: make this remove a band based on band rank and reassign ranks of bands
//        AppExecutors.getInstance().diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                int bandRank = bandsList.size() - bandPos - 1;
//                appDatabase.bandDao().
//            }
//        });
    }

    public void swapBands(final int bandPos1, final int bandPos2)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int band1Rank = bandsList.size() - bandPos1 - 1;
                int band2Rank = bandsList.size() - bandPos2 - 1;
                appDatabase.bandDao().swapByRank(band1Rank,band2Rank);
                bandsList = appDatabase.bandDao().getAll();
                support.firePropertyChange("bandsPopulated", null, null);
            }
        });
    }

    public void getAngles(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                anglesList = appDatabase.angleDao().getAll();
                support.firePropertyChange("anglesPopulated", null, null);
            }
        });
    }

    public void swapAngles(final int anglePos1, final int anglePos2)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int angle1Rank = anglesList.size() - anglePos1 - 1;
                int angle2Rank = anglesList.size() - anglePos2 - 1;
                Log.i("Angle", "swapping rank " + angle1Rank + " to " + angle2Rank);
                appDatabase.angleDao().swapByRank(angle1Rank,angle2Rank);
                anglesList = appDatabase.angleDao().getAll();
                support.firePropertyChange("anglesPopulated", null, null);
            }
        });
    }
}
