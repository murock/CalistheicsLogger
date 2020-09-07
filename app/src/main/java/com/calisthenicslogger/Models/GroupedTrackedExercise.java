package com.calisthenicslogger.Models;

public class GroupedTrackedExercise {

    private String title;
    private String bodyText;

    public GroupedTrackedExercise(String title, String bodyText){
        this.title = title;
        this.bodyText = bodyText;
    }

    public GroupedTrackedExercise(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }
}
