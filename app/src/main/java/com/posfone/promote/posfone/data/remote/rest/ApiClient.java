package com.posfone.promote.posfone.data.remote.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "http://accounts.protechgenie.in/";
   // public static final String BASE_URL = "https://accounts.pay729.guru/";
    public static final String X_API_KEY = "b29a3099fe2e30ae7b6e580c6ac6482c";
    public static final String STRIPE_KEY = "sk_test_unk6a4i4SKWi6FCfFSCgSdWu";
    public static  final String mDefaultPublishKey = "pk_test_AuNZcbyO5Pi12b4xkg9YV5CR";

    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {

            GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
            Gson gson = builder.create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

}
