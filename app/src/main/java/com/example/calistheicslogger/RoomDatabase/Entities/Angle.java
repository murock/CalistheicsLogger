package com.example.calistheicslogger.RoomDatabase.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "angles")
public class Angle
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "angle")
    private String angle;

    // 0 is hardest 0=>n
    @ColumnInfo(name = "rank" )
    private int rank;

    public Angle(int id, String angle, int rank){
        this.id = id;
        this.angle = angle;
        this.rank = rank;
    }

    @Ignore
    public Angle(String angle, int rank){
        this.angle = angle;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }


    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    // TODO Remove this and replace with band selecting/creating dialog
    public static Angle[] populateData(){
        return new Angle[]{
                new Angle("Shallow",2),
                new Angle("Medium",1),
                new Angle("Steep",0),
        };
    }
}
