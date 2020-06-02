package com.calisthenicslogger.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.calisthenicslogger.R;
import com.calisthenicslogger.RoomDatabase.DatabaseCommunicator;
import com.calisthenicslogger.Tools.PropertyTextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends Activity implements PropertyChangeListener {

    DatabaseCommunicator databaseCommunicator;
    List<String> uniqueTimestamps;
    String lastDateSelected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);
        databaseCommunicator = DatabaseCommunicator.getInstance(this);
        databaseCommunicator.addPropertyChangeListener(this);
        databaseCommunicator.getUniqueTimestamps();
        this.CreateCalendarView();
        this.PopulateScrollView();
    }

    private void PopulateCalendar(){
        List<EventDay> events = new ArrayList<>();
        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.calendar_dot);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.RED);
        for(String timestamp : uniqueTimestamps)
        {
            Calendar calendar = Calendar.getInstance();
            int year = Integer.parseInt(timestamp.substring(0,4));
            int month = Integer.parseInt(timestamp.substring(5,7));
            int day = Integer.parseInt(timestamp.substring(8));
            calendar.set(year,month - 1,day);
            events.add(new EventDay(calendar, wrappedDrawable, Color.RED));
        }
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setEvents(events);
    }

    private void CreateCalendarView(){
        CalendarView calendarView = findViewById(R.id.calendarView);

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(clickedDayCalendar.getTime());
                if(date.equals(lastDateSelected))
                {
                    // Second click
                    newMainAcitivity(date);
                }else{
                    // First Click
                    databaseCommunicator.getExercisesFromDate(date);
                }
                lastDateSelected = date;
            }
        });
    }

    private void PopulateScrollView(){
//        LinearLayout exercisePeakList = findViewById(R.id.linearLayout);
//        for(int i = 0; i < 10 ; i++)
//        {
//            PropertyTextView exerciseTextView = new PropertyTextView(CalendarActivity.this);
//            exerciseTextView.setClickable(true);
//            exerciseTextView.exerciseName = "Test";
//            exerciseTextView.setText("This is a test " + i);
//            exercisePeakList.addView(exerciseTextView);
//        }
    }

    private void newMainAcitivity(String timestamp){
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.putExtra("Timestamp", timestamp);
        startActivity(mainActivity);
    }

    private void newTrackAcitivity(String exercise){
        Intent trackActivity = new Intent(this, TrackActivity.class);
        trackActivity.putExtra("Exercise", exercise);
        trackActivity.putExtra("Date", lastDateSelected);
        startActivity(trackActivity);
    }

    private View.OnClickListener handleExerciseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PropertyTextView textView = (PropertyTextView)v;
            Toast.makeText(CalendarActivity.this, "You pressed: " + textView.exerciseName ,Toast.LENGTH_SHORT).show();
            newTrackAcitivity(textView.exerciseName);
        }
    };

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
        } else if (evt.getPropertyName() == "exerciseFromDatePopulated"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LinearLayout linearLayout = findViewById(R.id.linearLayout);
                    MainActivity.populateDaysExercises(linearLayout, handleExerciseClick, CalendarActivity.this);
                }
            });
        }
    }
}
