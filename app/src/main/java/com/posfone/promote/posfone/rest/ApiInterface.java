package com.posfone.promote.posfone.rest;

import com.posfone.promote.posfone.model.GetCountryResponse;
import com.posfone.promote.posfone.model.GetStateResponse;
import com.posfone.promote.posfone.model.LoginResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("app/login")
    Call<LoginResponse> doLogin(@Header("X-API-KEY") String api_kep,@Body HashMap<String, String> user);


    @GET("app/country")
    Call<GetCountryResponse> getCountryList(@Header("X-API-KEY") String api_kep);

    @POST("app/state")
    Call<GetStateResponse> getStateList(@Header("X-API-KEY") String api_kep, @Body HashMap<String, String> map);
}
