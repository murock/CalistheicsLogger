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

    public Tool(int id, String name, int rank){
        this.id = id;
        this.name = name;
        this.rank = rank;
    }

    @Ignore
    public Tool(String name, int rank){
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


    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    // TODO Remove this and replace with band selecting/creating dialog
    public static Tool[] populateData(){
        return new Tool[]{
                new Tool("Adv",4),
                new Tool("Tucked",3),
                new Tool("Parallettes",2),
                new Tool("Shallow Angle",1),
                new Tool("Steep Angle",0),
        };
    }
}
