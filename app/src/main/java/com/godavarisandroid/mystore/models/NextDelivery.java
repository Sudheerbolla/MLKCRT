package com.godavarisandroid.mystore.models;

import java.io.Serializable;

/**
 * Created by UMA on 4/22/2018.
 */
public class NextDelivery implements Serializable{
    public String sId;
    public String userId;
    public String productId;
    public String startDate;
    public String endDate;
    public String subType;
    public String subQuantity;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String status;
    public String subscriptionPauseExp;
    public String isPaused;
    public String srNumber;
    public String sdId;
    public String date;
    public String quantity1;
    public String deliveryStatus;
    public String productName;
    public String image;
    public String quanityName;
    public String description;
    public String catId;
    public String isAllCities;
    public String brandId;
    public String price;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int quantity;

    public NextDelivery(String sId, String userId, String productId, String startDate, String endDate, String subType,
                        String subQuantity,
                        String status, String subscriptionPauseExp, String isPaused, String srNumber, String sdId, String date,
                        int quantity, String deliveryStatus, String productName, String image, String quanityName, String description,
                        String catId, String isAllCities, String brandId, String price) {
        this.sId = sId;
        this.userId = userId;
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subType = subType;
        this.subQuantity = subQuantity;
        this.status = status;
        this.subscriptionPauseExp = subscriptionPauseExp;
        this.isPaused = isPaused;
        this.srNumber = srNumber;
        this.sdId = sdId;
        this.date = date;
        this.quantity = quantity;
        this.deliveryStatus = deliveryStatus;
        this.productName = productName;
        this.image = image;
        this.quanityName = quanityName;
        this.description = description;
        this.catId = catId;
        this.isAllCities = isAllCities;
        this.brandId = brandId;
        this.price = price;
    }
}
