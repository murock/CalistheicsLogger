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

    @ColumnInfo(name = "colour_code")
    private int colourCode;

    // 1 is hardest 1=>n   0 is always 'No'
    @ColumnInfo(name = "rank" )
    private int rank;

    public Band(int id, String colour, int colourCode, int rank){
        this.id = id;
        this.colour = colour;
        this.colourCode = colourCode;
        this.rank = rank;
    }

    @Ignore
    public Band(String colour, int colourCode, int rank){
        this.colour = colour;
        this.colourCode  = colourCode;
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

    public int getColourCode() {
        return colourCode;
    }

    public void setColourCode(int colourCode) {
        this.colourCode = colourCode;
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
                new Band("Green",0xFF29B40F,4),
                new Band("Purple", 0xFF4C0FB4,3),
                new Band("Black",0xFF000000,2),
                new Band("Red",0xFFE70202,1),
                new Band("No", 0xFFFFFFFF,0),
        };
    }
}
