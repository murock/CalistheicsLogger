package com.example.calistheicslogger.RoomDatabase.Entities;

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

    public FinalProgression(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Ignore
    public FinalProgression(String name){
        this.name = name;
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
                new FinalProgression("Human Flag"),
                new FinalProgression("Back Lever"),
                new FinalProgression("Front Lever"),
                new FinalProgression("Handstand"),
                new FinalProgression("Planche"),
                new FinalProgression("Muscle up"),
        };
    }
}
