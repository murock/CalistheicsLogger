package com.calisthenicslogger.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.calisthenicslogger.Models.GroupedTrackedExercise;
import com.calisthenicslogger.Repositories.ExerciseRepository;
import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<TrackedExercise>> daysExercises;
    private LiveData<List<GroupedTrackedExercise>> transformedDaysExercises = Transformations.map(daysExercises, GetGroupedExercises())
    private ExerciseRepository repo;
    private  MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();

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

    private List<GroupedTrackedExercise> GetGroupedExercises(){

    }


}
