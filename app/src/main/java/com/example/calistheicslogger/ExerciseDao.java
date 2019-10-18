package com.example.calistheicslogger;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface ExerciseDao {

    @Insert
    public void addExercise(Exercise exercise);


}
