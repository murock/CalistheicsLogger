package com.example.calistheicslogger.RoomDatabase.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tools")
public class Tool
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    // 0 is hardest 0=>n
    @ColumnInfo(name = "rank" )
    private int rank;

    @ColumnInfo(name = "progressions")
    private String progressions;


    public Tool(int id, String name, int rank, String progressions){
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.progressions = progressions;
    }

    @Ignore
    public Tool(String name, String progressions,int rank) {
        this.name = name;
        this.rank = rank;
        this.progressions = progressions;
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


    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getProgressions() {
        return progressions;
    }

    public void setProgressions(String progressions) {
        this.progressions = progressions;
    }

    public static Tool[] populateData(){
        return new Tool[]{
                new Tool("Rings",",All,",9),
                new Tool("Negative",",All,",8),
                new Tool("Wall",",All,",7),
                new Tool("Box",",All,",6),
                new Tool("Straddle",",All,",5),
                new Tool("Adv",",All,",4),
                new Tool("Tucked",",All,",3),
                new Tool("Parallettes",",All,",2),
                new Tool("Shallow Angle",",All,",1),
                new Tool("Steep Angle",",All,",0),
        };
    }


}
