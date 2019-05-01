package com.example.wofferapp;

import com.google.firebase.firestore.GeoPoint;

public class OfferDetails {

    private int id;
    private String title;
    private GeoPoint position;
    private String img;
    private String description;
    private String reward;

    public OfferDetails(){}

    public OfferDetails(int id, String title, GeoPoint position, String img, String description, String reward){
        //
    }

    public int getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public String getImg() { return img; }

    public String getDescription() { return description; }

    public String getReward() { return reward; }

}
