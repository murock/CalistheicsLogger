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

    @ColumnInfo(name = "tempo_controlled")
    private Boolean tempoControlled;

    @ColumnInfo(name = "angle")
    private Boolean angle;

    @ColumnInfo(name = "progression_name")
    private String progression;

    public Exercise(int id, String name, String categories, String type, Boolean bandAssisted, Boolean weightLoadable, String progression, Boolean tempoControlled, Boolean angle){
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.type = type;
        this.bandAssisted = bandAssisted;
        this.weightLoadable = weightLoadable;
        this.progression = progression;
        this.tempoControlled = tempoControlled;
        this.angle = angle;
    }

    @Ignore
    public Exercise(String name, String categories, String type, Boolean bandAssisted, Boolean weightLoadable, String progression, Boolean tempoControlled, Boolean angle){
        this.name = name;
        this.categories = categories;
        this.type = type;
        this.bandAssisted = bandAssisted;
        this.weightLoadable = weightLoadable;
        this.progression = progression;
        this.tempoControlled = tempoControlled;
        this.angle = angle;
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

    public Boolean getAngle(){
        return angle;
    }

    public void setAngle(Boolean angle) {this.angle = angle; }

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
                new Exercise("Human Flag", "Back", "Isometric", false, false, "Human Flag", false, false),
                new Exercise("Back Lever","Chest Abs Back", "Isometric", false, false, "Back Lever", false, false),
                new Exercise("Muscle up", "Biceps Back", "Weight and Reps", true, true,"Muscle up", false, false),
        };
    }
}
