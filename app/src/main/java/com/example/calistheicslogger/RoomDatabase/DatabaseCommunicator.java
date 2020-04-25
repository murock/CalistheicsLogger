package com.example.calistheicslogger.RoomDatabase;

import android.content.Context;

import com.example.calistheicslogger.RoomDatabase.Entities.Exercise;
import com.example.calistheicslogger.RoomDatabase.Entities.FinalProgression;
import com.example.calistheicslogger.RoomDatabase.Entities.Tool;
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
    public static TrackedExercise latestExercise;
    public static List<Band> bandsList;
    public static List<Tool> toolsList;
    public static List<String> uniqueTimestamps;
    public static List<String> exerciseNames;
    public static List<String> progressions;
    public static  List<String> categories;

    public static String exerciseType;
    public static String toolProgressions;


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

    public void getLatestExercise(String name){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                latestExercise = appDatabase.trackedExerciseDao().getLatestTrackedExercise(name);
                support.firePropertyChange("latestExercise", null, null);
            }
        });
    }

    public void updateTrackedExercise(TrackedExercise updatedExercise,String name, String timestamp, int setNo)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.trackedExerciseDao().deleteTrackedExercise(name,timestamp,setNo);
                appDatabase.trackedExerciseDao().addTrackedExercise(updatedExercise);
                support.firePropertyChange("trackedExercisesUpdated", null, null);
            }
        });
    }

    public void deleteTrackedExercise(String name, String timestamp, int setNo){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.trackedExerciseDao().deleteTrackedExercise(name,timestamp,setNo);
                appDatabase.trackedExerciseDao().updateRemovedSet(setNo);
                support.firePropertyChange("trackedExercisesUpdated", null, null);
            }
        });
    }

    public void getExercisesFromDate(final String date)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                trackedExercisesFromDate = appDatabase.trackedExerciseDao().getTrackedExercisesFromDate(date);
                // Fire event
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
                exerciseType = appDatabase.exerciseDao().getTypeFromName(exerciseName);
                if (exerciseType.equals("Isometric"))
                {
                    personalRecordsList = appDatabase.trackedExerciseDao().getPersonalIsometricRecords(exerciseName);
                }else{
                    personalRecordsList = appDatabase.trackedExerciseDao().getPersonalRecords(exerciseName);
                }

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
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int bandRank = bandsList.size() - bandPos - 1;
                appDatabase.bandDao().removeBandByRank(bandRank);
                appDatabase.bandDao().updateRemovedBand(bandRank);
                bandsList = appDatabase.bandDao().getAll();
                support.firePropertyChange("bandsPopulated", null, null);
            }
        });
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

    public void removeTool(final int toolPos){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int toolRank = toolsList.size() - toolPos - 1;
                appDatabase.toolDao().removeToolByRank(toolRank);
                appDatabase.toolDao().updateRemovedTool(toolRank);
                toolsList = appDatabase.toolDao().getAll();
                support.firePropertyChange("toolsPopulated", null, null);
            }
        });
    }

    public void addTool(final Tool tool){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.toolDao().addTool(tool);
                toolsList = appDatabase.toolDao().getAll();
                support.firePropertyChange("toolsPopulated", null, null);
            }
        });
    }

    public void getTools(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                toolsList = appDatabase.toolDao().getAll();
                support.firePropertyChange("toolsPopulated", null, null);
            }
        });
    }

    public void swapTools(final int toolPos1, final int toolPos2)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int tool1Rank = toolsList.size() - toolPos1 - 1;
                int tool2Rank = toolsList.size() - toolPos2 - 1;
                appDatabase.toolDao().swapByRank(tool1Rank,tool2Rank);
                toolsList = appDatabase.toolDao().getAll();
                support.firePropertyChange("toolsPopulated", null, null);
            }
        });
    }

    public void updateToolByRank(final int rank, final String selectedProgression)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.toolDao().updateToolByRank(rank, selectedProgression);
            }
        });
    }

    public void getProgressionsByRank(final int rank)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                toolProgressions = appDatabase.toolDao().getProgressionsByRank(rank);
                support.firePropertyChange("toolProgressions", null, null);
            }
        });
    }

    public void getUniqueTimestamps()
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                uniqueTimestamps = appDatabase.trackedExerciseDao().getAllUniqueTimestamps();
                support.firePropertyChange("uniqueTimestampsPopulated", null,null);
            }
        });
    }

    public void getExerciseTypeFromName(final String exerciseName)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                exerciseType = appDatabase.exerciseDao().getTypeFromName(exerciseName);
                support.firePropertyChange("exerciseTypePopulated", null, null);
            }
        });
    }

    public void updateExercise(Exercise updatedExercise)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.exerciseDao().updateExercise(updatedExercise);
                support.firePropertyChange("exerciseUpdated", null, null);
            }
        });
    }

    public void getNamesFromProgression(String progression)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                exerciseNames = appDatabase.exerciseDao().getNamesFromProgression(progression);
                support.firePropertyChange("exerciseNames", null, null);
            }
        });
    }

    public void getNamesFromCategory(String category)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                String categoryWithPerc = "%" + category + "%";
                exerciseNames = appDatabase.exerciseDao().getNamesFromCategory(categoryWithPerc);
                support.firePropertyChange("exerciseNames", null, null);
            }
        });
    }

    public void getAllNames()
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                exerciseNames = appDatabase.exerciseDao().getAllNames();
                support.firePropertyChange("exerciseNames", null, null);
            }
        });
    }

    public void removeExercise(String exercise)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.exerciseDao().delete(appDatabase.exerciseDao().getExerciseFromName(exercise));
            }
        });
    }

    public void getAllProgressions()
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                progressions = appDatabase.finalProgressionDao().getAllNames();
                support.firePropertyChange("progressions",null,null);
            }
        });
    }

    public void addProgression(final FinalProgression progression){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.finalProgressionDao().addFinalProgression(progression);
                progressions = appDatabase.finalProgressionDao().getAllNames();
                support.firePropertyChange("progressions",null,null);
            }
        });
    }

    public void removeProgression(final int progPos){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int progRank = progressions.size() - progPos - 1;
                appDatabase.finalProgressionDao().removeProgressionByRank(progRank);
                appDatabase.finalProgressionDao().updateRemovedProgression(progRank);
                progressions = appDatabase.finalProgressionDao().getAllNames();
                support.firePropertyChange("progressions",null,null);
            }
        });
    }

    public void swapProgressions(final int progPos1, final int progPos2)
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int prog1Rank = progressions.size() - progPos1 - 1;
                int prog2Rank = progressions.size() - progPos2 - 1;
                appDatabase.finalProgressionDao().swapByRank(prog1Rank,prog2Rank);
                progressions = appDatabase.finalProgressionDao().getAllNames();
                support.firePropertyChange("progressions",null,null);
            }
        });
    }

    public void getAllCategories()
    {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                categories = appDatabase.categoryDao().getAllNames();
                support.firePropertyChange("categories",null,null);
            }
        });
    }

}
