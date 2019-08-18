package com.madadgar.objects;

import java.util.ArrayList;

public class BloodResponse {

    public String name;
    public String contactDetails;
    public BloodGroup bloodGroup;

    public BloodResponse(){

    }

    public BloodResponse(String name, String contactDetails, BloodGroup bloodGroup){

        this.name = name;
        this.contactDetails = contactDetails;
        this.bloodGroup = bloodGroup;

    }

}
