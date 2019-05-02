package com.example.wofferapp;

import com.google.firebase.firestore.auth.User;

import java.lang.reflect.Array;

public class UserDetails {

    private String id;
    private int currentOfferid;
    private int[] completedOffers;
    private int colorTheme;

    public UserDetails(){}

    public UserDetails(String id, int currentOfferid, int[] completedOffers, int colorTheme){

    }

    public String getId() {
        return id;
    }

    public int getCurrentOfferid() {
        return currentOfferid;
    }

    public int[] getCompletedOffers() {
        return completedOffers;
    }

    public int getColorTheme() {
        return colorTheme;
    }

}
