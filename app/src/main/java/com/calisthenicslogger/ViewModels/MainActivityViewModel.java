package com.calisthenicslogger.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.calisthenicslogger.Repositories.ExerciseRepository;
import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<TrackedExercise>> daysExercises;
    private ExerciseRepository repo;

    public void init(){
        if (daysExercises != null){
            return;
        }
        repo = ExerciseRepository.getInstance();
        daysExercises = repo.getTrackedExercises();
    }

    public LiveData<List<TrackedExercise>> getDaysExercises(){
        return daysExercises;
    }
}
