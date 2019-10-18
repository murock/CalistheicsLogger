package com.example.calistheicslogger;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Exercise.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ExerciseDao exerciseDao();
}
