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

    public Angle(int id, String angle){
        this.id = id;
        this.angle = angle;
    }

    @Ignore
    public Angle(String angle){
        this.angle = angle;
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

    // TODO Remove this and replace with band selecting/creating dialog
    public static Angle[] populateData(){
        return new Angle[]{
                new Angle("Shallow"),
                new Angle("Medium"),
                new Angle("Steep"),
        };
    }
}
