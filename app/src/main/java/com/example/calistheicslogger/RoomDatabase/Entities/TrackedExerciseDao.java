package com.example.calistheicslogger.RoomDatabase.Entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TrackedExerciseDao {

    @Query("SELECT * FROM tracked_exercises")
    List<TrackedExercise> getAll();

    @Query("SELECT exercise_name FROM tracked_exercises")
    List<String> getAllNames();

    @Query("SELECT * FROM tracked_exercises WHERE exercise_name =:name AND timestamp =:timestamp")
    List<TrackedExercise> getTrackedExercisesFromNameAndDate(String name, String timestamp);

    @Insert
    void addTrackedExercise(TrackedExercise trackedExercise);

    @Insert
    void addMultipleTrackedExercises(TrackedExercise[] trackedExercises);

    @Update
    void updateTrackedExercise(TrackedExercise trackedExercise);

    @Delete
    void delete(TrackedExercise trackedExercise);
}
