package com.madadgar.objects;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class ServiceProvider extends User {

    public String service;
    public ArrayList<Request> emergencyRequests;

    public ServiceProvider(){
        this.emergencyRequests = new ArrayList<>();
    }

    public ServiceProvider(String name, String email, UserLocation userLocation, String service, ArrayList<Request> requests){

        super(name, email, userLocation);
        this.service = service;
        this.emergencyRequests = requests;

    }

}
