package com.example.calistheicslogger.RoomDatabase.Entities;

        import androidx.room.Dao;
        import androidx.room.Delete;
        import androidx.room.Insert;
        import androidx.room.Query;
        import androidx.room.Update;

        import java.util.List;

@Dao
public interface BandDao {

    @Query("SELECT * FROM bands order by rank desc")
    List<Band> getAll();

    @Query("SELECT band_colour FROM bands")
    List<String> getAllBandColours();

   // @Query("UPDATE bands set rank = (case rank when :band1Rank then :band2Rank else :band1Rank end) where rank in (:band1Rank, :band2Rank)")
    @Query("UPDATE bands " +
            "set rank = " +
            "(case " +
            "when rank = :band1Rank then :band2Rank " +
            "when rank <= :band2Rank and rank >= :band1Rank then rank - 1 " +
            "when rank <= :band1Rank and rank >= :band2Rank then rank + 1 " +
            "else rank " +
            "end)")
    void swapByRank(int band1Rank, int band2Rank);

    @Insert
    void addBand(Band band);

    @Insert
    void addMultipleBands(Band[] band);

    @Update
    void updateBand(Band band);

    @Delete
    void deleteBand(Band band);
}
