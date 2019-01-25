package com.godavarisandroid.mystore.models;

/**
 * Created by UMA on 4/22/2018.
 */
public class Ads {
    public String id, image, url;

    //    public Ads(String id, String image) {
//        this.id = id;
//        this.image = image;
//    }
    public Ads(String id, String image, String url) {
        this.id = id;
        this.url = url;
        this.image = image;
    }

}
