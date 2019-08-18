package com.madadgar.objects;

public class Request {

    public String description = "";
    public String imageId = "";
    public UserLocation location;

    public Request() {
        location = new UserLocation();
    }

    public Request(String description, String imageId, UserLocation location){

        this.imageId = imageId;
        this.description = description;
        this.location = location;

    }
}
