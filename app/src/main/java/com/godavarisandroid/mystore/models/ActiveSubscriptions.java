package com.godavarisandroid.mystore.models;

import org.json.JSONObject;

/**
 * Created by UMA on 4/22/2018.
 */
public class ActiveSubscriptions {
    public String sId, userId, productId, startDate, endDate, status, pName, quantityName, description, price, subType, image,
            pauseStartDate, pauseEndDate, dateOfStatus;
    public JSONObject quantityObject;

    public ActiveSubscriptions(String sId, String userId, String productId, String startDate, String endDate, String status,
                               String pName, String quantityName, String description, String price, String subType, String image,
                               JSONObject quantityObject, String pauseStartDate, String pauseEndDate, String dateOfStatus) {
        this.sId = sId;
        this.userId = userId;
        this.productId = productId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.pName = pName;
        this.quantityName = quantityName;
        this.description = description;
        this.price = price;
        this.subType = subType;
        this.image = image;
        this.quantityObject = quantityObject;
        this.pauseStartDate = pauseStartDate;
        this.pauseEndDate = pauseEndDate;
        this.dateOfStatus = dateOfStatus;
    }
}
