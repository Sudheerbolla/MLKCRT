package com.godavarisandroid.mystore.models;

import org.json.JSONObject;

public class Notification {
    public String id, title, time, description, status;

    public Notification(JSONObject jsonObject) {
        this.id = jsonObject.optString("id");
        this.title = jsonObject.optString("title");
        this.time = jsonObject.optString("date");
        this.description = jsonObject.optString("text");
        this.status = jsonObject.optString("status");
    }
    //                    {
//                        "id": "1",
//                            "title": "Not1",
//                            "text": "Sample text",
//                            "date": "2018-05-02",
//                            "created_on": "2018-05-02 00:00:00",
//                            "status": "1"
//                    }

}
