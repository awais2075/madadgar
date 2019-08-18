package com.madadgar.helper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.madadgar.objects.BloodDoner;
import com.madadgar.objects.BloodReciever;
import com.madadgar.objects.Request;
import com.madadgar.objects.UserLocation;

public class Current {

    public static String EMAIL;
    public static String NAME;
    public static String KEY;
    public static UserLocation userLocation;

    // emergency
    public static String emergencyType;
    public static String emergencyProviderKey;

    // sp request
    public static Request request;

    //
    public static BloodDoner bloodDoner;
    public static BloodReciever bloodReciever;

    //
    public static DatabaseReference databaseReference = null;

}
