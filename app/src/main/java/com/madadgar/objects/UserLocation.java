package com.madadgar.objects;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserLocation {

    public double altitude, longitude, latitude;
    public float speed, bearing, accuracy;
    public long time;

    public UserLocation(){

    }

    public UserLocation(double altitude, double longitude, double latitude,
                        float speed, float bearing, float accuracy, long time) {

        this.altitude = altitude;
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.bearing = bearing;
        this.accuracy = accuracy;
        this.time = time;

    }

}
