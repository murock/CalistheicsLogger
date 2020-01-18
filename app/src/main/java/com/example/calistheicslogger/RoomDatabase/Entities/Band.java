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
    private String colourCode;

    // 1 is hardest 1=>n   0 is always 'No'
    @ColumnInfo(name = "rank" )
    private int rank;

    public Band(int id, String colour, String colourCode, int rank){
        this.id = id;
        this.colour = colour;
        this.colourCode = colourCode;
        this.rank = rank;
    }

    @Ignore
    public Band(String colour, String colourCode, int rank){
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

    public String getColourCode() {
        return colourCode;
    }

    public void setColourCode(String colourCode) {
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
                new Band("Green","#FF29B40F",4),
                new Band("Purple", "#FF4C0FB4",3),
                new Band("Black","#FF000000",2),
                new Band("Red","#FFE70202",1),
                new Band("No", "#FFFFFFFF",0),
        };
    }
}
