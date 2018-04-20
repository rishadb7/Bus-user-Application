package com.haya.user;

/**
 * Created by hayaj on 3/9/2018.
 */


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {


    @GET("get_alldata.php")
    Call<List<LocationPoints>> getLocationPoints(@Query("apiKey")String apiKey);

    @GET("get_alldata2.php")
    Call<List<LocationPoints2>> getLocationPoints2(@Query("apiKey2")String apiKey2);


    @GET("get_location3.php")
    Call<List<Bus>> getBusLocation(@Query("apiKey2")String apiKey2);

    //994595
}
