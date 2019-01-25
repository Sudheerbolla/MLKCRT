package com.godavarisandroid.mystore.models;

import java.util.ArrayList;

/**
 * Created by Excentd11 on 11/6/2017.
 */

public class Categories {
    public String id, name, image, imagePath;
    public ArrayList<SubCategories> mSubCategories;

    public Categories(String id, String name, String image, String imagePath, ArrayList<SubCategories> mSubCategories) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.imagePath = imagePath;
        this.mSubCategories = mSubCategories;
    }
}
