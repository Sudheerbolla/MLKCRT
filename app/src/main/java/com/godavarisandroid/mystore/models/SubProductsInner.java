package com.godavarisandroid.mystore.models;

/**
 * Created by Excentd11 on 11/6/2017.
 */

public class SubProductsInner {
    public String id, name, quantityName, description, status, price, startDate, endDate, image, cName;

    public SubProductsInner(String id, String name, String quantityName, String description, String status, String price,
                            String startDate, String endDate, String image, String cName) {
        this.id = id;
        this.name = name;
        this.quantityName = quantityName;
        this.description = description;
        this.status = status;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.image = image;
        this.cName = cName;
    }
}
