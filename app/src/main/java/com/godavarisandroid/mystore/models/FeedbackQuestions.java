package com.godavarisandroid.mystore.models;

/**
 * Created by UMA on 4/22/2018.
 */
public class FeedbackQuestions {
    public String id, name;

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int selectedPosition=-1;

    public FeedbackQuestions(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
