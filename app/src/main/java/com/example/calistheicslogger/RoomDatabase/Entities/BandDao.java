package com.example.calistheicslogger.RoomDatabase.Entities;

        import androidx.room.Dao;
        import androidx.room.Delete;
        import androidx.room.Insert;
        import androidx.room.Query;
        import androidx.room.Update;

        import java.util.List;

@Dao
public interface BandDao {

    @Query("SELECT * FROM bands")
    List<Band> getAll();

    @Query("SELECT band_colour FROM bands")
    List<String> getAllBandColours();

    @Insert
    void addBand(Band band);

    @Insert
    void addMultipleBands(Band[] band);

    @Update
    void updateBand(Band band);

    @Delete
    void deleteBand(Band band);
}
