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

    // 1 is hardest 1=>n   0 is always 'No'
    @ColumnInfo(name = "rank" )
    private int rank;

    public Band(int id, String colour, int rank){
        this.id = id;
        this.colour = colour;
        this.rank = rank;
    }

    @Ignore
    public Band(String colour, int rank){
        this.colour = colour;
        this.rank = rank;
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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    // TODO Remove this and replace with band selecting/creating dialog
    public static Band[] populateData(){
        return new Band[]{
                new Band("Green",4),
                new Band("Purple", 3),
                new Band("Black",2),
                new Band("Red",1),
                new Band("No", 0),
        };
    }
}
