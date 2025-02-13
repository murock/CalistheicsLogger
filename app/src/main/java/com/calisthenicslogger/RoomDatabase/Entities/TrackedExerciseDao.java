package com.calisthenicslogger.RoomDatabase.Entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TrackedExerciseDao {

    @Query("SELECT * FROM tracked_exercises")
    List<TrackedExercise> getAll();

    @Query("SELECT exercise_name FROM tracked_exercises")
    List<String> getAllNames();

    @Query("SELECT DISTINCT timestamp FROM tracked_exercises")
    List<String> getAllUniqueTimestamps();

    @Query("SELECT * FROM tracked_exercises WHERE exercise_name =:name AND timestamp =:timestamp ORDER BY set_number")
    List<TrackedExercise> getTrackedExercisesFromNameAndDate(String name, String timestamp);

    @Query("SELECT * FROM tracked_exercises WHERE timestamp =:timestamp ORDER BY exercise_name, set_number")
    List<TrackedExercise> getTrackedExercisesFromDate(String timestamp);

    @Query("SELECT *, max(set_number) set_number FROM tracked_exercises WHERE timestamp =:timestamp GROUP BY exercise_name ")
    List<TrackedExercise> getTrackedExercisesMaxSetFromDate(String timestamp);

    @Query("SELECT * FROM tracked_exercises WHERE exercise_name =:name order by timestamp desc")
    List<TrackedExercise> getTrackedExercisesFromName(String name);

    @Query("SELECT *FROM tracked_exercises WHERE exercise_name =:name order by timestamp desc, set_number desc limit 1")
    TrackedExercise getLatestTrackedExercise(String name);

    @Query("DELETE FROM tracked_exercises WHERE exercise_name =:name AND timestamp =:timestamp AND set_number =:setNo")
    void deleteTrackedExercise(String name, String timestamp, int setNo);

    @Query("UPDATE tracked_exercises SET set_number = set_number - 1 WHERE set_number > :setNo")
    void updateRemovedSet(int setNo);

    @Query("UPDATE tracked_exercises set set_number = (case set_number when :setNo1 then :setNo2 else :setNo1 end) where set_number in (:setNo1, :setNo2)")
    void swapBySetNumber(int setNo1, int setNo2);

    @Query("-- gets the max reps at each set of exercise_name, band, weight\n" +
            "with repsCTE as (\n" +
            "  select *,max(reps) reps\n" +
            "  from tracked_exercises\n" +
            "  where exercise_name = :name\n" +
            "  group by exercise_name, band, weight ), \n" +
            "\n" +
            "-- gets the max weight at each set of exercise_name, band, reps\n" +
            "weightCTE as (\n" +
            "  select *,max(weight) weight\n" +
            "  from tracked_exercises\n" +
            "  where exercise_name = :name\n" +
            "  group by exercise_name, band, reps ) \n" +
            "\n" +
            "select * \n" +
            "from repsCTE r join weightCTE w\n" +
            "on r.id = w.id\n" +
            "order by band,reps" )
    List<TrackedExercise> getPersonalRecords(String name);

    @Query("-- gets the max time at each set of exercise_name, band, weight\n" +
            "with repsCTE as (\n" +
            "  select *,max(time) time\n" +
            "  from tracked_exercises\n" +
            "  where exercise_name = :name\n" +
            "  group by exercise_name, band, weight ), \n" +
            "\n" +
            "-- gets the max weight at each set of exercise_name, band, time\n" +
            "weightCTE as (\n" +
            "  select *,max(weight) weight\n" +
            "  from tracked_exercises\n" +
            "  where exercise_name = :name\n" +
            "  group by exercise_name, band, time ) \n" +
            "\n" +
            "select * \n" +
            "from repsCTE r join weightCTE w\n" +
            "on r.id = w.id\n" +
            "order by band,time" )
    List<TrackedExercise> getPersonalIsometricRecords(String name);

    @Query("select *,max(reps) reps\n" +
            "  from tracked_exercises\n" +
            "  where exercise_name = :name\n" +
            "  group by exercise_name, band, weight, timestamp\n" +
            "  order by timestamp")
    List<TrackedExercise> getRepsChartData(String name);

    @Insert
    void addTrackedExercise(TrackedExercise trackedExercise);

    @Insert
    void addMultipleTrackedExercises(TrackedExercise[] trackedExercises);

    @Update
    void updateTrackedExercise(TrackedExercise trackedExercise);

    @Delete
    void delete(TrackedExercise trackedExercise);
}
