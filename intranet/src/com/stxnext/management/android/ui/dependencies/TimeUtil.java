package com.stxnext.management.android.ui.dependencies;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtil {

    public static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat defaultTimeFormat = new SimpleDateFormat("HH:mm");
    
    public static String updateCalendarAndGetFormat(Calendar cal, int year, int monthOfYear, int dayOfMonth ){
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return defaultDateFormat.format(cal.getTime());
    }
    
    public static String updateCalendarTimeAndGetFormat(Calendar cal, int hourOfDay, int minute){
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        return defaultTimeFormat.format(cal.getTime());
    }
    
}
