package com.calisthenicslogger.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.calisthenicslogger.Models.GroupedTrackedExercise;
import com.calisthenicslogger.Repositories.ExerciseRepository;
import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;
import com.calisthenicslogger.Tools.Utilities;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<TrackedExercise>> daysExercises;
    private LiveData<List<GroupedTrackedExercise>> transformedDaysExercises =
            Transformations.map(
                    daysExercises,
                    daysExercisesItem ->{
        List<GroupedTrackedExercise> groupedTrackedExercises = new ArrayList<>();
        for(TrackedExercise exercise : daysExercisesItem){
        GroupedTrackedExercise groupedTrackedExercise = new GroupedTrackedExercise(exercise.getName(), Utilities.getTrackedExerciseString(exercise, true));
        groupedTrackedExercises.add(groupedTrackedExercise);
    }
        return  groupedTrackedExercises;
    });

    //, transformedDaysExercises -> {return GetTransformedDaysExercises();});
    private ExerciseRepository repo;
    private  MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();

    public void init(){
        if (daysExercises != null){
            return;
        }
        repo = ExerciseRepository.getInstance();
        daysExercises = repo.getTrackedExercises();
    }

    public LiveData<List<TrackedExercise>> GetDaysExercises(){
        return daysExercises;
    }

    public LiveData<List<GroupedTrackedExercise>> GetGroupedExercises(){
        return transformedDaysExercises;
    }

    public List<GroupedTrackedExercise> GetTransformedDaysExercises(){
        List<GroupedTrackedExercise> groupedTrackedExercises = new ArrayList<>();

        for(TrackedExercise exercise : daysExercises.getValue()){
            GroupedTrackedExercise groupedTrackedExercise = new GroupedTrackedExercise(exercise.getName(), Utilities.getTrackedExerciseString(exercise, true));
            groupedTrackedExercises.add(groupedTrackedExercise);
        }
        return  groupedTrackedExercises;
    }


}
