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

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "band_assisted")
    private Boolean bandAssisted;

    @ColumnInfo(name = "weight_loadable")
    private Boolean weightLoadable;

    @ColumnInfo(name = "progression_name")
    private String progression;

    public Exercise(int id, String name, String category, String type, Boolean bandAssisted, Boolean weightLoadable, String progression){
        this.id = id;
        this.name = name;
        this.category = category;
        this.type = type;
        this.bandAssisted = bandAssisted;
        this.weightLoadable = weightLoadable;
        this.progression = progression;
    }

    @Ignore
    public Exercise(String name, String category, String type, Boolean bandAssisted, Boolean weightLoadable, String progression){
        this.name = name;
        this.name = name;
        this.category = category;
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
}
