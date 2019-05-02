package com.example.wofferapp;

import java.lang.reflect.Array;
import java.util.List;

public class UserDetails {

    private String id;
    private int currentOfferid;
    private List<Integer> completedOffers;
    private int colorTheme;

    public UserDetails(){}

    public UserDetails(String uid, int currentOff, List<Integer> completedOff, int color){
        id = uid;
        currentOfferid = currentOff;
        completedOffers = completedOff;
        colorTheme = color;
    }

    public String getId() {
        return id;
    }

    public int getCurrentOfferid() {
        return currentOfferid;
    }

    public List<Integer> getCompletedOffers() {
        return completedOffers;
    }

    public int getColorTheme() {
        return colorTheme;
    }

    public void setCurrentOfferid(int currentOfferid) {
        this.currentOfferid = currentOfferid;
    }

    public void setColorTheme(int colorTheme) {
        this.colorTheme = colorTheme;
    }

    public void setCompletedOffers(List<Integer> completedOffers) {
        this.completedOffers = completedOffers;
    }

}
