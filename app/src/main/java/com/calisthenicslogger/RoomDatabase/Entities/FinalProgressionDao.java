package com.calisthenicslogger.RoomDatabase.Entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FinalProgressionDao {

    @Query("SELECT * FROM final_progressions order by rank desc")
    List<FinalProgression> getAll();

    @Query("SELECT final_progression_name FROM final_progressions order by rank desc")
    List<String> getAllNames();

    @Query("UPDATE final_progressions " +
            "set rank = " +
            "(case " +
            "when rank = :prog1Rank then :prog2Rank " +
            "when rank <= :prog2Rank and rank >= :prog1Rank then rank - 1 " +
            "when rank <= :prog1Rank and rank >= :prog2Rank then rank + 1 " +
            "else rank " +
            "end)")
    void swapByRank(int prog1Rank, int prog2Rank);

    @Query("DELETE FROM final_progressions WHERE rank == :rank")
    void removeProgressionByRank(int rank);

    @Query("UPDATE final_progressions SET rank = rank - 1 WHERE rank > :rank")
    void updateRemovedProgression(int rank);

    @Insert
    void addFinalProgression(FinalProgression finalProgression);

    @Insert
    void addMultipleFinalProgressions(FinalProgression[] finalProgression);

    @Update
    void updateFinalProgression(FinalProgression finalProgression);

    @Delete
    void deleteFinalProgression(FinalProgression finalProgression);
}
