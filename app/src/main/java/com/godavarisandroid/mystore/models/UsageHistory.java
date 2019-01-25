package com.godavarisandroid.mystore.models;

/**
 * Created by UMA on 4/22/2018.
 */
public class UsageHistory {
    public String amount, monthName, year, date;

    public UsageHistory(String amount,String monthName,String year,String date) {
        this.amount = amount;
        this.monthName = monthName;
        this.year = year;
        this.date = date;
    }
}
