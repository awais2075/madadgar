package com.madadgar.objects;

import java.util.ArrayList;

public class BloodReciever extends User {

    public BloodResponse response;
    public UserLocation userLocation;

    public BloodReciever(){

    }

    public BloodReciever(String name, String email, BloodResponse response, UserLocation userLocation){

        super(name, email, null);
        this.response = response;
        this.userLocation = userLocation;

    }

}
