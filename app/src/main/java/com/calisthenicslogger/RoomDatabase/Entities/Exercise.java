package com.calisthenicslogger.RoomDatabase.Entities;

        import androidx.room.ColumnInfo;
        import androidx.room.Entity;
        import androidx.room.Ignore;
        import androidx.room.Index;
        import androidx.room.PrimaryKey;

@Entity(tableName = "exercises",
        indices = {@Index(value = "exercise_name", unique = true)})

public class Exercise
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "exercise_name")
    private String name;

    @ColumnInfo(name = "categories")
    private String categories;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "band_assisted")
    private Boolean bandAssisted;

    @ColumnInfo(name = "weight_loadable")
    private Boolean weightLoadable;

    @ColumnInfo(name = "tempo_controlled")
    private Boolean tempoControlled;

    @ColumnInfo(name = "tool")
    private Boolean tool;

    @ColumnInfo(name = "cluster")
    private Boolean cluster;

    @ColumnInfo(name = "progression_name")
    private String progression;

    @ColumnInfo(name = "weight_increment")
    private double weightIncrement;

    public Exercise(int id, String name, String categories, String type, Boolean bandAssisted, Boolean weightLoadable, String progression, Boolean tempoControlled, Boolean tool, Boolean cluster, double weightIncrement){
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.type = type;
        this.bandAssisted = bandAssisted;
        this.weightLoadable = weightLoadable;
        this.progression = progression;
        this.tempoControlled = tempoControlled;
        this.tool = tool;
        this.cluster = cluster;
        this.weightIncrement = weightIncrement;
    }

    @Ignore
    public Exercise(String name, String categories, String type, Boolean bandAssisted, Boolean weightLoadable, String progression, Boolean tempoControlled, Boolean tool, Boolean cluster, double weightIncrement){
        this.name = name;
        this.categories = categories;
        this.type = type;
        this.bandAssisted = bandAssisted;
        this.weightLoadable = weightLoadable;
        this.progression = progression;
        this.tempoControlled = tempoControlled;
        this.tool = tool;
        this.cluster = cluster;
        this.weightIncrement = weightIncrement;
    }

    public Boolean getTempoControlled() {
        return tempoControlled;
    }

    public void setTempoControlled(Boolean tempoControlled) {
        this.tempoControlled = tempoControlled;
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

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getBandAssisted() {
        return bandAssisted;
    }

    public void setBandAssisted(Boolean bandAssisted) {
        this.bandAssisted = bandAssisted;
    }

    public Boolean getTool(){
        return tool;
    }

    public void setTool(Boolean tool) {this.tool = tool; }

    public Boolean getWeightLoadable() {
        return weightLoadable;
    }

    public void setWeightLoadable(Boolean weightLoadable) {
        this.weightLoadable = weightLoadable;
    }

    public String getProgression() {
        return progression;
    }

    public void setProgression(String progression) {
        this.progression = progression;
    }

    public double getWeightIncrement() {
        return weightIncrement;
    }

    public void setWeightIncrement(double weightIncrement) {
        this.weightIncrement = weightIncrement;
    }

    public Boolean getCluster() {
        return cluster;
    }

    public void setCluster(Boolean cluster) {
        this.cluster = cluster;
    }

    public static Exercise[] populateData(){
        return new Exercise[]{
                new Exercise("Barbell Squat", "Legs", "Weight and Reps", false, true,"Standalone", false, false, false, 1.25),
                new Exercise("Barbell Deadlift", "Back Legs", "Weight and Reps", false, true,"Standalone", false, false, false, 1.25),
                new Exercise("Barbell Bench Press", "Triceps, Chest", "Weight and Reps", false, true,"Standalone", false, false, false, 1.25),
                new Exercise("Squat", "Legs", "Reps", false, false,"Standalone", false, false, false, 1.25),
                new Exercise("Pistol Squat", "legs", "Reps", false, false,"Standalone", false, false, false, 1.25),
                new Exercise("Plank", "Abs", "Isometric", false, false,"Standalone", false, true, false, 1.25),
                new Exercise("Human Flag", "Back", "Isometric", false, false, "Human Flag", false, false,false, 1.25),
                new Exercise("Back Lever","Chest Abs Biceps Back", "Isometric", false, false, "Back Lever", false, true, false, 1.25),
                new Exercise("Muscle up", "Biceps Back", "Reps", true, false,"Muscle up", false, false,false, 1.25),
                new Exercise("Pull up", "Biceps Back", "Weight and Reps", true, true,"Muscle up", false, false,false, 1.25),
                new Exercise("Chin up", "Biceps Back", "Weight and Reps", true, true,"Muscle up", false, false,false, 1.25),
                new Exercise("Horizontal Row", "Biceps Back", "Weight and Reps", true, true,"Front Lever", false, false,false, 1.25),
                new Exercise("Front Lever", "Back", "Isometric", true, false,"Front Lever", false, true,false, 1.25),
                new Exercise("Push Up", "Triceps Chest", "Weight and Reps", false, true,"Planche", false, false, false, 1.25),
                new Exercise("Pseudo Planche Push Up", "Triceps Chest", "Reps", false, false,"Planche", false, false, false, 1.25),
                new Exercise("Planche", "Triceps Chest", "Isometric", false, true,"Planche", false, false, false, 1.25),
                new Exercise("Dips", "Triceps Chest", "Weight and Reps", false, true,"Planche", false, false, false, 1.25),
                new Exercise("Handstand", "Triceps Shoulders", "Isometric", false, false,"Handstand", false, true, false, 1.25),
                new Exercise("Handstand Push up", "Triceps Shoulders", "Reps", false, false,"Handstand", false, true, false, 1.25),
                new Exercise("Frog to HS Hold", "Triceps Shoulders", "Isometric", false, false,"Handstand", false, true, false, 1.25),
                new Exercise("Frogstand", "Triceps Shoulders", "Isometric", false, false,"Handstand", false, true, false, 1.25),
                new Exercise("Pike Push up", "Triceps Shoulders", "Reps", false, false,"Handstand", false, true, false, 1.25),
                new Exercise("L-Sit", "Abs", "Isometric", false, false,"L-Sit", false, true, false, 1.25),
                new Exercise("L-Sit Compression Hold", "Abs", "Isometric", false, false,"L-Sit", false, true, false, 1.25),
                new Exercise("L-Sit Compression", "Abs", "Reps", false, false,"L-Sit", false, true, false, 1.25),
        };
    }


}
