package com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.format;

import android.support.annotation.NonNull;

import com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.CalendarDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateFormatDayFormatter implements DayFormatter {

    private final DateFormat dateFormat;

    public DateFormatDayFormatter() {
//        this.dateFormat = new SimpleDateFormat("d", Locale.getDefault());
        this.dateFormat = new SimpleDateFormat("d", Locale.getDefault());
    }

    public DateFormatDayFormatter(@NonNull DateFormat format) {
        this.dateFormat = format;
    }

    @Override
    @NonNull
    public String format(@NonNull CalendarDay day) {
        return dateFormat.format(day.getDate());
    }
}
