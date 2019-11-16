package com.example.calistheicslogger.RoomDatabase.Entities;

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

    @ColumnInfo(name = "progression_name")
    private String progression;

    public Exercise(int id, String name, String categories, String type, Boolean bandAssisted, Boolean weightLoadable, String progression){
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.type = type;
        this.bandAssisted = bandAssisted;
        this.weightLoadable = weightLoadable;
        this.progression = progression;
    }

    @Ignore
    public Exercise(String name, String categories, String type, Boolean bandAssisted, Boolean weightLoadable, String progression){
        this.name = name;
        this.categories = categories;
        this.type = type;
        this.bandAssisted = bandAssisted;
        this.weightLoadable = weightLoadable;
        this.progression = progression;
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

    public static Exercise[] populateData(){
        return new Exercise[]{
                new Exercise("Human Flag", "Back", "Isometric", false, false, "Human Flag"),
                new Exercise("Back Lever","Chest Abs Back", "Isometric", false, false, "Back Lever"),
                new Exercise("Muscle up", "Biceps Back", "Weight and Reps", true, true,"Muscle up"),
        };
    }
}
