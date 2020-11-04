package com.calisthenicslogger.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.calisthenicslogger.Models.GroupedTrackedExercise;
import com.calisthenicslogger.Repositories.ExerciseRepository;
import com.calisthenicslogger.RoomDatabase.Entities.TrackedExercise;
import com.calisthenicslogger.Tools.Utilities;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    private LiveData<List<GroupedTrackedExercise>> transformedDaysExercises;

    private ExerciseRepository repo;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        repo = new ExerciseRepository(application);
        SetUpLiveData();
    }

    // Legacy? TODO: Remove? use constructor instead?
    public void init(){
        if (transformedDaysExercises != null){
            return;
        }
        repo = ExerciseRepository.getInstance();
        SetUpLiveData();
    }

    private void SetUpLiveData(){
        LiveData<List<TrackedExercise>> trackedExercises =
                repo.getTrackedExercises();

        transformedDaysExercises = Transformations.map(trackedExercises,
                new Function<List<TrackedExercise>, List<GroupedTrackedExercise>>() {
                    @Override
                    public List<GroupedTrackedExercise> apply(List<TrackedExercise> input) {
                        List<GroupedTrackedExercise> groupedTrackedExercises = new ArrayList<>();
                        List<String> trackedExerciseEntries = new ArrayList<>();
                        String currentTrackedExerciseTitle = "";
                        String bodyText = "";
                        for(TrackedExercise exercise : input){
                            if (!currentTrackedExerciseTitle.equals(exercise.getName())){
                                // Exercise has changed so fill in data for GroupedTrackedExercise
                                bodyText = "";
                                for(String entry : trackedExerciseEntries){
                                    bodyText = bodyText + entry + "\n";
                                }
                                GroupedTrackedExercise groupedTrackedExercise = new GroupedTrackedExercise(currentTrackedExerciseTitle, bodyText);
                                groupedTrackedExercises.add(groupedTrackedExercise);
                                trackedExerciseEntries.clear();
                            }
                            // Add another entry under the same title
                            trackedExerciseEntries.add(Utilities.getTrackedExerciseString(exercise, true));
                            currentTrackedExerciseTitle = exercise.getName();
                        }
                        //Add the final set of exercises
                        GroupedTrackedExercise groupedTrackedExercise = new GroupedTrackedExercise(currentTrackedExerciseTitle, bodyText);
                        groupedTrackedExercises.add(groupedTrackedExercise);
                        return  groupedTrackedExercises;
                    }
                });
    }

    //public LiveData<List<TrackedExercise>> GetDaysExercises(){
//        return daysExercises;
//    }

    public LiveData<List<GroupedTrackedExercise>> GetTransformedDaysExercises(){
        return transformedDaysExercises;
    }

//    public List<GroupedTrackedExercise> GetTransformedDaysExercises1(){
//        List<GroupedTrackedExercise> groupedTrackedExercises = new ArrayList<>();
//
//        for(TrackedExercise exercise : daysExercises.getValue()){
//            GroupedTrackedExercise groupedTrackedExercise = new GroupedTrackedExercise(exercise.getName(), Utilities.getTrackedExerciseString(exercise, true));
//            groupedTrackedExercises.add(groupedTrackedExercise);
//        }
//        return  groupedTrackedExercises;
//    }

//    public LiveData<List<GroupedTrackedExercise>> GetTransformedDaysExercises(){
//        //LiveData<List<TrackedExercise>> trackedExercises = repo.getTrackedExercises();
//
//        transformedDaysExercises =  Transformations.map(daysExercises,
//                newData -> GetTransformedDaysExercises1());
//        return  transformedDaysExercises;
//    }


}
