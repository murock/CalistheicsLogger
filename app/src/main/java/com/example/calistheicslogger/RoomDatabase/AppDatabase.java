package com.example.calistheicslogger.RoomDatabase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.calistheicslogger.RoomDatabase.Entities.Band;
import com.example.calistheicslogger.RoomDatabase.Entities.BandDao;
import com.example.calistheicslogger.RoomDatabase.Entities.Category;
import com.example.calistheicslogger.RoomDatabase.Entities.CategoryDao;
import com.example.calistheicslogger.RoomDatabase.Entities.Exercise;
import com.example.calistheicslogger.RoomDatabase.Entities.ExerciseDao;
import com.example.calistheicslogger.RoomDatabase.Entities.FinalProgression;
import com.example.calistheicslogger.RoomDatabase.Entities.FinalProgressionDao;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.Executors;

@Database(entities = {Exercise.class, Category.class, FinalProgression.class, Band.class},version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "app_db";
    private static AppDatabase instance;

    private PropertyChangeSupport support;

    protected AppDatabase() {
        support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        support.removePropertyChangeListener(pcl);
    }

    private void OnDatabaseInitialised() {
        support.firePropertyChange("initialised", null, null);
    }

    public static synchronized AppDatabase getInstance(final Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    DB_NAME)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            Executors.newSingleThreadScheduledExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("Exercise ","got here");
                                    // Pre populate database
                                    getInstance(context).categoryDao().addMultipleCategories(Category.populateData());
                                    getInstance(context).finalProgressionDao().addMultipleFinalProgressions(FinalProgression.populateData());
                                    getInstance(context).exerciseDao().addMultipleExercises(Exercise.populateData());
                                    getInstance(context).bandDao().addMultipleBands(Band.populateData());
                                    instance.OnDatabaseInitialised();
                                }
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }

    public abstract ExerciseDao exerciseDao();

    public abstract CategoryDao categoryDao();

    public abstract FinalProgressionDao finalProgressionDao();

    public abstract BandDao bandDao();
}
