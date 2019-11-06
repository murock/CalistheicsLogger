package com.example.calistheicslogger.RoomDatabase.Entities;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {

    @Query("SELECT * FROM categories")
    List<Category> getAll();

    @Query("SELECT category_name FROM categories")
    List<String> getAllNames();

    @Insert
    void addCategory(Category category);

    @Insert
    void addMultipleCategories(Category[] categories);

    @Update
    void updateCategory(Category category);

    @Delete
    void deleteCategory(Category category);
}
