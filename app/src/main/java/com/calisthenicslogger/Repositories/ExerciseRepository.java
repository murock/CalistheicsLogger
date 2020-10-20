package com.calisthenicslogger.Repositories;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.calisthenicslogger.Models.GroupedTrackedExercise;
import com.calisthenicslogger.RoomDatabase.AppDatabase;
import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseRepository {
    private static  ExerciseRepository instance;
    private ArrayList<TrackedExercise> dataSet = new ArrayList<>();
    private AppDatabase appDatabase;

    public ExerciseRepository(Application application){
        appDatabase = AppDatabase.getInstance(application);
    }

    // Empty constructor LEGACY TODO: Remove
    private ExerciseRepository(){

    }

    public static ExerciseRepository getInstance(){
        if (instance == null){
            instance = new ExerciseRepository();
        }
        return instance;
    }

    public MutableLiveData<List<TrackedExercise>> getTrackedExercises(){
        setTrackedExercises();
        MutableLiveData<List<TrackedExercise>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    private void setTrackedExercises(){
        String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());


        // add a bunch of trackedexercises to dataSet
        //String name, String timestamp, int setNumber, int reps, double weight, String time, String band, int distance, String tempo, String tool, int rest, String cluster){

        dataSet.add(new TrackedExercise("Bench","test",1,5,1f,"000000","band",1,"tempo","tool",1,"cluster"));
        dataSet.add(new TrackedExercise("Bench","test",2,5,1f,"000000","band",1,"tempo","tool",1,"cluster"));

        dataSet.add(new TrackedExercise("Deadlift","test",1,5,10f,"000000","band",1,"tempo","tool",1,"cluster"));
        dataSet.add(new TrackedExercise("Deadlift","test",2,5,10f,"000000","band",1,"tempo","tool",1,"cluster"));

//        dataSet.add(new GroupedTrackedExercise("Bench", "1 5 reps 10kg\n2 5 reps 12.5kg"));
//        dataSet.add(new GroupedTrackedExercise("Deadlift", "1 5 reps 100kg\n2 5 reps 120.5kg"));
    }
}
