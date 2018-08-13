package com.posfone.promote.posfone.rest;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

public class RESTClient {

    public static String HOST_IP = "http://accounts.protechgenie.in/";
    public static final String LOGIN = "auth.php";
    public static final String COUNTRY = "app/country";
    public static final String STATE = "app/state";
    public static final String SIGN_UP = "app/signup";




    private static OkHttpClient client = new OkHttpClient();


    private static MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");


    public static Call call_GET(String requestApi, HashMap<String,String> header, Callback callback) {

        Request.Builder builder = new Request.Builder();
        builder.url(HOST_IP+requestApi);

        for (String key : header.keySet()) {
            builder.addHeader(key,header.get(key));
        }
        Request request =  builder.get().build();

        //builder.post(RequestBody.create(mediaType, body));
        //Request request = builder.build();

        Call call = client.newCall(request);
        call.enqueue(callback);
        printRequest(request);
        return call;
    }

    public static Call call_POST(String requestApi, HashMap<String,String> header, String body, Callback callback) {

        Request.Builder builder = new Request.Builder();
        builder.url(HOST_IP+requestApi);

        for (String key : header.keySet()) {
            builder.addHeader(key,header.get(key));
        }
        builder.post(RequestBody.create(mediaType, body));
        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        printRequest(request);
        return call;
    }







    /**
     * Method will print Http Request body to the LogCat Error
     * @param request pass the request to print its body
     */
    private static void printRequest(Request request) {
        Log.e("-----------", "-------------------------------------------------------------------------");
        try {
            Buffer buffer = new Buffer();
            request.body().writeTo(buffer);
            Log.e("WEB_SERVICE", "BODY \t-> " + buffer.readUtf8());
            Log.e("WEB_SERVICE", "URL\t-> " + request.url().toString());
            Log.e("-----------", "-------------------------------------------------------------------------");
        } catch (IOException |StringIndexOutOfBoundsException  e) {
            Log.e("WEB_SERVICE", e.getMessage());
        } catch (Exception  e) {
            Log.e("WEB_SERVICE", e.getMessage());
        }
    }
}
