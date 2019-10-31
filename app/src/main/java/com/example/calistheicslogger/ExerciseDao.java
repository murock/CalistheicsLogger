package com.example.calistheicslogger;

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

    @Insert
    void addExercise(Exercise exercise);

    @Update
    void updateExercise(Exercise exercise);

    @Delete
    void delete(Exercise exercise);
}
