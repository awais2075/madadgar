package com.madadgar.objects;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String name;
    public String key;
    public UserLocation userLocation;

    public User(){

    }

    public User(String name, String email, UserLocation userLocation){

        this.name = name;
        this.key = email;
        this.userLocation = userLocation;

    }

}
