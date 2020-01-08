package com.example.calistheicslogger.RoomDatabase.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "tracked_exercises")

public class TrackedExercise
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "exercise_name")
    private String name;

    @ColumnInfo(name = "timestamp")
    private String timestamp;

    @ColumnInfo(name = "set_number")
    private int setNumber;

    @ColumnInfo(name = "reps")
    private String reps;

    @ColumnInfo(name = "weight")
    private double weight;

    @ColumnInfo(name = "time")
    private String time;

    @ColumnInfo(name = "band")
    private String band;

    @ColumnInfo(name = "distance")
    private int distance;

    @ColumnInfo(name = "tempo")
    private String tempo;

    @ColumnInfo(name = "angle")
    private String angle;

    public TrackedExercise(int id, String name, String timestamp, int setNumber, String reps, double weight, String time, String band, int distance, String tempo, String angle){
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
        this.time = time;
        this.band = band;
        this.distance = distance;
        this.tempo = tempo;
        this.angle = angle;
    }

    @Ignore
    public TrackedExercise(String name, String timestamp, int setNumber, String reps, double weight, String time, String band, int distance, String tempo, String angle){
        this.name = name;
        this.timestamp = timestamp;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
        this.time = time;
        this.band = band;
        this.distance = distance;
        this.tempo = tempo;
        this.angle = angle;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public String getReps() {
        return reps;
    }

    public void setReps(String reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }
}
