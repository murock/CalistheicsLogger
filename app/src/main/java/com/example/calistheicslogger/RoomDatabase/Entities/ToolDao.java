package com.example.calistheicslogger.RoomDatabase.Entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToolDao {

    @Query("SELECT * FROM tools order by rank desc")
    List<Tool> getAll();

    @Query("SELECT name FROM tools order by rank desc")
    List<String> getAllNames();

    @Query("UPDATE tools " +
            "set rank = " +
            "(case " +
            "when rank = :tool1Rank then :tool2Rank " +
            "when rank <= :tool2Rank and rank >= :tool1Rank then rank - 1 " +
            "when rank <= :tool1Rank and rank >= :tool2Rank then rank + 1 " +
            "else rank " +
            "end)")
    void swapByRank(int tool1Rank, int tool2Rank);

    @Query("DELETE FROM tools WHERE rank == :rank")
    void removeToolByRank(int rank);

    @Query("UPDATE tools SET rank = rank - 1 WHERE rank > :rank")
    void updateRemovedTool(int rank);

    @Insert
    void addTool(Tool tool);

    @Insert
    void addMultipleTools(Tool[] tool);

    @Update
    void updateTool(Tool tool);

    @Delete
    void deleteTool(Tool tool);
}
