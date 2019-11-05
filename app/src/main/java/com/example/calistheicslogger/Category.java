package com.example.calistheicslogger;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "category_name")
    private String name;

    public Category(int id, String name){
        this.id = id;
        this.name = name;
    }

    @Ignore
    public Category(String name){
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

    public static Category[] populateData(){
        return new Category[]{
                new Category("Shoulders"),
                new Category("Triceps"),
                new Category("Biceps"),
                new Category("Chest"),
                new Category("Back"),
                new Category("Legs"),
                new Category("Abs")
        };
    }
}
