package com.example.calistheicslogger.RoomDatabase.Entities;

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

    @Insert
    void addExercise(Exercise exercise);

    @Insert
    void addMultipleExercises(Exercise[] exercises);

    @Update
    void updateExercise(Exercise exercise);

    @Delete
    void delete(Exercise exercise);
}
