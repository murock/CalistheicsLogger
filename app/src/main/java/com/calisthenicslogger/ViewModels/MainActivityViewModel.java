package com.calisthenicslogger.ViewModels;

import android.util.Log;

import androidx.arch.core.util.Function;
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

public class MainActivityViewModel extends ViewModel {

    private LiveData<List<GroupedTrackedExercise>> transformedDaysExercises;

    private ExerciseRepository repo;

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
                        for(TrackedExercise exercise : input){
                            Log.i("Alfie curr title is", currentTrackedExerciseTitle);
                            Log.i("Alfie exercise name is", exercise.getName());
                            if (!currentTrackedExerciseTitle.equals(exercise.getName())){
                                Log.i("Alfie", "is in If");
                                // Exercise has changed so fill in data for GroupedTrackedExercise
                                String bodyText = "";
                                for(String entry : trackedExerciseEntries){
                                    Log.i("Alfie entry is", entry);
                                    bodyText.concat(entry + "/n");
                                    Log.i("Alfie bodyText is1", bodyText);
                                }
                                Log.i("Alfie bodyText is", bodyText);
                                GroupedTrackedExercise groupedTrackedExercise = new GroupedTrackedExercise(currentTrackedExerciseTitle, bodyText);
                                groupedTrackedExercises.add(groupedTrackedExercise);

                                trackedExerciseEntries.clear();

                            }
                            // Add another entry under the same title
                            trackedExerciseEntries.add(Utilities.getTrackedExerciseString(exercise, true));
                            currentTrackedExerciseTitle = exercise.getName();

                        }
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
