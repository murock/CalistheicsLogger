package com.example.calistheicslogger.RoomDatabase.Entities;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExerciseDao {

    @Query("SELECT * FROM exercises")
    List<Exercise> getAll();

    @Query("SELECT exercise_name FROM exercises")
    List<String> getAllNames();

    @Query("SELECT * FROM exercises WHERE exercise_name =:name")
    Exercise getExerciseFromName(String name);

    @Query("SELECT exercise_name FROM exercises WHERE exercise_name = :exerciseName")
    Cursor checkExists(String exerciseName);

    @Query("SELECT type FROM exercises WHERE exercise_name = :exerciseName")
    String getTypeFromName(String exerciseName);

    @Query("SELECT exercise_name FROM exercises WHERE progression_name = :progressionName")
    List<String> getNamesFromProgression(String progressionName);

    @Insert
    void addExercise(Exercise exercise);

    @Insert
    void addMultipleExercises(Exercise[] exercises);

    @Update
    void updateExercise(Exercise exercise);

    @Delete
    void delete(Exercise exercise);
}
