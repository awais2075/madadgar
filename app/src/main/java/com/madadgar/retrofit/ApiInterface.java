package com.madadgar.retrofit;

import com.madadgar.model.retrofit.Direction;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiInterface {
    @GET("json")
    Call<Direction> getDirections(@QueryMap  HashMap<String, String> map);
 
    }