package com.example.calistheicslogger.Activities;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);
        this.CreateCalendarView();
    }

    private void CreateCalendarView(){
        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.calendar_dot);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.GREEN);
        events.add(new EventDay(calendar, wrappedDrawable, Color.GREEN));

        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setEvents(events);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                Log.i("Alfie day is ", clickedDayCalendar.getTime().toString());
            }
        });
    }
}
