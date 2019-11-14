package com.example.calistheicslogger.RoomDatabase.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "bands")
public class Band
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "band_colour")
    private String colour;

    public Band(int id, String colour){
        this.id = id;
        this.colour = colour;
    }

    @Ignore
    public Band(String colour){
        this.colour = colour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    // TODO Remove this and replace with band selecting/creating dialog
    public static Band[] populateData(){
        return new Band[]{
                new Band("Green"),
                new Band("Purple"),
                new Band("Black"),
                new Band("Red"),
        };
    }
}
