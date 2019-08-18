package com.madadgar.objects;

public class BloodRequest {

    public String key;
    public String description;
    public BloodGroup blood;

    public BloodRequest(){

    }

    public BloodRequest(String key, String desp, BloodGroup blood){

        this.key = key;
        this.description = desp;
        this.blood = blood;

    }

}
