package com.godavarisandroid.mystore.models;

/**
 * Created by UMA on 4/22/2018.
 */
public class DeliveryHistory {
    public String pId, pName, quantityName, date, totalPrice, price, quantity, image;

    public DeliveryHistory(String pId, String pName, String quantityName, String date, String totalPrice, String price,
                           String quantity, String image) {
        this.pId = pId;
        this.pName = pName;
        this.quantityName = quantityName;
        this.date = date;
        this.totalPrice = totalPrice;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }
}
