package com.calisthenicslogger.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<TrackedExercise>> daysExercises;

    public LiveData<List<TrackedExercise>> getDaysExercises(){
        return daysExercises;
    }
}
