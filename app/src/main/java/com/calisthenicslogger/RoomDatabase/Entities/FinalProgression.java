package com.calisthenicslogger.RoomDatabase.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "final_progressions")
public class FinalProgression
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "final_progression_name")
    private String name;

    @ColumnInfo(name = "rank" )
    private int rank;

    public FinalProgression(int id, String name, int rank){
        this.id = id;
        this.name = name;
        this.rank = rank;
    }

    @Ignore
    public FinalProgression(String name, int rank){

        this.name = name;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static FinalProgression[] populateData(){
        return new FinalProgression[]{
                new FinalProgression("Standalone", 7),
                new FinalProgression("L-Sit", 6),
                new FinalProgression("Handstand", 5),
                new FinalProgression("Back Lever", 4),
                new FinalProgression("Muscle up", 3),
                new FinalProgression("Human Flag", 2),
                new FinalProgression("Front Lever", 1),
                new FinalProgression("Planche", 0),
        };
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
