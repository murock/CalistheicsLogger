package com.example.calistheicslogger.RoomDatabase.Entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AngleDao {

    @Query("SELECT * FROM angles")
    List<Band> getAll();

    @Query("SELECT angle FROM angles")
    List<String> getAllAngles();

    @Insert
    void addAngle(Angle angle);

    @Insert
    void addMultipleAngles(Angle[] angle);

    @Update
    void updateAngle(Angle angle);

    @Delete
    void deleteAngle(Angle angle);
}
