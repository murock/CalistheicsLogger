package com.example.calistheicslogger.RoomDatabase.Entities;

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

    @Query("SELECT * FROM tracked_exercises WHERE exercise_name =:name AND timestamp =:timestamp ORDER BY set_number")
    List<TrackedExercise> getTrackedExercisesFromNameAndDate(String name, String timestamp);

    @Query("SELECT * FROM tracked_exercises WHERE timestamp =:timestamp ORDER BY exercise_name, set_number")
    List<TrackedExercise> getTrackedExercisesFromDate(String timestamp);

    @Query("SELECT * FROM tracked_exercises WHERE exercise_name =:name")
    List<TrackedExercise> getTrackedExercisesFromName(String name);

    @Query("SELECT *, MAX(set_number) FROM tracked_exercises WHERE timestamp =:timestamp")
    TrackedExercise getLastTrackedExercise(String timestamp);

    //@Query("UPDATE tracked_exercises set set_number = -set_number where set_number =:setNo1 or set_number =:setNo2;")
    @Query("UPDATE tracked_exercises set set_number = (case set_number when :setNo1 then :setNo2 else :setNo1 end) where set_number in (:setNo1, :setNo2)")
    void swapBySetNumber(int setNo1, int setNo2);

    @Query("SELECT * FROM tracked_exercises INNER JOIN (SELECT band, MAX(reps) AS Maxreps FROM tracked_exercises WHERE exercise_name =:name GROUP BY band ) topset ON tracked_exercises.band = topset.band AND tracked_exercises.reps = topset.Maxreps")
    List<TrackedExercise> getPersonalRecords2(String name);

    @Query("SELECT *, MAX(weight) weight  FROM tracked_exercises WHERE exercise_name =:name GROUP BY reps,band ")
    List<TrackedExercise> getPersonalRecords1(String name);

//    @Query("with cte as (\n" +
//            "  select t.exercise_name,t.band, t.reps, t.weight\n" +
//            "  from ( \n" +
//            "    select *,\n" +
//            "      row_number() over (partition by band order by reps desc) rnreps,\n" +
//            "      row_number() over (partition by band, reps order by weight desc) rnweight\n" +
//            "    from tracked_exercises\n" +
//            "  ) t  \n" +
//            "  where exercise_name = 'Muscle Up' and (t.rnreps = 1 or t.rnweight = 1)\n" +
//            ")  \n" +
//            "select * from cte c\n" +
//            "where not exists (\n" +
//            "  select 1 from cte\n" +
//            "  where band = c.band and reps > c.reps and weight >= c.weight\n" +
//            ")  ")
    @Query("with cte as (\n" +
            "  select t.id,t.exercise_name,t.timestamp,t.set_number,t.band, t.reps, t.weight,t.time,t.distance,t.tempo,t.angle\n" +
            "  from ( \n" +
            "    select *,\n"+      "" +
            "      row_number() over (partition by band order by reps desc) rnreps,\n" +
            "      row_number() over (partition by band, reps order by weight desc) rnweight" +
            "    from tracked_exercises\n" +
            "  ) t  \n" +
            "  where exercise_name = :name\n" +
            ")  \n" +
            "select * from cte c\n" +
            "where not exists (\n" +
            "  select 1 from cte\n" +
            "  where band = c.band and reps > c.reps and weight >= c.weight\n" +
            ")  \n")
    List<TrackedExercise> getPersonalRecords(String name);

    @Insert
    void addTrackedExercise(TrackedExercise trackedExercise);

    @Insert
    void addMultipleTrackedExercises(TrackedExercise[] trackedExercises);

    @Update
    void updateTrackedExercise(TrackedExercise trackedExercise);

    @Delete
    void delete(TrackedExercise trackedExercise);
}
