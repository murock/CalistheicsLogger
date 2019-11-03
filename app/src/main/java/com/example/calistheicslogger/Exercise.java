package com.example.calistheicslogger;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises")
public class Exercise
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "exercise_name")
    private String name;

    public Exercise(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Ignore
    public Exercise(String name){
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
}
