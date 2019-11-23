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

    @Query("SELECT * FROM tracked_exercises WHERE exercise_name =:name AND timestamp =:timestamp")
    List<TrackedExercise> getTrackedExercisesFromNameAndDate(String name, String timestamp);

    @Query("SELECT *, MAX(set_number) FROM tracked_exercises")
    TrackedExercise getLastTrackedExercise();

    //@Query("UPDATE tracked_exercises set set_number = -set_number where set_number =:setNo1 or set_number =:setNo2;")
    @Query("UPDATE tracked_exercises set set_number = (case set_number when :setNo1 then -:setNo2 else -:setNo1 end) where set_number in (:setNo1, :setNo2)")
    void swapBySetNumber(int setNo1, int setNo2);

//    @Query("UPDATE tracked_exercises set set_number = -(" +
//            "case set_number" +
//            "when -=:setNo1 then )")
    @Query("UPDATE tracked_exercises set set_number = -set_number where set_number < 0")
    void doSetSwap();

    @Insert
    void addTrackedExercise(TrackedExercise trackedExercise);

    @Insert
    void addMultipleTrackedExercises(TrackedExercise[] trackedExercises);

    @Update
    void updateTrackedExercise(TrackedExercise trackedExercise);

    @Delete
    void delete(TrackedExercise trackedExercise);
}
