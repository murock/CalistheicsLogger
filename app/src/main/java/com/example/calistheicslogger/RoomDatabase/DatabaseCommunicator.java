package com.example.calistheicslogger.RoomDatabase;

import android.content.Context;
import android.util.Log;

import com.example.calistheicslogger.RoomDatabase.Entities.TrackedExercise;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public class DatabaseCommunicator {

    private PropertyChangeSupport support;
    private AppDatabase appDatabase;
    private static DatabaseCommunicator instance;

    public static List<TrackedExercise> trackedExercisesFromDate;
    public static List<TrackedExercise> exerciseHistoryList;
    public static List<TrackedExercise> personalRecordsList;

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
                List<TrackedExercise> trackedExercises = personalRecordsList;
                for(int i=0; i<personalRecordsList.size() - 1; i++)
                {
                    TrackedExercise trackedExercise = personalRecordsList.get(i);
                    TrackedExercise trackedExercise1 = personalRecordsList.get(i + 1);
                    Log.i("Alfie reps1: ",trackedExercise.getReps() + "" );
                    Log.i("Alfie reps2: ",trackedExercise1.getReps() + "" );
                    Log.i("Alfie weight1: ",trackedExercise.getWeight() + "" );
                    Log.i("Alfie weight2: ",trackedExercise1.getWeight() + "" );
                    if (trackedExercise.getBand() == trackedExercise1.getBand() &&
                        trackedExercise.getReps() <= trackedExercise1.getReps() &&
                        trackedExercise.getWeight() <= trackedExercise.getWeight())
                    {

                        trackedExercises.remove(trackedExercise);
                    }
                }
                personalRecordsList = trackedExercises;
                support.firePropertyChange("personalRecordsPopulated", null, null);
            }
        });
    }
}
