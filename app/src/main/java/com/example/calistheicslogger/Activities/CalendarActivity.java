package com.example.calistheicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.calistheicslogger.R;
import com.example.calistheicslogger.RoomDatabase.DatabaseCommunicator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends Activity implements PropertyChangeListener {

    DatabaseCommunicator databaseCommunicator;
    List<String> uniqueTimestamps;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        databaseCommunicator.getUniqueTimestamps();
        this.CreateCalendarView();
    }

    private void PopulateCalendar(){
        List<EventDay> events = new ArrayList<>();
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.calendar_dot);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.RED);
        for(String timestamp : uniqueTimestamps)
        {
            Calendar calendar = Calendar.getInstance();
            int day = Integer.parseInt(timestamp.substring(0,2));
            int month = Integer.parseInt(timestamp.substring(3,5));
            int year = Integer.parseInt(timestamp.substring(6));
            calendar.set(year,month - 1,day);
            events.add(new EventDay(calendar, wrappedDrawable, Color.RED));
        }
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setEvents(events);
    }

    private void CreateCalendarView(){
//        List<EventDay> events = new ArrayList<>();
//
//        Calendar calendar = Calendar.getInstance();
//
//        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.calendar_dot);
//        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
//        DrawableCompat.setTint(wrappedDrawable, Color.GREEN);
//        events.add(new EventDay(calendar, wrappedDrawable, Color.GREEN));
//
        CalendarView calendarView = findViewById(R.id.calendarView);
//        calendarView.setEvents(events);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(clickedDayCalendar.getTime());
                Log.i("Alfie day is ", clickedDayCalendar.getTime().toString());
                newMainAcitivity(date);
            }
        });
    }

    private void newMainAcitivity(String timestamp){
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.putExtra("Timestamp", timestamp);
        startActivity(mainActivity);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName() == "uniqueTimestampsPopulated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uniqueTimestamps = databaseCommunicator.uniqueTimestamps;
                    PopulateCalendar();
                }
            });
        }
    }
}
