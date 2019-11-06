package com.example.calistheicslogger.RoomDatabase.Entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FinalProgressionDao {

    @Query("SELECT * FROM final_progressions")
    List<FinalProgression> getAll();

    @Query("SELECT final_progression_name FROM final_progressions")
    List<String> getAllNames();

    @Insert
    void addFinalProgression(FinalProgression finalProgression);

    @Insert
    void addMultipleFinalProgressions(FinalProgression[] finalProgression);

    @Update
    void updateFinalProgression(FinalProgression finalProgression);

    @Delete
    void deleteFinalProgression(FinalProgression finalProgression);
}
