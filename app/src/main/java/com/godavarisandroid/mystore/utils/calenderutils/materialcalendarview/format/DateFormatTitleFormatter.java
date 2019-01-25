package com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.format;

import com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview.CalendarDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateFormatTitleFormatter implements TitleFormatter {

    private final DateFormat dateFormat;

    public DateFormatTitleFormatter() {
        this.dateFormat = new SimpleDateFormat("LLLL yyyy", Locale.getDefault()
        );
    }

    public DateFormatTitleFormatter(DateFormat format) {
        this.dateFormat = format;
    }

    @Override
    public CharSequence format(CalendarDay day) {
        return dateFormat.format(day.getDate());
    }
}
