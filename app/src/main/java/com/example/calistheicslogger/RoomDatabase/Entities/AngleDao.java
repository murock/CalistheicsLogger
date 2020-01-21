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
    List<Angle> getAll();

    @Query("SELECT angle FROM angles")
    List<String> getAllAngles();

    @Query("UPDATE angles " +
            "set rank = " +
            "(case " +
            "when rank = :angle1Rank then :angle2Rank " +
            "when rank <= :angle2Rank and rank >= :angle1Rank then rank - 1 " +
            "when rank <= :angle1Rank and rank >= :angle2Rank then rank + 1 " +
            "else rank " +
            "end)")
    void swapByRank(int angle1Rank, int angle2Rank);

    @Insert
    void addAngle(Angle angle);

    @Insert
    void addMultipleAngles(Angle[] angle);

    @Update
    void updateAngle(Angle angle);

    @Delete
    void deleteAngle(Angle angle);
}
