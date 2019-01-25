package com.godavarisandroid.mystore.models;

/**
 * Created by UMA on 4/22/2018.
 */
public class Wallet {
    public String id, name;
    public int innerItems;

    public Wallet(String id, String name, int innerItems) {
        this.id = id;
        this.name = name;
        this.innerItems = innerItems;
    }
}
