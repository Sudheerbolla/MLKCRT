package com.godavarisandroid.mystore.models;

/**
 * Created by UMA on 4/22/2018.
 */
public class PaymentHistory {
    public String id, amount, date, paymentType, recieptNumber;

    public PaymentHistory(String id, String amount, String date, String paymentType, String recieptNumber) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.paymentType = paymentType;
        this.recieptNumber = recieptNumber;
    }
}
