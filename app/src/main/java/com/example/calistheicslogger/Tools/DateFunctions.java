package com.example.calistheicslogger.Tools;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFunctions {

    public static Date GetDateFromTimestamp(String timestamp)
    {
        Calendar calendar = Calendar.getInstance();
        int year = Integer.parseInt(timestamp.substring(0,4));
        int month = Integer.parseInt(timestamp.substring(5,7));
        int day = Integer.parseInt(timestamp.substring(8));
        calendar.set(year,month - 1,day);
        return calendar.getTime();
    }

    public static String GetUKDateFormat(String timestamp)
    {
        Date date = DateFunctions.GetDateFromTimestamp(timestamp);
        return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date);
    }
}
