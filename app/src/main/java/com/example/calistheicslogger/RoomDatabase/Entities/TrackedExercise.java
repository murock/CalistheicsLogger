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
    private int reps;

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

    @ColumnInfo(name = "tool")
    private String tool;

    @ColumnInfo(name = "free_text")
    private String freeText;

    @ColumnInfo(name = "rest")
    private int rest;

    @ColumnInfo(name = "cluster")
    private String cluster;

    public TrackedExercise(int id, String name, String timestamp, int setNumber, int reps, double weight, String time, String band, int distance, String tempo, String tool, int rest, String cluster){
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
        this.tool = tool;
        this.rest = rest;
        this.cluster = cluster;
    }

    @Ignore
    public TrackedExercise(String name, String timestamp, int setNumber, int reps, double weight, String time, String band, int distance, String tempo, String tool, int rest, String cluster){
        this.name = name;
        this.timestamp = timestamp;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weight = weight;
        this.time = time;
        this.band = band;
        this.distance = distance;
        this.tempo = tempo;
        this.tool = tool;
        this.rest = rest;
        this.cluster = cluster;
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

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
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

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public int getRest() {
        return rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
