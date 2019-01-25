package com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.format;

import android.support.annotation.NonNull;

import com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.CalendarDay;

public interface DayFormatter {

    @NonNull
    String format(@NonNull CalendarDay day);

    public static final DayFormatter DEFAULT = new DateFormatDayFormatter();
}
