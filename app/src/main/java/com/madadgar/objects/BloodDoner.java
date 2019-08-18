package com.madadgar.objects;


public class BloodDoner extends User {

    public BloodGroup bloodGroup;
    public BloodRequest request;
    public UserLocation userLocation;

    public BloodDoner(){
    }

    public BloodDoner(String name, String email, BloodGroup bloodGroup, BloodRequest request, UserLocation location){

        super(name, email, null);
        this.bloodGroup = bloodGroup;
        this.userLocation = location;

    }

}
